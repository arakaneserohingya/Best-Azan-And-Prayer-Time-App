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

import com.alhadara.omar.azan.Configurations;
import com.example.omar.azanapkmostafa.R;

public class SettingsActivity extends AppCompatActivity {

    public final static String settingsFile = "settings.txt";
    public static SharedPreferences settingsPref;
    public static SharedPreferences.Editor settingsEditor;
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
        settingsPref = getSharedPreferences(settingsFile,MODE_PRIVATE);
        settingsEditor = settingsPref.edit();
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
                    startActivity(intent);
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


    public static boolean isActivated(View view){
        return settingsPref.getBoolean("status" + view.getId(),true);
    }
    public static void setStatus(View view){
        view.setClickable(isActivated(view));
        view.setAlpha(isActivated(view)?1.0f:0.2f);
    }
    public static void setStatus(View view,boolean bool){
        settingsEditor.putBoolean("status"+view.getId(),bool);
        settingsEditor.commit();
        view.setClickable(bool);
        view.setAlpha(bool?1.0f:0.2f);
    }
    public static boolean isChecked(View view) {
        return settingsPref.getBoolean("checked"+view.getId(),false);
    }
    public static void setCheckBox(View view) {
        CheckBox box = view.findViewById(R.id.settings_check_box_checkbox);
        box.setChecked(isChecked(view));
    }
    public static void setCheckBox(View view,boolean bool) {
        CheckBox box = view.findViewById(R.id.settings_check_box_checkbox);
        box.setChecked(bool);
        settingsEditor.putBoolean("checked"+view.getId(),bool);
        settingsEditor.commit();
    }
    public static int generateViewID(int l1,int l2,int l3){
        return (l1*10000)+(l2*100)+(l3);
    }
}
