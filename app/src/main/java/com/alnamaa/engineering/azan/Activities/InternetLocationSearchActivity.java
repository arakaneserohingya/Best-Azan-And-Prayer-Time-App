package com.alnamaa.engineering.azan.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.alnamaa.engineering.azan.R;


public class InternetLocationSearchActivity extends AppCompatActivity {

    private String tempLocationFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internet_location_search);
        tempLocationFile = getIntent().getExtras().getString("filename");
        findViewById(R.id.internet_location_search_activity_manually_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InternetLocationSearchActivity.this,ManuallyLocationActivity.class);
                intent.putExtra("filename",tempLocationFile);
                startActivity(intent);
            }
        });

        ((EditText)findViewById(R.id.internet_location_search_activity_internet_search)).setImeActionLabel("بحث", KeyEvent.KEYCODE_ENTER);
        ((EditText)findViewById(R.id.internet_location_search_activity_internet_search)).setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) &&
                        (i == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    Toast.makeText(InternetLocationSearchActivity.this,"Can't perform operations in this commit!", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        });
        findViewById(R.id.internet_location_search_activity_current_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        if(getIntent().getExtras().getBoolean("new")) findViewById(R.id.internet_location_search_activity_current_button).setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        SharedPreferences preferences = getSharedPreferences(tempLocationFile,MODE_PRIVATE);
        if(preferences.getBoolean("islocationassigned",false)) finish();
        super.onResume();
    }
}
