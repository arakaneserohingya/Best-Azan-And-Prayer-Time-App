package com.alnamaa.engineering.azan.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.alnamaa.engineering.azan.Alarms.AlarmsScheduler;
import com.alnamaa.engineering.azan.Display._DisplaySET;
import com.alnamaa.engineering.azan.Locations._LocationSET;
import com.alnamaa.engineering.azan.R;
import com.alnamaa.engineering.azan.Times._TimesSET;

import java.util.Calendar;

public class AdjustLocationActivity extends AppCompatActivity {

    private String locationFile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adjust_location);
        Toolbar toolbar = findViewById(R.id.toolbar_adjust_location_activity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("+ " + getResources().getString(R.string.add_location));
        ((ImageView) findViewById(R.id.adjust_location_activity_image)).setImageDrawable(getResources().getDrawable(
                _DisplaySET.getAppTheme(this) == _DisplaySET.WHITE ?R.drawable.background:R.drawable.background_black));

        locationFile = getIntent().getExtras().getString("location_file");
        initializeEditBoxes();
        findViewById(R.id.adjust_location_activity_save_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences pref = AdjustLocationActivity.this.getSharedPreferences(locationFile,MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("location_name",((EditText)findViewById(R.id.adjust_location_activity_current_location_edittext)).getText().toString());
                editor.putFloat("latitude",Float.parseFloat(((EditText)findViewById(R.id.adjust_location_activity_latitude_edittext)).getText().toString()));
                editor.putFloat("longitude",Float.parseFloat(((EditText)findViewById(R.id.adjust_location_activity_longitude_edittext)).getText().toString()));
                editor.putFloat("timezone",Float.parseFloat(((EditText)findViewById(R.id.adjust_location_activity_time_location_edittext)).getText().toString()));
                editor.apply();
                if(locationFile.equals(_LocationSET.currentLocation)) {
                    _TimesSET.updateTimes(getApplicationContext());
                    AlarmsScheduler.fire(getApplicationContext(), Calendar.getInstance());
                    LocationsActivity.reloadLocationsActivityOnResume = true;

                }
                AdjustLocationActivity.this.finish();
            }
        });
    }

    private void initializeEditBoxes() {
        ((EditText)findViewById(R.id.adjust_location_activity_current_location_edittext)).setText(
                getSharedPreferences(locationFile,MODE_PRIVATE).getString("location_name","")
        );
        ((EditText)findViewById(R.id.adjust_location_activity_time_location_edittext)).setText(
                Float.toString(getSharedPreferences(locationFile,MODE_PRIVATE).getFloat("timezone",0))
        );
        ((EditText)findViewById(R.id.adjust_location_activity_longitude_edittext)).setText(
                Float.toString(getSharedPreferences(locationFile,MODE_PRIVATE).getFloat("longitude",0))
        );
        ((EditText)findViewById(R.id.adjust_location_activity_latitude_edittext)).setText(
                Float.toString(getSharedPreferences(locationFile,MODE_PRIVATE).getFloat("latitude",0))
        );
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
