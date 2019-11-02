package com.alhadara.omar.azan.Alarms;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.Toast;

import androidx.core.app.AlarmManagerCompat;

import com.example.omar.azanapkmostafa.R;

import java.util.Calendar;

public class SilentTimeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int index = intent.getExtras().getInt("index");
        String type = intent.getExtras().getString("type");
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if(type.equals("fire") && (am.getRingerMode()==AudioManager.RINGER_MODE_NORMAL)){
            vibrate(context);
            showMsg(context);
            switchPhone(context);
            backNormal(context,index,_AlarmSET.getSilentWakeFor(context,index));
        }else if(type.equals("backNormal") && (am.getRingerMode()!=AudioManager.RINGER_MODE_NORMAL)){
            vibrate(context);
            am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        }
    }

    private void switchPhone(Context context) {
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if(_AlarmSET.isSilentVibrateActivated(context)) am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
        else    am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
    }

    private void showMsg(Context context) {
        if(_AlarmSET.isOnSilentMsgActivated(context))
            Toast.makeText(context,context.getResources().getString(R.string.the_device_is_set_on_silent_mode),Toast.LENGTH_SHORT).show();
    }

    public static void fire(Context context, int index, Calendar calendar,boolean trig){
        Intent in = new Intent(context, SilentTimeReceiver.class);
        in.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        in.putExtra("index",index);
        in.putExtra("type","fire");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, _AlarmSET.SILENT_REQUEST_CODE + index, in, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if(trig) AlarmManagerCompat.setExactAndAllowWhileIdle(manager,AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);
        else manager.cancel(pendingIntent);
    }
    private void backNormal(Context context,int index, Calendar calendar){
        Intent in = new Intent(context, SilentTimeReceiver.class);
        in.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        in.putExtra("index",index);
        in.putExtra("type","backNormal");
        in.setAction(Long.toString(System.currentTimeMillis())); // setAction is necessary to differentiate pendingIntents
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, in, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        AlarmManagerCompat.setExactAndAllowWhileIdle(manager,AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);
    }

    private void vibrate(Context context) {
        if (_AlarmSET.isOnSilentVibrateActivated(context)) {
            Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                v.vibrate(500);
            }
        }
    }
}
