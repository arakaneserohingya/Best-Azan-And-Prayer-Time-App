package com.alhadara.omar.azan.Times;


import android.content.Context;
import android.content.SharedPreferences;

import com.alhadara.omar.azan.Activities.MainActivity;
import com.alhadara.omar.azan.Display._DisplaySET;
import com.alhadara.omar.azan.Locations._LocationSET;
import com.github.msarhan.ummalqura.calendar.UmmalquraCalendar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static android.content.Context.MODE_PRIVATE;

public class _TimesSET {

    public final static String prayersFile = "prayer_times.txt";
    public final static String adjustTimesFile = "adjust_times.txt";

    public static String[] times = {"03:34","05:28","12:38","16:22","19:49","21:29"};
    public static int[] iqamaTimes = {30,0,20,20,5,5};
    public static void initializeTimesForCurrent(Context context){
        times = initializeTimesFor(context, _LocationSET.currentLocation,Calendar.getInstance());
    }
    public static String[] initializeTimesFor(Context context,String locationFile, Calendar time){
        firsttime(context);
        String[] str = {"","","","","",""};
        SharedPreferences pref = context.getSharedPreferences(locationFile,MODE_PRIVATE);
        float latitude = pref.getFloat("latitude",0);
        float longitude = pref.getFloat("longitude",0);
        float timezone = pref.getFloat("timezone",0);
        PrayTime prayers = new PrayTime();

        prayers.setTimeFormat(prayers.Time24);
        prayers.setCalcMethod(getCalcMethod(context,prayers));
        prayers.setAsrJuristic(getAsrJuristic(context));
        prayers.setAdjustHighLats(getAdjustHighLats(context,prayers));
        int[] offsets = {getAdjust(context,0) + getDstInMinutes(context),
                            getAdjust(context,1) + getDstInMinutes(context),
                            getAdjust(context,2) + getDstInMinutes(context),
                            getAdjust(context,3) + getDstInMinutes(context),
                            getAdjust(context,4) + getDstInMinutes(context),
                            getAdjust(context,5) + getDstInMinutes(context),
                            getAdjust(context,6) + getDstInMinutes(context) + (adjustIshaRamadan(context,prayers)?30:0)
        }; // {Fajr,Sunrise,Dhuhr,Asr,Sunset,Maghrib,Isha}
        prayers.tune(offsets);

        ArrayList<String> prayerTimes = prayers.getPrayerTimes(time,
                latitude, longitude, timezone);
        ArrayList<String> prayerNames = prayers.getTimeNames();

        StringBuilder res = new StringBuilder();
        for (int i = 0; i < prayerTimes.size(); i++) {
            System.out.println(prayerNames.get(i) + " - " + prayerTimes.get(i));
            res.append(prayerNames.get(i) + " - " + prayerTimes.get(i)+"\n");
        }
        str[0]=prayerTimes.get(0);
        str[1]=prayerTimes.get(1);
        str[2]=(isJumuahTimeDiff(context)&&time.get(Calendar.DAY_OF_WEEK)==Calendar.FRIDAY)?
                getJumuahTime(context):prayerTimes.get(2);
        str[3]=prayerTimes.get(3);
        str[4]=prayerTimes.get(5);
        str[5]=prayerTimes.get(6);
        return str;
    }
    public static String getJumuahTime(Context context) {
        return context.getSharedPreferences(prayersFile,MODE_PRIVATE).getString("jumuah_time","11:00");
    }
    public static void setJumuahTime(Context context,int i, int i1) {
        SharedPreferences preferences = context.getSharedPreferences(prayersFile,MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("jumuah_time",i+":"+i1);
        editor.commit();
    }
    public static boolean isJumuahTimeDiff(Context context) {
        return context.getSharedPreferences(prayersFile,MODE_PRIVATE).getBoolean("jumuah_different",false);
    }
    public static void setJumuahDifferent(Context context, boolean b) {
        SharedPreferences preferences = context.getSharedPreferences(prayersFile,MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("jumuah_different",b);
        editor.commit();
    }

    private static int getDstInMinutes(Context context) {
        return context.getSharedPreferences(prayersFile,MODE_PRIVATE).getInt("dst",0)*60;
    }

    private static boolean adjustIshaRamadan(Context context, PrayTime prayers) {
        UmmalquraCalendar calendar = new UmmalquraCalendar();
        return context.getSharedPreferences(prayersFile, MODE_PRIVATE).getInt("method", prayers.MWL) == prayers.Makkah
                && context.getSharedPreferences(prayersFile, MODE_PRIVATE).getBoolean("adjust_isha_in_ramadan", false)
                && calendar.get(Calendar.MONTH) == UmmalquraCalendar.RAMADHAN;
    }

    private static int getAdjust(Context context, int i) {
        return context.getSharedPreferences(adjustTimesFile,MODE_PRIVATE).getInt("adjust_"+i,0);
    }

    private static int getCalcMethod(Context context, PrayTime time){
        return context.getSharedPreferences(prayersFile,MODE_PRIVATE).getInt("method",time.MWL);
    }
    private static int getAdjustHighLats(Context context, PrayTime prayers) {
        if(context.getSharedPreferences(prayersFile,MODE_PRIVATE).getInt("method",prayers.MWL) == 7)
            return prayers.OneSeventh;
        else if(context.getSharedPreferences(prayersFile,MODE_PRIVATE).getInt("method",prayers.MWL) == 4)
            return prayers.None;
        return context.getSharedPreferences(prayersFile,MODE_PRIVATE).getBoolean("angle_based_method",false)?
                prayers.AngleBased:prayers.None;
    }
    private static int getAsrJuristic(Context context) {
        return context.getSharedPreferences(prayersFile,MODE_PRIVATE).getInt("asr_method",0);
    }
    private static void firsttime(Context context){
        SharedPreferences preferences = context.getSharedPreferences(prayersFile,MODE_PRIVATE);
        SharedPreferences adjust = context.getSharedPreferences(adjustTimesFile,MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        SharedPreferences.Editor adjustEditor = adjust.edit();
        if(!preferences.getBoolean("firsttime",true)) return;
        editor.putBoolean("firsttime",false);
        editor.putInt("method",3);
        editor.putInt("asr_method",0);
        for (int i=0;i<6;i++) adjustEditor.putInt("adjust_"+i,0);
        editor.putInt("dst",0);
        editor.putString("jumuah_time","11:00");
        editor.commit();
        adjustEditor.commit();
    }


    public static void updateTimes(Context context){
        if(!_LocationSET.isLocationAssigned(context)) return;
        initializeTimesForCurrent(context);
        MainActivity.reloadMainActivityOnResume = true;
    }

    public static int commingTimePointIndex(){
        int i = 0;
        while(System.currentTimeMillis() > getPrayerTimeMillis(i)) i++;
        return i;
    }

    public static String getPrayerTimeString(Context context, int i){
        if(_DisplaySET.isTime24(context)) return times[i];
        else return (Integer.parseInt(times[i].substring(0, 2))<13?
                times[i].substring(0, 2):(Integer.parseInt(times[i].substring(0, 2))-12)<10?
                "0" + (Integer.parseInt(times[i].substring(0, 2))-12):
                Integer.toString(Integer.parseInt(times[i].substring(0, 2))-12))
                + times[i].substring(2, 5);
    }

    public static String getPrayerPhaseString(Context context, int i){
        if(_DisplaySET.isTime24(context)) return "";
        else return Integer.parseInt(times[i].substring(0, 2))>12?"PM":"AM";
    }

    public static long getPrayerTimeMillis(int i) {
       Calendar cal = new GregorianCalendar();
       cal.set(Calendar.HOUR_OF_DAY,Integer.parseInt(times[i].substring(0, 2)));
       cal.set(Calendar.MINUTE,Integer.parseInt(times[i].substring(3, 5)));
       cal.set(Calendar.SECOND,0);
       return cal.getTimeInMillis();
    }
}