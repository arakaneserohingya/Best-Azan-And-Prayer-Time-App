package com.alhadara.omar.azan.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alhadara.omar.azan.Alarms.AlarmsScheduler;
import com.alhadara.omar.azan.Configurations;
import com.example.omar.azanapkmostafa.R;

import java.util.Calendar;

public class AdjustLocationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adjust_location);
        Toolbar toolbar = findViewById(R.id.toolbar_adjust_location_activity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("+ " + getResources().getString(R.string.add_location));
        findViewById(R.id.adjust_location_activity_save_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast tempToast = Toast.makeText(getApplicationContext(),"Can't perform operations in this commit!",Toast.LENGTH_SHORT);
                tempToast.show();
            }
        });
        initializeEditBoxes();
        findViewById(R.id.adjust_location_activity_save_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Configurations.setCurrentLocation( getApplicationContext(),
                        ((EditText)findViewById(R.id.adjust_location_activity_current_location_edittext)).getText().toString(),
                        Float.parseFloat(((EditText)findViewById(R.id.adjust_location_activity_latitude_edittext)).getText().toString()),
                        Float.parseFloat(((EditText)findViewById(R.id.adjust_location_activity_longitude_edittext)).getText().toString()),
                        Float.parseFloat(((EditText)findViewById(R.id.adjust_location_activity_time_location_edittext)).getText().toString())
                );
                Configurations.updateTimes(getApplicationContext());
                AlarmsScheduler.fire(getApplicationContext(), Calendar.getInstance());
                LocationsActivity.reloadLocationActivityOnResume = true;
                finish();
            }
        });
    }

    private void initializeEditBoxes() {
        ((EditText)findViewById(R.id.adjust_location_activity_current_location_edittext)).setText(
                getSharedPreferences(Configurations.mainConFile,MODE_PRIVATE).getString("location_name","")
        );
        ((EditText)findViewById(R.id.adjust_location_activity_time_location_edittext)).setText(
                Float.toString(getSharedPreferences(Configurations.mainConFile,MODE_PRIVATE).getFloat("timezone",0))
        );
        ((EditText)findViewById(R.id.adjust_location_activity_longitude_edittext)).setText(
                Float.toString(getSharedPreferences(Configurations.mainConFile,MODE_PRIVATE).getFloat("longitude",0))
        );
        ((EditText)findViewById(R.id.adjust_location_activity_latitude_edittext)).setText(
                Float.toString(getSharedPreferences(Configurations.mainConFile,MODE_PRIVATE).getFloat("latitude",0))
        );
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
