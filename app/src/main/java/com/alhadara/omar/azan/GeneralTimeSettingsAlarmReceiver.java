package com.alhadara.omar.azan;




import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;


import com.alhadara.omar.azan.Activities.MainActivity;

import java.util.Calendar;


public class GeneralTimeSettingsAlarmReceiver extends BroadcastReceiver {

    public static int AZAN_REQUEST_CODE = 150;
    public static int IQAMA_REQUEST_CODE = 160;
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "ALAZAN notifications activated", Toast.LENGTH_SHORT).show();


        Times.initializeTimes(MainActivity.CURRENT_LATITUDE,MainActivity.CURRENT_LONGITUDE,MainActivity.CURRENT_TIMEZONE);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.SECOND,0);
        Intent in = new Intent(context, TimePrayerAlarmReceiver.class);
        for(int i=0;i<6;i++) {
            cal.set(Calendar.HOUR_OF_DAY,Integer.parseInt(Times.times[i].substring(0,2)));
            cal.set(Calendar.MINUTE,Integer.parseInt(Times.times[i].substring(3,5)));
            if(MainActivity.isAlarmActivated(context,"azan",i) &&
                    Calendar.getInstance().getTimeInMillis() < cal.getTimeInMillis()) {
                in.putExtra("type",0);
                in.putExtra("index",i);
                alarmTrig(context
                        ,PendingIntent.getBroadcast(context, AZAN_REQUEST_CODE + i, in, 0)
                        ,cal);
            }
           /* cal.add(Calendar.MINUTE,Integer.parseInt(Times.iqamaDiffTimes[i]));
            if(MainActivity.isAlarmActivated(context,"iqama",i) && i!=1 No Alarm For Shorooq &&
                    Calendar.getInstance().getTimeInMillis() <= cal.getTimeInMillis()) {
                in.putExtra("type",1);
                in.putExtra("index",i);
                alarmTrig(context
                        ,PendingIntent.getBroadcast(context, IQAMA_REQUEST_CODE + i, in, 0)
                        ,cal);
            }*/
        }

        //Activate this for tomorrow

        cal.add(Calendar.DATE,1);
        cal.set(Calendar.HOUR_OF_DAY,0);
        cal.set(Calendar.MINUTE,1);
        alarmTrig(context,MainActivity.generalTimeSettingIntent,cal);


    }
    private void alarmTrig(Context context,PendingIntent pendingIntent,Calendar calendar){
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= 23) {
            manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else if (Build.VERSION.SDK_INT >= 19) {
            manager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else {
            manager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }
}