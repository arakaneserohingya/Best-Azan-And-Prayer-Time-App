package com.alhadara.omar.azan.Activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import android.content.res.Configuration;
import android.graphics.drawable.AnimationDrawable;

import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.AlarmManagerCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.alhadara.omar.azan.Alarms.GeneralSettingsReceiver;
import com.alhadara.omar.azan.Configurations;
import com.alhadara.omar.azan.Constants;
import com.alhadara.omar.azan.TM;
import com.alhadara.omar.azan.Times;
import com.example.omar.azanapkmostafa.R;
import com.google.android.material.navigation.NavigationView;

import java.util.Calendar;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int GENERAL_ALARM_REQUEST_CODE = 133;
    private int upComingTimePoint;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configurations.initializeMainConfigurations(getApplicationContext());
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
        Configurations.setCurrentLocation(this,(float)33.513805,(float)36.276527,3);
        Configurations.setReloadMainActivityOnResume(false);// False in MainActivity itself
        upComingTimePoint = TM.commingTimePointIndex(Times.times);
        initializeTimePoints();
        startTimer();
        triggerAlarmManager();
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_settings) {
            // Handle the camera action
        } else if (id == R.id.nav_convert_date) {

        } else if (id == R.id.nav_compass) {

        } else if (id == R.id.nav_location) {
            startActivity(new Intent(this,LocationsActivity.class));
        } else if (id == R.id.nav_about) {

        } else if (id == R.id.nav_problems) {

        } else if (id == R.id.nav_share) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void initializeTimePoints(){
        ViewGroup timePointLayout = findViewById(R.id.time_point_layout);
        ViewGroup timepoint;
        for(int i=0;i<6;i++) {
            final int s = i;
            timepoint = (ViewGroup) timePointLayout.getChildAt(i);
            attributingTimePoint(timepoint,i);
            timepoint.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(MainActivity.this, TimePointSettingsActivity.class);
                    intent.putExtra("TimePointIndex",s);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_top,R.anim.slide_out_top);
                }
            });

        }
        if(Configurations.orientation != Configuration.ORIENTATION_LANDSCAPE) reorderTimePointForPortrait();
        tintingUpComingTimePoint(true);
    }
    public void attributingTimePoint(ViewGroup timepoint, int i){
        ((TextView)timepoint.findViewById(R.id.time_point_ampm)).setText("AM");
        ((TextView)timepoint.findViewById(R.id.time_point_text)).setText(Constants.alias[i]);

        String hours = Times.times[i].substring(0, 2);
        int h = Integer.parseInt(hours);
        if (h > 12) {
            h = h - 12;
            ((TextView)timepoint.findViewById(R.id.time_point_ampm)).setText("PM");
            hours = "0" + Integer.toString(h);
        }
        String tm = hours + Times.times[i].substring(2, 5);
        ((TextView)timepoint.findViewById(R.id.time_point_time)).setText(tm);
    }
    public void reorderTimePointForPortrait(){
        ViewGroup timePointLayout = findViewById(R.id.time_point_layout);
        ViewGroup timepoint ;
        for(int i=0;i<7;i++) {
            if(((ViewGroup) timePointLayout.getChildAt(i)).getChildCount() == 1) {
                timepoint = (ViewGroup) timePointLayout.getChildAt(i);
                timePointLayout.removeView(timepoint);
                timePointLayout.addView(timepoint,upComingTimePoint);
                break;
            }
        }
    }
    public void handleProgressBarForLandscape(int remainTime){
        int goneTimePoint = upComingTimePoint -1;
        if(goneTimePoint < 0) goneTimePoint = 5;
        ProgressBar progressBar = findViewById(R.id.progress_bar_landscape);
        progressBar.setProgress((int) (100-(100*remainTime/(TM.difference(Times.times[goneTimePoint],Times.times[upComingTimePoint])))));
    }
    public void tintingUpComingTimePoint(boolean tint){
        if(Configurations.orientation == Configuration.ORIENTATION_LANDSCAPE)
            ((ViewGroup)findViewById(R.id.time_point_layout)).getChildAt(upComingTimePoint)
                    .setBackgroundColor(getResources().getColor(tint?R.color.colorPrimary:R.color.widgetColorSettingsBox));
        else ((ViewGroup)findViewById(R.id.time_point_layout)).getChildAt(upComingTimePoint + 1)
                .setBackgroundColor(getResources().getColor(tint?R.color.colorPrimary:R.color.widgetColorSettingsBox));
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
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int h,m,s;
                int remainTime = TM.difference(TM.getTime(),Times.times[upComingTimePoint]);
                if(remainTime < 1) {
                    tintingUpComingTimePoint(false);
                    upComingTimePoint = TM.commingTimePointIndex(Times.times);
                    if(Configurations.orientation != Configuration.ORIENTATION_LANDSCAPE) reorderTimePointForPortrait();
                    tintingUpComingTimePoint(true);
                }else {
                    h = (int)remainTime /3600;
                    m = (int)((remainTime-(h*3600))/60);
                    s = (int)remainTime - (h*3600) - (m*60);
                    ViewGroup countdown = findViewById(R.id.main_activity_timer);
                    if(Configurations.orientation == Configuration.ORIENTATION_LANDSCAPE) handleProgressBarForLandscape(remainTime);
                    ((TextView)(countdown.getChildAt(0))).setText(Integer.toString(h));
                    ((TextView)(countdown.getChildAt(2))).setText(Integer.toString(m));
                    ((TextView)(countdown.getChildAt(4))).setText(Integer.toString(s));
                }
                handler.postDelayed(this,1000);
            }
        };
        runnable.run();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if(Configurations.orientation != getResources().getConfiguration().orientation){
            Configurations.orientation = getResources().getConfiguration().orientation;
            setContentView(R.layout.activity_main);
            initializeTimePoints();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(Configurations.getReloadMainActivityOnResume()){
            Configurations.setReloadMainActivityOnResume(false);
            recreate();
        }
    }

    public void triggerAlarmManager() {

        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND,3);
        Intent intent = new Intent(MainActivity.this, GeneralSettingsReceiver.class);
        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        PendingIntent generalTimeSettingIntent = PendingIntent.getBroadcast(MainActivity.this, GENERAL_ALARM_REQUEST_CODE, new Intent(MainActivity.this, GeneralSettingsReceiver.class), 0);
        AlarmManagerCompat.setExactAndAllowWhileIdle(manager,AlarmManager.RTC_WAKEUP,cal.getTimeInMillis(),generalTimeSettingIntent);
    }
}
