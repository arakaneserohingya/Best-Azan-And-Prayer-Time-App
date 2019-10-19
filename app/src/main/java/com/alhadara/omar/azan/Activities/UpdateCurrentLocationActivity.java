package com.alhadara.omar.azan.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alhadara.omar.azan.Alarms.AlarmsScheduler;
import com.alhadara.omar.azan.Configurations;
import com.alhadara.omar.azan.LocationHandler;
import com.example.omar.azanapkmostafa.R;

import java.util.Calendar;
import java.util.List;

public class UpdateCurrentLocationActivity extends AppCompatActivity {

    private Handler handler;
    private final String tempLocationFile = "updatecurrenttemplocationfile.txt";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_current_location);

        Toolbar toolbar = findViewById(R.id.toolbar_update_current_location_activity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.update_current_location));
        setTitleLocation();

        findViewById(R.id.update_current_location_activity_internet_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UpdateCurrentLocationActivity.this, InternetLocationSearchActivity.class);
                intent.putExtra("filename","temp.txt");
                startActivity(intent);
            }
        });
        findViewById(R.id.update_current_location_activity_manually_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UpdateCurrentLocationActivity.this, ManuallyLocationActivity.class);
                intent.putExtra("filename","temp.txt");
                startActivity(intent);

            }
        });
        findViewById(R.id.update_current_location_activity_gps_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!checkGpsAvailability()) Toast.makeText(UpdateCurrentLocationActivity.this,getResources().getString(R.string.this_device_has_no_gps_equipment),Toast.LENGTH_SHORT).show();
                else if(!checkGpsEnabled()) Toast.makeText(UpdateCurrentLocationActivity.this,getResources().getString(R.string.please_enable_gps_first),Toast.LENGTH_SHORT).show();
                else getLocationFromNetwork();
            }
        });
        findViewById(R.id.update_current_location_activity_last_location_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(UpdateCurrentLocationActivity.this);
                final AlertDialog alertDialog = builder.setMessage("Updating location ...").create();
                alertDialog.setCancelable(false);
                alertDialog.show();
                handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        alertDialog.cancel();
                        if(!LocationsActivity.assignLocation(UpdateCurrentLocationActivity.this,LocationsActivity.lastLocation,tempLocationFile))
                            Toast.makeText(UpdateCurrentLocationActivity.this,"Failed to find location inforamtion!",Toast.LENGTH_SHORT).show();
                    }
                },1000);
            }
        });
        findViewById(R.id.update_current_location_activity_network_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!checkNetworkAvailability()) Toast.makeText(UpdateCurrentLocationActivity.this,getResources().getString(R.string.please_check_for_internet_connection),Toast.LENGTH_SHORT).show();
                else getLocationFromNetwork();
            }
        });
        findViewById(R.id.update_current_location_activity_save_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!LocationsActivity.assignLocation(UpdateCurrentLocationActivity.this,tempLocationFile,Configurations.mainConFile)) { UpdateCurrentLocationActivity.this.finish(); return; }
                Configurations.setLocationAssigned(UpdateCurrentLocationActivity.this);
                Configurations.updateTimes(UpdateCurrentLocationActivity.this);
                Configurations.setReloadMainActivityOnResume(true);
                LocationsActivity.reloadLocationsActivityOnResume = true;
                AlarmsScheduler.fire(UpdateCurrentLocationActivity.this,Calendar.getInstance());
                UpdateCurrentLocationActivity.this.finish();
            }
        });
        findViewById(R.id.update_current_location_activity_location_edit_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UpdateCurrentLocationActivity.this, AdjustLocationActivity.class));
            }
        });
    }

    private boolean checkNetworkAvailability() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    private boolean checkGpsEnabled() {
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private boolean checkGpsAvailability() {
        final LocationManager mgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if ( mgr == null ) return false;
        final List<String> providers = mgr.getAllProviders();
        if ( providers == null ) return false;
        return providers.contains(LocationManager.GPS_PROVIDER);
    }

    private void setTitleLocation() {
        float lo = getSharedPreferences(LocationsActivity.lastLocation,MODE_PRIVATE).getFloat("longitude",-99999);
        float la = getSharedPreferences(LocationsActivity.lastLocation,MODE_PRIVATE).getFloat("latitude",-99999);
        ((TextView) findViewById(R.id.update_current_location_activity_location_title)).setText(lo!=-99999?(la + " , " + lo):"");
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void getLocationFromNetwork(){

       final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog alertDialog = builder.setMessage("Updating location ...").create();
        alertDialog.setCancelable(false);
        alertDialog.show();
        final LocationHandler locationHandler = new LocationHandler(this, this.getApplicationContext(), tempLocationFile,2000);
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            int i=0;
            @Override
            public void run() {
                if(!locationHandler.NEW_LOCATION_FLAG && i<10) {
                    handler.postDelayed(this, 1000);
                    i++;
                    if(!alertDialog.isShowing())alertDialog.show();
                }else if(!locationHandler.NEW_LOCATION_FLAG){
                    Toast.makeText(UpdateCurrentLocationActivity.this,"Location update failed!",Toast.LENGTH_SHORT).show();
                    locationHandler.finish();
                    if(alertDialog.isShowing())alertDialog.cancel();
                }else {
                    LocationsActivity.reloadLocationsActivityOnResume = true;
                    Toast.makeText(UpdateCurrentLocationActivity.this,"Location Updated successfully!",Toast.LENGTH_SHORT).show();
                    if(alertDialog.isShowing())alertDialog.cancel();
                    LocationsActivity.assignLocation(UpdateCurrentLocationActivity.this,tempLocationFile,LocationsActivity.lastLocation);
                    setTitleLocation();
                }
            }}, 1000);
    }


    @Override
    public void finish() {
        Configurations.clearFile(this,tempLocationFile);
        super.finish();
    }

    @Override
    protected void onStop() {
        Configurations.clearFile(this,tempLocationFile);
        super.onStop();
    }

    @Override
    protected void onResume() {
        if(getSharedPreferences("temp.txt",MODE_PRIVATE).getBoolean("islocationassigned",false)){
            LocationsActivity.assignLocation(UpdateCurrentLocationActivity.this,"temp.txt",tempLocationFile);
            Configurations.clearFile(UpdateCurrentLocationActivity.this,"temp.txt");
            Toast.makeText(UpdateCurrentLocationActivity.this,"Location Updated successfully!",Toast.LENGTH_SHORT).show();
        }
        super.onResume();
    }
}
