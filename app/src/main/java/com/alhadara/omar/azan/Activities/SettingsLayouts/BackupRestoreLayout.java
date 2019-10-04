package com.alhadara.omar.azan.Activities.SettingsLayouts;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.omar.azanapkmostafa.R;

public class BackupRestoreLayout extends LinearLayout {
    private Activity activity;
    public BackupRestoreLayout(Activity ac) {
        super(ac);
        activity = ac;
        LinearLayout layout = (LinearLayout) inflate(ac, R.layout.settings_layouts_backup_and_restore,this);
        layout = (LinearLayout) layout.getChildAt(0);
        String[] headers = getResources().getStringArray(R.array.settings_layout_header_backup_and_restore);
        String[] details = getResources().getStringArray(R.array.settings_layout_details_backup_and_restore);
        for(int i=0,k=0;i<layout.getChildCount();i++){
            if(layout.getChildAt(i) instanceof TextView) continue;
            ViewGroup sublayout = (ViewGroup) layout.getChildAt(i);
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
    private void clicker(View view, final int k) {
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }
}
