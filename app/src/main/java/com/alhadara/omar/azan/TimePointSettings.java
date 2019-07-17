package com.alhadara.omar.azan;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;

import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import com.example.omar.azanapkmostafa.R;

import org.w3c.dom.Text;

public class TimePointSettings extends AppCompatActivity {

    private int index;
    private int upComingTimePoint;
    private int remainTime;
    private Handler handler;
    private int diffWithUpComingTimePoint;
    private MediaPlayer mediaPlayer;
    private int diff_h;
    private int diff_m;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_point_settings);
        Toolbar toolbar = findViewById(R.id.toolbar_time_point_settings);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        index=getIntent().getExtras().getInt("TimePointIndex");
        getSupportActionBar().setTitle("إعدادات صلاة "+ Constants.alias[index]);
        mediaPlayer = MediaPlayer.create(TimePointSettings.this,R.raw.azan_haram);

        widgetsAdder();
        handler = new Handler();
        startTimer();
    }

    public void widgetsAdder(){
        /* Azan widget configuration*/
        final LinearLayout azanBox = findViewById(R.id.time_point_settings_azan_switch_box);
        final SharedPreferences azanPref = this.getSharedPreferences("azan.txt",Context.MODE_PRIVATE);
        final SharedPreferences.Editor azanEditor = azanPref.edit();
        ((TextView) azanBox.findViewById(R.id.switch_box_text)).setText("التنبيه وقت الأذان");
        ((TextView) azanBox.findViewById(R.id.configuration_text)).setText("اختيار نغمة التنبيه");
        ((SwitchCompat) azanBox.findViewById(R.id.switch_box_trigger)).setChecked(azanPref.getBoolean(Integer.toString(index),true));
        ((LinearLayout) azanBox.findViewById(R.id.switch_box_configuration_box)).setVisibility(
                azanPref.getBoolean(Integer.toString(index),true)? View.VISIBLE:View.GONE);
        ((SwitchCompat) azanBox.findViewById(R.id.switch_box_trigger)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                azanEditor.putBoolean(Integer.toString(index),((SwitchCompat) azanBox.findViewById(R.id.switch_box_trigger)).isChecked());
                azanEditor.commit();
                ((LinearLayout) azanBox.findViewById(R.id.switch_box_configuration_box)).setVisibility(
                        ((SwitchCompat) azanBox.findViewById(R.id.switch_box_trigger)).isChecked()? View.VISIBLE:View.GONE);
            }
        });
        ((ImageButton) azanBox.findViewById(R.id.configuration_button)).setImageResource(R.drawable.ic_library_music_black_24dp);

        /* Iqama widget configuration*/
        final LinearLayout iqamaBox = findViewById(R.id.time_point_settings_iqama_switch_box);
        final SharedPreferences iqamaPref = this.getSharedPreferences("iqama.txt",Context.MODE_PRIVATE);
        final SharedPreferences.Editor iqamaEditor = iqamaPref.edit();
        ((TextView) iqamaBox.findViewById(R.id.switch_box_text)).setText("التنبيه وقت الإقامة");
        ((TextView) iqamaBox.findViewById(R.id.configuration_text)).setText("اختيار نغمة التنبيه");
        ((SwitchCompat) iqamaBox.findViewById(R.id.switch_box_trigger)).setChecked(iqamaPref.getBoolean(Integer.toString(index),true));
        ((LinearLayout) iqamaBox.findViewById(R.id.switch_box_configuration_box)).setVisibility(
                iqamaPref.getBoolean(Integer.toString(index),true)? View.VISIBLE:View.GONE);
        ((SwitchCompat) iqamaBox.findViewById(R.id.switch_box_trigger)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                iqamaEditor.putBoolean(Integer.toString(index),((SwitchCompat) iqamaBox.findViewById(R.id.switch_box_trigger)).isChecked());
                iqamaEditor.commit();
                ((LinearLayout) iqamaBox.findViewById(R.id.switch_box_configuration_box)).setVisibility(
                        ((SwitchCompat) iqamaBox.findViewById(R.id.switch_box_trigger)).isChecked()? View.VISIBLE:View.GONE);
            }
        });
        ((ImageButton) iqamaBox.findViewById(R.id.configuration_button)).setImageResource(R.drawable.ic_library_music_black_24dp);


        /* Adjust times widget*/
        final LinearLayout adjustTimesBox = findViewById(R.id.time_point_settings_adjust_times);
        ((ImageButton) adjustTimesBox.findViewById(R.id.configuration_button)).setImageResource(R.drawable.ic_edit_black_24dp);
        ((TextView) adjustTimesBox.findViewById(R.id.configuration_text)).setText("تعديل الأوقات يدوياً");
        adjustTimesBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TimePointSettings.this,AdjustTimesActivity.class));
            }
        });

        /* Reset times*/
        final LinearLayout resetTimesBox = findViewById(R.id.time_point_settings_reset_times);
        ((ImageButton) resetTimesBox.findViewById(R.id.configuration_button)).setImageResource(R.drawable.ic_restore_black_24dp);
        ((TextView) resetTimesBox.findViewById(R.id.configuration_text)).setText("استعادة الإعدادات العامة");
        resetTimesBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor delayTimesEdit = getSharedPreferences("delaytime.txt",MODE_PRIVATE).edit();
                for(int i=0;i<6;i++) {
                    delayTimesEdit.putInt(Integer.toString(i),0);
                    delayTimesEdit.commit();
                }
                SharedPreferences pref = getSharedPreferences("mainconfig.txt",MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putBoolean("recreate_mainactivity_onresume",true);
                editor.commit();
            }
        });

    }



    public void firstInitialization(){
        upComingTimePoint = TM.commingTimePointIndex(Times.times);
        LinearLayout countdownlayout = findViewById(R.id.countdown_layout_time_point_settings);
        diffWithUpComingTimePoint = TM.difference(Times.times[upComingTimePoint],Times.times[index]);
        diff_h = (int)diffWithUpComingTimePoint /3600;
        diff_m = (int)((diffWithUpComingTimePoint-(diff_h*3600))/60);
        if(index >= upComingTimePoint) {

            countdownlayout.setVisibility(View.VISIBLE);
            ((TextView) countdownlayout.getChildAt(0)).setText(Constants.alias[index]);
        } else countdownlayout.setVisibility(View.GONE);
    }



    public void startTimer(){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int h,m,s;
                remainTime = TM.difference(TM.getTime(),Times.times[upComingTimePoint]);
                if(remainTime < 1) {
                    if(alarmActive("azan.txt",upComingTimePoint)) mediaPlayer.start();
                    firstInitialization();
                }else {

                    h = (int)remainTime /3600;
                    m = (int)((remainTime-(h*3600))/60);
                    s = (int)remainTime - (h*3600) - (m*60);
                    LinearLayout countdown = findViewById(R.id.countdown_timer_time_point_settings);
                    ((TextView)(countdown.getChildAt(0))).setText(Integer.toString(h + diff_h));
                    ((TextView)(countdown.getChildAt(2))).setText(Integer.toString(m + diff_m));
                    ((TextView)(countdown.getChildAt(4))).setText(Integer.toString(s));
                }
                handler.postDelayed(this,1000);
            }
        };
        firstInitialization();
        runnable.run();
    }

    boolean alarmActive(String alarmType, int index) {
        SharedPreferences pref;
        pref = this.getSharedPreferences(alarmType, Context.MODE_PRIVATE);
        return pref.getBoolean("index",true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        if(mediaPlayer.isPlaying()){
            mediaPlayer.stop();
        }else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        mediaPlayer.stop();
        super.onDestroy();
    }
}
