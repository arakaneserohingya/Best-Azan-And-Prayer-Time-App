package com.alhadara.omar.azan.Alarms;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.alhadara.omar.azan.Constants;
import com.example.omar.azanapkmostafa.R;

import java.io.IOException;

public class TimePrayerService extends Service implements MediaPlayer.OnCompletionListener,MediaPlayer.OnPreparedListener {
    private MediaPlayer player;
    private Handler handlerVibrateAndLed;
    private Runnable runnable;

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
            handleVibrateAndLed();
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
        handlerVibrateAndLed.removeCallbacks(runnable);
        stopSelf();
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        player.start();
    }

    private Uri getUriFor(int type, int index) {
        Uri uri = null;
        switch (type==_AlarmSET.AZAN_REQUEST_CODE?
                getSharedPreferences(_AlarmSET.azanFile,MODE_PRIVATE).getInt("notification_type",1):getSharedPreferences(_AlarmSET.iqamaFile,MODE_PRIVATE).getInt("reminder_type",1)){
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
        if(type==_AlarmSET.AZAN_REQUEST_CODE && getSharedPreferences(_AlarmSET.azanFile,MODE_PRIVATE).getBoolean("use_different",false)){
            uri = Uri.parse(getSharedPreferences(_AlarmSET.azanFile,MODE_PRIVATE).getString("uri"+(index>0?index-1:0),getDefaultMultimediaString(type))); //index 1 for sunrise
        }else if(type==_AlarmSET.AZAN_REQUEST_CODE){
            uri = Uri.parse(getSharedPreferences(_AlarmSET.azanFile,MODE_PRIVATE).getString("uri",getDefaultMultimediaString(type)));
        }else{
            uri = Uri.parse(getSharedPreferences(_AlarmSET.iqamaFile,MODE_PRIVATE).getString("uri",getDefaultMultimediaString(type)));
        }
        return uri;
    }
    private String getDefaultMultimediaString(int type){
        return "android.resource://"+getPackageName()+"/raw/" + (type==_AlarmSET.AZAN_REQUEST_CODE?"azan":"iqama");
    }

    private void handleVibrateAndLed() {
        handlerVibrateAndLed = new Handler();
        runnable = new Runnable() {
            Camera cam;
            @Override
            public void run() {
                if (_AlarmSET.isVibrateActivated(TimePrayerService.this)) {
                    Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                    } else {
                        v.vibrate(500);
                    }
                }
                /*if (_AlarmSET.isLEDActivated(TimePrayerService.this) && getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
                    if(cam == null) {
                        cam = Camera.open();
                        Camera.Parameters params = cam.getParameters();
                        params.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                        cam.setParameters(params);
                        cam.startPreview();
                        cam.autoFocus(new Camera.AutoFocusCallback() {
                            public void onAutoFocus(boolean success, Camera camera) {
                            }
                        });
                    }else{
                        cam.stopPreview();
                        cam.release();
                        cam = null;
                    }
                }*/
                handlerVibrateAndLed.postDelayed(this,1000);
            }
        };
        handlerVibrateAndLed.postDelayed(runnable,1000);
    }
}
