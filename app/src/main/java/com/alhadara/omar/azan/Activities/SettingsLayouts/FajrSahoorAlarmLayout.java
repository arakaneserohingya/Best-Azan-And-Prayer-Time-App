package com.alhadara.omar.azan.Activities.SettingsLayouts;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.omar.azanapkmostafa.R;

public class FajrSahoorAlarmLayout extends LinearLayout {
    private Activity activity;
    private LinearLayout layout;
    public FajrSahoorAlarmLayout(Activity ac) {
        super(ac);
        activity = ac;
        layout = (LinearLayout) inflate(ac, R.layout.settings_layouts_fajr_and_sahoor_alarms,this);
        layout = (LinearLayout) layout.getChildAt(0);
        String[] headers = getResources().getStringArray(R.array.settings_layout_header_fajr_and_sahoor_alarms);
        String[] details = getResources().getStringArray(R.array.settings_layout_details_fajr_and_sahoor_alarms);
        for(int i=0,k=0;i<layout.getChildCount();i++){
            if(layout.getChildAt(i) instanceof TextView) continue;
            ViewGroup sublayout = (ViewGroup) layout.getChildAt(i);
            sublayout.setId(_SET.generateViewID(5,k+1,0));
            ((TextView)sublayout.getChildAt(0)).setText(
                    headers[k]
            );
            ((TextView)sublayout.getChildAt(1)).setText(
                    details[k]
            );
            clicker(sublayout,k);
            k++;

        }
    }
    private void clicker(View view, final int k) {
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(k==1){
                    CheckBox box = view.findViewById(R.id.settings_check_box_checkbox);
                    box.setChecked(!box.isChecked());
                }else if(k==4){
                    CheckBox box = view.findViewById(R.id.settings_check_box_checkbox);
                    box.setChecked(!box.isChecked());
                }else if(k==5){
                    CheckBox box = view.findViewById(R.id.settings_check_box_checkbox);
                    box.setChecked(!box.isChecked());
                } else if(k==6){
                    CheckBox box = view.findViewById(R.id.settings_check_box_checkbox);
                    box.setChecked(!box.isChecked());
                }else if(k==7){
                    CheckBox box = view.findViewById(R.id.settings_check_box_checkbox);
                    box.setChecked(!box.isChecked());
                }
            }
        });
    }
}
