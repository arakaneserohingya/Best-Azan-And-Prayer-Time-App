package com.alhadara.omar.azan;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.omar.azanapkmostafa.R;
import com.google.android.material.navigation.NavigationView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TimePoint[] timePoint;
    private int upComingTimePoint;
    private int remainTime;
    private Handler handler;
    private MediaPlayer mediaPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(TM.hijriDateByFormat("D M Y",true,true));

        backgroundAnimation();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        timePoint = new TimePoint[6];
        mediaPlayer = MediaPlayer.create(MainActivity.this,R.raw.azan_haram);
        Times.initializeTimes(33.513805,36.276527,3);
        Times.applyDelayPreferences(this);
        initializeTimePoints();
        handler = new Handler();
        upComingTimePoint = TM.commingTimePointIndex(Times.times);
        startTimer();
    }

    @Override
    public void onBackPressed() {
        if(mediaPlayer.isPlaying()){
            mediaPlayer.stop();
        }else {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void initializeTimePoints(){
        ViewGroup timePointLayout = findViewById(R.id.time_point_layout);

        for(int i=0;i<6;i++) {
            final int s = i;
            timePoint[i] = new TimePoint(this);
            timePoint[i].setAttributes(i,Times.times);
            ((LinearLayout)timePoint[i].findViewById(R.id.time_point_inner)).setOnClickListener(new View.OnClickListener() { // timePoint[i] has multiple views including countdown timer
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(MainActivity.this,TimePointSettings.class);
                    intent.putExtra("TimePointIndex",s);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_top,R.anim.slide_out_top);
                }
            });
            timePointLayout.addView(timePoint[i]);
        }
    }


    public void backgroundAnimation() {
        AnimationDrawable animation = new AnimationDrawable();
        animation.addFrame(getResources().getDrawable(R.drawable.b1), Constants.BACKGROUND_DURATION);
        animation.addFrame(getResources().getDrawable(R.drawable.b2), Constants.BACKGROUND_DURATION);
        animation.addFrame(getResources().getDrawable(R.drawable.b3), Constants.BACKGROUND_DURATION);
        animation.setOneShot(false);

        ImageView imageAnim =  (ImageView) findViewById(R.id.background);
        imageAnim.setImageDrawable(animation);

        // start the animation!
        animation.start();
    }
    public void startTimer(){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int h,m,s;
                remainTime = TM.difference(TM.getTime(),Times.times[upComingTimePoint]);
                if(remainTime < 1) {
                    if(alarmActive("azan.txt",upComingTimePoint))mediaPlayer.start();
                    timePoint[upComingTimePoint].meComming(false);
                    upComingTimePoint = TM.commingTimePointIndex(Times.times);
                    timePoint[upComingTimePoint].meComming(true);
                }else {
                    int goneTimePoint = upComingTimePoint -1;
                    if(goneTimePoint < 0) goneTimePoint = 5;
                    h = (int)remainTime /3600;
                    m = (int)((remainTime-(h*3600))/60);
                    s = (int)remainTime - (h*3600) - (m*60);
                    timePoint[upComingTimePoint].setCounterText(h,m,s);

                    LinearLayout countdown = findViewById(R.id.countdown_point_time_landscape);
                    ProgressBar progressBar = findViewById(R.id.progress_bar_landscape);
                    progressBar.setProgress((int) (100-(100*remainTime/(TM.difference(Times.times[goneTimePoint],Times.times[upComingTimePoint])))));
                    ((TextView)(countdown.getChildAt(0))).setText(Integer.toString(h));
                    ((TextView)(countdown.getChildAt(2))).setText(Integer.toString(m));
                    ((TextView)(countdown.getChildAt(4))).setText(Integer.toString(s));
                }
                handler.postDelayed(this,1000);
            }
        };
        timePoint[upComingTimePoint].meComming(true);
        runnable.run();
    }

    boolean alarmActive(String alarmType,int index) {
        SharedPreferences pref;
        SharedPreferences.Editor editor;
        pref = this.getSharedPreferences(alarmType,Context.MODE_PRIVATE);
        return pref.getBoolean("index",true);
    }

    @Override
    protected void onDestroy() {
        mediaPlayer.stop();
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mediaPlayer.stop();
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            LinearLayout countdown = findViewById(R.id.countdown_point_time_landscape);
            ProgressBar progressBar = findViewById(R.id.progress_bar_landscape);
            countdown.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            timePoint[upComingTimePoint].meComming(false);

        }else {
            LinearLayout countdown = findViewById(R.id.countdown_point_time_landscape);
            ProgressBar progressBar = findViewById(R.id.progress_bar_landscape);
            countdown.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            timePoint[upComingTimePoint].meComming(true);
        }
    }
}
