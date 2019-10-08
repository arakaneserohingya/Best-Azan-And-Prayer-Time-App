package com.alhadara.omar.azan.Activities.SettingsLayouts;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alhadara.omar.azan.Alarms.AlarmsScheduler;
import com.example.omar.azanapkmostafa.R;

import java.util.Calendar;

public class PrayerNotificationToneEachLayout extends LinearLayout {
    private Activity activity;
    private LinearLayout layout;
    public PrayerNotificationToneEachLayout(Activity ac) {
        super(ac);
        activity = ac;
        layout = (LinearLayout) inflate(ac, R.layout.settings_layouts_prayer_notification_tone_each,this);
        layout = (LinearLayout) layout.getChildAt(0);
        String[] headers = getResources().getStringArray(R.array.settings_layout_header_iqama_reminder_tone);
        String[] details = getResources().getStringArray(R.array.settings_layout_details_iqama_reminder_tone);
        for(int i=0,k=0;i<layout.getChildCount();i++){
            if(layout.getChildAt(i) instanceof TextView) continue;
            ViewGroup sublayout = (ViewGroup) layout.getChildAt(i);
            sublayout.setId(activity.getIntent().getExtras().getInt("id")+((k+1)*10));
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
                    SharedPreferences pref = activity.getSharedPreferences(AlarmsScheduler.azanFile, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("use_sd_for" + activity.getIntent().getExtras().getInt("index"),_SET.isChecked(group));
                    editor.commit();
                    AlarmsScheduler.fire(activity, Calendar.getInstance());
                    _SET.setStatus(layout.getChildAt(0),!_SET.isChecked(group));
                    _SET.setStatus(layout.getChildAt(4),_SET.isChecked(group));
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
                }
            }
        });
        _SET.setStatus(group);
        if(group.getChildCount()>2) _SET.setCheckBox(group);
        if(k==2) _SET.setDescription(group);
    }
}
