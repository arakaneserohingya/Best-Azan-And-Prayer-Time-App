package com.alhadara.omar.azan.Activities.SettingsLayouts;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatSeekBar;

import com.alhadara.omar.azan.Alarms.AlarmsScheduler;
import com.alhadara.omar.azan.Alarms._AlarmSET;
import com.alhadara.omar.azan.RadioDialog;
import com.alhadara.omar.azan.SeekBarDialog;
import com.example.omar.azanapkmostafa.R;

import java.util.Calendar;

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
    @SuppressLint("ResourceType")
    private void clicker(final ViewGroup group, final int k) {
        group.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(k==1){
                    _SET.setCheckBox(group,!_SET.isChecked(group));
                    SharedPreferences pref = activity.getSharedPreferences(_AlarmSET.sahoorFile, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("sahooralarm",_SET.isChecked(group));
                    editor.commit();
                    AlarmsScheduler.fire(activity, Calendar.getInstance());
                    _SET.setStatus(layout.getChildAt(4),_SET.isChecked(group));
                    _SET.setStatus(layout.getChildAt(6),_SET.isChecked(group));
                    _SET.setStatus(layout.getChildAt(8),_SET.isChecked(group));
                    _SET.setStatus(layout.getChildAt(10),_SET.isChecked(group));
                    _SET.setStatus(layout.getChildAt(12),_SET.isChecked(group));
                    _SET.setStatus(layout.getChildAt(14),_SET.isChecked(group));
                    _SET.setStatus(layout.getChildAt(16),_SET.isChecked(group) && _SET.isChecked(layout.getChildAt(14)));
                    _SET.setStatus(layout.getChildAt(18),_SET.isChecked(group));
                }else if(k==2){
                    SeekBarDialog dialog = new SeekBarDialog(activity,_AlarmSET.sahoorFile,"time",getResources().getString(R.string.alarm_time_from_fajr_time));
                    dialog.initialize(-90, 20, new SeekBarDialog.run() {
                        @Override
                        public void go(int checked) {
                            AlarmsScheduler.fire(activity,Calendar.getInstance());
                            _SET.setDescription(group,Integer.toString(checked));
                        }
                    });
                    dialog.show();
                }else if(k==3){
                    Uri ringtone = Uri.parse(activity.getSharedPreferences(_AlarmSET.sahoorFile,Context.MODE_PRIVATE).getString("uri",RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM).toString()));
                    Intent intent=new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, ringtone);
                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI, ringtone);
                    activity.startActivityForResult(intent , group.getId());
                }else if(k==4){
                    _SET.setCheckBox(group,!_SET.isChecked(group));
                    SharedPreferences pref = activity.getSharedPreferences(_AlarmSET.sahoorFile, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("vibrate",_SET.isChecked(group));
                    editor.commit();
                } else if(k==5){
                    _SET.setCheckBox(group,!_SET.isChecked(group));
                    SharedPreferences pref = activity.getSharedPreferences(_AlarmSET.sahoorFile, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("screen_on",_SET.isChecked(group));
                    editor.commit();
                }else if(k==6){
                    _SET.setCheckBox(group,!_SET.isChecked(group));
                    SharedPreferences pref = activity.getSharedPreferences(_AlarmSET.sahoorFile, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("work_on_silent",_SET.isChecked(group));
                    editor.commit();
                }else if(k==7){
                    _SET.setCheckBox(group,!_SET.isChecked(group));
                    SharedPreferences pref = activity.getSharedPreferences(_AlarmSET.sahoorFile, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("stop_automatically",_SET.isChecked(group));
                    editor.commit();
                    _SET.setStatus(layout.getChildAt(16),_SET.isChecked(group));
                }else if(k==8){
                    SeekBarDialog dialog = new SeekBarDialog(activity,_AlarmSET.sahoorFile,"stop_time",getResources().getString(R.string.sahoor_activity_time_to_stop_dialog_title));
                    dialog.initialize(5, 45, new SeekBarDialog.run() {
                        @Override
                        public void go(int checked) {
                            _SET.setDescription(group,Integer.toString(checked));
                        }
                    });
                    dialog.show();
                }else if(k==9){
                    RadioDialog dialog = new RadioDialog(activity,_AlarmSET.sahoorFile,"snooze_time",((TextView) group.getChildAt(0)).getText().toString());
                    dialog.initialize(new String[]{"2", "5", "10", "15", "20"}, new int[]{2, 5, 10, 15, 20}, new RadioDialog.run() {
                        @Override
                        public void go(int checked) {

                        }
                    });
                    dialog.show();
                }
            }
        });
        _SET.setStatus(group);
        if(group.getChildCount()>2) _SET.setCheckBox(group);
        if(group.getId() == 50300 ||group.getId() == 50900) _SET.setDescription(group);
    }
}
