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



import com.alhadara.omar.azan.Activities.SettingsThirdActivity;
import com.alhadara.omar.azan.Alarms.AlarmsScheduler;
import com.alhadara.omar.azan.Alarms._AlarmSET;
import com.alhadara.omar.azan.Configurations;
import com.alhadara.omar.azan.RadioDialog;
import com.alhadara.omar.azan.SeekBarDialog;
import com.example.omar.azanapkmostafa.R;

import java.util.Calendar;

public class NotificationsLayout extends LinearLayout {
    private Activity activity;
    private LinearLayout layout;

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
            sublayout.setId(_SET.generateViewID(4,k+1,0));
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
                    _SET.setCheckBox(group,!_SET.isChecked(group));
                    SharedPreferences pref = activity.getSharedPreferences(_AlarmSET.azanFile, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("notify",_SET.isChecked(group));
                    editor.commit();
                    AlarmsScheduler.fire(activity, Calendar.getInstance());
                    _SET.setStatus(layout.getChildAt(1),_SET.isChecked(group));
                    _SET.setStatus(layout.getChildAt(5),_SET.isChecked(group));
                    _SET.setStatus(layout.getChildAt(7),_SET.isChecked(group));
                }else if(k==2){
                    RadioDialog dialog = new RadioDialog(activity,_AlarmSET.azanFile,"notification_type",((TextView) group.getChildAt(0)).getText().toString());
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
                    _SET.setCheckBox(group,!_SET.isChecked(group));
                    SharedPreferences pref = activity.getSharedPreferences(_AlarmSET.azanFile, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("clear_notification",_SET.isChecked(group));
                    editor.commit();
                    _SET.setStatus((ViewGroup) layout.getChildAt(11),_SET.isChecked(group));
                }else if(k==5){
                    SeekBarDialog dialog = new SeekBarDialog(activity,_AlarmSET.azanFile,"clear_time",((TextView)group.getChildAt(0)).getText().toString());
                    dialog.initialize(1, 60, new SeekBarDialog.run() {
                        @Override
                        public void go(int checked) {
                            AlarmsScheduler.fire(activity,Calendar.getInstance());
                        }
                    });
                    dialog.show();
                }else if(k==6){
                    Intent intent = new Intent(activity, SettingsThirdActivity.class);
                    intent.putExtra("layout",3);
                    intent.putExtra("key",6);
                    activity.startActivity(intent);
                }else if(k==7){
                    _SET.setCheckBox(group,!_SET.isChecked(group));
                    SharedPreferences pref = activity.getSharedPreferences(_AlarmSET.iqamaFile, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("remind",_SET.isChecked(group));
                    editor.commit();
                    AlarmsScheduler.fire(activity, Calendar.getInstance());
                    _SET.setStatus(layout.getChildAt(18),_SET.isChecked(group));
                    _SET.setStatus(layout.getChildAt(20),_SET.isChecked(group));
                }else if(k==8){
                    RadioDialog dialog = new RadioDialog(activity,_AlarmSET.iqamaFile,"reminder_type",((TextView) group.getChildAt(0)).getText().toString());
                    dialog.initialize(getResources().getStringArray(R.array.notification_audio_type), new int[]{0,1,2,3}, new RadioDialog.run() {
                        @Override
                        public void go(int checked) {
                            AlarmsScheduler.fire(activity, Calendar.getInstance());
                        }
                    });
                    dialog.show();
                }else if(k==9){
                    Intent intent = new Intent(activity, SettingsThirdActivity.class);
                    intent.putExtra("layout",3);
                    intent.putExtra("key",9);
                    activity.startActivity(intent);
                }else if(k==10){
                    _SET.setCheckBox(group,!_SET.isChecked(group));
                    SharedPreferences pref = activity.getSharedPreferences(_AlarmSET.iqamaFile, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("clear_reminder",_SET.isChecked(group));
                    editor.commit();
                    _SET.setStatus(layout.getChildAt(24),_SET.isChecked(group));
                }else if(k==11){
                    SeekBarDialog dialog = new SeekBarDialog(activity,_AlarmSET.iqamaFile,"clear_time",((TextView)group.getChildAt(0)).getText().toString());
                    dialog.initialize(1, 60, new SeekBarDialog.run() {
                        @Override
                        public void go(int checked) {
                            AlarmsScheduler.fire(activity,Calendar.getInstance());
                        }
                    });
                    dialog.show();
                }else if(k==12){
                    _SET.setCheckBox(group,!_SET.isChecked(group));
                    SharedPreferences pref = activity.getSharedPreferences(_AlarmSET.azanFile, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("vibrate",_SET.isChecked(group));
                    editor.commit();
                    pref = activity.getSharedPreferences(_AlarmSET.iqamaFile, Context.MODE_PRIVATE);
                    editor = pref.edit();
                    editor.putBoolean("vibrate",_SET.isChecked(group));
                    editor.commit();
                }else if(k==13){
                    _SET.setCheckBox(group,!_SET.isChecked(group));
                    SharedPreferences pref = activity.getSharedPreferences(_AlarmSET.azanFile, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("led",_SET.isChecked(group));
                    editor.commit();
                    pref = activity.getSharedPreferences(_AlarmSET.iqamaFile, Context.MODE_PRIVATE);
                    editor = pref.edit();
                    editor.putBoolean("led",_SET.isChecked(group));
                    editor.commit();
                }
            }
        });
        _SET.setStatus(group);
        if(group.getChildCount()>2) _SET.setCheckBox(group);
    }
}
