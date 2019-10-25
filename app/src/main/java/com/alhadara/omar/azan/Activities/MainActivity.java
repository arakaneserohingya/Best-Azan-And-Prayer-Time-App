package com.alhadara.omar.azan.Activities;

import android.content.Intent;

import android.content.res.Configuration;

import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.alhadara.omar.azan.Alarms.AlarmsScheduler;
import com.alhadara.omar.azan.Display._DisplaySET;
import com.alhadara.omar.azan.Locations._LocationSET;
import com.alhadara.omar.azan.TM;
import com.alhadara.omar.azan.Times;
import com.example.omar.azanapkmostafa.R;
import com.github.msarhan.ummalqura.calendar.UmmalquraCalendar;
import com.google.android.material.navigation.NavigationView;

import java.util.Calendar;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static boolean reloadMainActivityOnResume = false;
    private int upComingTimePoint;
    private Handler handler;
    private Runnable runnable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*      Initialization Methods      */
        _LocationSET.checkCurrentLocation(this);
        Times.updateTimes(this);
        _DisplaySET.setLanguagePreferences(this);
        reloadMainActivityOnResume = false;
        /*              ********            */


        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        upComingTimePoint = TM.commingTimePointIndex(Times.times);
        initializeDateViews();
        initializeTimePoints();
        startTimer();
        AlarmsScheduler.fire(this,Calendar.getInstance()); //set Alarms
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_settings) {
            startActivity(new Intent(this,SettingsActivity.class));
        } else if (id == R.id.nav_convert_date) {
            startActivity(new Intent(this, ConvertDateActivity.class));
        } else if (id == R.id.nav_compass) {

        } else if (id == R.id.nav_location) {
            startActivity(new Intent(this, LocationsActivity.class));
        } else if (id == R.id.nab_remembrance) {
            startActivity(new Intent(this, AzkarActivity.class));
        } else if (id == R.id.nav_about) {

        } else if (id == R.id.nav_problems) {

        } else if (id == R.id.nav_share) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void initializeDateViews() {
        UmmalquraCalendar hijCal = new UmmalquraCalendar();
        Calendar cal = Calendar.getInstance();
        ((TextView) findViewById(R.id.main_activity_day_of_week)).setText(getResources().getStringArray(R.array.day_of_week)[cal.get(Calendar.DAY_OF_WEEK) - 1]);
        ((TextView) findViewById(R.id.main_activity_day_of_week)).setTypeface(_DisplaySET.getTypeFace(this));
        ((TextView) findViewById(R.id.main_activity_hijri_month_number)).setText(Integer.toString(hijCal.get(Calendar.DAY_OF_MONTH)));
        ((TextView) findViewById(R.id.main_activity_hijri_month_name)).setText(getResources().getStringArray(R.array.hijri_month)[hijCal.get(Calendar.MONTH)]);
        ((TextView) findViewById(R.id.main_activity_hijri_month_name)).setTypeface(_DisplaySET.getTypeFace(this));
        ((TextView) findViewById(R.id.main_activity_gregorian_month_number)).setText(Integer.toString(cal.get(Calendar.DAY_OF_MONTH)));
        ((TextView) findViewById(R.id.main_activity_gregorian_month_name)).setText(getResources().getStringArray(R.array.gregorian_month)[cal.get(Calendar.MONTH)]);
        ((TextView) findViewById(R.id.main_activity_gregorian_month_name)).setTypeface(_DisplaySET.getTypeFace(this));
    }

    public void initializeTimePoints() {
        if(!_LocationSET.isLocationAssigned(this)) return;
        ViewGroup timePointLayout = findViewById(R.id.time_point_layout);
        ViewGroup timepoint;
        for (int i = 0; i < 6; i++) {
            final int s = i;
            timepoint = (ViewGroup) timePointLayout.getChildAt(i);
            attributingTimePoint(timepoint, i);
            timepoint.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(MainActivity.this, TimePointSettingsActivity.class);
                    intent.putExtra("TimePointIndex", s);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_top);
                }
            });

        }
        if (getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE)
            reorderTimePointForPortrait();
        tintingUpComingTimePoint(true);
    }

    public void attributingTimePoint(ViewGroup timepoint, int i) {

        ((TextView) timepoint.findViewById(R.id.time_point_text)).setText(getResources().getStringArray(R.array.prayer_time)[i]);
        ((TextView) timepoint.findViewById(R.id.time_point_text)).setTypeface(_DisplaySET.getTypeFace(this));
        ((TextView) timepoint.findViewById(R.id.time_point_ampm)).setText(TM.getPrayerPhaseString(this,i));
        ((TextView) timepoint.findViewById(R.id.time_point_ampm)).setVisibility(_DisplaySET.isTime24(this)?View.GONE:View.VISIBLE);
        ((TextView) timepoint.findViewById(R.id.time_point_ampm)).setTypeface(_DisplaySET.getTypeFace(this));
        ((TextView) timepoint.findViewById(R.id.time_point_time)).setText(TM.getPrayerTimeString(this,i));
    }

    public void reorderTimePointForPortrait() {
        ViewGroup timePointLayout = findViewById(R.id.time_point_layout);
        ViewGroup timepoint;
        for (int i = 0; i < 7; i++) {
            if (((ViewGroup) timePointLayout.getChildAt(i)).getChildCount() == 1) {
                timepoint = (ViewGroup) timePointLayout.getChildAt(i);
                timePointLayout.removeView(timepoint);
                timePointLayout.addView(timepoint, upComingTimePoint);
                break;
            }
        }
    }

    public void handleProgressBarForLandscape(int remainTime) {
        int goneTimePoint = upComingTimePoint - 1;
        if (goneTimePoint < 0) goneTimePoint = 5;
        ProgressBar progressBar = findViewById(R.id.progress_bar_landscape);
        progressBar.setProgress((int) (100 - (100 * remainTime / (TM.difference(Times.times[goneTimePoint], Times.times[upComingTimePoint])))));
    }

    public void tintingUpComingTimePoint(boolean tint) {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            ((ViewGroup) findViewById(R.id.time_point_layout)).getChildAt(upComingTimePoint)
                    .setBackgroundColor(getResources().getColor(tint ? R.color.colorPrimary : R.color.widgetColorSettingsBox));
        else ((ViewGroup) findViewById(R.id.time_point_layout)).getChildAt(upComingTimePoint + 1)
                .setBackgroundColor(getResources().getColor(tint ? R.color.colorPrimary : R.color.widgetColorSettingsBox));
    }


    public void startTimer() {
        if(!_LocationSET.isLocationAssigned(this)) return;
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                int h, m, s;
                int remainTime = TM.difference(TM.getTime(), Times.times[upComingTimePoint]);
                if (remainTime < 1) {
                    tintingUpComingTimePoint(false);
                    upComingTimePoint = TM.commingTimePointIndex(Times.times);
                    if (getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE)
                        reorderTimePointForPortrait();
                    tintingUpComingTimePoint(true);
                } else {
                    h = (int) remainTime / 3600;
                    m = (int) ((remainTime - (h * 3600)) / 60);
                    s = (int) remainTime - (h * 3600) - (m * 60);
                    ViewGroup countdown = findViewById(R.id.main_activity_timer);
                    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
                        handleProgressBarForLandscape(remainTime);
                    ((TextView) (countdown.getChildAt(0))).setText(Integer.toString(h));
                    ((TextView) (countdown.getChildAt(2))).setText(Integer.toString(m));
                    ((TextView) (countdown.getChildAt(4))).setText(Integer.toString(s));
                }
                handler.postDelayed(this,1000);
            }
        };
        runnable.run();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(reloadMainActivityOnResume){
            reloadMainActivityOnResume = false;
            recreate();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(handler != null)handler.removeCallbacks(runnable);
    }
}
