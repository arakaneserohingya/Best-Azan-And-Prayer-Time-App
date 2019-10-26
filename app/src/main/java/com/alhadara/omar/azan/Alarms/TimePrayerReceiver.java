package com.alhadara.omar.azan.Alarms;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.AlarmManagerCompat;
import androidx.core.app.NotificationCompat;

import com.alhadara.omar.azan.Activities.SahoorActivity;
import com.alhadara.omar.azan.Constants;
import com.example.omar.azanapkmostafa.R;

import java.util.Calendar;


public class TimePrayerReceiver extends BroadcastReceiver {


    private String id;
    @Override
    public void onReceive(Context context, Intent intent) {
        int type = intent.getExtras().getInt("type");
        int index = intent.getExtras().getInt("index");
        if(type == _AlarmSET.SAHOOR_REQUEST_CODE) startSahoor(context);
        else {
            startSoundService(context, type, index);
            String[] content = type == _AlarmSET.AZAN_REQUEST_CODE ?
                    context.getResources().getStringArray(R.array.prayer_time_notifications) :
                    context.getResources().getStringArray(R.array.iqama_time_notifications);
            id = (type == _AlarmSET.AZAN_REQUEST_CODE) ? "azan" : "iqama";
            sendNotification(context, content[index == 0 ? 0 : (_AlarmSET.isJumuah(Calendar.getInstance()) && index == 2) ? 5 : index - 1]);
            setCancellation(context, type);
        }
    }

    private void startSahoor(Context context) {
        Intent intent = new Intent(context,SahoorAlarmSnoozeService.class);
        context.startService(intent);
    }

    private void startSoundService(Context context, int type, int index) {
        Intent audioServiceIntent = new Intent(context,TimePrayerService.class);
        audioServiceIntent.putExtra("type",type);
        audioServiceIntent.putExtra("index",index);
        audioServiceIntent.putExtra("mode",true);
        context.startService(audioServiceIntent);
    }

    /*private boolean isTimeDiffers(int type, int index) {
        Calendar calendar = Calendar.getInstance();
        long now = calendar.getTimeInMillis();
        now = now / 1000;
        calendar.set(Calendar.HOUR_OF_DAY,Integer.parseInt(_TimesSET.times[index].substring(0,2)));
        calendar.set(Calendar.MINUTE,Integer.parseInt(_TimesSET.times[index].substring(3,5)));
        calendar.set(Calendar.SECOND,0);
        if(type != AlarmsScheduler.AZAN_REQUEST_CODE ) calendar.add(Calendar.MINUTE,_TimesSET.iqamaTimes[index]); // for iqama
        long time = calendar.getTimeInMillis();
        time = time / 1000;
        return (now - time) > 30;
    }*/

    private void sendNotification(Context context,String msg) {
        NotificationManager alarmNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= 26) {

            NotificationChannel mChannel = alarmNotificationManager.getNotificationChannel(id);
            if (mChannel == null) {
                mChannel = new NotificationChannel(id, msg, NotificationManager.IMPORTANCE_HIGH);
                mChannel.enableVibration(true);
                mChannel.setSound(null,null);
                alarmNotificationManager.createNotificationChannel(mChannel);
            }
        }
        NotificationCompat.Builder  builder = new NotificationCompat.Builder(context, id);
        builder.setContentTitle(Constants.APP_NAME)
                .setSmallIcon(R.drawable.logo)
                .setContentText(msg)
                .setAutoCancel(true)
                .setContentIntent(cancelIntent(context))
                .setTicker(Constants.APP_NAME)
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        alarmNotificationManager.notify(Constants.APP_NOTIFICATION_ID, builder.build());
    }

    private void setCancellation(Context context,int type){
        if(_AlarmSET.clearNotify(context,type)){
            AlarmManagerCompat.setExactAndAllowWhileIdle((AlarmManager) context.getSystemService(Context.ALARM_SERVICE),AlarmManager.RTC_WAKEUP,_AlarmSET.getClearTimeInMillis(context,type),cancelIntent(context));
        }
    }
    private PendingIntent cancelIntent(Context context){
        Intent intent = new Intent(context,TimePrayerService.class);
        intent.putExtra("mode",false);
        PendingIntent cIntent = PendingIntent.getService(
                context,0,intent,PendingIntent.FLAG_CANCEL_CURRENT
        );
        return  cIntent;
    }

}
