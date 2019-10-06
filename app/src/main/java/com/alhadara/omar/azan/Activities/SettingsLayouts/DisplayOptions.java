package com.alhadara.omar.azan.Activities.SettingsLayouts;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.util.IslamicCalendar;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.app.AlarmManagerCompat;

import com.alhadara.omar.azan.Activities.MainActivity;
import com.alhadara.omar.azan.Activities.SettingsActivity;
import com.alhadara.omar.azan.Configurations;
import com.alhadara.omar.azan.RadioDialog;
import com.example.omar.azanapkmostafa.R;

public class DisplayOptions extends LinearLayout {
    private Activity activity;
    private LinearLayout layout;
    public DisplayOptions(Activity ac) {
        super(ac);
        activity = ac;
        layout = (LinearLayout) inflate(ac, R.layout.settings_layouts_display_options,this);
        layout = (LinearLayout) layout.getChildAt(0);
        String[] headers = getResources().getStringArray(R.array.settings_layout_header_display_options);
        String[] details = getResources().getStringArray(R.array.settings_layout_details_display_options);
        for(int i=0,k=0;i<layout.getChildCount();i++){
            if(layout.getChildAt(i) instanceof TextView) continue;
            ViewGroup sublayout = (ViewGroup) layout.getChildAt(i);
            sublayout.setId(SettingsActivity.generateViewID(3,k+1,0));
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
                    RadioDialog dialog = new RadioDialog(activity,Configurations.mainConFile,"language",((TextView) group.getChildAt(0)).getText().toString());
                    dialog.initialize(new String[]{"English", "عربي"}, new String[]{"en", "ar"}, new RadioDialog.run() {
                        @Override
                        public void go(int checked) {
                            Intent i = activity.getBaseContext().getPackageManager().
                                    getLaunchIntentForPackage(activity.getBaseContext().getPackageName());
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            activity.startActivity(i);
                            activity.finish();
                        }
                    });
                    dialog.show();
                }else if(k==1){
                    RadioDialog dialog = new RadioDialog(activity,Configurations.mainConFile,"numbers_language",((TextView) group.getChildAt(0)).getText().toString());
                    dialog.initialize(new String[]{"English","عربي",getResources().getString(R.string.app_language)}, new int[]{0,1,2}, new RadioDialog.run() {
                        @Override
                        public void go(int checked) {

                        }
                    });
                    dialog.show();
                }else if(k==2){
                    RadioDialog dialog = new RadioDialog(activity,Configurations.mainConFile,"theme",((TextView) group.getChildAt(0)).getText().toString());
                    dialog.initialize(getResources().getStringArray(R.array.app_theme), new int[]{0,1}, new RadioDialog.run() {
                        @Override
                        public void go(int checked) {

                        }
                    });
                    dialog.show();
                }else if(k==3){
                    RadioDialog dialog = new RadioDialog(activity,Configurations.mainConFile,"font",((TextView) group.getChildAt(0)).getText().toString());
                    dialog.initialize(getResources().getStringArray(R.array.app_font), new int[]{0,1,2}, new RadioDialog.run() {
                        @Override
                        public void go(int checked) {

                        }
                    });
                    dialog.show();
                }else if(k==4){
                    SettingsActivity.setCheckBox(group,!SettingsActivity.isChecked(group));
                    SharedPreferences pref = activity.getSharedPreferences(Configurations.mainConFile, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("time24",SettingsActivity.isChecked(group));
                    editor.commit();
                    Configurations.setReloadMainActivityOnResume(true);
                }else{
                    RadioDialog dialog = new RadioDialog(activity,Configurations.mainConFile,"widget_theme_"+(k-5),((TextView) group.getChildAt(0)).getText().toString());
                    dialog.initialize(getResources().getStringArray(R.array.widget_theme), new int[]{0,1}, new RadioDialog.run() {
                        @Override
                        public void go(int checked) {
                            Configurations.setReloadMainActivityOnResume(true);
                        }
                    });
                    dialog.show();
                }
            }
        });
    }
}
