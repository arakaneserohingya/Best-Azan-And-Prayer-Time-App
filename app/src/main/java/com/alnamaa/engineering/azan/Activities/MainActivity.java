package com.alnamaa.engineering.azan.Activities;

import android.content.Intent;

import android.content.res.Configuration;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.LayoutDirection;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.alnamaa.engineering.azan.Alarms.AlarmsScheduler;
import com.alnamaa.engineering.azan.Alarms._AlarmSET;
import com.alnamaa.engineering.azan.Display._DisplaySET;
import com.alnamaa.engineering.azan.Locations._LocationSET;
import com.alnamaa.engineering.azan.R;
import com.alnamaa.engineering.azan.Times._TimesSET;
import com.github.msarhan.ummalqura.calendar.UmmalquraCalendar;
import android.icu.util.IslamicCalendar;
import com.google.android.material.navigation.NavigationView;

import java.text.NumberFormat;
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
        _AlarmSET.setPowerConceptions(this);
        _AlarmSET.setAlarmPermissions(this);
        _TimesSET.updateTimes(this);
        _DisplaySET.setLanguagePreferences(this);
        reloadMainActivityOnResume = false;
        /*              ********            */

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(getResources().getColor(_DisplaySET.getAppTheme(this)== _DisplaySET.WHITE ?
                R.color.colorPrimaryWhite:R.color.colorPrimaryBlack));
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getHeaderView(0).setBackground(getResources().getDrawable(_DisplaySET.getAppTheme(this)==_DisplaySET.WHITE ?
                R.drawable.side_nav_bar:R.drawable.side_nav_bar_black));
        navigationView.setNavigationItemSelectedListener(this);

        upComingTimePoint = _TimesSET.comingTimePointIndex();
        if(_TimesSET.isItGoogleCalendar(this)) initializeDateViewsWithGoogleApi();
        else initializeDateViews();
        initializeTimePoints();
        initializeTheme();
        startTimer();
        AlarmsScheduler.fire(this,Calendar.getInstance()); //set Alarms
    }

    private void initializeTheme() {
        ((ImageView) findViewById(R.id.background)).setImageDrawable(getResources().getDrawable(
                _DisplaySET.getAppTheme(this) == _DisplaySET.WHITE ?R.drawable.background:R.drawable.background_black
        ));
        ((TextView) findViewById(R.id.main_activity_day_of_week)).setTextColor(
            _DisplaySET.getAppTheme(this)==_DisplaySET.WHITE ? Color.BLACK:Color.WHITE);
        ((TextView) findViewById(R.id.main_activity_hijri_month_number)).setTextColor(
                _DisplaySET.getAppTheme(this)==_DisplaySET.WHITE ? Color.BLACK:Color.WHITE);
        ((TextView) findViewById(R.id.main_activity_hijri_month_name)).setTextColor(
                _DisplaySET.getAppTheme(this)==_DisplaySET.WHITE ? Color.BLACK:Color.WHITE);
        ((TextView) findViewById(R.id.main_activity_gregorian_month_number)).setTextColor(
                _DisplaySET.getAppTheme(this)==_DisplaySET.WHITE ? Color.BLACK:Color.WHITE);
        ((TextView) findViewById(R.id.main_activity_gregorian_month_name)).setTextColor(
                _DisplaySET.getAppTheme(this)==_DisplaySET.WHITE ? Color.BLACK:Color.WHITE);
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            ((ProgressBar)findViewById(R.id.progress_bar_landscape)).setProgressDrawable(getResources().getDrawable(
                    _DisplaySET.getAppTheme(this) == _DisplaySET.WHITE ?R.drawable.circle:R.drawable.circle_black
            ));
            ViewGroup group = findViewById(R.id.main_activity_timer);
            for(int i=0;i<group.getChildCount();i++){
                if(group.getChildAt(i) instanceof TextView) ((TextView) group.getChildAt(i)).setTextColor(
                        _DisplaySET.getAppTheme(this)==_DisplaySET.WHITE ?getResources().getColor(R.color.widgetColor):Color.WHITE
                );
            }
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_settings) {
            startActivityForResult(new Intent(this,SettingsActivity.class),10000);
        } else if (id == R.id.nav_convert_date) {
            if(_TimesSET.isItGoogleCalendar(this))
                startActivityForResult(new Intent(this, ConvertDateGoogleApiActivity.class),10000);
            else
                startActivityForResult(new Intent(this, ConvertDateActivity.class),10000);
        }  else if (id == R.id.nav_location) {
            startActivityForResult(new Intent(this, LocationsActivity.class),10000);
        } else if (id == R.id.nab_remembrance) {
            startActivityForResult(new Intent(this, AzkarActivity.class),10000);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void initializeDateViews() {
        UmmalquraCalendar hijCal = _TimesSET.getUmmalquraCalendar(this);
        Calendar cal = Calendar.getInstance();
        NumberFormat nf = _DisplaySET.getNumberFormat(this);
        ((TextView) findViewById(R.id.main_activity_day_of_week)).setText(getResources().getStringArray(R.array.day_of_week)[cal.get(Calendar.DAY_OF_WEEK) - 1]);
        ((TextView) findViewById(R.id.main_activity_day_of_week)).setTypeface(_DisplaySET.getTypeFace(this));
        ((TextView) findViewById(R.id.main_activity_hijri_month_number)).setText(nf.format(hijCal.get(UmmalquraCalendar.DAY_OF_MONTH)));
        ((TextView) findViewById(R.id.main_activity_hijri_month_name)).setText(getResources().getStringArray(R.array.hijri_month)[hijCal.get(UmmalquraCalendar.MONTH)]);
        ((TextView) findViewById(R.id.main_activity_hijri_month_name)).setTypeface(_DisplaySET.getTypeFace(this));
        ((TextView) findViewById(R.id.main_activity_gregorian_month_number)).setText(nf.format(cal.get(Calendar.DAY_OF_MONTH)));
        ((TextView) findViewById(R.id.main_activity_gregorian_month_name)).setText(getResources().getStringArray(R.array.gregorian_month)[cal.get(Calendar.MONTH)]);
        ((TextView) findViewById(R.id.main_activity_gregorian_month_name)).setTypeface(_DisplaySET.getTypeFace(this));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void initializeDateViewsWithGoogleApi() {
        IslamicCalendar hijCal = _TimesSET.getGoogleCalendar(this);
        Calendar cal = Calendar.getInstance();
        NumberFormat nf = _DisplaySET.getNumberFormat(this);
        ((TextView) findViewById(R.id.main_activity_day_of_week)).setText(getResources().getStringArray(R.array.day_of_week)[cal.get(Calendar.DAY_OF_WEEK) - 1]);
        ((TextView) findViewById(R.id.main_activity_day_of_week)).setTypeface(_DisplaySET.getTypeFace(this));
        ((TextView) findViewById(R.id.main_activity_hijri_month_number)).setText(nf.format(hijCal.get(IslamicCalendar.DAY_OF_MONTH)));
        ((TextView) findViewById(R.id.main_activity_hijri_month_name)).setText(getResources().getStringArray(R.array.hijri_month)[hijCal.get(IslamicCalendar.MONTH)]);
        ((TextView) findViewById(R.id.main_activity_hijri_month_name)).setTypeface(_DisplaySET.getTypeFace(this));
        ((TextView) findViewById(R.id.main_activity_gregorian_month_number)).setText(nf.format(cal.get(Calendar.DAY_OF_MONTH)));
        ((TextView) findViewById(R.id.main_activity_gregorian_month_name)).setText(getResources().getStringArray(R.array.gregorian_month)[cal.get(Calendar.MONTH)]);
        ((TextView) findViewById(R.id.main_activity_gregorian_month_name)).setTypeface(_DisplaySET.getTypeFace(this));
    }

    public void initializeTimePoints() {
        if(!_LocationSET.isLocationAssigned(this)) return;
        ViewGroup timePointLayout = findViewById(R.id.time_point_layout);
        ViewGroup timepoint;
        final Toast toast = Toast.makeText(this,"",Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER,0,0);
        for (int i = 0; i < 6; i++) {
            final int s = i;
            timepoint = (ViewGroup) timePointLayout.getChildAt(i);
            attributingTimePoint(timepoint, i);
            timepoint.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    toast.setText(getTimeDiffString(s));
                    toast.show();
                }
            });

        }
        if (getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
            _DisplaySET.tintWidget(this,(ViewGroup) timePointLayout.getChildAt(6));
            reorderTimePointForPortrait();
        }
        tintingUpComingTimePoint(true);
    }

    public void attributingTimePoint(ViewGroup timepoint, int i) {

        ((TextView) timepoint.findViewById(R.id.time_point_text)).setText(getResources().getStringArray(R.array.prayer_time)[i]);
        ((TextView) timepoint.findViewById(R.id.time_point_text)).setTypeface(_DisplaySET.getTypeFace(this));
        ((TextView) timepoint.findViewById(R.id.time_point_ampm)).setText(_TimesSET.getPrayerPhaseString(this,i));
        ((TextView) timepoint.findViewById(R.id.time_point_ampm)).setVisibility(_DisplaySET.isTime24(this)?View.GONE:View.VISIBLE);
        if(getResources().getConfiguration().getLayoutDirection() == LayoutDirection.LTR) {
            View view = timepoint.findViewById(R.id.time_point_ampm);
            ViewGroup layout = (ViewGroup) view.getParent();
            layout.removeView(view);
            layout.addView(view);
        }
        ((TextView) timepoint.findViewById(R.id.time_point_time)).setText(_TimesSET.getPrayerTimeString(this,i));
        _DisplaySET.tintWidget(this,timepoint);
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
        progressBar.setProgress((int) (100 - (100 * remainTime / ((_TimesSET.getPrayerTimeMillis(upComingTimePoint,true)- _TimesSET.getPrayerTimeMillis(goneTimePoint,false))/1000))));
    }

    public void tintingUpComingTimePoint(boolean tint) {
        int widNum = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE?
                upComingTimePoint:(upComingTimePoint+1);
        if(tint)_DisplaySET.tintWidgetUpComing(this, (ViewGroup) ((ViewGroup) findViewById(R.id.time_point_layout)).getChildAt(widNum));
        else _DisplaySET.tintWidget(this, (ViewGroup) ((ViewGroup) findViewById(R.id.time_point_layout)).getChildAt(widNum));
    }


    public void startTimer() {
        if(!_LocationSET.isLocationAssigned(this)) return;
        final NumberFormat nf = _DisplaySET.getNumberFormat(this);
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                int h, m, s;
                int remainTime = (int) ((_TimesSET.getPrayerTimeMillis(upComingTimePoint,true) - System.currentTimeMillis())/1000);
                if (remainTime < 1) {
                    tintingUpComingTimePoint(false);
                    upComingTimePoint = _TimesSET.comingTimePointIndex();
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
                    ((TextView) (countdown.getChildAt(0))).setText(nf.format(h));
                    ((TextView) (countdown.getChildAt(2))).setText(nf.format(m));
                    ((TextView) (countdown.getChildAt(4))).setText(nf.format(s));
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        recreate();
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(handler != null)handler.removeCallbacks(runnable);
        reloadMainActivityOnResume = true;
    }


    private String getTimeDiffString(int i) {
        String s = "";
        NumberFormat nf = _DisplaySET.getNumberFormat(this);
        long diff = _TimesSET.getPrayerTimeMillis(i,false) - System.currentTimeMillis();
        if(diff < 0) s+=getResources().getString(R.string.toast_prayer_widget_click_was);
        diff /= 1000;
        int h = (int) (diff /3600);
        int m = (int) (diff - (h*3600))/60;
        if(h<0) h=-h;
        if(m<0) m=-m;
        if(h!=0) s+= " " + (h>2?nf.format(h) + " ":"") + getHoursWordString(h) + (m!=0?" "+ getResources().getString(R.string.toast_prayer_widget_click_and):"");
        if(m!=0) s+= " " + (m>2?nf.format(m) + " ":"") + getMinutesWordString(m);
        s+= diff<0?" "+ getResources().getString(R.string.toast_prayer_widget_click_ago):" "+ getResources().getString(R.string.toast_prayer_widget_click_remaining);
        if(h==0&&m==0) s= getResources().getString(R.string.toast_prayer_widget_click_just);
        return s;
    }

    private String getMinutesWordString(int m) {
        switch (m){
            case 1:
                return getResources().getString(R.string.toast_prayer_widget_click_one_minute);
            case 2:
                return getResources().getString(R.string.toast_prayer_widget_click_two_minutes);
            default:
                return getResources().getString(R.string.toast_prayer_widget_click_minutes);
        }
    }

    private String getHoursWordString(int h) {
        switch (h){
            case 1:
                return getResources().getString(R.string.toast_prayer_widget_click_one_hour);
            case 2:
                return getResources().getString(R.string.toast_prayer_widget_click_two_hours);
            default:
                if(h>10) return getResources().getString(R.string.toast_prayer_widget_click_hours_more_than_ten);
                return getResources().getString(R.string.toast_prayer_widget_click_hours);
        }
    }
}
