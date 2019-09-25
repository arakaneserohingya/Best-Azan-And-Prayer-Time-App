package com.alhadara.omar.azan;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.alhadara.omar.azan.Activities.UpdateCurrentLocationActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.tasks.OnSuccessListener;


public class LocationHandler {

    static final int ACCESS_LOCATION_PERMISSION = 1;
    public boolean NEW_LOCATION_FLAG;
    private LocationRequest locationRequest;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback locationCallbacks;

    public LocationHandler(final Activity activity, Context context, final String tempLocationFile, int interval){

        locationRequest = new LocationRequest();
        locationRequest.setInterval(interval);
        locationRequest.setFastestInterval(interval);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        NEW_LOCATION_FLAG = false;
        final SharedPreferences temp = activity.getSharedPreferences(tempLocationFile, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = temp.edit();

        //FusedLocationProviderClient fusedLocationProviderClient;
        //fusedLocationProviderClient.
        //https://developer.android.com/training/permissions/requesting
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted

            // No explanation needed; request the permission
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    ACCESS_LOCATION_PERMISSION);

            // ACCESS_LOCATION_PERMISSION is an
            // app-defined int constant. The callback method gets the
            // result of the request.
            return;
        }

        mFusedLocationClient = new FusedLocationProviderClient(context);

        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(activity, new OnSuccessListener<android.location.Location>() {
                    @Override
                    public void onSuccess(android.location.Location location) {

                        editor.putFloat("latitude",(float) location.getLatitude());
                        editor.putFloat("longitude",(float) location.getLongitude());
                        editor.putFloat("timezone",TM.getTimeOffset((float) location.getLatitude(),(float) location.getLongitude()));
                        editor.putBoolean("islocationassigned",true);
                        editor.commit();
                    }
                });

        locationCallbacks = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if(locationResult == null ){
                    return;
                }
                android.location.Location location = locationResult.getLastLocation();
                NEW_LOCATION_FLAG = true;

                editor.putFloat("latitude",(float) location.getLatitude());
                editor.putFloat("longitude",(float) location.getLongitude());
                editor.putFloat("timezone",TM.getTimeOffset((float) location.getLatitude(),(float) location.getLongitude()));
                editor.putBoolean("islocationassigned",true);
                editor.commit();
                finish();
            }

            @Override
            public void onLocationAvailability(LocationAvailability locationAvailability) {
                super.onLocationAvailability(locationAvailability);
            }
        };

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            return;
        }
        mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallbacks,activity.getMainLooper());
    }
    public void finish(){
        mFusedLocationClient.removeLocationUpdates(locationCallbacks);
        mFusedLocationClient.flushLocations();
    }
}
