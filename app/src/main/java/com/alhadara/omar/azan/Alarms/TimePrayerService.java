package com.alhadara.omar.azan.Alarms;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.alhadara.omar.azan.Constants;

import java.io.IOException;

public class TimePrayerService extends Service implements MediaPlayer.OnCompletionListener,MediaPlayer.OnPreparedListener {
    private MediaPlayer player;
    private final SharedPreferences azanPref = getSharedPreferences(_AlarmSET.azanFile,MODE_PRIVATE);
    private final SharedPreferences iqamaPref = getSharedPreferences(_AlarmSET.iqamaFile,MODE_PRIVATE);

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(!checkDestroy(intent)) {
            player = new MediaPlayer();
            player.setOnPreparedListener(this);
            player.setOnCompletionListener(this);
            Uri uri = getUriFor(intent.getExtras().getInt("type"),intent.getExtras().getInt("index"));
            try {
                player.setDataSource(this,uri);
                player.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private boolean checkDestroy(Intent intent) {
        if (intent.getExtras().getBoolean("mode")) return false;
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(Constants.APP_NOTIFICATION_ID);
        onDestroy();
        return true;
    }

    @Override
    public void onDestroy() {
        onCompletion(null);
        super.onDestroy();
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        player.stop();
        stopSelf();
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        player.start();
    }

    private Uri getUriFor(int type, int index) {
        Uri uri = null;
        switch (type==_AlarmSET.AZAN_REQUEST_CODE?
        azanPref.getInt("notification_type",1):iqamaPref.getInt("reminder_type",1)){
            case 0:
                uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                break;
            case 1:
                uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                break;
            case 2:
                uri = getMultimediaUriFor(type,index);
                break;
            case 3:
                uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                break;
            default:
                uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                break;
        }
        return uri;
    }

    private Uri getMultimediaUriFor(int type, int index) {
        Uri uri = null;
        if(type==_AlarmSET.AZAN_REQUEST_CODE && azanPref.getBoolean("use_different",false)){
            uri = Uri.parse(azanPref.getString("uri"+(index>0?index-1:0),null)); //index 1 for sunrise
        }else if(type==_AlarmSET.AZAN_REQUEST_CODE){
            uri = Uri.parse(azanPref.getString("uri",null));
        }else{
            uri = Uri.parse(iqamaPref.getString("uri",null));
        }
        if(uri==null) uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        return uri;
    }
}
