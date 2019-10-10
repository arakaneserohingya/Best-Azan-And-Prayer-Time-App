package com.alhadara.omar.azan.Activities.SettingsLayouts;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alhadara.omar.azan.Activities.SettingsForthActivity;
import com.alhadara.omar.azan.Alarms.AlarmsScheduler;
import com.alhadara.omar.azan.Alarms._AlarmSET;
import com.example.omar.azanapkmostafa.R;

import java.util.Calendar;

public class PrayerNotificationToneLayout extends LinearLayout {
    private Activity activity;
    private LinearLayout layout;
    public PrayerNotificationToneLayout(Activity ac) {
        super(ac);
        activity = ac;
        layout = (LinearLayout) inflate(ac, R.layout.settings_layouts_prayer_notification_tone,this);
        layout = (LinearLayout) layout.getChildAt(0);
        String[] headers = getResources().getStringArray(R.array.settings_layout_header_prayer_notification_tone);
        String[] details = getResources().getStringArray(R.array.settings_layout_details_prayer_notification_tone);
        for(int i=0,k=0;i<layout.getChildCount();i++){
            if(layout.getChildAt(i) instanceof TextView) continue;
            ViewGroup sublayout = (ViewGroup) layout.getChildAt(i);
            sublayout.setId(_SET.generateViewID(4,4,k+1));
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
                if(k==0){
                    Uri ringtone = null;
                    Intent intent=new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, ringtone);
                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI, ringtone);
                    activity.startActivityForResult(intent , group.getId());
                }else if(k==1){
                    _SET.setCheckBox(group,!_SET.isChecked(group));
                    SharedPreferences pref = activity.getSharedPreferences(_AlarmSET.azanFile, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("use_sd",_SET.isChecked(group));
                    editor.remove("uri");
                    editor.commit();
                    AlarmsScheduler.fire(activity, Calendar.getInstance());
                    _SET.setStatus(layout.getChildAt(0),!_SET.isChecked(group));
                    _SET.setStatus(layout.getChildAt(4),_SET.isChecked(group));
                    _SET.setDescription((ViewGroup) layout.getChildAt(4),"");
                }else if(k==2){
                    System.gc();
                    Intent intent = new Intent();
                    intent.setType("audio/*");
                    if (Build.VERSION.SDK_INT < 19) {
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        intent = Intent.createChooser(intent, "Select file");
                    } else {
                        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        String[] mimetypes = { "audio/*" };
                        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
                    }
                    activity.startActivityForResult(intent , group.getId());
                }else if(k==3){
                    _SET.setCheckBox(group,!_SET.isChecked(group));
                    SharedPreferences pref = activity.getSharedPreferences(_AlarmSET.azanFile, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("use_different",_SET.isChecked(group));
                    editor.commit();
                    AlarmsScheduler.fire(activity, Calendar.getInstance());
                    _SET.setStatus(layout.getChildAt(0),!_SET.isChecked(group));
                    _SET.setStatus(layout.getChildAt(2),!_SET.isChecked(group));
                    _SET.setCheckBox(layout.getChildAt(2),false);
                    _SET.setStatus(layout.getChildAt(4),false);
                    _SET.setStatus(layout.getChildAt(9),_SET.isChecked(group));
                    _SET.setStatus(layout.getChildAt(11),_SET.isChecked(group));
                    _SET.setStatus(layout.getChildAt(13),_SET.isChecked(group));
                    _SET.setStatus(layout.getChildAt(15),_SET.isChecked(group));
                    _SET.setStatus(layout.getChildAt(17),_SET.isChecked(group));
                }else{
                    Intent intent = new Intent(activity, SettingsForthActivity.class);
                    intent.putExtra("id",group.getId());
                    intent.putExtra("index",k-4);
                    activity.startActivity(intent);
                }
            }
        });
        _SET.setStatus(group);
        if(group.getChildCount()>2) _SET.setCheckBox(group);
        if(group.getId() == 40403 ) _SET.setDescription(group);
    }


}
