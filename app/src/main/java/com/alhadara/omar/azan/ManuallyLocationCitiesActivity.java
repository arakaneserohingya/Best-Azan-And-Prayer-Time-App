package com.alhadara.omar.azan;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.omar.azanapkmostafa.R;
import com.opencsv.CSVReader;

import java.io.IOException;
import java.io.InputStreamReader;

public class ManuallyLocationCitiesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manually_location_cities);
        addCitiesList("");
        final EditText search = findViewById(R.id.manually_location_cities_activity_search);
        search.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                addCitiesList(search.getText().toString());
            }
            @Override public void afterTextChanged(Editable editable) {}
        });
    }

    private void addCitiesList(String citiesContainString) {
        LinearLayout layout = findViewById(R.id.manually_location_cities_activity_countries_layout);
        layout.removeAllViews();
        LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                1);
        TextView tx;
        View divider;
        String countryName = getIntent().getExtras().getString("COUNTRY_NAME");

        try {
            CSVReader reader = new CSVReader(new InputStreamReader(getApplicationContext().getAssets().open("location_table_"+countryName.charAt(0)+".csv")));
            String[] nextLine;
            nextLine = new String[5];
            reader.skip(getIntent().getExtras().getInt("COUNTRY_INDEX")-1);
            while ((nextLine = reader.readNext()) != null) {
                // nextLine[] is an array of values from the line
                if(!nextLine[0].equals(countryName)) break;
                if(!nextLine[1].toLowerCase().contains(citiesContainString.toLowerCase())) continue;
                tx = new TextView(this);
                tx.setPadding(15,15,15,15);
                tx.setTextColor(Color.WHITE);
                tx.setGravity(Gravity.LEFT);
                //tx.setLayoutParams(layoutParams);
                tx.setText( nextLine[1]);
                tx.setTextSize(15);
                layout.addView(tx);
                divider = new View(this);
                divider.setLayoutParams(dividerParams);
                divider.setBackgroundColor(Color.DKGRAY);
                layout.addView(divider);
            }
        } catch (IOException e) {

        }
    }
}
