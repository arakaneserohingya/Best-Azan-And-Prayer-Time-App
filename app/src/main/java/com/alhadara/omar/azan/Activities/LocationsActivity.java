package com.alhadara.omar.azan.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alhadara.omar.azan.Configurations;
import com.alhadara.omar.azan.Constants;
import com.alhadara.omar.azan.Times;
import com.example.omar.azanapkmostafa.R;

import java.util.Calendar;

public class LocationsActivity extends AppCompatActivity {

    public static boolean reloadLocationsActivityOnResume = false;
    public final String locationsFile = "locations.txt";
    public static final String lastLocation = "lastlocation.txt";
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
        String id = getSharedPreferences(s,MODE_PRIVATE).getString("location_name","").replaceAll("[^a-zA-Z0-9\\.\\-]", "").toLowerCase();
        if(isAlreadyExistInLocationsFile(id)) {Toast.makeText(this,"Location already exist!",Toast.LENGTH_SHORT).show(); return;}
        // ID == Location file name should differs, in order to avoid file overwriting
        assignLocation(this,s,id+".txt");
        addToLocationsFile(id);
    }
    private void addToLocationsFile(String id) {
        SharedPreferences locations = getSharedPreferences(locationsFile,MODE_PRIVATE);
        SharedPreferences.Editor editor = locations.edit();
        int index = locations.getInt("locationsnumber",0) + 1;
        editor.putString("location" + index ,id);
        editor.putInt("locationsnumber",index);
        editor.commit();
    }
    private void removeFromLocationsFile(int i) {
        SharedPreferences locations = getSharedPreferences(locationsFile,MODE_PRIVATE);
        SharedPreferences.Editor editor = locations.edit();
        int locationsNumber = locations.getInt("locationsnumber",1);
        for(int j=i;j<6;j++){
            editor.putString("location"+j,locations.getString("location"+(j+1),""));
        }
        editor.putInt("locationsnumber",locationsNumber-1);
        editor.commit();
    }
    private boolean isAlreadyExistInLocationsFile(String ID) {
        SharedPreferences locations = getSharedPreferences(locationsFile,MODE_PRIVATE);
        for(int i=1;i<6;i++){
            if(locations.getString("location" + i , "").equals(ID)) return true;
        }
        return false;
    }

    private void locationsWidgets(){
        SharedPreferences locations = getSharedPreferences(locationsFile,MODE_PRIVATE);
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
                                Configurations.clearFile(LocationsActivity.this,locationID+".txt");
                                removeFromLocationsFile(k);
                                widget.setVisibility(View.GONE);
                            } else if(i==1){
                                assignLocation(LocationsActivity.this,locationID+".txt",Configurations.mainConFile);
                                Configurations.setReloadMainActivityOnResume(true);
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
