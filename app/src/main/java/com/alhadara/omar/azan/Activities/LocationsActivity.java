package com.alhadara.omar.azan.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alhadara.omar.azan.Configurations;
import com.example.omar.azanapkmostafa.R;

public class LocationsActivity extends AppCompatActivity {

    public static boolean reloadLocationsActivityOnResume = false;
    public final String locationsFile = "locations.txt";
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
                getSharedPreferences(Configurations.mainConFile,MODE_PRIVATE).getString("location_name","")
        );
        ((View) findViewById(R.id.locations_activity_add_location_floating_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getSharedPreferences(locationsFile,MODE_PRIVATE).getInt("locationsnumber",0) >= MAXIMUM_SAVED_LOCATIONS){
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
        if(Configurations.isLocationAssigned(LocationsActivity.this))
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
            addLocation("newlocation.txt");
            reloadLocationsActivityOnResume = true;
            Configurations.clearFile(this,"newlocation.txt");
        }
        if(reloadLocationsActivityOnResume){
            reloadLocationsActivityOnResume = false;
            recreate();
        }
        super.onResume();
    }

    private void addLocation(String s) {
        SharedPreferences locations = getSharedPreferences(locationsFile,MODE_PRIVATE);
        SharedPreferences.Editor mainEditor = locations.edit();
        int index = locations.getInt("locationsnumber",0);
        assignLocation(this,s,"location_" + Integer.toString(index+1) +".txt");
        mainEditor.putInt("locationsnumber",index+1);
        mainEditor.commit();
    }

    private void locationsWidgets(){
        SharedPreferences locations = getSharedPreferences(locationsFile,MODE_PRIVATE);
        SharedPreferences location_i;
        ViewGroup list = findViewById(R.id.locations_activity_locations_list_layout);
        for(int i=1; i<=locations.getInt("locationsnumber",0);i++){
            location_i = getSharedPreferences("location_" + i +".txt",MODE_PRIVATE);
            list.getChildAt(i-1).setVisibility(
                    location_i.getBoolean("islocationassigned",false)?View.VISIBLE:View.INVISIBLE
            );
            ((TextView)((ViewGroup)list.getChildAt(i-1)).getChildAt(0)).setText(
                    location_i.getString("location_name","")
            );
        }
    }

    public static boolean assignLocation(Context context,String from, String to){
        SharedPreferences From = context.getSharedPreferences(from,MODE_PRIVATE);
        SharedPreferences To = context.getSharedPreferences(to,MODE_PRIVATE);
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
}
