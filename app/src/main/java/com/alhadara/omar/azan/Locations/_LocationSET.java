package com.alhadara.omar.azan.Locations;

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

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.alhadara.omar.azan.TimezoneMapper;
import com.example.omar.azanapkmostafa.R;

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
    public final static int GPS_REQUEST = 1,NETWORK_REQUEST =2,GPS_AND_NETWORK_REQUEST=3;

    public static void getLocation(final Activity activity, final String tempLocationFile,int request_type, final locationUpdateResult todo) {
        final Handler handler = new Handler();
        final AlertDialog builder = new AlertDialog.Builder(activity).create();
        builder.setMessage("Updating Location...");
        final LocationManager locationManger = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        final LocationListener listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (location != null) {
                    handler.removeCallbacks(onUpdateLocationTimesUp);
                    builder.cancel();
                    assignLocation(activity, location, tempLocationFile);
                    todo.onSuccess();
                    locationManger.removeUpdates(this);
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
        onUpdateLocationTimesUp = new Runnable() {
            @Override
            public void run() {
                builder.cancel();
                todo.onFail();
                locationManger.removeUpdates(listener);
            }
        };
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
            }
        });
       if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
               && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
           // TODO: Consider calling
           //    Activity#requestPermissions
           // here to request the missing permissions, and then overriding
           //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
           //                                          int[] grantResults)
           // to handle the case where the user grants the permission. See the documentation
           // for Activity#requestPermissions for more details.
           todo.onFail();
           return;
       }
       builder.show();
        if(request_type == NETWORK_REQUEST || request_type == GPS_AND_NETWORK_REQUEST)
            locationManger.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, listener);
       if(request_type == GPS_REQUEST || request_type == GPS_AND_NETWORK_REQUEST)
           locationManger.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
       handler.postDelayed(onUpdateLocationTimesUp,20000);
    }
    public static boolean getLastLocation(Activity activity, String tempLocationFile) {
        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted

                // No explanation needed; request the permission

                // ACCESS_LOCATION_PERMISSION is an
                // app-defined int constant. The callback method gets the
                // result of the request.
                return false;
            }
            Location lastKnownLocationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location location;
            if (lastKnownLocationGPS != null) {
                location = lastKnownLocationGPS;
            } else {
                location =  locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            }
            if(location !=null) {
                assignLocation(activity, location, tempLocationFile);
                return true;
            }
        }
        return false;
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
                    Toast.makeText(activity,"Location updated successfully!",Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFail() {
                    if(getLastLocation(activity, currentLocation)){
                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        builder.setMessage(activity.getResources().getString(R.string.current_location_not_found));
                        builder.setNeutralButton(activity.getResources().getString(R.string.mdtp_ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        builder.show();
                        activity.recreate();
                    }else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        builder.setMessage(activity.getResources().getString(R.string.current_location_not_found));
                        builder.setNeutralButton(activity.getResources().getString(R.string.mdtp_ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        builder.show();
                    }

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
        builder.setMessage("Updating Location...");
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
                    getLocation(activity,tempLocationFile,GPS_AND_NETWORK_REQUEST,result);
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
        return TimeUnit.HOURS.convert(mGMTOffset, TimeUnit.MILLISECONDS);
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
