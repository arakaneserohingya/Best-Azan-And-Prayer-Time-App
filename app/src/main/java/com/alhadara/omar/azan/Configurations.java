package com.alhadara.omar.azan;

import android.app.Activity;
import android.app.AlarmManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Build;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.omar.azanapkmostafa.R;


import java.util.Locale;
import java.util.TimeZone;


public class Configurations {
    private static boolean reloadMainActivityOnResume = false;
    public static String mainConFile = "mainconfigurations.txt";
    private static String locationsFile = "locations.txt";
    public static boolean isAppActive = false;

    public static void setReloadMainActivityOnResume(boolean bool) {
        reloadMainActivityOnResume = bool;
    }
    public static boolean getReloadMainActivityOnResume() {
        return reloadMainActivityOnResume;
    }

    public static void initializeMainConfigurations(Activity context){
        if(isAppActive) return;
        isAppActive = true;
        setLocationPreferences(context);
        updateTimes(context);
        setLanguagePreferences(context);
        resolveConstants(context);
        reloadMainActivityOnResume = false;
    }

    private static void setLocationPreferences(final Activity activity) {
        SharedPreferences pref = activity.getSharedPreferences(mainConFile,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        if(!pref.getBoolean("islocationassigned",false)){
            /*Get LocationHandler from network api*/
            //
            LocationHandler locationHandler = new LocationHandler(activity, activity.getApplicationContext(),
                    new LocationHandler.LocationRunnable() {
                 @Override
                 public void doWithLocation(Location location, float timeZone,String LocatoinAddress) {
                     Toast.makeText(activity, "location is "
                             +location.getLatitude()+" "
                                     +location.getLongitude()+" "
                             +location.getTime()+" "
                             , Toast.LENGTH_SHORT).show();
                 }
             });

            if (true /*if api not succeed*/){
                editor.putString("location_name","Damascus, Syria");
                editor.putFloat("latitude",(float)33.513805);
                editor.putFloat("longitude",(float)36.276527);
                editor.putFloat("timezone",3);
            }
            editor.putBoolean("islocationassigned",true);
            editor.commit();
            showDialogLocationUpdateFail(activity,pref);
        }
    }
    private static void showDialogLocationUpdateFail(final Context context,SharedPreferences pref) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Update location from network failed!");
        builder.setMessage(pref.getString("location_name","") + " is set as current location!");
        builder.setCancelable(true);
        builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }
    private static void setLanguagePreferences(Context context) {
        SharedPreferences pref = context.getSharedPreferences(mainConFile,Context.MODE_PRIVATE);
        Configuration configuration = context.getResources().getConfiguration();
        Locale locale = new Locale(pref.getString("language","en"));
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN){
            configuration.setLocale(locale);
            configuration.setLayoutDirection(locale);
            context.getResources().updateConfiguration(configuration, context.getResources().getDisplayMetrics());
        }
    }
    private static void resolveConstants(Context context) {
        Constants.hijriMonthes = context.getResources().getStringArray(R.array.hijri_month);
        Constants.gregorianMonthes = context.getResources().getStringArray(R.array.gregorian_month);
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
    public static void updateTimes(Context context){
        SharedPreferences pref = context.getSharedPreferences(mainConFile,Context.MODE_PRIVATE);
        Times.initializeTimes(  pref.getFloat("latitude",0),
                                pref.getFloat("longitude",0),
                                pref.getFloat("timezone",0));
        adjustTimes(context);
        reloadMainActivityOnResume = true;
    }
    public static void setCurrentLocation(Context context,String location_name,float latitude,float longitude,float timezone) {
        SharedPreferences pref = context.getSharedPreferences(mainConFile,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("location_name",location_name);
        editor.putFloat("latitude",latitude);
        editor.putFloat("longitude",longitude);
        editor.putFloat("timezone",timezone);
        editor.commit();
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
