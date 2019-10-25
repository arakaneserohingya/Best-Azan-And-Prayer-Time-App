package com.alhadara.omar.azan;



import android.content.Context;
import android.widget.Toast;

import com.alhadara.omar.azan.Display._DisplaySET;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class TM {

    public static int getHours24(String time){
        return Integer.parseInt(time.substring(0,2));
    }
    public static int getHours12(String time) {
        int i = Integer.parseInt(time.substring(0, 2));
        if(i == 0) i = 12;
        else if(i>12) i = i-12;
        return i;
    }
    public static int getMinute(String time) {
        return Integer.parseInt(time.substring(3,5));
    }
    public static int getSecond(String time) {
        if(time.length() < 6) return 0;
        else return Integer.parseInt(time.substring(6,8));
    }

    public static int difference(String time1, String time2) {
        String dif = "";
        int res,h,m,s;
        int sec2 = (getHours24(time2)*3600) + (getMinute(time2)*60) + getSecond(time2);
        int sec1 = (getHours24(time1)*3600) + (getMinute(time1)*60) + getSecond(time1);
        if(sec1 > sec2) res = (86400 - sec1) + sec2;
        else res = sec2 - sec1;
        return res;
    }
    public static int commingTimePointIndex(String[] times){
        String time = TM.getTime();
        for(int i=0;i<6;++i) {
            if(TM.getHours24(time) < TM.getHours24(times[i])) return i;
            else if (TM.getHours24(time) == TM.getHours24(times[i]) &&
                    TM.getMinute(time) < TM.getMinute(times[i])) return i;
        }
        return 0;
    }
    public static String getTime(){
        DateFormat dFormat = new SimpleDateFormat("HH:mm:ss");
        return dFormat.format(Calendar.getInstance().getTime());
    }
    public static String getPrayerTimeString(Context context,int i){
        if(_DisplaySET.isTime24(context)) return Times.times[i];
        else return (Integer.parseInt(Times.times[i].substring(0, 2))<13?
                Times.times[i].substring(0, 2):(Integer.parseInt(Times.times[i].substring(0, 2))-12)<10?
                "0" + (Integer.parseInt(Times.times[i].substring(0, 2))-12):
                Integer.toString(Integer.parseInt(Times.times[i].substring(0, 2))-12))
                + Times.times[i].substring(2, 5);
    }
    public static String getPrayerPhaseString(Context context,int i){
        if(_DisplaySET.isTime24(context)) return "";
        else return Integer.parseInt(Times.times[i].substring(0, 2))>12?"PM":"AM";
    }
}
