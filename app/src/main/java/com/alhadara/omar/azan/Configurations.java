package com.alhadara.omar.azan;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import com.alhadara.omar.azan.Locations._LocationSET;


import java.util.Locale;


public class Configurations {
    private static boolean reloadMainActivityOnResume = false;
    public static String displayFile = "display.txt";


    public static void setReloadMainActivityOnResume(boolean bool) {
        reloadMainActivityOnResume = bool;
    }
    public static boolean getReloadMainActivityOnResume() {
        return reloadMainActivityOnResume;
    }

    public static void initializeMainConfigurations(Activity ac){
        _LocationSET.checkCurrentLocation(ac);
        updateTimes(ac);
        setLanguagePreferences(ac);
        reloadMainActivityOnResume = false;
    }


    public static void setLanguagePreferences(Context context) {
        SharedPreferences pref = context.getSharedPreferences(displayFile,Context.MODE_PRIVATE);
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
        if(!_LocationSET.isLocationAssigned(context)) return;
        Times.initializeTimesForCurrent(context);
        reloadMainActivityOnResume = true;
    }

    public static void clearFile(Context context,String s) {
        SharedPreferences pref = context.getSharedPreferences(s,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
    }
}
