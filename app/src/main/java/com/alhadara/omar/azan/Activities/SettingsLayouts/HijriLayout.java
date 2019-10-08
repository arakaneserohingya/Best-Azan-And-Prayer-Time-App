package com.alhadara.omar.azan.Activities.SettingsLayouts;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alhadara.omar.azan.Configurations;
import com.alhadara.omar.azan.RadioDialog;
import com.example.omar.azanapkmostafa.R;

public class HijriLayout extends LinearLayout {
    private Activity activity;
    private LinearLayout layout;
    public HijriLayout(Activity ac) {
        super(ac);
        activity = ac;
        layout = (LinearLayout) inflate(ac, R.layout.settings_layouts_hijri,this);
        layout = (LinearLayout) layout.getChildAt(0);
        String[] headers = getResources().getStringArray(R.array.settings_layout_header_hijri);
        String[] details = getResources().getStringArray(R.array.settings_layout_details_hijri);
        for(int i=0,k=0;i<layout.getChildCount();i++){
            if(layout.getChildAt(i) instanceof TextView) continue;
            ViewGroup sublayout = (ViewGroup) layout.getChildAt(i);
            sublayout.setId(_SET.generateViewID(2,k+1,0));
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
                    CheckBox box = view.findViewById(R.id.settings_check_box_checkbox);
                    box.setChecked(!box.isChecked());
                }else{
                    RadioDialog dialog = new RadioDialog(activity, Configurations.mainConFile,"hijri_adjust",((TextView)group.getChildAt(0)).getText().toString());
                    dialog.initialize(new String[]{"+2", "+1", "0", "-1", "-2"}, new int[]{2, 1, 0, -1, -2}, new RadioDialog.run() {
                        @Override
                        public void go(int checked) {
                            Configurations.setReloadMainActivityOnResume(true);
                        }
                    });
                }
            }
        });
    }
}
