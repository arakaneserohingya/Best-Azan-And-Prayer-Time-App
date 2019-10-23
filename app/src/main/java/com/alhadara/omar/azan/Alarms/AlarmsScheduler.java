package com.alhadara.omar.azan.Alarms;




import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.widget.Toast;


import androidx.core.app.AlarmManagerCompat;

import com.alhadara.omar.azan.Configurations;
import com.alhadara.omar.azan.Locations._LocationSET;

import java.util.Calendar;


public class AlarmsScheduler extends BroadcastReceiver {


    private static final int GENERAL_ALARM_REQUEST_CODE = 133;



    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "ALAZAN notifications activated", Toast.LENGTH_SHORT).show();

        if(_AlarmSET.isFirstTime(context)) _AlarmSET.firstTime(context);
        Configurations.updateTimes(context);
        Calendar cal;
        for(int i=0;i<6;i++) {
            if(i==1) continue; // No notifications & reminders for Sunrise...

            cal = _AlarmSET.getNotifyTimeFor(context,i);
            if(_AlarmSET.notifyActivated(context) && _AlarmSET.notifyActivatedFor(context,i) &&
                    System.currentTimeMillis() < cal.getTimeInMillis()) {
                alarmTrig(context, _AlarmSET.AZAN_REQUEST_CODE, i, cal, true);
            } else alarmTrig(context,_AlarmSET.AZAN_REQUEST_CODE,i,cal,false);

            cal = _AlarmSET.getRemindTimeFor(context,i);
            if(_AlarmSET.remindActivated(context)&&_AlarmSET.remindActivatedFor(context,i) &&
                    Calendar.getInstance().getTimeInMillis() <= cal.getTimeInMillis())
                alarmTrig(context,_AlarmSET.IQAMA_REQUEST_CODE,i,cal,true);
            else alarmTrig(context,_AlarmSET.IQAMA_REQUEST_CODE,i,cal,false);

            cal = _AlarmSET.getSilentTimeFor(context,i);
            if(_AlarmSET.silentActivated(context)&&_AlarmSET.silentActivatedFor(context,i) &&
                    Calendar.getInstance().getTimeInMillis() <= cal.getTimeInMillis())
                SilentTimeReceiver.fire(context,i,cal,true);
            else SilentTimeReceiver.fire(context,i,cal,false);;

        }
        cal = _AlarmSET.getSahoorTime(context);
        if(_AlarmSET.sahoorActivated(context) && Calendar.getInstance().getTimeInMillis() <= cal.getTimeInMillis()){
            alarmTrig(context,_AlarmSET.SAHOOR_REQUEST_CODE,0,cal,true);
        } else alarmTrig(context,_AlarmSET.SAHOOR_REQUEST_CODE,0,cal,false);

        //Activate this for tomorrow
        cal = Calendar.getInstance();
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
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, type + index, in, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if(trig) AlarmManagerCompat.setExactAndAllowWhileIdle(manager,AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);
        else manager.cancel(pendingIntent);
    }
    public static void fire(Context context,Calendar cal){
        if(!_LocationSET.isLocationAssigned(context)) return;
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