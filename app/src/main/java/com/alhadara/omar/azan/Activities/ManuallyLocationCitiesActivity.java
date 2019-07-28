package com.alhadara.omar.azan.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alhadara.omar.azan.CountriesRecyclerViewAdapter;
import com.example.omar.azanapkmostafa.R;
import com.opencsv.CSVReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ManuallyLocationCitiesActivity extends AppCompatActivity {

    private List<TextView> cities ;
    private List<TextView> visibleCities;
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manually_location_cities);

        readListFromCSV();

        recyclerView = findViewById(R.id.manually_location_cities_activity_countries_layout);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new CountriesRecyclerViewAdapter(visibleCities));

        final EditText search = findViewById(R.id.manually_location_cities_activity_search);
        search.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                updateRecycelerView(search.getText().toString());
            }
            @Override public void afterTextChanged(Editable editable) {}
        });
    }


    private void updateRecycelerView(String visibleCitiesString) {
        String s = visibleCitiesString.toLowerCase();
        visibleCities.clear();
        for(int i=0;i<cities.size();i++) if(cities.get(i).getText().toString().toLowerCase().contains(s))
            visibleCities.add(cities.get(i));
        recyclerView.getAdapter().notifyDataSetChanged();
    }
    private void readListFromCSV() {
        cities = new ArrayList<>();
        visibleCities = new ArrayList<>();
        TextView tx;
        String countryName = getIntent().getExtras().getString("COUNTRY_NAME");
        try {
            CSVReader reader = new CSVReader(new InputStreamReader(getApplicationContext().getAssets().open("location_table_"+countryName.charAt(0)+".csv")));
            String[] nextLine;
            nextLine = new String[5];
            reader.skip(getIntent().getExtras().getInt("COUNTRY_INDEX")-1);
            while ((nextLine = reader.readNext()) != null) {
                // nextLine[] is an array of values from the line
                if(!nextLine[0].equals(countryName)) break;
                tx = new TextView(this);
                tx.setText(nextLine[1]);
                cities.add(tx);
                visibleCities.add(tx);
            }
        } catch (IOException e) {

        }
    }
}
