package com.alhadara.omar.azan.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alhadara.omar.azan.Activities.SettingsLayouts.IqamaReminderTimesLayout;
import com.alhadara.omar.azan.Activities.SettingsLayouts.IqamaReminderToneLayout;
import com.alhadara.omar.azan.Activities.SettingsLayouts.ManualTimesAdjustmentsLayout;
import com.alhadara.omar.azan.Activities.SettingsLayouts.PrayerNotificationTimesLayout;
import com.alhadara.omar.azan.Activities.SettingsLayouts.PrayerNotificationToneLayout;
import com.alhadara.omar.azan.Activities.SettingsLayouts._SET;
import com.alhadara.omar.azan.Alarms.AlarmsScheduler;
import com.alhadara.omar.azan.Alarms._AlarmSET;
import com.example.omar.azanapkmostafa.R;

import java.io.File;
import java.net.URLDecoder;

public class SettingsThirdActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_third);
        Toolbar toolbar = findViewById(R.id.toolbar_settings_third_activity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setLayout();
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    private void setLayout() {
        ViewGroup scroll = findViewById(R.id.settings_third_activity_scrollview);
        int i = getIntent().getExtras().getInt("layout");
        int j = getIntent().getExtras().getInt("key");
        if(i==0&&j==6){
            getSupportActionBar().setTitle(getResources().getString(R.string.settings_manual_times_adjustments_title));
            ManualTimesAdjustmentsLayout layout = new ManualTimesAdjustmentsLayout(this);
            scroll.addView(layout);
        }else if(i==3&&j==0){
            getSupportActionBar().setTitle(getResources().getString(R.string.settings_prayer_notification_time_title));
            PrayerNotificationTimesLayout layout = new PrayerNotificationTimesLayout(this);
            scroll.addView(layout);
        }else if(i==3&&j==3){
            getSupportActionBar().setTitle(getResources().getString(R.string.settings_prayer_notification_tone_title));
            PrayerNotificationToneLayout layout = new PrayerNotificationToneLayout(this);
            scroll.addView(layout);
        }else if(i==3&&j==6){
            getSupportActionBar().setTitle(getResources().getString(R.string.settings_iqama_reminder_time_title));
            IqamaReminderTimesLayout layout = new IqamaReminderTimesLayout(this);
            scroll.addView(layout);
        }else if(i==3&&j==9){
            getSupportActionBar().setTitle(getResources().getString(R.string.settings_iqama_reminder_tone_title));
            IqamaReminderToneLayout layout = new IqamaReminderToneLayout(this);
            scroll.addView(layout);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        SharedPreferences pref = null;
        SharedPreferences.Editor editor = null;
        Uri uri = null;
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 40401: case 41001:
                    pref = getSharedPreferences(requestCode==40401?_AlarmSET.azanFile:_AlarmSET.iqamaFile,MODE_PRIVATE);
                    editor = pref.edit();
                    uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                    if(uri!=null) {
                        editor.putString("uri", uri.toString());
                        editor.commit();
                    } else {
                        editor.remove("uri");
                        editor.commit();
                    }
                    break;
                case 40403: case 41003:
                    pref = getSharedPreferences(requestCode==40403? _AlarmSET.azanFile:_AlarmSET.iqamaFile,MODE_PRIVATE);
                    editor = pref.edit();
                    uri = data.getData();
                    if(uri!=null) {
                        editor.putString("uri", uri.toString());
                        editor.commit();
                        String name= Uri.decode(uri.toString());
                        _SET.setDescription((ViewGroup) findViewById(requestCode)," "+ name.substring(name.lastIndexOf('/')+1));
                    } else {
                        editor.remove("uri");
                        editor.commit();
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
