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

import com.alhadara.omar.azan.Activities.SettingsActivity;
import com.alhadara.omar.azan.Activities.SettingsThirdActivity;
import com.alhadara.omar.azan.Alarms.AlarmsScheduler;
import com.alhadara.omar.azan.Configurations;
import com.alhadara.omar.azan.RadioDialog;
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
            sublayout.setId(SettingsActivity.generateViewID(1,k+1,0));
            ((TextView)sublayout.getChildAt(0)).setText(
                    headers[k]
            );
            ((TextView)sublayout.getChildAt(1)).setText(
                    details[k]
            );
            if(sublayout.getChildCount() == 3) setCheckBoxStatus((CheckBox) sublayout.getChildAt(2),k);
            clicker(sublayout,k);
            k++;

        }
    }

    private void setCheckBoxStatus(CheckBox child,int k) {
        child.setChecked(SettingsActivity.settingsPref.getBoolean("ischecked0_" + k,false));
    }

    private void clicker(final ViewGroup group, final int k) {
        group.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(k==0){
                    RadioDialog dialog = new RadioDialog(activity,Configurations.mainConFile,"method",((TextView) group.getChildAt(0)).getText().toString());
                    dialog.initialize(getResources().getStringArray(R.array.prayer_calculations_methods), new int[]{0, 1, 2, 3, 4, 5, 6, 7}, new RadioDialog.run() {
                        @Override
                        public void go(int checked) {
                            /*Modify Times to change method according to mainConFile*/
                            Configurations.updateTimes(activity);
                            AlarmsScheduler.fire(activity, Calendar.getInstance());
                            SettingsActivity.setStatus(layout.getChildAt(3), checked != 0 && checked != 7);
                            SettingsActivity.setStatus(layout.getChildAt(5), checked == 0);
                        }
                    });
                    dialog.show();
                }else if (k == 1) {
                    SettingsActivity.setCheckBox(group,!SettingsActivity.isChecked(group));
                    SharedPreferences pref = activity.getSharedPreferences(Configurations.mainConFile, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("angle_based_method",SettingsActivity.isChecked(group));
                    editor.commit();
                    Configurations.updateTimes(activity);
                    AlarmsScheduler.fire(activity, Calendar.getInstance());
                } else if (k == 2) {
                    SettingsActivity.setCheckBox(group,!SettingsActivity.isChecked(group));
                    SharedPreferences pref = activity.getSharedPreferences(Configurations.mainConFile, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("adjust_isha_in_ramadan",SettingsActivity.isChecked(group));
                    editor.commit();
                    Configurations.updateTimes(activity);
                    AlarmsScheduler.fire(activity, Calendar.getInstance());
                }else if(k==3){
                    RadioDialog dialog = new RadioDialog(activity,Configurations.mainConFile,"asr_method",((TextView) group.getChildAt(0)).getText().toString());
                    dialog.initialize(getResources().getStringArray(R.array.asr_methods), new String[]{"shafi", "hanafi"}, new RadioDialog.run() {
                        @Override
                        public void go(int checked) {
                            Configurations.updateTimes(activity);
                            AlarmsScheduler.fire(activity, Calendar.getInstance());
                        }
                    });
                    dialog.show();
                }else if(k==4){
                    RadioDialog dialog = new RadioDialog(activity,Configurations.mainConFile,"dst",((TextView) group.getChildAt(0)).getText().toString());
                    dialog.initialize(new String[]{"-1","0","+1"}, new int[]{-1,0,1}, new RadioDialog.run() {
                        @Override
                        public void go(int checked) {
                            Configurations.updateTimes(activity);
                            AlarmsScheduler.fire(activity, Calendar.getInstance());
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
        SettingsActivity.setStatus(group);
        if(group.getChildCount()>2) SettingsActivity.setCheckBox(group);
    }
}
