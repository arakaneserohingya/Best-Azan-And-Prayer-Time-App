package com.alhadara.omar.azan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Path;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Size;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.omar.azanapkmostafa.R;
import com.opencsv.CSVReader;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ManuallyLocationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manually_location);
        addCountriesList("");
        final EditText search = findViewById(R.id.manually_location_activity_search);
        search.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                addCountriesList(search.getText().toString());
            }
            @Override public void afterTextChanged(Editable editable) {}
        });
    }

    private void addCountriesList(String countriesContainString) {
        LinearLayout layout = findViewById(R.id.manually_location_activity_countries_layout);
        layout.removeAllViews();
        countriesContainString=countriesContainString.toLowerCase();
        LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                1);
        TextView tx;
        View divider;
        final Intent intent = new Intent(this,ManuallyLocationCitiesActivity.class);
        try {
            CSVReader reader = new CSVReader(new InputStreamReader(getApplicationContext().getAssets().open("location_table_countries.csv")));
            String[] nextLine;
            nextLine = new String[2];
            while ((nextLine = reader.readNext()) != null) {
                // nextLine[] is an array of values from the line
                if(!nextLine[1].toLowerCase().contains(countriesContainString)) continue;
                tx = new TextView(this);
                tx.setPadding(15,15,15,15);
                tx.setTextColor(Color.WHITE);
                tx.setGravity(Gravity.LEFT);
                //tx.setLayoutParams(layoutParams);
                tx.setText(nextLine[1]);
                tx.setTextSize(15);
                layout.addView(tx);
                divider = new View(this);
                divider.setLayoutParams(dividerParams);
                divider.setBackgroundColor(Color.DKGRAY);
                layout.addView(divider);
                final int countryIndex = Integer.parseInt(nextLine[0]);
                final String countryName = nextLine[1];
                tx.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        intent.putExtra("COUNTRY_INDEX",countryIndex);
                        intent.putExtra("COUNTRY_NAME",countryName);
                        startActivity(intent);
                    }
                });
            }
        } catch (IOException e) {

        }
    }
}
