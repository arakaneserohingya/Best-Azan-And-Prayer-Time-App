package com.alhadara.omar.azan.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.alhadara.omar.azan.Display._DisplaySET;
import com.alhadara.omar.azan.Locations._LocationSET;
import com.alhadara.omar.azan.Times._TimesSET;
import com.example.omar.azanapkmostafa.R;
import com.github.msarhan.ummalqura.calendar.UmmalquraCalendar;

import net.alhazmy13.hijridatepicker.date.gregorian.GregorianDatePickerDialog;
import net.alhazmy13.hijridatepicker.date.hijri.HijriDatePickerDialog;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ConvertDateActivity extends AppCompatActivity
        implements  HijriDatePickerDialog.OnDateSetListener,
        GregorianDatePickerDialog.OnDateSetListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(_DisplaySET.getAppTheme(this)==_DisplaySET.THEME_WHITE?R.style.AppTheme_NoActionBar:R.style.AppThemeDark_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convert_date);
        Toolbar toolbar = findViewById(R.id.toolbar_convert_date_activity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        manageRadioGroup();
        manageDialogs();
    }

    private void manageRadioGroup() {
        setTextsDefault();
        ((RadioGroup) findViewById(R.id.convert_date_activity_radio_group)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                setTextsDefault();
                if(i == R.id.convert_date_activity_from_gregorian_radio_button){
                    ((RadioButton) findViewById(R.id.convert_date_activity_from_hijri_radio_button)).setChecked(false);
                    switchRadio(findViewById(R.id.convert_date_activity_from_gregorian_button),
                            findViewById(R.id.convert_date_activity_from_hijri_button));
                }else{
                    ((RadioButton) findViewById(R.id.convert_date_activity_from_gregorian_radio_button)).setChecked(false);
                    switchRadio(findViewById(R.id.convert_date_activity_from_hijri_button),
                            findViewById(R.id.convert_date_activity_from_gregorian_button));
                }
            }
        });
    }
    private void switchRadio(View enabled,View disabled){
        enabled.setClickable(true);
        enabled.setAlpha(1.0f);
        disabled.setClickable(false);
        disabled.setAlpha(0.2f);
    }
    private void manageDialogs(){
        findViewById(R.id.convert_date_activity_from_hijri_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTextsDefault();
                UmmalquraCalendar now = _TimesSET.getUmmalquraCalendar(ConvertDateActivity.this);
                HijriDatePickerDialog dpd = HijriDatePickerDialog.newInstance(
                        ConvertDateActivity.this,
                        now.get(UmmalquraCalendar.YEAR),
                        now.get(UmmalquraCalendar.MONTH),
                        now.get(UmmalquraCalendar.DAY_OF_MONTH));
                dpd.show(ConvertDateActivity.this.getFragmentManager(), "HijriDatePickerDialog");
            }
        });
        findViewById(R.id.convert_date_activity_from_gregorian_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTextsDefault();
                Calendar now = Calendar.getInstance();
                GregorianDatePickerDialog dpd = GregorianDatePickerDialog.newInstance(
                        ConvertDateActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(ConvertDateActivity.this.getFragmentManager(),"GregorianDatePickerDialog");
            }
        });
        findViewById(R.id.convert_date_activity_from_gregorian_button).setClickable(false);
    }
    private void setTextsDefault(){
        Calendar gregorainCal = Calendar.getInstance();
        UmmalquraCalendar hijriCal = _TimesSET.getUmmalquraCalendar(this);
        ((TextView) findViewById(R.id.convert_date_activity_from_gregorian_button)).setText(
                _DisplaySET.formatStringNumbers(this,gregorainCal.get(Calendar.DAY_OF_MONTH) + " / " + (gregorainCal.get(Calendar.MONTH)+1) + " / " + gregorainCal.get(Calendar.YEAR))
        );
        ((TextView) findViewById(R.id.convert_date_activity_from_hijri_button)).setText(
                _DisplaySET.formatStringNumbers(this,hijriCal.get(Calendar.DAY_OF_MONTH) + " / " + (hijriCal.get(Calendar.MONTH)+1) + " / " + hijriCal.get(Calendar.YEAR))
        );
        ((TextView) findViewById(R.id.convert_date_activity_result_title)).setText("");
        findViewById(R.id.convert_date_activity_result_prayers_layout).setVisibility(View.GONE);
        findViewById(R.id.convert_date_activity_result_drop_button).setVisibility(View.GONE);
    }

    @Override
    public void onDateSet(GregorianDatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        NumberFormat nf = _DisplaySET.getNumberFormat(this);
        Calendar now = Calendar.getInstance();
        calendar.set(Calendar.YEAR,year);
        calendar.set(Calendar.MONTH,monthOfYear);
        calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
        UmmalquraCalendar u = _TimesSET.getUmmalquraCalendar(this);
        long l = (calendar.getTimeInMillis() - now.getTimeInMillis())/86400000;
        u.add(UmmalquraCalendar.DATE,(int)l);
        ((TextView)findViewById(R.id.convert_date_activity_result_title))
                .setText(_DisplaySET.formatStringNumbers(this,getResources().getStringArray(R.array.day_of_week)[u.get(UmmalquraCalendar.DAY_OF_WEEK) - 1]
                        + "  " + u.get(UmmalquraCalendar.DAY_OF_MONTH) + "/" + (u.get(UmmalquraCalendar.MONTH)+1) + "/" +
                        u.get(UmmalquraCalendar.YEAR)));
        ((TextView) findViewById(R.id.convert_date_activity_from_gregorian_button)).setText(
                _DisplaySET.formatStringNumbers(this,dayOfMonth + " / " + (monthOfYear + 1) + " / " + year)
        );
        managePrayerTimes(calendar);
    }



    @Override
    public void onDateSet(HijriDatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        UmmalquraCalendar u = _TimesSET.getUmmalquraCalendar(this);
        UmmalquraCalendar now = _TimesSET.getUmmalquraCalendar(this);
        NumberFormat nf = _DisplaySET.getNumberFormat(this);
        u.set(UmmalquraCalendar.YEAR, year);
        u.set(UmmalquraCalendar.MONTH,monthOfYear);
        u.set(UmmalquraCalendar.DAY_OF_MONTH,dayOfMonth);
        _TimesSET.setUmmalquraCalendar(this,u);
        Calendar calendar = Calendar.getInstance();
        long l = (u.getTimeInMillis() - now.getTimeInMillis())/86400000;
        calendar.add(Calendar.DATE,(int)l);
        ((TextView)findViewById(R.id.convert_date_activity_result_title))
                .setText(getResources().getStringArray(R.array.day_of_week)[calendar.get(Calendar.DAY_OF_WEEK) - 1]
                        + "  " + _DisplaySET.formatStringNumbers(this,(new SimpleDateFormat("dd/MM/yyyy")).format(calendar.getTime())));
        ((TextView) findViewById(R.id.convert_date_activity_from_hijri_button)).setText(
                _DisplaySET.formatStringNumbers(this,dayOfMonth + " / " + (monthOfYear + 1) + " / " + year)
        );
        managePrayerTimes(calendar);
    }

    private void managePrayerTimes(Calendar c) {
        final View button = findViewById(R.id.convert_date_activity_result_drop_button);
        final ViewGroup group = findViewById(R.id.convert_date_activity_result_prayers_layout);
        button.setVisibility(View.VISIBLE);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                group.setVisibility(View.VISIBLE);
                button.setVisibility(View.GONE);
            }
        });
        String[] times = _TimesSET.initializeTimesFor(this, _LocationSET.currentLocation,c);
        for(int i=0;i<times.length;i++){
            ((TextView)group.getChildAt(i+1)).setText(getResources().getStringArray(R.array.prayer_time)[i] + "\n" + _DisplaySET.formatStringNumbers(this,times[i]));
        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
