package com.alhadara.omar.azan.Activities.SettingsLayouts;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.alhadara.omar.azan.Activities.SettingsThirdActivity;
import com.alhadara.omar.azan.Alarms.AlarmsScheduler;
import com.alhadara.omar.azan.Configurations;
import com.alhadara.omar.azan.RadioDialog;
import com.alhadara.omar.azan.Times;
import com.example.omar.azanapkmostafa.R;

import java.util.Calendar;

public class PrayerTimesLayout extends LinearLayout {
    private Activity activity;
    private LinearLayout layout;
    public PrayerTimesLayout(Activity ac) {
        super(ac);
        activity = ac;
        layout = (LinearLayout) inflate(ac, R.layout.settings_layouts_prayer_times,this);
        layout = (LinearLayout) layout.getChildAt(0);
        String[] headers = getResources().getStringArray(R.array.settings_layout_header_prayer_times);
        String[] details = getResources().getStringArray(R.array.settings_layout_details_prayer_times);
        for(int i=0,k=0;i<layout.getChildCount();i++){
            if(layout.getChildAt(i) instanceof TextView) continue;
            ViewGroup sublayout = (ViewGroup) layout.getChildAt(i);
            sublayout.setId(_SET.generateViewID(1,k+1,0));
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
                    RadioDialog dialog = new RadioDialog(activity, Times.prayersFile,"method",((TextView) group.getChildAt(0)).getText().toString());
                    dialog.initialize(getResources().getStringArray(R.array.prayer_calculations_methods), new int[]{4, 3, 1, 5, 2, 6, 7}, new RadioDialog.run() {
                        @Override
                        public void go(int checked) {
                            /*Modify Times to change method according to mainConFile*/
                            Configurations.updateTimes(activity);
                            AlarmsScheduler.fire(activity, Calendar.getInstance());
                            _SET.setStatus(layout.getChildAt(3), checked != 0 && checked != 7);
                            _SET.setStatus(layout.getChildAt(5), checked == 0);
                        }
                    });
                    dialog.show();
                }else if (k == 1) {
                    _SET.setCheckBox(group,!_SET.isChecked(group));
                    SharedPreferences pref = activity.getSharedPreferences(Times.prayersFile, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("angle_based_method",_SET.isChecked(group));
                    editor.commit();
                    Configurations.updateTimes(activity);
                    AlarmsScheduler.fire(activity, Calendar.getInstance());
                } else if (k == 2) {
                    _SET.setCheckBox(group,!_SET.isChecked(group));
                    SharedPreferences pref = activity.getSharedPreferences(Times.prayersFile, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("adjust_isha_in_ramadan",_SET.isChecked(group));
                    editor.commit();
                    Configurations.updateTimes(activity);
                    AlarmsScheduler.fire(activity, Calendar.getInstance());
                }else if(k==3){
                    RadioDialog dialog = new RadioDialog(activity,Times.prayersFile,"asr_method",((TextView) group.getChildAt(0)).getText().toString());
                    dialog.initialize(getResources().getStringArray(R.array.asr_methods), new int[]{0, 1}, new RadioDialog.run() {
                        @Override
                        public void go(int checked) {
                            Configurations.updateTimes(activity);
                            AlarmsScheduler.fire(activity, Calendar.getInstance());
                        }
                    });
                    dialog.show();
                }else if(k==4){
                    RadioDialog dialog = new RadioDialog(activity,Times.prayersFile,"dst",((TextView) group.getChildAt(0)).getText().toString());
                    dialog.initialize(new String[]{"-1","0","+1"}, new int[]{-1,0,1}, new RadioDialog.run() {
                        @Override
                        public void go(int checked) {
                            Configurations.updateTimes(activity);
                            AlarmsScheduler.fire(activity, Calendar.getInstance());
                        }
                    });
                    dialog.show();
                }else if(k==5){
                    final AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
                    final int[] chosen = new int[]{Times.isJumuahTimeDiff(activity)?1:0};
                    dialog.setSingleChoiceItems(getResources().getStringArray(R.array.jumuah_time_different_array), chosen[0], new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            chosen[0] = i;
                        }
                    });
                    dialog.setPositiveButton(getResources().getString(R.string.mdtp_ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if(chosen[0] == 0) Times.setJumuahDifferent(activity,false);
                            else {
                                String s = Times.getJumuahTime(activity);
                                TimePickerDialog dialog1 = new TimePickerDialog(activity,android.R.style.Theme_Holo_Light_Dialog_NoActionBar, new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                                        Times.setJumuahDifferent(activity,true);
                                        Times.setJumuahTime(activity,i,i1);
                                    }
                                },Integer.parseInt(s.substring(0,s.indexOf(':'))),Integer.parseInt(s.substring(s.indexOf(':')+1)),false);
                                dialog1.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                dialog1.show();
                            }
                            Configurations.updateTimes(activity);
                            AlarmsScheduler.fire(activity,Calendar.getInstance());
                        }
                    });
                    dialog.show();
                }else if(k==6){
                    Intent intent = new Intent(activity, SettingsThirdActivity.class);
                    intent.putExtra("layout",0);
                    intent.putExtra("key",6);
                    activity.startActivity(intent);
                }
            }
        });
        _SET.setStatus(group);
        if(group.getChildCount()>2) _SET.setCheckBox(group);
    }


}
