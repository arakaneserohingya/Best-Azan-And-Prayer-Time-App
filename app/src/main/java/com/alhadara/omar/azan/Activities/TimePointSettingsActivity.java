package com.alhadara.omar.azan.Activities;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import com.alhadara.omar.azan.Configurations;
import com.alhadara.omar.azan.Constants;
import com.alhadara.omar.azan.TM;
import com.alhadara.omar.azan.Times;
import com.example.omar.azanapkmostafa.R;

public class TimePointSettingsActivity extends AppCompatActivity {

    private int index;
    private Handler handler;
    private Runnable runnable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_point_settings);
        Toolbar toolbar = findViewById(R.id.toolbar_time_point_settings);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        index=getIntent().getExtras().getInt("TimePointIndex");
        getSupportActionBar().setTitle( Constants.alias[index] +" "+ getResources().getString(R.string.prayer_time_settings));


        widgetsAdder();
        startTimer();
    }

    public void widgetsAdder(){
        /* Azan widget configuration*/
        final LinearLayout azanBox = findViewById(R.id.time_point_settings_azan_switch_box);

        ((TextView) azanBox.findViewById(R.id.switch_box_text)).setText(getResources().getString(R.string.activate_azan_notifications));
        ((TextView) azanBox.findViewById(R.id.configuration_text)).setText(getResources().getString(R.string.choose_alarm_tone));
        ((SwitchCompat) azanBox.findViewById(R.id.switch_box_trigger)).setChecked(Configurations.isAlarmActivated(this,"azan",index));
        ((LinearLayout) azanBox.findViewById(R.id.switch_box_configuration_box)).setVisibility(
                ((SwitchCompat) azanBox.findViewById(R.id.switch_box_trigger)).isChecked()? View.VISIBLE:View.GONE);
        ((SwitchCompat) azanBox.findViewById(R.id.switch_box_trigger)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                Configurations.setAlarmActivated(getApplicationContext(),"azan",index,((SwitchCompat) azanBox.findViewById(R.id.switch_box_trigger)).isChecked());
                ((LinearLayout) azanBox.findViewById(R.id.switch_box_configuration_box)).setVisibility(
                        ((SwitchCompat) azanBox.findViewById(R.id.switch_box_trigger)).isChecked()? View.VISIBLE:View.GONE);
            }
        });
        ((ImageButton) azanBox.findViewById(R.id.configuration_button)).setImageResource(R.drawable.ic_library_music_black_24dp);

        /* Iqama widget configuration*/
        final LinearLayout iqamaBox = findViewById(R.id.time_point_settings_iqama_switch_box);


        ((TextView) iqamaBox.findViewById(R.id.switch_box_text)).setText(getResources().getString(R.string.activate_iqama_notifications));
        ((TextView) iqamaBox.findViewById(R.id.configuration_text)).setText(getResources().getString(R.string.choose_alarm_tone));
        ((SwitchCompat) iqamaBox.findViewById(R.id.switch_box_trigger)).setChecked(Configurations.isAlarmActivated(this,"iqama",index));
        ((LinearLayout) iqamaBox.findViewById(R.id.switch_box_configuration_box)).setVisibility(
                ((SwitchCompat) iqamaBox.findViewById(R.id.switch_box_trigger)).isChecked()? View.VISIBLE:View.GONE);
        ((SwitchCompat) iqamaBox.findViewById(R.id.switch_box_trigger)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                Configurations.setAlarmActivated(getApplicationContext(),"iqama",index,((SwitchCompat) iqamaBox.findViewById(R.id.switch_box_trigger)).isChecked());
                ((LinearLayout) iqamaBox.findViewById(R.id.switch_box_configuration_box)).setVisibility(
                        ((SwitchCompat) iqamaBox.findViewById(R.id.switch_box_trigger)).isChecked()? View.VISIBLE:View.GONE);
            }
        });
        ((ImageButton) iqamaBox.findViewById(R.id.configuration_button)).setImageResource(R.drawable.ic_library_music_black_24dp);


        /* Adjust times widget*/
        final LinearLayout adjustTimesBox = findViewById(R.id.time_point_settings_adjust_times);
        ((ImageButton) adjustTimesBox.findViewById(R.id.configuration_button)).setImageResource(R.drawable.ic_edit_black_24dp);
        ((TextView) adjustTimesBox.findViewById(R.id.configuration_text)).setText(getResources().getString(R.string.adjust_time_manually));
        adjustTimesBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TimePointSettingsActivity.this,AdjustTimesActivity.class));
            }
        });

        /* Reset times*/
        final LinearLayout resetTimesBox = findViewById(R.id.time_point_settings_reset_times);
        ((ImageButton) resetTimesBox.findViewById(R.id.configuration_button)).setImageResource(R.drawable.ic_restore_black_24dp);
        ((TextView) resetTimesBox.findViewById(R.id.configuration_text)).setText(getResources().getString(R.string.reset_time_settings));
        resetTimesBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Configurations.resetTimeConfigurations(getApplicationContext());
            }
        });

    }



    public void startTimer(){
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                int h,m,s;
                int remainTime = TM.difference(TM.getTime(),Times.times[index]);
                if(remainTime < 1)
                    findViewById(R.id.countdown_layout_time_point_settings).setVisibility(View.GONE);
                else {
                    h = (int)remainTime /3600;
                    m = (int)((remainTime-(h*3600))/60);
                    s = (int)remainTime - (h*3600) - (m*60);
                    ViewGroup countdown = findViewById(R.id.countdown_timer_time_point_settings);
                    ((TextView)(countdown.getChildAt(0))).setText(Integer.toString(h));
                    ((TextView)(countdown.getChildAt(2))).setText(Integer.toString(m));
                    ((TextView)(countdown.getChildAt(4))).setText(Integer.toString(s));
                    handler.postDelayed(this,1000);
                }
            }
        };
        runnable.run();
    }



    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacks(runnable);
    }
}
