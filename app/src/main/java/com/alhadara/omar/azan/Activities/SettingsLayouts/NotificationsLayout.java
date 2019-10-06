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

import com.alhadara.omar.azan.Activities.SettingsActivity;
import com.alhadara.omar.azan.Activities.SettingsThirdActivity;
import com.alhadara.omar.azan.Alarms.AlarmsScheduler;
import com.alhadara.omar.azan.Configurations;
import com.alhadara.omar.azan.RadioDialog;
import com.example.omar.azanapkmostafa.R;

import java.util.Calendar;

public class NotificationsLayout extends LinearLayout {
    private Activity activity;
    private LinearLayout layout;
    private String FILE = "notifications.txt";
    public NotificationsLayout(Activity ac) {
        super(ac);
        activity = ac;
        layout = (LinearLayout) inflate(ac, R.layout.settings_layouts_notifications,this);
        layout = (LinearLayout) layout.getChildAt(0);
        String[] headers = getResources().getStringArray(R.array.settings_layout_header_notifications);
        String[] details = getResources().getStringArray(R.array.settings_layout_details_notifications);
        for(int i=0,k=0;i<layout.getChildCount();i++){
            if(layout.getChildAt(i) instanceof TextView) continue;
            ViewGroup sublayout = (ViewGroup) layout.getChildAt(i);
            sublayout.setId(SettingsActivity.generateViewID(4,k+1,0));
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

    private void clicker(final ViewGroup group, final int k) {
        group.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(k==0){
                    Intent intent = new Intent(activity, SettingsThirdActivity.class);
                    intent.putExtra("layout",3);
                    intent.putExtra("key",0);
                    activity.startActivity(intent);
                }else if(k==1){
                    SettingsActivity.setCheckBox(group,!SettingsActivity.isChecked(group));
                    SharedPreferences pref = activity.getSharedPreferences(FILE, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("notify",SettingsActivity.isChecked(group));
                    editor.commit();
                    AlarmsScheduler.fire(activity, Calendar.getInstance());
                }else if(k==2){
                    RadioDialog dialog = new RadioDialog(activity,FILE,"notification_type",((TextView) group.getChildAt(0)).getText().toString());
                    dialog.initialize(getResources().getStringArray(R.array.notification_audio_type), new int[]{0,1,2,3}, new RadioDialog.run() {
                        @Override
                        public void go(int checked) {
                            AlarmsScheduler.fire(activity, Calendar.getInstance());
                        }
                    });
                    dialog.show();
                }else if(k==3){
                    Intent intent = new Intent(activity, SettingsThirdActivity.class);
                    intent.putExtra("layout",3);
                    intent.putExtra("key",3);
                    activity.startActivity(intent);
                }else if(k==4){
                    SettingsActivity.setCheckBox(group,!SettingsActivity.isChecked(group));
                    SharedPreferences pref = activity.getSharedPreferences(FILE, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("clear_notification",SettingsActivity.isChecked(group));
                    editor.commit();
                    SettingsActivity.setStatus((ViewGroup) layout.getChildAt(11),SettingsActivity.isChecked(group));
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
        SettingsActivity.setStatus(group);
        if(group.getChildCount()>2) SettingsActivity.setCheckBox(group);
    }
}
