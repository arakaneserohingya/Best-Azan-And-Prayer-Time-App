package com.alnamaa.engineering.azan.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alnamaa.engineering.azan.Alarms.AlarmsScheduler;
import com.alnamaa.engineering.azan.Locations._LocationSET;
import com.alnamaa.engineering.azan.Times._TimesSET;
import com.example.engineering.azanapkmostafa.R;

import java.util.Calendar;

public class UpdateCurrentLocationActivity extends AppCompatActivity {

    private Handler handler;
    private final String tempLocationFile = "updatecurrenttemplocationfile";

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
        _LocationSET.getRequests(this);
        findViewById(R.id.update_current_location_activity_internet_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UpdateCurrentLocationActivity.this, InternetLocationSearchActivity.class);
                intent.putExtra("filename", "temp");
                startActivity(intent);
            }
        });
        findViewById(R.id.update_current_location_activity_manually_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UpdateCurrentLocationActivity.this, ManuallyLocationActivity.class);
                intent.putExtra("filename", "temp");
                startActivity(intent);

            }
        });
        findViewById(R.id.update_current_location_activity_gps_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!_LocationSET.checkGpsAvailability(UpdateCurrentLocationActivity.this))
                    Toast.makeText(UpdateCurrentLocationActivity.this, getResources().getString(R.string.this_device_has_no_gps_equipment), Toast.LENGTH_SHORT).show();
                else if (!_LocationSET.checkGpsEnabled(UpdateCurrentLocationActivity.this))
                    Toast.makeText(UpdateCurrentLocationActivity.this, getResources().getString(R.string.please_enable_gps_first), Toast.LENGTH_SHORT).show();
                else _LocationSET.getLocation(UpdateCurrentLocationActivity.this, tempLocationFile, new _LocationSET.locationUpdateResult() {
                        @Override
                        public void onSuccess() {
                            setTitleLocation();
                        }

                        @Override
                        public void onFail() {

                        }
                    });
            }
        });
        findViewById(R.id.update_current_location_activity_last_location_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                _LocationSET.getLastLocation(UpdateCurrentLocationActivity.this, tempLocationFile, new _LocationSET.locationUpdateResult() {
                    @Override
                    public void onSuccess() {
                        setTitleLocation();
                        LocationsActivity.reloadLocationsActivityOnResume = true;
                    }

                    @Override
                    public void onFail() {
                        Toast.makeText(UpdateCurrentLocationActivity.this,getResources().getString(R.string.update_current_activity_last_known_location_not_found),Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        findViewById(R.id.update_current_location_activity_network_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!_LocationSET.checkNetworkAvailability(UpdateCurrentLocationActivity.this))
                    Toast.makeText(UpdateCurrentLocationActivity.this, getResources().getString(R.string.please_check_for_internet_connection), Toast.LENGTH_SHORT).show();
                else _LocationSET.getLocation(UpdateCurrentLocationActivity.this, tempLocationFile, new _LocationSET.locationUpdateResult() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(UpdateCurrentLocationActivity.this,getResources().getString(R.string.toast_location_updated_successfully),Toast.LENGTH_SHORT).show();
                        setTitleLocation();
                    }

                    @Override
                    public void onFail() {
                        Toast.makeText(UpdateCurrentLocationActivity.this,getResources().getString(R.string.toast_failed_to_update_location),Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        findViewById(R.id.update_current_location_activity_save_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!_LocationSET.assignLocation(UpdateCurrentLocationActivity.this, tempLocationFile, _LocationSET.currentLocation)) {
                    UpdateCurrentLocationActivity.this.finish();
                    return;
                }
                _LocationSET.setLocationAssigned(UpdateCurrentLocationActivity.this);
                _TimesSET.updateTimes(UpdateCurrentLocationActivity.this);
                MainActivity.reloadMainActivityOnResume = true;
                LocationsActivity.reloadLocationsActivityOnResume = true;
                AlarmsScheduler.fire(UpdateCurrentLocationActivity.this, Calendar.getInstance());
                UpdateCurrentLocationActivity.this.finish();
            }
        });
        findViewById(R.id.update_current_location_activity_location_edit_button_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UpdateCurrentLocationActivity.this, AdjustLocationActivity.class));
            }
        });
    }

    private void setTitleLocation() {
        float lo = getSharedPreferences(tempLocationFile, MODE_PRIVATE).getFloat("longitude", -99999);
        float la = getSharedPreferences(tempLocationFile, MODE_PRIVATE).getFloat("latitude", -99999);
        ((TextView) findViewById(R.id.update_current_location_activity_location_title)).setText(lo != -99999 ? (la + " , " + lo) : "");
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void finish() {
        _LocationSET.clearTempLocationFile(this,tempLocationFile);
        super.finish();
    }

    @Override
    protected void onStop() {
        _LocationSET.clearTempLocationFile(this,tempLocationFile);
        super.onStop();
    }

    @Override
    protected void onResume() {
        if(getSharedPreferences("temp",MODE_PRIVATE).getBoolean("islocationassigned",false)){
            _LocationSET.assignLocation(UpdateCurrentLocationActivity.this,"temp",tempLocationFile);
            _LocationSET.clearTempLocationFile(UpdateCurrentLocationActivity.this,"temp");
            Toast.makeText(UpdateCurrentLocationActivity.this,"Location Updated successfully!",Toast.LENGTH_SHORT).show();
        }
        super.onResume();
    }
}
