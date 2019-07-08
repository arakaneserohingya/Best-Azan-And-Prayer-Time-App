package com.alhadara.omar.azan;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

class Times {

    static String[] times = {"03:34","05:28","12:38","16:22","19:49","21:29"};
    static void initializeTimes(double latitude,double longitude,double timezone ){
        PrayTime prayers = new PrayTime();

        prayers.setTimeFormat(prayers.Time24);
        prayers.setCalcMethod(prayers.MWL);
        prayers.setAsrJuristic(prayers.Shafii);
        prayers.setAdjustHighLats(prayers.AngleBased);
        int[] offsets = {0, 0, 0, 0, 0, 0, 0}; // {Fajr,Sunrise,Dhuhr,Asr,Sunset,Maghrib,Isha}
        prayers.tune(offsets);

        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);

        ArrayList<String> prayerTimes = prayers.getPrayerTimes(cal,
                latitude, longitude, timezone);
        ArrayList<String> prayerNames = prayers.getTimeNames();

        System.out.println(now);
        System.out.println(cal);
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < prayerTimes.size(); i++) {
            System.out.println(prayerNames.get(i) + " - " + prayerTimes.get(i));
            res.append(prayerNames.get(i) + " - " + prayerTimes.get(i)+"\n");
        }
        times[0]=prayerTimes.get(0);
        times[1]=prayerTimes.get(1);
        times[2]=prayerTimes.get(2);
        times[3]=prayerTimes.get(3);
        times[4]=prayerTimes.get(5);
        times[5]=prayerTimes.get(6);
    }
}