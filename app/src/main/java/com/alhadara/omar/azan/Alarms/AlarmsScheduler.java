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


public class AlarmsScheduler extends BroadcastReceiver {


    public static final int AZAN_REQUEST_CODE = 150;
    public static final int IQAMA_REQUEST_CODE = 160;
    private static final int GENERAL_ALARM_REQUEST_CODE = 133;
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "ALAZAN notifications activated", Toast.LENGTH_SHORT).show();


        Configurations.updateTimes(context);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.SECOND,0);
        for(int i=0;i<6;i++) {
            cal.set(Calendar.HOUR_OF_DAY,Integer.parseInt(Times.times[i].substring(0,2)));
            cal.set(Calendar.MINUTE,Integer.parseInt(Times.times[i].substring(3,5)));
            cal.set(Calendar.SECOND,0);
            if(Configurations.isAlarmActivated(context,"azan",i) &&
                    Calendar.getInstance().getTimeInMillis() < cal.getTimeInMillis())
                alarmTrig(context,AZAN_REQUEST_CODE,i,cal,true);
            else alarmTrig(context,AZAN_REQUEST_CODE,i,cal,false);
            
            cal.add(Calendar.MINUTE, Times.iqamaTimes[i]);
            if(Configurations.isAlarmActivated(context,"iqama",i) && i!=1 /* No iqama for shorooq*/ &&
                    Calendar.getInstance().getTimeInMillis() <= cal.getTimeInMillis())
                alarmTrig(context,IQAMA_REQUEST_CODE,i,cal,true);
            else alarmTrig(context,IQAMA_REQUEST_CODE,i,cal,false);
        }

        //Activate this for tomorrow
        cal.add(Calendar.DATE,1);
        cal.set(Calendar.HOUR_OF_DAY,0);
        cal.set(Calendar.MINUTE,1);
        fire(context,cal);


    }
    private void alarmTrig(Context context,int type,int index,Calendar calendar,boolean trig){
        Intent in = new Intent(context, TimePrayerReceiver.class);
        in.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        in.putExtra("type",type);
        in.putExtra("index",index);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, type + index, in, 0);
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if(trig) AlarmManagerCompat.setExactAndAllowWhileIdle(manager,AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);
        else manager.cancel(pendingIntent);
    }
    public static void fire(Context context,Calendar cal){
        Intent in = new Intent(context, AlarmsScheduler.class);
        cal.add(Calendar.SECOND,3);
        in.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        PendingIntent alarmsSchedulerIntent = PendingIntent.getBroadcast(context,GENERAL_ALARM_REQUEST_CODE, in,0);
        AlarmManagerCompat.setExactAndAllowWhileIdle(
                (AlarmManager) context.getSystemService(Context.ALARM_SERVICE),
                AlarmManager.RTC_WAKEUP,
                cal.getTimeInMillis(),
                alarmsSchedulerIntent
        );
    }
}