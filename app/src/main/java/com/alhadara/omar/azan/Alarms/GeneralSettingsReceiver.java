package com.alhadara.omar.azan.Alarms;




import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.widget.Toast;


import androidx.core.app.AlarmManagerCompat;

import com.alhadara.omar.azan.Configurations;
import com.alhadara.omar.azan.Times;

import java.util.Calendar;


public class GeneralSettingsReceiver extends BroadcastReceiver {

    public static final int AZAN_REQUEST_CODE = 150;
    public static final int IQAMA_REQUEST_CODE = 160;
    private static final int GENERAL_ALARM_REQUEST_CODE = 133;
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "ALAZAN notifications activated", Toast.LENGTH_SHORT).show();


        Configurations.Update(context);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.SECOND,0);
        Intent in;
        for(int i=0;i<6;i++) {
            cal.set(Calendar.HOUR_OF_DAY,Integer.parseInt(Times.times[i].substring(0,2)));
            cal.set(Calendar.MINUTE,Integer.parseInt(Times.times[i].substring(3,5)));
            if(Configurations.isAlarmActivated(context,"azan",i) &&
                    Calendar.getInstance().getTimeInMillis() < cal.getTimeInMillis()) {
                in = new Intent(context, TimePrayerReceiver.class);
                in.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
                in.setAction(cal.toString());
                in.putExtra("type",0);
                in.putExtra("index",i);
                alarmTrig(context
                        ,PendingIntent.getBroadcast(context, AZAN_REQUEST_CODE + i, in, 0)
                        ,cal);
            }
           /* cal.add(Calendar.MINUTE,Integer.parseInt(Times.iqamaDiffTimes[i]));
            if(Configurations.isAlarmActivated(context,"iqama",i) && i!=1 No Alarm For Shorooq &&
                    Calendar.getInstance().getTimeInMillis() <= cal.getTimeInMillis()) {
                in = new Intent(context, TimePrayerReceiver.class);
                in.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
                in.setAction(cal.toString());
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
        in = new Intent(context,GeneralSettingsReceiver.class);
        in.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        PendingIntent generalSettingsIntent = PendingIntent.getBroadcast(context,GENERAL_ALARM_REQUEST_CODE, in,0);
        alarmTrig(context,generalSettingsIntent,cal);


    }

    private void alarmTrig(Context context,PendingIntent pendingIntent,Calendar calendar){
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        AlarmManagerCompat.setExactAndAllowWhileIdle(manager,AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);
    }
}