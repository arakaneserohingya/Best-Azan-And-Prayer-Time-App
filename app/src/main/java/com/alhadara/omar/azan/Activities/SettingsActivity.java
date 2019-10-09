package com.alhadara.omar.azan.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alhadara.omar.azan.Activities.SettingsLayouts._SET;
import com.alhadara.omar.azan.Configurations;
import com.example.omar.azanapkmostafa.R;

public class SettingsActivity extends AppCompatActivity {

    public static boolean reloadSettingsActivityOnResume = false;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.toolbar_settings_activity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.settings));
        _SET.startSettings(this);
        ViewGroup group = findViewById(R.id.settings_activity_layout);
        final Intent intent = new Intent(this,SettingsSecondActivity.class);
        for(int i=0;i<group.getChildCount();i++){
            final int k =i;
            ((ImageButton) group.getChildAt(i).findViewById(R.id.settings_box_image)).setImageResource(getResources().obtainTypedArray(R.array.settings_image).getResourceId(i,-1));
            ((TextView) group.getChildAt(i).findViewById(R.id.settings_box_header)).setText(getResources().getStringArray(R.array.settings_menu)[i]);
            ((TextView) group.getChildAt(i).findViewById(R.id.settings_box_details)).setText(getResources().getStringArray(R.array.settings_submenu)[i]);
            group.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    intent.putExtra("index",k);
                    if((k==0||k==3) && !Configurations.isLocationAssigned(SettingsActivity.this))
                        Toast.makeText(SettingsActivity.this,getResources().getString(R.string.please_set_location_first),Toast.LENGTH_SHORT).show();
                    else startActivity(intent);
                }
            });

        }

    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {
        if(reloadSettingsActivityOnResume){
            reloadSettingsActivityOnResume = false;
            recreate();
        }
        super.onResume();
    }

}
