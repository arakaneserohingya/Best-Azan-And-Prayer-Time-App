package com.alnamaa.engineering.azan.Locations;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.alnamaa.engineering.azan.R;
import com.alnamaa.engineering.azan.TimezoneMapper;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class _LocationSET {

    public static final int ACCESS_LOCATION_PERMISSION = 1;
    private static boolean IS_PERMISSION_REQUEST_HAD_SHOWN_BEFORE = false; // true if permission request shown for one run time
    public final static String locationsFile = "locations";
    public final static String currentLocation = "currentlocation";
    private static Runnable onUpdateLocationTimesUp;

    public static void getLocation(final Activity activity, final String tempLocationFile, final locationUpdateResult todo) {
        final FusedLocationProviderClient providerClient = LocationServices.getFusedLocationProviderClient(activity);
        final Handler handler = new Handler();
        final AlertDialog builder = new AlertDialog.Builder(activity).create();
        builder.setMessage(activity.getResources().getString(R.string.alert_dialog_updating_location));
        final LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(1000); //Request for location each 1 sec during whole 10 sec
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        final LocationCallback callback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if(locationResult!=null) {
                    builder.cancel();
                    handler.removeCallbacks(onUpdateLocationTimesUp);
                    android.location.Location location = locationResult.getLastLocation();
                    _LocationSET.assignLocation(activity, location, tempLocationFile);
                    todo.onSuccess();
                    providerClient.removeLocationUpdates(this);
                }
            }
            @Override
            public void onLocationAvailability(LocationAvailability locationAvailability) {
                super.onLocationAvailability(locationAvailability);
            }
        };
        onUpdateLocationTimesUp = new Runnable() {
            @Override
            public void run() {
                builder.cancel();
                todo.onFail();
                providerClient.removeLocationUpdates(callback);
            }
        };
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
            }
        });
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            todo.onFail();
            return;
        }
        builder.show();
        providerClient.requestLocationUpdates(locationRequest, callback,activity.getMainLooper());
        handler.postDelayed(onUpdateLocationTimesUp, 10000);
    }
    public static void getLastLocation(final Activity activity, final String tempLocationFile, final locationUpdateResult result) {
        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            result.onFail();
        }
        FusedLocationProviderClient providerClient = LocationServices.getFusedLocationProviderClient(activity);
        providerClient.getLastLocation().addOnSuccessListener(activity, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(android.location.Location location) {
                result.onSuccess();
                _LocationSET.assignLocation(activity,location,tempLocationFile);
            }
        });
        providerClient.getLastLocation().addOnFailureListener(activity, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                result.onFail();
            }
        });
    }
    public static boolean assignLocation(Context context, String from, String to){
        SharedPreferences From = context.getSharedPreferences(from, Context.MODE_PRIVATE);
        SharedPreferences To = context.getSharedPreferences(to, Context.MODE_PRIVATE);
        if(!From.getBoolean("islocationassigned",false)){

            return false;
        }
        SharedPreferences.Editor editor = To.edit();
        editor.putString("location_name",From.getString("location_name",""));
        editor.putFloat("latitude",From.getFloat("latitude",0));
        editor.putFloat("longitude",From.getFloat("longitude",0));
        editor.putFloat("timezone",From.getFloat("timezone",0));
        editor.putBoolean("islocationassigned",true);
        editor.commit();
        return true;
    }
    public static boolean assignLocation(Context context, Location from, String to){

        SharedPreferences To = context.getSharedPreferences(to, Context.MODE_PRIVATE);
        if(from == null){

            return false;
        }
        SharedPreferences.Editor editor = To.edit();
        editor.putString("location_name","");
        editor.putFloat("latitude", (float) from.getLatitude());
        editor.putFloat("longitude", (float) from.getLongitude());
        editor.putFloat("timezone",getTimeOffset((float) from.getLatitude(),(float) from.getLongitude()));
        editor.putBoolean("islocationassigned",true);
        editor.commit();
        return true;
    }
    public static void checkCurrentLocation(final Activity activity) {
        if(!isLocationAssigned(activity)){
            getLocationWithRequests(activity, currentLocation, new locationUpdateResult() {
                @Override
                public void onSuccess() {
                    activity.recreate();
                    Toast.makeText(activity,activity.getResources().getString(R.string.toast_location_updated_successfully),Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFail() {
                    getLastLocation(activity, currentLocation, new locationUpdateResult() {
                        @Override
                        public void onSuccess() {
                            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                            builder.setMessage(activity.getResources().getString(R.string.current_location_not_found));
                            builder.setPositiveButton(activity.getResources().getString(R.string.mdtp_ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    activity.recreate();
                                    dialogInterface.dismiss();
                                }
                            });
                            builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialogInterface) {
                                    activity.recreate();
                                }
                            });
                            builder.show();
                        }

                        @Override
                        public void onFail() {
                            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                            builder.setMessage(activity.getResources().getString(R.string.current_location_not_found_and_last));
                            builder.setPositiveButton(activity.getResources().getString(R.string.mdtp_ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                            builder.show();
                        }
                    });
                }
            });
        }
    }
    public static void getRequests(final Activity activity){
        if(ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED )
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                ACCESS_LOCATION_PERMISSION);
    }
    public static void getLocationWithRequests(final Activity activity, final String tempLocationFile, final locationUpdateResult result){
        final Handler handler = new Handler();
        final AlertDialog builder = new AlertDialog.Builder(activity).create();
        builder.setMessage(activity.getResources().getString(R.string.alert_dialog_updating_location));
        builder.show();
        builder.setCancelable(false);
        Runnable runnable = new Runnable() {
            int i=0;
            @Override
            public void run() {
                ++i;
                if(ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED ) {
                    builder.cancel();
                    getLocation(activity,tempLocationFile,result);
                    return;
                }
                else if(i==15 /* i==10 -> 5 sec*/) {
                    builder.cancel();
                    result.onFail();
                    return;
                }
                if(!IS_PERMISSION_REQUEST_HAD_SHOWN_BEFORE){
                    IS_PERMISSION_REQUEST_HAD_SHOWN_BEFORE = true;
                    ActivityCompat.requestPermissions(activity,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            ACCESS_LOCATION_PERMISSION);
                }
                handler.postDelayed(this,500);
            }
        };
        handler.postDelayed(runnable,0);
    }
    public static void addToLocationsFile(Context context, String id) {
        SharedPreferences locations = context.getSharedPreferences(locationsFile, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = locations.edit();
        int index = locations.getInt("locationsnumber",0) + 1;
        editor.putString("location" + index ,id);
        editor.putInt("locationsnumber",index);
        editor.commit();
    }
    public static void removeFromLocationsFile(Context context, int i) {
        SharedPreferences locations = context.getSharedPreferences(locationsFile, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = locations.edit();
        int locationsNumber = locations.getInt("locationsnumber",1);
        for(int j=i;j<6;j++){
            editor.putString("location"+j,locations.getString("location"+(j+1),""));
        }
        editor.putInt("locationsnumber",locationsNumber-1);
        editor.commit();
    }

    public static void addLocationToSavedLocations(Context context, String locationFileName) {
        String id = context.getSharedPreferences(locationFileName, Context.MODE_PRIVATE).getString("location_name","").replaceAll("[^a-zA-Z0-9\\.\\-]", "").toLowerCase();
        if(isAlreadyExistInLocationsFile(context,id)) {Toast.makeText(context,"Location already exist!",Toast.LENGTH_SHORT).show(); return;}
        // ID == Location file name should differs, in order to avoid file overwriting
        assignLocation(context,locationFileName,id);
        addToLocationsFile(context,id);
    }

    private static boolean isAlreadyExistInLocationsFile(Context context, String ID) {
        SharedPreferences locations = context.getSharedPreferences(locationsFile, Context.MODE_PRIVATE);
        for(int i=1;i<6;i++){
            if(locations.getString("location" + i , "").equals(ID)) return true;
        }
        return false;
    }

    public static boolean checkNetworkAvailability(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    public static boolean checkGpsEnabled(Context context) {
        final LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public static boolean checkGpsAvailability(Context context) {
        final LocationManager mgr = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (mgr == null) return false;
        final List<String> providers = mgr.getAllProviders();
        if (providers == null) return false;
        return providers.contains(LocationManager.GPS_PROVIDER);
    }

    public static boolean isLocationAssigned(Context context){
        return context.getSharedPreferences(currentLocation,Context.MODE_PRIVATE).getBoolean("islocationassigned",false);
    }

    public static void setLocationAssigned(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(currentLocation,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("islocationassigned",true);
        editor.commit();
    }

    public static float getTimeOffset(float latitude, float longitude) {
        Calendar mCalendar = new GregorianCalendar();
        TimeZone mTimeZone = TimeZone.getTimeZone(TimezoneMapper.latLngToTimezoneString(latitude,longitude));
        int mGMTOffset = mTimeZone.getRawOffset();
        if (mTimeZone.inDaylightTime(mCalendar.getTime())){
            mGMTOffset += mTimeZone.getDSTSavings();
        }
        return TimeUnit.HOURS.convert(mTimeZone.getRawOffset(), TimeUnit.MILLISECONDS);
    }

    public static void clearTempLocationFile(Context context, String s) {
        SharedPreferences pref = context.getSharedPreferences(s,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
    }

    public interface locationUpdateResult {
        void onSuccess();
        void onFail();
    }
}
