package com.alhadara.omar.azan.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.ViewGroup;

import com.alhadara.omar.azan.Activities.SettingsLayouts.ManualTimesAdjustmentsLayout;
import com.alhadara.omar.azan.Activities.SettingsLayouts.PrayerNotificationTimesLayout;
import com.alhadara.omar.azan.Activities.SettingsLayouts.PrayerNotificationToneLayout;
import com.example.omar.azanapkmostafa.R;

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
        }
    }
}
