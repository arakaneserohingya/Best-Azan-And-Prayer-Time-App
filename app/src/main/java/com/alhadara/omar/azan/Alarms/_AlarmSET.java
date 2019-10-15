package com.alhadara.omar.azan.Alarms;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.alhadara.omar.azan.Activities.SahoorActivity;
import com.alhadara.omar.azan.Times;

import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;

public class _AlarmSET {
    public static final String azanFile = "notifications.txt";
    public static final String iqamaFile = "reminders.txt";
    public static final String sahoorFile = "sahoor.txt";
    public static final int AZAN_REQUEST_CODE = 150;
    public static final int IQAMA_REQUEST_CODE = 160;
    public static final int SAHOOR_REQUEST_CODE = 140;
    public static final int SAHOOR_ALARM_TIME = -20;

    public static boolean notifyActivated(Context context) {
        return context.getSharedPreferences(azanFile, MODE_PRIVATE).getBoolean("notify",true);
    }
    public static boolean notifyActivatedFor(Context context,int i){
        if(isJumuah(Calendar.getInstance()) && i==2) return context.getSharedPreferences(azanFile, MODE_PRIVATE).getBoolean("notify_5",true);
        return context.getSharedPreferences(azanFile, MODE_PRIVATE).getBoolean("notify_"+(i>0?i-1:0),true);
    }

