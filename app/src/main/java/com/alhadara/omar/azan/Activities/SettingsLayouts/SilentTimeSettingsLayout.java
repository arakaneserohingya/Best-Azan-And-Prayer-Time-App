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
import com.alhadara.omar.azan.SeekBarDialog;
import com.example.omar.azanapkmostafa.R;

import java.util.Calendar;

public class SilentTimeSettingsLayout extends LinearLayout {
    private LinearLayout layout;
    private Activity activity;

    public SilentTimeSettingsLayout(Activity ac) {
        super(ac);
        activity = ac;
        layout = (LinearLayout) inflate(ac, R.layout.settings_layouts_silent_time_settings,this);
        layout = (LinearLayout) layout.getChildAt(0);
        String[] headers = getResources().getStringArray(R.array.settings_layout_header_silent_time_settings);
        String[] details = getResources().getStringArray(R.array.settings_layout_details_silent_time_settings);
        for(int i=0,k=0;i<layout.getChildCount();i++){
            if(layout.getChildAt(i) instanceof TextView) continue;
            ViewGroup sublayout = (ViewGroup) layout.getChildAt(i);
            sublayout.setId(_SET.generateViewID(6,2,k+1));
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
                if((k%3)==0){
                    SeekBarDialog dialog = new SeekBarDialog(activity, _AlarmSET.silentFile,"silent_time_"+(k/3),getResources().getString(R.string.after_prayer_time));
                    dialog.initialize(0, 30, new SeekBarDialog.run() {
                        @Override
                        public void go(int checked) {
                            AlarmsScheduler.fire(activity, Calendar.getInstance());
                            _SET.setDescription(group,Integer.toString(checked));
                        }
                    });
                    dialog.show();
                }else if((k%3)==1){
                    SeekBarDialog dialog = new SeekBarDialog(activity, _AlarmSET.silentFile,"silent_period_"+((k-1)/3),getResources().getString(R.string.silent_period_in_minutes));
                    dialog.initialize(5, 90, new SeekBarDialog.run() {
                        @Override
                        public void go(int checked) {
                            _SET.setDescription(group,Integer.toString(checked));
                        }
                    });
                    dialog.show();
                }else{
                    _SET.setCheckBox(group,!_SET.isChecked(group));
                    SharedPreferences pref = activity.getSharedPreferences(_AlarmSET.silentFile, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("active_"+((k-2)/3),_SET.isChecked(group));
                    editor.commit();
                    AlarmsScheduler.fire(activity, Calendar.getInstance());
                    _SET.setStatus(findViewById(60200+k-1),_SET.isChecked(group));
                    _SET.setStatus(findViewById(60200+k),_SET.isChecked(group));
                }
            }
        });
        _SET.setStatus(group);
        if(group.getChildCount()>2) _SET.setCheckBox(group);
        if((k%3)==0 || (k%3)==1) _SET.setDescription(group);
    }
}
