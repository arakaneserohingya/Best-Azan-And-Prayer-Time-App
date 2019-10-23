package com.alhadara.omar.azan;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.widget.Toast;

import com.example.omar.azanapkmostafa.R;


import java.util.Locale;


public class Configurations {
    private static boolean reloadMainActivityOnResume = false;
    public static String mainConFile = "mainconfigurations.txt";


    public static void setReloadMainActivityOnResume(boolean bool) {
        reloadMainActivityOnResume = bool;
    }
    public static boolean getReloadMainActivityOnResume() {
        return reloadMainActivityOnResume;
    }

    public static void initializeMainConfigurations(Context context){
        updateTimes(context);
        setLanguagePreferences(context);
        reloadMainActivityOnResume = false;
    }


    public static void setLanguagePreferences(Context context) {
        SharedPreferences pref = context.getSharedPreferences(mainConFile,Context.MODE_PRIVATE);
        Configuration configuration = context.getResources().getConfiguration();
        Locale locale = new Locale(pref.getString("language","en"));
        configuration.setLocale(locale);
        configuration.setLayoutDirection(locale);
        context.getResources().updateConfiguration(configuration, context.getResources().getDisplayMetrics());
    }
    public static boolean isAlarmActivated(Context context,String type, int index){
        return context.getSharedPreferences(type + ".txt",Context.MODE_PRIVATE)
                .getBoolean(Integer.toString(index),true);
    }
    public static void setAlarmActivated(Context context,String type,int index,boolean isActivated) {
        SharedPreferences pref = context.getSharedPreferences(type + ".txt",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(Integer.toString(index),isActivated);
        editor.commit();
    }
    public static void updateTimes(Context context){
        if(!isLocationAssigned(context)) return;
        Times.initializeTimesForCurrent(context);
        reloadMainActivityOnResume = true;
    }

    public static boolean isLocationAssigned(Context context){
        return context.getSharedPreferences(mainConFile,Context.MODE_PRIVATE).getBoolean("islocationassigned",false);
    }
    public static void setLocationAssigned(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(mainConFile,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("islocationassigned",true);
        editor.commit();
    }

    public static void clearFile(Context context,String s) {
        SharedPreferences pref = context.getSharedPreferences(s,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
    }
}
