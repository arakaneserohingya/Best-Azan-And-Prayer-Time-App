package com.alhadara.omar.azan.Activities.SettingsLayouts;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alhadara.omar.azan.Activities.SettingsActivity;
import com.alhadara.omar.azan.Configurations;
import com.alhadara.omar.azan.RadioDialog;
import com.example.omar.azanapkmostafa.R;

public class DisplayOptions extends LinearLayout {
    private Activity activity;
    public DisplayOptions(Activity ac) {
        super(ac);
        activity = ac;
        LinearLayout layout = (LinearLayout) inflate(ac, R.layout.settings_layouts_display_options,this);
        layout = (LinearLayout) layout.getChildAt(0);
        String[] headers = getResources().getStringArray(R.array.settings_layout_header_display_options);
        String[] details = getResources().getStringArray(R.array.settings_layout_details_display_options);
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
                if(k==0){
                    RadioDialog dialog = new RadioDialog(activity,Configurations.mainConFile,"language",getResources().getString(R.string.choose_display_language));
                    dialog.initialize(new String[]{"English", "عربي"}, new String[]{"en", "ar"}, new Runnable() {
                        @Override
                        public void run() {
                            Configurations.setLanguagePreferences(activity);
                            Configurations.setReloadMainActivityOnResume(true);
                            SettingsActivity.reloadSettingsActivityOnResume = true;
                            activity.recreate();
                        }
                    });
                    dialog.show();
                }else if(k==4){
                    CheckBox box = view.findViewById(R.id.settings_check_box_checkbox);
                    box.setChecked(!box.isChecked());
                }
            }
        });
    }
}
