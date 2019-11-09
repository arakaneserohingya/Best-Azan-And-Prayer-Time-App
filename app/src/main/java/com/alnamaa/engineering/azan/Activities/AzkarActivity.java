package com.alnamaa.engineering.azan.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.LayoutDirection;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.alnamaa.engineering.azan.AzkarRecyclerViewAdapter;
import com.alnamaa.engineering.azan.R;


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AzkarActivity extends AppCompatActivity {

    private List<ArrayList<String>> main,sanad,rep,spel,trans;
    private List<String> listMain,listSanad,listRep,listSpel,listTrans;
    private RecyclerView recyclerView;
    private boolean[] transVisibility;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_azkar);
        initializeLists();
        initializeSpinner();
        recyclerView = findViewById(R.id.activity_azkar_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        transVisibility = new boolean[]{false};
        recyclerView.setAdapter(new AzkarRecyclerViewAdapter(listMain,listSanad,listRep,listSpel,listTrans,transVisibility));
        ((Switch) findViewById(R.id.activity_azkar_switch)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                transVisibility[0] = b;
                recyclerView.getAdapter().notifyDataSetChanged();
            }
        });
        findViewById(R.id.activity_azkar_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AzkarActivity.this.onBackPressed();
            }
        });
        if(getResources().getConfiguration().getLayoutDirection() == LayoutDirection.RTL)
            ((ImageView) findViewById(R.id.activity_azkar_back)).setImageDrawable(getResources().getDrawable(
                  R.drawable.ic_arrow_forward_black_24dp
            ));
    }

    private void initializeSpinner() {
        Spinner spinner = findViewById(R.id.activity_azkar_spinner);
        ArrayAdapter<String>adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,getResources().getStringArray(R.array.azkar_spinner_list));

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                updateList(i);
                recyclerView.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void updateList(int i) {
        listMain.clear();
        listSanad.clear();
        listRep.clear();
        listSpel.clear();
        listTrans.clear();
        switch (i){
            case 0:
                listMain.addAll(new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.azkar_1))));
                listSanad.addAll(new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.azkar_sanad_1))));
                listRep.addAll(new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.azkar_repetition_1))));
                listSpel.addAll(new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.azkar_spel_1))));
                listTrans.addAll(new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.azkar_translate_1))));
                break;
            case 1:
                listMain.addAll(new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.azkar_2))));
                listSanad.addAll(new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.azkar_sanad_2))));
                listRep.addAll(new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.azkar_repetition_2))));
                listSpel.addAll(new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.azkar_spel_2))));
                listTrans.addAll(new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.azkar_translate_2))));
                break;
            case 2:
                listMain.addAll(new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.azkar_3))));
                listSanad.addAll(new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.azkar_sanad_3))));
                listRep.addAll(new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.azkar_repetition_3))));
                listSpel.addAll(new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.azkar_spel_3))));
                listTrans.addAll(new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.azkar_translate_3))));
                break;
            case 3:
                listMain.addAll(new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.azkar_4))));
                listSanad.addAll(new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.azkar_sanad_4))));
                listRep.addAll(new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.azkar_repetition_4))));
                listSpel.addAll(new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.azkar_spel_4))));
                listTrans.addAll(new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.azkar_translate_4))));
                break;
            default:
        }
    }

    private void initializeLists() {
        listMain = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.azkar_1)));
        listSanad = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.azkar_sanad_1)));
        listRep = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.azkar_repetition_1)));
        listSpel = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.azkar_spel_1)));
        listTrans = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.azkar_translate_1)));
    }
}
