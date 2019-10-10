package com.alhadara.omar.azan.Alarms;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.alhadara.omar.azan.Times;

import java.util.Calendar;

public class _AlarmSET {
    public static final String azanFile = "notifications.txt";
    public static final String iqamaFile = "reminders.txt";
    public static final int AZAN_REQUEST_CODE = 150;
    public static final int IQAMA_REQUEST_CODE = 160;

    public static boolean notifyActivated(Context context) {
        return context.getSharedPreferences(azanFile,Context.MODE_PRIVATE).getBoolean("notify",true);
    }
    public static boolean notifyActivatedFor(Context context,int i){
        if(isJumuah(Calendar.getInstance()) && i==2) return context.getSharedPreferences(azanFile,Context.MODE_PRIVATE).getBoolean("notify_5",true);
        return context.getSharedPreferences(azanFile,Context.MODE_PRIVATE).getBoolean("notify_"+(i>0?i-1:0),true);
    }

    public static boolean remindActivated(Context context) {
        return context.getSharedPreferences(iqamaFile,Context.MODE_PRIVATE).getBoolean("remind",true);
    }
    public static boolean remindActivatedFor(Context context,int i){
        if(isJumuah(Calendar.getInstance()) && i==2) return context.getSharedPreferences(iqamaFile,Context.MODE_PRIVATE).getBoolean("remind_5",true);
        return context.getSharedPreferences(iqamaFile,Context.MODE_PRIVATE).getBoolean("remind_"+(i>0?i-1:0),true);
    }
    public static boolean isJumuah(Calendar cal) {
        return cal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY;
    }
    public static Calendar getNotifyTimeFor(Context context, int i) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY,Integer.parseInt(Times.times[i].substring(0,2)));
        cal.set(Calendar.MINUTE,Integer.parseInt(Times.times[i].substring(3,5)));
        cal.set(Calendar.SECOND,0);
        if(isJumuah(cal) && i==2) cal.add(Calendar.MINUTE,(-1)*context.getSharedPreferences(azanFile,Context.MODE_PRIVATE).getInt("notification_time_5",0));
        else cal.add(Calendar.MINUTE,(-1)*context.getSharedPreferences(azanFile,Context.MODE_PRIVATE).getInt("notification_time_"+(i>0?i-1:0),0));
        return cal;
    }
    public static Calendar getRemindTimeFor(Context context,int i) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY,Integer.parseInt(Times.times[i].substring(0,2)));
        cal.set(Calendar.MINUTE,Integer.parseInt(Times.times[i].substring(3,5)));
        cal.set(Calendar.SECOND,0);
        if(isJumuah(cal) && i==2) cal.add(Calendar.MINUTE,context.getSharedPreferences(iqamaFile,Context.MODE_PRIVATE).getInt("remind_time_5",Times.iqamaTimes[i]));
        else cal.add(Calendar.MINUTE,context.getSharedPreferences(iqamaFile,Context.MODE_PRIVATE).getInt("remind_time_"+(i>0?i-1:0),Times.iqamaTimes[i]));
        return cal;
    }
    public static boolean clearNotify(Context context, int type) {
        return context.getSharedPreferences(type==AZAN_REQUEST_CODE?azanFile:iqamaFile,Context.MODE_PRIVATE)
                .getBoolean(type==AZAN_REQUEST_CODE?"clear_notification":"clear_reminder",false);
    }
    public static long getClearTimeInMillis(Context context,int type) {
        Calendar cal=Calendar.getInstance();
        cal.add(Calendar.MINUTE,context.getSharedPreferences(type==AZAN_REQUEST_CODE?azanFile:iqamaFile,Context.MODE_PRIVATE)
                .getInt("clear_time",0));
        return cal.getTimeInMillis();
    }
    public static void firstTime(Context context){
        SharedPreferences azan = context.getSharedPreferences(azanFile,Context.MODE_PRIVATE);
        SharedPreferences.Editor azanEditor = azan.edit();
        SharedPreferences iqama = context.getSharedPreferences(iqamaFile,Context.MODE_PRIVATE);
        SharedPreferences.Editor iqamaEditor = iqama.edit();
        azanEditor.putBoolean("firsttime",false);
        iqamaEditor.putBoolean("firsttime",false);
        azanEditor.putInt("notification_type",1);
        iqamaEditor.putInt("reminder_type",1);
        for(int i=0;i<5;i++) iqamaEditor.putInt("remind_time_"+i,Times.iqamaTimes[i>0?i+1:0]);
        iqamaEditor.putInt("remind_time_5",Times.iqamaTimes[2]);
        azanEditor.commit();
        iqamaEditor.commit();
    }
    public static boolean isFirstTime(Context context){
        return context.getSharedPreferences(azanFile,Context.MODE_PRIVATE).getBoolean("firsttime",true);
    }

    public static boolean isVibrateActivated(Context context) {
        return context.getSharedPreferences(azanFile,Context.MODE_PRIVATE).getBoolean("vibrate",false);
    }

    public static boolean isLEDActivated(Context context) {
        return context.getSharedPreferences(azanFile,Context.MODE_PRIVATE).getBoolean("led",false);
    }
}
