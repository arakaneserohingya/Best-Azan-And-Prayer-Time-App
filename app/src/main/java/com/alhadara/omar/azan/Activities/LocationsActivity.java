package com.alhadara.omar.azan.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alhadara.omar.azan.Locations._LocationSET;
import com.alhadara.omar.azan.Times;
import com.example.omar.azanapkmostafa.R;

import java.util.Calendar;

public class LocationsActivity extends AppCompatActivity {

    public static boolean reloadLocationsActivityOnResume = false;
    private final int MAXIMUM_SAVED_LOCATIONS = 5;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations);
        Toolbar toolbar = findViewById(R.id.toolbar_locations_activity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.saved_locations));
        locationsWidgets();
        ((TextView) findViewById(R.id.locations_activity_current_location_text)).setText(
                getSharedPreferences(_LocationSET.currentLocation,MODE_PRIVATE).getString("location_name","")
        );
        ((View) findViewById(R.id.locations_activity_add_location_floating_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getSharedPreferences(_LocationSET.locationsFile,MODE_PRIVATE).getInt("locationsnumber",0) >= MAXIMUM_SAVED_LOCATIONS){
                    Toast.makeText(LocationsActivity.this,"Maximum number of locations is five!",Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(LocationsActivity.this,InternetLocationSearchActivity.class);
                intent.putExtra("filename","newlocation.txt");
                startActivity(intent);
            }
        });
        findViewById(R.id.locations_activity_update_current_location).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LocationsActivity.this, UpdateCurrentLocationActivity.class));
            }
        });
        if(_LocationSET.isLocationAssigned(LocationsActivity.this))
            findViewById(R.id.locations_activity_adjust_current_location).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   startActivity(new Intent(LocationsActivity.this,AdjustLocationActivity.class));
                }
            });
        else ((TextView)findViewById(R.id.locations_activity_adjust_current_location)).setTextColor(Color.DKGRAY);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {

        SharedPreferences preferences = getSharedPreferences("newlocation.txt",MODE_PRIVATE);
        if(preferences.getBoolean("islocationassigned",false)) {
            _LocationSET.addLocationToSavedLocations(LocationsActivity.this,"newlocation.txt");
            reloadLocationsActivityOnResume = true;
            _LocationSET.clearTempLocationFile(this,"newlocation.txt");
        }
        if(reloadLocationsActivityOnResume){
            reloadLocationsActivityOnResume = false;
            recreate();
        }
        super.onResume();
    }

    private void locationsWidgets(){
        SharedPreferences locations = getSharedPreferences(_LocationSET.locationsFile,MODE_PRIVATE);
        SharedPreferences location_i;
        ViewGroup list = findViewById(R.id.locations_activity_locations_list_layout);
        for(int i=1; i<6;i++){
            final int k = i;
            final String locationID = locations.getString("location"+i,"");
            final ViewGroup widget = (ViewGroup)list.getChildAt(i-1);
            if(locationID.equals("")) break;
            location_i = getSharedPreferences(locationID + ".txt",MODE_PRIVATE);
            String[] prayertimes = Times.initializeTimesFor(LocationsActivity.this,locationID+".txt", Calendar.getInstance());
            widget.setVisibility(
                    location_i.getBoolean("islocationassigned",false)?View.VISIBLE:View.INVISIBLE
            );
            ((TextView)(widget).getChildAt(0)).setText(
                    location_i.getString("location_name","")
            );

            widget.getChildAt(1).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(LocationsActivity.this);
                    builder.setTitle(getResources().getString(R.string.choose_an_option));
                    builder.setItems(getResources().getStringArray(R.array.location_widget_options), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (i == 0) {
                                _LocationSET.clearTempLocationFile(LocationsActivity.this,locationID+".txt");
                                _LocationSET.removeFromLocationsFile(LocationsActivity.this,k);
                                widget.setVisibility(View.GONE);
                            } else if(i==1){
                                _LocationSET.assignLocation(LocationsActivity.this,locationID+".txt", _LocationSET.currentLocation);
                                MainActivity.reloadMainActivityOnResume = true;
                                ((TextView) findViewById(R.id.locations_activity_current_location_text)).setText(((TextView)widget.getChildAt(0)).getText());
                            }
                        }
                    });
                    builder.show();
                }
            });
            ((TextView)(((ViewGroup)list.getChildAt(i-1)).getChildAt(3))).setText(getResources().getStringArray(R.array.prayer_time)[0] + "\n" + prayertimes[0]);
            ((TextView)(((ViewGroup)list.getChildAt(i-1)).getChildAt(4))).setText(getResources().getStringArray(R.array.prayer_time)[1] + "\n" + prayertimes[1]);
            ((TextView)(((ViewGroup)list.getChildAt(i-1)).getChildAt(5))).setText(getResources().getStringArray(R.array.prayer_time)[2] + "\n" + prayertimes[2]);
            ((TextView)(((ViewGroup)list.getChildAt(i-1)).getChildAt(6))).setText(getResources().getStringArray(R.array.prayer_time)[3] + "\n" + prayertimes[3]);
            ((TextView)(((ViewGroup)list.getChildAt(i-1)).getChildAt(7))).setText(getResources().getStringArray(R.array.prayer_time)[4] + "\n" + prayertimes[4]);
            ((TextView)(((ViewGroup)list.getChildAt(i-1)).getChildAt(8))).setText(getResources().getStringArray(R.array.prayer_time)[5] + "\n" + prayertimes[5]);
        }
    }


}
