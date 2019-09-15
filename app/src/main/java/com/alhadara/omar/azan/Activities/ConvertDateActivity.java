package com.alhadara.omar.azan.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.omar.azanapkmostafa.R;
import com.github.msarhan.ummalqura.calendar.UmmalquraCalendar;

import net.alhazmy13.hijridatepicker.date.gregorian.GregorianDatePickerDialog;
import net.alhazmy13.hijridatepicker.date.hijri.HijriDatePickerDialog;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Callable;

public class ConvertDateActivity extends AppCompatActivity
        implements  HijriDatePickerDialog.OnDateSetListener,
                    GregorianDatePickerDialog.OnDateSetListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
                    switchRadio((Button)findViewById(R.id.convert_date_activity_from_gregorian_button),
                            (Button) findViewById(R.id.convert_date_activity_from_hijri_button));
                }else{
                    ((RadioButton) findViewById(R.id.convert_date_activity_from_gregorian_radio_button)).setChecked(false);
                    switchRadio((Button)findViewById(R.id.convert_date_activity_from_hijri_button),
                            (Button) findViewById(R.id.convert_date_activity_from_gregorian_button));
                }
            }
        });
    }
    private void switchRadio(Button enabled,Button disabled){
        enabled.setClickable(true);
        enabled.setBackgroundColor(getResources().getColor(R.color.enabled_button));
        enabled.setTextColor(Color.WHITE);
        disabled.setClickable(false);
        disabled.setBackgroundColor(getResources().getColor(R.color.disabled_button));
        disabled.setTextColor(Color.DKGRAY);
    }
    private void manageDialogs(){
        findViewById(R.id.convert_date_activity_from_hijri_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UmmalquraCalendar now = new UmmalquraCalendar();
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
        UmmalquraCalendar hijriCal = new UmmalquraCalendar();
        ((Button) findViewById(R.id.convert_date_activity_from_gregorian_button)).setText(
                gregorainCal.get(Calendar.DAY_OF_MONTH) + " / " + (gregorainCal.get(Calendar.MONTH)+1) + " / " + gregorainCal.get(Calendar.YEAR)
        );
        ((Button) findViewById(R.id.convert_date_activity_from_hijri_button)).setText(
                hijriCal.get(Calendar.DAY_OF_MONTH) + " / " + (hijriCal.get(Calendar.MONTH)+1) + " / " + hijriCal.get(Calendar.YEAR)
        );
        ((TextView) findViewById(R.id.convert_date_activity_result)).setText("");
    }

    @Override
    public void onDateSet(GregorianDatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        Calendar now = Calendar.getInstance();
        calendar.set(Calendar.YEAR,year);
        calendar.set(Calendar.MONTH,monthOfYear);
        calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
        UmmalquraCalendar u = new UmmalquraCalendar();
        long l = (calendar.getTimeInMillis() - now.getTimeInMillis())/86400000;
        u.add(UmmalquraCalendar.DATE,(int)l);
        ((TextView)findViewById(R.id.convert_date_activity_result)).setText(
                u.get(UmmalquraCalendar.DAY_OF_MONTH) + " / " + (u.get(UmmalquraCalendar.MONTH)+1) + " / " + u.get(UmmalquraCalendar.YEAR)
        );
        ((TextView) findViewById(R.id.convert_date_activity_from_gregorian_button)).setText(
                dayOfMonth + " / " + (monthOfYear + 1) + " / " + year
        );

    }

    @Override
    public void onDateSet(HijriDatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        UmmalquraCalendar u = new UmmalquraCalendar();
        UmmalquraCalendar now = new UmmalquraCalendar();
        u.set(UmmalquraCalendar.YEAR, year);
        u.set(UmmalquraCalendar.MONTH,monthOfYear);
        u.set(UmmalquraCalendar.DAY_OF_MONTH,dayOfMonth);
        Calendar calendar = Calendar.getInstance();
        long l = (u.getTimeInMillis() - now.getTimeInMillis())/86400000;
        calendar.add(Calendar.DATE,(int)l);
        ((TextView)findViewById(R.id.convert_date_activity_result)).setText(
                calendar.get(Calendar.DAY_OF_MONTH) + " / " + (calendar.get(Calendar.MONTH)+1) + " / " + calendar.get(Calendar.YEAR)
        );
        ((TextView) findViewById(R.id.convert_date_activity_from_hijri_button)).setText(
                dayOfMonth + " / " + (monthOfYear + 1) + " / " + year
        );
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
