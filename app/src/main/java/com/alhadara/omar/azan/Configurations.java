package com.alhadara.omar.azan;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.Build;
import android.widget.Toast;

import com.example.omar.azanapkmostafa.R;


import java.util.Locale;
import java.util.TimeZone;


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


    private static void setLanguagePreferences(Context context) {
        SharedPreferences pref = context.getSharedPreferences(mainConFile,Context.MODE_PRIVATE);
        Configuration configuration = context.getResources().getConfiguration();
        Locale locale = new Locale(pref.getString("language","ar"));
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN){
            configuration.setLocale(locale);
            configuration.setLayoutDirection(locale);
            context.getResources().updateConfiguration(configuration, context.getResources().getDisplayMetrics());
        }
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
        adjustTimes(context);
        reloadMainActivityOnResume = true;
    }
    private static void adjustTimes(Context context){
        SharedPreferences delayTimesPref = context.getSharedPreferences("delaytime.txt",Context.MODE_PRIVATE);
        int delay;
        for (int i=0;i<6;i++) {
            delay = delayTimesPref.getInt(Integer.toString(i),0);
            if(delay != 0) {
                Toast.makeText(context, "تعديل وقت " + context.getResources().getStringArray(R.array.prayer_time)[i] + " بمقدار" + delay + " دقيقة", Toast.LENGTH_SHORT).show();
                Times.applyDelay(i,delay);
            }
        }
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
