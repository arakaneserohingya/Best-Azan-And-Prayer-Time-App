package com.alhadara.omar.azan.Alarms;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.alhadara.omar.azan.Constants;
import com.example.omar.azanapkmostafa.R;

public class TimePrayerService extends Service implements MediaPlayer.OnCompletionListener {
    private MediaPlayer mediaPlayer;


    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = MediaPlayer.create(this, R.raw.azan_haram);
        mediaPlayer.setOnCompletionListener(this);

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent.getExtras().getBoolean("mode")) mediaPlayer.start();
        else {
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(Constants.APP_NOTIFICATION_ID);
            onDestroy();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        onCompletion(null);
        super.onDestroy();
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        this.mediaPlayer.stop();
        stopSelf();
    }
}