package com.alhadara.omar.azan.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.omar.azanapkmostafa.R;

public class UpdateCurrentLocationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_current_location);

        Toolbar toolbar = findViewById(R.id.toolbar_update_current_location_activity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.update_current_location));

        final Toast tempToast = Toast.makeText(this,"Can't perform operations in this commit!",Toast.LENGTH_SHORT);
        findViewById(R.id.update_current_location_activity_internet_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UpdateCurrentLocationActivity.this, InternetLocationSearchActivity.class);
                intent.putExtra("new",false);
                startActivity(intent);
            }
        });
        findViewById(R.id.update_current_location_activity_manually_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UpdateCurrentLocationActivity.this, ManuallyLocationActivity.class));
            }
        });
        findViewById(R.id.update_current_location_activity_gps_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tempToast.show();
            }
        });
        findViewById(R.id.update_current_location_activity_last_location_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tempToast.show();
            }
        });
        findViewById(R.id.update_current_location_activity_network_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tempToast.show();
            }
        });
        findViewById(R.id.update_current_location_activity_save_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tempToast.show();
            }
        });
        findViewById(R.id.update_current_location_activity_location_edit_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UpdateCurrentLocationActivity.this, AdjustLocationActivity.class));
            }
        });
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
