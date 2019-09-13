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
    private MediaPlayer azanPlayer,iqamaPlayer;


    @Override
    public void onCreate() {
        super.onCreate();
        azanPlayer = MediaPlayer.create(this, R.raw.azan);
        iqamaPlayer = MediaPlayer.create(this,R.raw.iqama);
        azanPlayer.setOnCompletionListener(this);
        iqamaPlayer.setOnCompletionListener(this);

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent.getExtras().getBoolean("mode")) {
            if(intent.getExtras().getInt("type") == AlarmsScheduler.AZAN_REQUEST_CODE) azanPlayer.start();
            else iqamaPlayer.start();
        }
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
        this.azanPlayer.stop();
        this.iqamaPlayer.stop();
        stopSelf();
    }
}
