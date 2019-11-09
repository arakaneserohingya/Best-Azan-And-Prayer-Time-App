package com.alnamaa.engineering.azan.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.ViewGroup;
import com.alnamaa.engineering.azan.Settings._SET;
import com.alnamaa.engineering.azan.Alarms._AlarmSET;
import com.alnamaa.engineering.azan.Settings.SettingsRecyclerViewAdapter;
import com.example.engineering.azanapkmostafa.R;

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
        RecyclerView recyclerView = findViewById(R.id.settings_third_activity_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemViewCacheSize(0);
        recyclerView.setAdapter(new SettingsRecyclerViewAdapter(this,getIntent().getExtras().getInt("index")));
        getSupportActionBar().setTitle(getIntent().getExtras().getString("title"));
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
