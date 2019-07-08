package com.alhadara.omar.azan;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.omar.azanapkmostafa.R;

public class TimePointSettings extends AppCompatActivity {

    private int index;
    private int upComingTimePoint;
    private int remainTime;
    private Handler handler;
    private int diffWithUpComingTimePoint;
    private MediaPlayer mediaPlayer;
    private int diff_h;
    private int diff_m;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_point_settings);
        Toolbar toolbar = findViewById(R.id.toolbar_time_point_settings);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        index=getIntent().getExtras().getInt("TimePointIndex");
        getSupportActionBar().setTitle("إعدادات صلاة "+ Constants.alias[index]);
        mediaPlayer = MediaPlayer.create(TimePointSettings.this,R.raw.azan_haram);

        widgetsAdder();
        handler = new Handler();
        startTimer();
    }

    public void widgetsAdder(){
        LinearLayout innerLayout = findViewById(R.id.time_point_settings_inner_layout);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        lp.setMargins(10,10,10,10);
        LinearLayout l = (LinearLayout) getLayoutInflater().inflate(R.layout.widget_settings_box,null);
         SwitchBox azan = new SwitchBox(this,"azan.txt", Integer.toString(index),
               true,"التنبيه وقت الأذان",true);
       /* azan.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer.isPlaying()){
               //     azan.imageButton.setImageResource(R.drawable.ic_stop_black_24dp);
                    mediaPlayer.stop();
                }else {
                //    azan.imageButton.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                    mediaPlayer.start();
                }
            }
        });*/
        l.addView(azan);
        l.setLayoutParams(lp);
        innerLayout.addView(l);
        l = (LinearLayout) getLayoutInflater().inflate(R.layout.widget_settings_box,null);
        SwitchBox iqama = new SwitchBox(this,"iqama.txt", Integer.toString(index),
                true,"التنبيه وقت الإقامة",false);
        l.addView(iqama);
        l.setLayoutParams(lp);
        innerLayout.addView(l);
    }



    public void firstInitialization(){
        upComingTimePoint = TM.commingTimePointIndex(Times.times);
        LinearLayout countdownlayout = findViewById(R.id.countdown_layout_time_point_settings);
        diffWithUpComingTimePoint = TM.difference(Times.times[upComingTimePoint],Times.times[index]);
        diff_h = (int)diffWithUpComingTimePoint /3600;
        diff_m = (int)((diffWithUpComingTimePoint-(diff_h*3600))/60);
        if(index >= upComingTimePoint) {

            countdownlayout.setVisibility(View.VISIBLE);
            ((TextView) countdownlayout.getChildAt(0)).setText(Constants.alias[index]);
        } else countdownlayout.setVisibility(View.GONE);
    }



    public void startTimer(){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int h,m,s;
                remainTime = TM.difference(TM.getTime(),Times.times[upComingTimePoint]);
                if(remainTime < 1) {
                    if(alarmActive("azan.txt",upComingTimePoint)) mediaPlayer.start();
                    firstInitialization();
                }else {

                    h = (int)remainTime /3600;
                    m = (int)((remainTime-(h*3600))/60);
                    s = (int)remainTime - (h*3600) - (m*60);
                    LinearLayout countdown = findViewById(R.id.countdown_timer_time_point_settings);
                    ((TextView)(countdown.getChildAt(0))).setText(Integer.toString(h + diff_h));
                    ((TextView)(countdown.getChildAt(2))).setText(Integer.toString(m + diff_m));
                    ((TextView)(countdown.getChildAt(4))).setText(Integer.toString(s));
                }
                handler.postDelayed(this,1000);
            }
        };
        firstInitialization();
        runnable.run();
    }

    boolean alarmActive(String alarmType, int index) {
        SharedPreferences pref;
        pref = this.getSharedPreferences(alarmType, Context.MODE_PRIVATE);
        return pref.getBoolean("index",true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        if(mediaPlayer.isPlaying()){
            mediaPlayer.stop();
        }else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        mediaPlayer.stop();
        super.onDestroy();
    }
}
