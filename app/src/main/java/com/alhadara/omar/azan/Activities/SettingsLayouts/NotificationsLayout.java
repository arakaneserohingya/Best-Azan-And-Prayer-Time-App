package com.alhadara.omar.azan.Activities.SettingsLayouts;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.widget.AppCompatCheckBox;

import com.example.omar.azanapkmostafa.R;

public class NotificationsLayout extends LinearLayout {
    private Activity activity;
    private String FILE = "notifications.txt";
    public NotificationsLayout(Activity ac) {
        super(ac);
        activity = ac;
        LinearLayout layout = (LinearLayout) inflate(ac, R.layout.settings_layouts_notifications,this);
        layout = (LinearLayout) layout.getChildAt(0);
        String[] headers = getResources().getStringArray(R.array.settings_layout_header_notifications);
        String[] details = getResources().getStringArray(R.array.settings_layout_details_notifications);
        for(int i=0,k=0;i<layout.getChildCount();i++){
            if(layout.getChildAt(i) instanceof TextView) continue;
            ViewGroup sublayout = (ViewGroup) layout.getChildAt(i);
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
                }else if(k==7){
                    CheckBox box = view.findViewById(R.id.settings_check_box_checkbox);
                    box.setChecked(!box.isChecked());
                }else if(k==10){
                    CheckBox box = view.findViewById(R.id.settings_check_box_checkbox);
                    box.setChecked(!box.isChecked());
                }else if(k==12){
                    CheckBox box = view.findViewById(R.id.settings_check_box_checkbox);
                    box.setChecked(!box.isChecked());
                }else if(k==13){
                    CheckBox box = view.findViewById(R.id.settings_check_box_checkbox);
                    box.setChecked(!box.isChecked());
                }
            }
        });
    }
}
