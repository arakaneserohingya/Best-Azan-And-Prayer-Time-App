package com.alhadara.omar.azan.Alarms;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.alhadara.omar.azan.Activities.MainActivity;
import com.alhadara.omar.azan.Constants;
import com.alhadara.omar.azan.Times;
import com.example.omar.azanapkmostafa.R;

import java.util.Calendar;


public class TimePrayerReceiver extends BroadcastReceiver {


    private String id;
    private Intent audioServiceIntent;
    @Override
    public void onReceive(Context context, Intent intent) {
        int type = intent.getExtras().getInt("type");
        int index = intent.getExtras().getInt("index");
        if(isTimeDiffers(type,index)) return;
        audioServiceIntent = new Intent(context,TimePrayerService.class);
        audioServiceIntent.putExtra("mode",true);
        context.startService(audioServiceIntent);
        String[] content = {"موعد صلاة","موعد الإقامة لصلاة"};

        if(type==0) id="azan";else id="iqama";
        sendNotification(context, content[type]+" "+ Constants.alias[index]);
    }

    private boolean isTimeDiffers(int type, int index) {
        Calendar calendar = Calendar.getInstance();
        long now = calendar.getTimeInMillis();
        now = now / 1000;
        calendar.set(Calendar.HOUR_OF_DAY,Integer.parseInt(Times.times[index].substring(0,2)));
        calendar.set(Calendar.MINUTE,Integer.parseInt(Times.times[index].substring(3,5)));
        calendar.set(Calendar.SECOND,0);
        //if(type !=0 ) calendar.add(Calendar.MINUTE,Integer.parseInt(Times.iqamaDiffTimes[index])); // for iqama
        long time = calendar.getTimeInMillis();
        time = time / 1000;
        return (now - time) > 30;
    }

    private void sendNotification(Context context,String msg) {
        NotificationManager alarmNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        //get pending intent
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        //Create notification

        if (Build.VERSION.SDK_INT >= 26) {

            NotificationChannel mChannel = alarmNotificationManager.getNotificationChannel(id);
            if (mChannel == null) {
                mChannel = new NotificationChannel(id, msg, NotificationManager.IMPORTANCE_HIGH);
                mChannel.enableVibration(true);
                alarmNotificationManager.createNotificationChannel(mChannel);
            }
        }
        NotificationCompat.Builder  builder = new NotificationCompat.Builder(context, id);

        audioServiceIntent.putExtra("mode",false);


        builder.setContentTitle(Constants.APP_NAME)
                .setSmallIcon(R.drawable.logo)
                .setContentText(msg)
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setContentIntent(contentIntent)
                .setTicker(Constants.APP_NAME)
                .addAction(R.drawable.ic_stop_black_24dp, context.getApplicationContext().getResources().getString(R.string.stop),PendingIntent.getService(
                        context,0,audioServiceIntent,PendingIntent.FLAG_CANCEL_CURRENT
                ));

        alarmNotificationManager.notify(Constants.APP_NOTIFICATION_ID, builder.build());
    }

}
