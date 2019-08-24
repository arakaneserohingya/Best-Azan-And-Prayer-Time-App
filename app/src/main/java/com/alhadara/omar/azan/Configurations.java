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

    public static void setReloadMainActivityOnResume(boolean bool) {
        reloadMainActivityOnResume = bool;
    }
    public static boolean getReloadMainActivityOnResume() {
        return reloadMainActivityOnResume;
    }

    public static void initializeMainConfigurations(Context context){
        SharedPreferences pref = context.getSharedPreferences("mainconfigurations.txt",Context.MODE_PRIVATE);
        Configuration configuration = context.getResources().getConfiguration();
        Locale locale = new Locale(pref.getString("language","en"));
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN){
            configuration.setLocale(locale);
            configuration.setLayoutDirection(locale);
            context.getResources().updateConfiguration(configuration, context.getResources().getDisplayMetrics());
        }
        resolveConstants(context);
    }

    private static void resolveConstants(Context context) {
        Constants.hijriMonthes = context.getResources().getStringArray(R.array.hijri_month);
        Constants.dayesOfWeek = context.getResources().getStringArray(R.array.day_of_week);
        Constants.alias = context.getResources().getStringArray(R.array.prayer_time);
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
    public static float getCurrentLatitude(Context context){
        return context.getSharedPreferences("currentlocation.txt",Context.MODE_PRIVATE)
                .getFloat("latitude",0);
    }

    public static float getCurrentLongitude(Context context){
        return context.getSharedPreferences("currentlocation.txt",Context.MODE_PRIVATE)
                .getFloat("longitude",0);
    }

    public static float getCurrentTimezone(Context context){
        return context.getSharedPreferences("currentlocation.txt",Context.MODE_PRIVATE)
                .getFloat("timezone",0);
    }
    public static void Update(Context context){
        SharedPreferences pref = context.getSharedPreferences("currentlocation.txt",Context.MODE_PRIVATE);
        Times.initializeTimes(  pref.getFloat("latitude",0),
                                pref.getFloat("longitude",0),
                                pref.getFloat("timezone",0));
        adjustTimes(context);
        reloadMainActivityOnResume = true;
    }
    public static void setCurrentLocation(Context context,float latitude,float longitude,float timezone) {
        SharedPreferences pref = context.getSharedPreferences("currentlocation.txt",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putFloat("latitude",latitude);
        editor.putFloat("longitude",longitude);
        editor.putFloat("timezone",timezone);
        editor.commit();
        Times.initializeTimes(latitude,longitude,timezone);
        adjustTimes(context);
        reloadMainActivityOnResume = true;
    }
    private static void adjustTimes(Context context){
        SharedPreferences delayTimesPref = context.getSharedPreferences("delaytime.txt",Context.MODE_PRIVATE);
        int delay;
        for (int i=0;i<6;i++) {
            delay = delayTimesPref.getInt(Integer.toString(i),0);
            if(delay != 0) {
                Toast.makeText(context, "تعديل وقت " + Constants.alias[i] + " بمقدار" + delay + " دقيقة", Toast.LENGTH_SHORT).show();
                Times.applyDelay(i,delay);
            }
        }
    }
    public static void resetTimeConfigurations(Context context){
        SharedPreferences pref = context.getSharedPreferences("delaytime.txt",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
        pref = context.getSharedPreferences("azan.txt",Context.MODE_PRIVATE);
        editor = pref.edit();
        editor.clear();
        editor.commit();
        pref = context.getSharedPreferences("iqama.txt",Context.MODE_PRIVATE);
        editor = pref.edit();
        editor.clear();
        editor.commit();
        pref = context.getSharedPreferences("currentlocation.txt",Context.MODE_PRIVATE);
        Times.initializeTimes(  pref.getFloat("latitude",0),
                pref.getFloat("longitude",0),
                pref.getFloat("timezone",0));
        reloadMainActivityOnResume = true;
    }
}
