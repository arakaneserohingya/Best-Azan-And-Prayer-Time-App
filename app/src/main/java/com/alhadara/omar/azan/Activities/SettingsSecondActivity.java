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

import com.alhadara.omar.azan.Activities.SettingsLayouts.BackupRestoreLayout;
import com.alhadara.omar.azan.Activities.SettingsLayouts.DisplayOptions;
import com.alhadara.omar.azan.Activities.SettingsLayouts.FajrSahoorAlarmLayout;
import com.alhadara.omar.azan.Activities.SettingsLayouts.HijriLayout;
import com.alhadara.omar.azan.Activities.SettingsLayouts.NotificationsLayout;
import com.alhadara.omar.azan.Activities.SettingsLayouts.PrayerTimesLayout;
import com.alhadara.omar.azan.Activities.SettingsLayouts.SilentLayout;
import com.alhadara.omar.azan.Alarms._AlarmSET;
import com.example.omar.azanapkmostafa.R;

public class SettingsSecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_second);
        Toolbar toolbar = findViewById(R.id.toolbar_settings_second_activity);
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
    private void setLayout(){
        ViewGroup scroll = findViewById(R.id.settings_second_activity_scrollview);
        if(getIntent().getExtras().getInt("index") == 0){
            getSupportActionBar().setTitle(getResources().getString(R.string.settings_prayer_times_title));
            PrayerTimesLayout layout = new PrayerTimesLayout(this);
            scroll.addView(layout);
        }else if(getIntent().getExtras().getInt("index") == 1){
            getSupportActionBar().setTitle(getResources().getString(R.string.settings_hijri_title));
            HijriLayout layout = new HijriLayout(this);
            scroll.addView(layout);
        }else if(getIntent().getExtras().getInt("index") == 2){
            getSupportActionBar().setTitle(getResources().getString(R.string.settings_display_options_title));
            DisplayOptions layout = new DisplayOptions(this);
            scroll.addView(layout);
        }else if(getIntent().getExtras().getInt("index") == 3){
            getSupportActionBar().setTitle(getResources().getString(R.string.settings_notifications_title));
            NotificationsLayout layout = new NotificationsLayout(this);
            scroll.addView(layout);
        }else if(getIntent().getExtras().getInt("index") == 4){
            getSupportActionBar().setTitle(getResources().getString(R.string.settings_fajr_and_sahoor_alarms_title));
            FajrSahoorAlarmLayout layout = new FajrSahoorAlarmLayout(this);
            scroll.addView(layout);
        }else if(getIntent().getExtras().getInt("index") == 5){
            getSupportActionBar().setTitle(getResources().getString(R.string.settings_silent_title));
            SilentLayout layout = new SilentLayout(this);
            scroll.addView(layout);
        }else {
            getSupportActionBar().setTitle(getResources().getString(R.string.settings_backup_and_restore_title));
            BackupRestoreLayout layout = new BackupRestoreLayout(this);
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
                case 50400:
                    pref = getSharedPreferences(_AlarmSET.sahoorFile,MODE_PRIVATE);
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
                default:
                    break;
            }
        }
    }
}
