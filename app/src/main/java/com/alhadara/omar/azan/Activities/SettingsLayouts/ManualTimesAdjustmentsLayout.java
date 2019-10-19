package com.alhadara.omar.azan.Activities.SettingsLayouts;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alhadara.omar.azan.Alarms.AlarmsScheduler;
import com.alhadara.omar.azan.Alarms._AlarmSET;
import com.alhadara.omar.azan.Configurations;
import com.alhadara.omar.azan.SeekBarDialog;
import com.alhadara.omar.azan.Times;
import com.example.omar.azanapkmostafa.R;

import java.util.Calendar;

public class ManualTimesAdjustmentsLayout extends LinearLayout {
    private Activity activity;
    private LinearLayout layout;
    public ManualTimesAdjustmentsLayout(Activity ac) {
        super(ac);
        activity = ac;
        layout = (LinearLayout) inflate(ac, R.layout.settings_layouts_manual_times_adjustments,this);
        layout = (LinearLayout) layout.getChildAt(0);
        String[] headers = getResources().getStringArray(R.array.settings_layout_header_manual_times_adjustments);
        String[] details = getResources().getStringArray(R.array.settings_layout_details_manual_times_adjustments);
        for(int i=0,k=0;i<layout.getChildCount();i++){
            if(layout.getChildAt(i) instanceof TextView) continue;
            ViewGroup sublayout = (ViewGroup) layout.getChildAt(i);
            sublayout.setId(_SET.generateViewID(1,7,k+1));
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
                if(k>0 && k<7){
                    SeekBarDialog dialog = new SeekBarDialog(activity, Times.adjustTimesFile,"adjust_"+(k-1),((TextView)group.getChildAt(0)).getText().toString());
                    dialog.initialize(-60, 60, new SeekBarDialog.run() {
                        @Override
                        public void go(int checked) {
                            Configurations.updateTimes(activity);
                            AlarmsScheduler.fire(activity, Calendar.getInstance());
                            _SET.setDescription(group,Integer.toString(checked));
                        }
                    });
                    dialog.show();
                }
                else if(k==7){
                    SharedPreferences p = activity.getSharedPreferences(Times.adjustTimesFile,Context.MODE_PRIVATE);
                    SharedPreferences.Editor e = p.edit();
                    for(int i=2;i<8;i++){
                        e.putInt("adjust_" +(i-2),0);
                        _SET.setDescription((ViewGroup) findViewById(10700 + i),"0");
                    }
                    e.commit();
                    Toast.makeText(activity,getResources().getString(R.string.adjustments_is_reset),Toast.LENGTH_SHORT).show();
                }
            }
        });
        if(k>0 && k<7) _SET.setDescription(group);
    }
}
