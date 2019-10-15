package com.alhadara.omar.azan.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.alhadara.omar.azan.Alarms.SahoorAlarmSnoozeService;
import com.alhadara.omar.azan.Alarms._AlarmSET;
import com.alhadara.omar.azan.Configurations;
import com.alhadara.omar.azan.Times;
import com.example.omar.azanapkmostafa.R;
import com.github.msarhan.ummalqura.calendar.UmmalquraCalendar;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SahoorActivity extends AppCompatActivity implements MediaPlayer.OnPreparedListener {
    private MediaPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configurations.initializeMainConfigurations(this);
        setContentView(R.layout.activity_sahoor);
        ((AppCompatTextView) findViewById(R.id.sahoor_activity_fajr_time)).setText(getResources().getString(R.string.sahoor_activty_fajr_time_apex) + getFajrIn12());
        ((Button) findViewById(R.id.sahoor_activity_middle_ball)).setText((new SimpleDateFormat("hh:mm aa")).format(new Date()));
        initializeDateViews();
        startRingtonePlayer();
        keepScreenOn();
        findViewById(R.id.sahoor_activity_snooze_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snooze();
            }
        });
        findViewById(R.id.sahoor_activity_stop_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stop();
            }
        });
    }


    private void keepScreenOn() {
        final Window win= getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        win.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        if(_AlarmSET.isSahoorKeepScreenOn(this)){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }



    private void stop() {
        player.stop();
        player = null;
        SahoorAlarmSnoozeService.isStopped = true;
        finish();
    }

    private void snooze() {
        player.stop();
        player = null;
        SahoorAlarmSnoozeService.isSnoozed = true;
        finish();
    }

    private void startRingtonePlayer() {
        if(checkSilence()) return;
        if(player != null) return;
        player = new MediaPlayer();
        player.setOnPreparedListener(this);
        player.setLooping(true);
        Uri uri = Uri.parse(getSharedPreferences(_AlarmSET.sahoorFile,MODE_PRIVATE).getString("uri",
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM).toString()));
        setAudioAttributes();
        try {
            player.setDataSource(this,uri);
            player.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean checkSilence() {
        AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        if(am.getRingerMode() == AudioManager.RINGER_MODE_SILENT ||am.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE)
            return !_AlarmSET.isSahoorWorkOnSilent(this);
        return false;
    }

    private void setAudioAttributes() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();
            player.setAudioAttributes(attributes);
        }else{
            player.setAudioStreamType(AudioManager.STREAM_ALARM);
        }
    }

    private String getFajrIn12() {
        return (Integer.parseInt(Times.times[0].substring(0, 2))<13?
                Times.times[0].substring(0, 2):(Integer.parseInt(Times.times[0].substring(0, 2))-12)<10?
                "0" + (Integer.parseInt(Times.times[0].substring(0, 2))-12):
                Integer.toString(Integer.parseInt(Times.times[0].substring(0, 2))-12))
                + Times.times[0].substring(2, 5) + (Integer.parseInt(Times.times[0].substring(0, 2))>12?" PM":" AM");
    }

    public void initializeDateViews() {
        UmmalquraCalendar hijCal = new UmmalquraCalendar();
        Calendar cal = Calendar.getInstance();
        ((TextView) findViewById(R.id.sahoor_activity_hijri_month_number)).setText(Integer.toString(hijCal.get(Calendar.DAY_OF_MONTH)));
        ((TextView) findViewById(R.id.sahoor_activity_hijri_month_name)).setText(getResources().getStringArray(R.array.hijri_month)[hijCal.get(Calendar.MONTH)]);
        ((TextView) findViewById(R.id.sahoor_activity_gregorian_month_number)).setText(Integer.toString(cal.get(Calendar.DAY_OF_MONTH)));
        ((TextView) findViewById(R.id.sahoor_activity_gregorian_month_name)).setText(getResources().getStringArray(R.array.gregorian_month)[cal.get(Calendar.MONTH)]);
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        player.start();
    }
}