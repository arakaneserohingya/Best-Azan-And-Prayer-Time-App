package com.alhadara.omar.azan.Activities.SettingsLayouts;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alhadara.omar.azan.Activities.SettingsThirdActivity;
import com.alhadara.omar.azan.Alarms.AlarmsScheduler;
import com.alhadara.omar.azan.Alarms._AlarmSET;
import com.example.omar.azanapkmostafa.R;

import java.util.Calendar;

public class SilentLayout extends LinearLayout {
    private Activity activity;
    private LinearLayout layout;
    public SilentLayout(Activity ac) {
        super(ac);
        activity = ac;
        layout = (LinearLayout) inflate(ac, R.layout.settings_layouts_silent,this);
        layout = (LinearLayout) layout.getChildAt(0);
        String[] headers = getResources().getStringArray(R.array.settings_layout_header_silent);
        String[] details = getResources().getStringArray(R.array.settings_layout_details_silent);
        for(int i=0,k=0;i<layout.getChildCount();i++){
            if(layout.getChildAt(i) instanceof TextView) continue;
            ViewGroup sublayout = (ViewGroup) layout.getChildAt(i);
            sublayout.setId(_SET.generateViewID(6,k+1,0));
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
                    _SET.setCheckBox(group,!_SET.isChecked(group));
                    SharedPreferences pref = activity.getSharedPreferences(_AlarmSET.silentFile, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("active",_SET.isChecked(group));
                    editor.commit();
                    AlarmsScheduler.fire(activity, Calendar.getInstance());
                    _SET.setStatus(layout.getChildAt(3),_SET.isChecked(group));
                    _SET.setStatus(layout.getChildAt(5),_SET.isChecked(group));
                    _SET.setStatus(layout.getChildAt(7),_SET.isChecked(group));
                    _SET.setStatus(layout.getChildAt(9),_SET.isChecked(group));
                }else if(k==1){
                    Intent intent = new Intent(activity, SettingsThirdActivity.class);
                    intent.putExtra("layout",5);
                    intent.putExtra("key",1);
                    activity.startActivity(intent);
                }else if(k==2){
                    _SET.setCheckBox(group,!_SET.isChecked(group));
                    SharedPreferences pref = activity.getSharedPreferences(_AlarmSET.silentFile, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("vibrate_on_set",_SET.isChecked(group));
                    editor.commit();
                }else if(k==3){
                    _SET.setCheckBox(group,!_SET.isChecked(group));
                    SharedPreferences pref = activity.getSharedPreferences(_AlarmSET.silentFile, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("vibrate",_SET.isChecked(group));
                    editor.commit();
                }else if(k==4){
                    _SET.setCheckBox(group,!_SET.isChecked(group));
                    SharedPreferences pref = activity.getSharedPreferences(_AlarmSET.silentFile, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("msg",_SET.isChecked(group));
                    editor.commit();
                }
            }
        });
        _SET.setStatus(group);
        if(group.getChildCount()>2) _SET.setCheckBox(group);
    }
}
