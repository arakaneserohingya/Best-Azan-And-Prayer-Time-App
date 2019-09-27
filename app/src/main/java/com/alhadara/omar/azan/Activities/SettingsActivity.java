package com.alhadara.omar.azan.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.alhadara.omar.azan.Configurations;
import com.example.omar.azanapkmostafa.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.toolbar_settings_activity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.settings));
        ViewGroup group = findViewById(R.id.settings_activity_box_layout);
        for(int i=0;i<group.getChildCount();i++){
            ((ImageButton)((ViewGroup)group.getChildAt(i)).getChildAt(0)).setImageResource(getResources().obtainTypedArray(R.array.settings_image).getResourceId(i,-1));
            ((TextView)((ViewGroup)group.getChildAt(i)).getChildAt(1)).setText(getResources().getStringArray(R.array.settings_menu)[i]);
            ((TextView)((ViewGroup)group.getChildAt(i)).getChildAt(2)).setText(getResources().getStringArray(R.array.settings_submenu)[i]);

        }
        group.getChildAt(3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final SharedPreferences pref = getSharedPreferences(Configurations.mainConFile,MODE_PRIVATE);
                final SharedPreferences.Editor editor = pref.edit();
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                builder.setTitle(getResources().getString(R.string.choose_display_language));
                builder.setItems(new String[]{"English", "عربي"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(i==0){
                            editor.putString("language","en");
                            editor.commit();
                            Configurations.setLanguagePreferences(SettingsActivity.this);
                            Configurations.setReloadMainActivityOnResume(true);
                            SettingsActivity.this.recreate();
                        }else{
                            editor.putString("language","ar");
                            editor.commit();
                            Configurations.setLanguagePreferences(SettingsActivity.this);
                            Configurations.setReloadMainActivityOnResume(true);
                            SettingsActivity.this.recreate();
                        }
                    }
                });
                builder.show();
            }
        });

    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
