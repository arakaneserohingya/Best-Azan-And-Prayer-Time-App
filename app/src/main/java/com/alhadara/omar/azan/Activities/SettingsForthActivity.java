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

import com.alhadara.omar.azan.Activities.SettingsLayouts.PrayerNotificationToneEachLayout;
import com.alhadara.omar.azan.Activities.SettingsLayouts._SET;
import com.alhadara.omar.azan.Alarms.AlarmsScheduler;
import com.example.omar.azanapkmostafa.R;

public class SettingsForthActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_forth);
        Toolbar toolbar = findViewById(R.id.toolbar_settings_forth_activity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setLayout();
    }

    private void setLayout() {
        ViewGroup scroll = findViewById(R.id.settings_forth_activity_scrollview);
        PrayerNotificationToneEachLayout layout = new PrayerNotificationToneEachLayout(this);
        scroll.addView(layout);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        SharedPreferences pref = null;
        SharedPreferences.Editor editor = null;
        Uri uri = null;
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 40415: case 40416: case 40417: case 40418: case 40419:
                    pref = getSharedPreferences(AlarmsScheduler.azanFile,MODE_PRIVATE);
                    editor = pref.edit();
                    uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                    if(uri!=null) {
                        editor.putString("uri"+((requestCode%10)-5), uri.toString());
                        editor.commit();
                    } else {
                        Toast.makeText(this,getResources().getString(R.string.no_tone_set),Toast.LENGTH_SHORT).show();
                        editor.remove("uri"+((requestCode%10)-5));
                        editor.commit();
                    }
                    break;
                case 40435: case 40436: case 40437: case 40438: case 40439:
                    pref = getSharedPreferences(AlarmsScheduler.azanFile,MODE_PRIVATE);
                    editor = pref.edit();
                    uri = data.getData();
                    if(uri!=null) {
                        editor.putString("uri"+((requestCode%10)-5), uri.toString());
                        editor.commit();
                        String name= Uri.decode(uri.toString());
                        _SET.setDescription((ViewGroup) findViewById(requestCode)," "+ name.substring(name.lastIndexOf('/')+1));
                    } else {
                        Toast.makeText(this,getResources().getString(R.string.no_tone_set),Toast.LENGTH_SHORT).show();
                        editor.remove("uri"+((requestCode%10)-5));
                        editor.commit();
                    }
                    break;
                default:
                    break;
            }
        }
    }

}
