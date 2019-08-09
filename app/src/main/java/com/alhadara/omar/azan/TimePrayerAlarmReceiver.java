package com.alhadara.omar.azan;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.alhadara.omar.azan.Activities.MainActivity;
import com.example.omar.azanapkmostafa.R;


public class TimePrayerAlarmReceiver extends BroadcastReceiver {


    private String id;
    @Override
    public void onReceive(Context context, Intent intent) {
        MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.azan_haram);
        mediaPlayer.start();
        String[] content = {"موعد صلاة","موعد الإقامة لصلاة"};
        int type = intent.getExtras().getInt("type");
        if(type==0) id="azan";else id="iqama";
        sendNotification(context, content[type]+" "+ Constants.alias[intent.getExtras().getInt("index")]);
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
                mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                alarmNotificationManager.createNotificationChannel(mChannel);
            }
        }
        NotificationCompat.Builder  builder = new NotificationCompat.Builder(context, id);

        builder.setContentTitle(Constants.APP_NAME)
                .setSmallIcon(android.R.drawable.ic_popup_reminder)
                .setContentText(msg)
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setContentIntent(contentIntent)
                .setTicker(Constants.APP_NAME)
                .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});

        alarmNotificationManager.notify(0, builder.build());
    }
}
