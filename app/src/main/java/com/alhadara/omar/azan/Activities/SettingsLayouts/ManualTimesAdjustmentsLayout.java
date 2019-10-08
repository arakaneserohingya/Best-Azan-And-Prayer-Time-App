package com.alhadara.omar.azan.Activities.SettingsLayouts;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.omar.azanapkmostafa.R;

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

    private void clicker(final View sublayout,final int k) {
        sublayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}
