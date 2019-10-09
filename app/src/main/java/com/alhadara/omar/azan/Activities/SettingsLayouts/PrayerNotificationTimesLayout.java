package com.alhadara.omar.azan.Activities.SettingsLayouts;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alhadara.omar.azan.Alarms.AlarmsScheduler;
import com.alhadara.omar.azan.Alarms._AlarmSET;
import com.alhadara.omar.azan.SeekBarDialog;
import com.example.omar.azanapkmostafa.R;

import java.util.Calendar;

public class PrayerNotificationTimesLayout extends LinearLayout {
    private Activity activity;
    private LinearLayout layout;
    public PrayerNotificationTimesLayout(Activity ac) {
        super(ac);
        activity = ac;
        layout = (LinearLayout) inflate(ac, R.layout.settings_layouts_prayer_notification_time,this);
        layout = (LinearLayout) layout.getChildAt(0);
        String[] headers = getResources().getStringArray(R.array.settings_layout_header_prayer_notification_time);
        String[] details = getResources().getStringArray(R.array.settings_layout_details_prayer_notification_time);
        for(int i=0,k=0;i<layout.getChildCount();i++){
            if(layout.getChildAt(i) instanceof TextView) continue;
            ViewGroup sublayout = (ViewGroup) layout.getChildAt(i);
            sublayout.setId(_SET.generateViewID(4,1,k+1));
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

    private void clicker(final ViewGroup group,final int k) {
        group.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if((k%2)==0){
                    SeekBarDialog dialog = new SeekBarDialog(activity, _AlarmSET.azanFile,"notification_time_"+(k/2),getResources().getString(R.string.before_prayer_time));
                    dialog.initialize(0, 60, new SeekBarDialog.run() {
                        @Override
                        public void go(int checked) {
                            AlarmsScheduler.fire(activity, Calendar.getInstance());
                            _SET.setDescription(group,Integer.toString(checked));
                        }
                    });
                    dialog.show();
                }else{
                    _SET.setCheckBox(group,!_SET.isChecked(group));
                    SharedPreferences pref = activity.getSharedPreferences(_AlarmSET.azanFile, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("notify_"+((k-1)/2),_SET.isChecked(group));
                    editor.commit();
                    AlarmsScheduler.fire(activity, Calendar.getInstance());
                    _SET.setStatus(layout.getChildAt((((5*k)-5)/2)+1),_SET.isChecked(group));
                }
            }
        });
        _SET.setStatus(group);
        if(group.getChildCount()>2) _SET.setCheckBox(group);
        if((k%2)==0) _SET.setDescription(group);
    }
}