    public static boolean remindActivated(Context context) {
        return context.getSharedPreferences(iqamaFile, MODE_PRIVATE).getBoolean("remind",true);
    }
    public static boolean remindActivatedFor(Context context,int i){
        if(isJumuah(Calendar.getInstance()) && i==2) return context.getSharedPreferences(iqamaFile, MODE_PRIVATE).getBoolean("remind_5",true);
        return context.getSharedPreferences(iqamaFile, MODE_PRIVATE).getBoolean("remind_"+(i>0?i-1:0),true);
    }
    public static boolean sahoorActivated(Context context) {
        return context.getSharedPreferences(sahoorFile, MODE_PRIVATE).getBoolean("sahooralarm",false);
    }
    public static boolean isJumuah(Calendar cal) {
        return cal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY;
    }
    public static Calendar getNotifyTimeFor(Context context, int i) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY,Integer.parseInt(Times.times[i].substring(0,2)));
        cal.set(Calendar.MINUTE,Integer.parseInt(Times.times[i].substring(3,5)));
        cal.set(Calendar.SECOND,0);
        if(isJumuah(cal) && i==2) cal.add(Calendar.MINUTE,(-1)*context.getSharedPreferences(azanFile, MODE_PRIVATE).getInt("notification_time_5",0));
        else cal.add(Calendar.MINUTE,(-1)*context.getSharedPreferences(azanFile, MODE_PRIVATE).getInt("notification_time_"+(i>0?i-1:0),0));
        return cal;
    }
    public static Calendar getRemindTimeFor(Context context,int i) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY,Integer.parseInt(Times.times[i].substring(0,2)));
        cal.set(Calendar.MINUTE,Integer.parseInt(Times.times[i].substring(3,5)));
        cal.set(Calendar.SECOND,0);
        if(isJumuah(cal) && i==2) cal.add(Calendar.MINUTE,context.getSharedPreferences(iqamaFile, MODE_PRIVATE).getInt("remind_time_5",Times.iqamaTimes[i]));
        else cal.add(Calendar.MINUTE,context.getSharedPreferences(iqamaFile, MODE_PRIVATE).getInt("remind_time_"+(i>0?i-1:0),Times.iqamaTimes[i]));
        return cal;
    }
    public static Calendar getSahoorTime(Context context) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY,Integer.parseInt(Times.times[0].substring(0,2)));
        cal.set(Calendar.MINUTE,Integer.parseInt(Times.times[0].substring(3,5)));
        cal.set(Calendar.SECOND,0);
        cal.add(Calendar.MINUTE,context.getSharedPreferences(sahoorFile, MODE_PRIVATE).getInt("time",-20));
        return cal;
    }
    public static boolean clearNotify(Context context, int type) {
        return context.getSharedPreferences(type==AZAN_REQUEST_CODE?azanFile:iqamaFile, MODE_PRIVATE)
                .getBoolean(type==AZAN_REQUEST_CODE?"clear_notification":"clear_reminder",false);
    }
    public static long getClearTimeInMillis(Context context,int type) {
        Calendar cal=Calendar.getInstance();
        cal.add(Calendar.MINUTE,context.getSharedPreferences(type==AZAN_REQUEST_CODE?azanFile:iqamaFile, MODE_PRIVATE)
                .getInt("clear_time",0));
        return cal.getTimeInMillis();
    }
    public static void firstTime(Context context){
        SharedPreferences azan = context.getSharedPreferences(azanFile, MODE_PRIVATE);
        SharedPreferences.Editor azanEditor = azan.edit();
        SharedPreferences iqama = context.getSharedPreferences(iqamaFile, MODE_PRIVATE);
        SharedPreferences.Editor iqamaEditor = iqama.edit();
        SharedPreferences sahoor = context.getSharedPreferences(sahoorFile, MODE_PRIVATE);
        SharedPreferences.Editor sahoorEditor = sahoor.edit();
        azanEditor.putBoolean("firsttime",false);
        iqamaEditor.putBoolean("firsttime",false);
        azanEditor.putInt("notification_type",1);
        iqamaEditor.putInt("reminder_type",1);
        for(int i=0;i<5;i++) iqamaEditor.putInt("remind_time_"+i,Times.iqamaTimes[i>0?i+1:0]);
        iqamaEditor.putInt("remind_time_5",Times.iqamaTimes[2]);
        sahoorEditor.putInt("time",SAHOOR_ALARM_TIME);
        sahoorEditor.putInt("stop_time",20);
        sahoorEditor.putInt("snooze_time",5);
        azanEditor.commit();
        iqamaEditor.commit();
        sahoorEditor.commit();
    }
    public static boolean isFirstTime(Context context){
        return context.getSharedPreferences(azanFile, MODE_PRIVATE).getBoolean("firsttime",true);
    }

    public static boolean isVibrateActivated(Context context) {
        return context.getSharedPreferences(azanFile, MODE_PRIVATE).getBoolean("vibrate",false);
    }

    public static boolean isLEDActivated(Context context) {
        return context.getSharedPreferences(azanFile, MODE_PRIVATE).getBoolean("led",false);
    }

    public static boolean isSahoorVibrateActivated(Context context) {
        return context.getSharedPreferences(sahoorFile, MODE_PRIVATE).getBoolean("vibrate",true);
    }

    public static boolean isSahoorKeepScreenOn(Context context) {
        return context.getSharedPreferences(sahoorFile, MODE_PRIVATE).getBoolean("screen_on",true);
    }

    public static boolean isSahoorWorkOnSilent(Context context) {
        return context.getSharedPreferences(sahoorFile, MODE_PRIVATE).getBoolean("work_on_silent",true);
    }

    public static boolean isSahoorTimeUp(Context context) {
        if(context.getSharedPreferences(sahoorFile, MODE_PRIVATE).getBoolean("stop_automatically",false))
            return false;
        Calendar cal = Calendar.getInstance();
        Calendar fajr = _AlarmSET.getNotifyTimeFor(context,0);
        return (cal.getTimeInMillis() - fajr.getTimeInMillis()) > getSahoorStopTimeInMillis(context);
    }

    public static long getSahoorStopTimeInMillis(Context context) {
        return context.getSharedPreferences(sahoorFile, MODE_PRIVATE).getInt("stop_time",20)*60000;
    }

    public static long getSahoorSnoozeTimeInMillis(Context context) {
        return context.getSharedPreferences(_AlarmSET.sahoorFile,MODE_PRIVATE).getInt("snooze_time",5)*60000;
    }
}
