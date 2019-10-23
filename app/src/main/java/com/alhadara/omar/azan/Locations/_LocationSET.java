package com.alhadara.omar.azan.Locations;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
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

    public final static String locationsFile = "locations.txt";
    public static String currentLocation = "currentlocation.txt";

    public static void getLocationFromNetwork(final Activity activity, final String tempLocationFile, final locationSuccess success) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final AlertDialog alertDialog = builder.setMessage("Updating location ...").create();
        alertDialog.show();
        final LocationHandler locationHandler = new LocationHandler(activity, activity.getApplicationContext(), tempLocationFile, 2000);
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            int i = 0;

            @Override
            public void run() {
                if (!locationHandler.NEW_LOCATION_FLAG && i < 10) {
                    handler.postDelayed(this, 1000);
                    i++;
                    if (!alertDialog.isShowing()) alertDialog.show();
                    success.onFail();
                } else if (!locationHandler.NEW_LOCATION_FLAG) {
                    Toast.makeText(activity, "Location update failed!", Toast.LENGTH_SHORT).show();
                    locationHandler.finish();
                    if (alertDialog.isShowing()) alertDialog.cancel();
                    success.onFail();
                } else {

                    Toast.makeText(activity, "Location Updated successfully!", Toast.LENGTH_SHORT).show();
                    if (alertDialog.isShowing()) alertDialog.cancel();
                    success.onSuccess();
                    //LocationsActivity.assignLocation(activity, tempLocationFile, LocationsActivity.lastLocation);
                }
            }
        };
        handler.postDelayed(runnable,1000);
        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                handler.removeCallbacks(runnable);
            }
        });
    }

    public static boolean getLastLocation(Activity activity, String tempLocationFile) {
        final boolean[] b = new boolean[] {false};
        final SharedPreferences temp = activity.getSharedPreferences(tempLocationFile, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = temp.edit();
        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted

                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LocationHandler.ACCESS_LOCATION_PERMISSION);

                // ACCESS_LOCATION_PERMISSION is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
            Location lastKnownLocationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location location;
            if (lastKnownLocationGPS != null) {
                location = lastKnownLocationGPS;
            } else {
                location =  locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            }
            editor.putFloat("latitude",(float) location.getLatitude());
            editor.putFloat("longitude",(float) location.getLongitude());
            editor.putFloat("timezone", getTimeOffset((float) location.getLatitude(),(float) location.getLongitude()));
            editor.putBoolean("islocationassigned",true);
            editor.commit();
            b[0] = true;
        }
        return b[0];
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

    public static void checkCurrentLocation(final Activity activity) {
        if(!isLocationAssigned(activity)){
            getLocationFromNetwork(activity, currentLocation, new locationSuccess() {
                @Override
                public void onSuccess() {
                    activity.recreate();
                }

                @Override
                public void onFail() {
                    getLastLocation(activity,currentLocation);
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setMessage(activity.getResources().getString(R.string.current_location_not_found));
                    builder.setNeutralButton(activity.getResources().getString(R.string.mdtp_ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    activity.recreate();
                }
            });
        }
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
        assignLocation(context,locationFileName,id+".txt");
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

    public interface locationSuccess{
        void onSuccess();
        void onFail();
    }
}
