package com.alhadara.omar.azan.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alhadara.omar.azan.Configurations;
import com.alhadara.omar.azan.Constants;
import com.alhadara.omar.azan.TM;
import com.alhadara.omar.azan.Times;
import com.example.omar.azanapkmostafa.R;

public class AdjustTimesActivity extends AppCompatActivity {

    private String[] times;
    private SharedPreferences delayTimesPref;
    private SharedPreferences.Editor delayTimesEdit;
    private int[] delays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adjust_times);
        Toolbar toolbar = findViewById(R.id.toolbar_adjust_times_activity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.adjust_time_manually));
        delayTimesPref = getSharedPreferences("delaytime.txt",MODE_PRIVATE);
        delayTimesEdit = delayTimesPref.edit();
        times = new String[6];
        delays = new int[6];
        for(int i=0;i<6;i++) { times[i]= Times.times[i]; delays[i] = delayTimesPref.getInt(Integer.toString(i),0);}

        widgetsAdder();
        ((ImageButton) findViewById(R.id.adjust_times_activity_save_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(int i=0;i<6;i++) {
                    delayTimesEdit.putInt(Integer.toString(i),delays[i]);
                    delayTimesEdit.commit();
                    Times.times[i] = times[i];
                    finish();
                }
                Configurations.setReloadMainActivityOnResume(true);
            }
        });
    }

    private void widgetsAdder() {
        LinearLayout adjustTimeLayout;
        for(int i=0;i<6;i++) {
            final int j =i;

            adjustTimeLayout = (LinearLayout) ((LinearLayout) findViewById(R.id.adjust_time_widget_layout)).getChildAt(i);
            final LinearLayout ad = adjustTimeLayout;
            ((TextView) adjustTimeLayout.findViewById(R.id.adjust_time_widget_text)).setText(
                    getResources().getStringArray(R.array.prayer_time)[i] + "  " + times[i]);
            ((TextView) ad.findViewById(R.id.adjust_time_widget_minutes_number)).setText(Integer.toString(delays[i]));
            ((ImageButton) adjustTimeLayout.findViewById(R.id.adjust_time_widget_increase)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(delays[j] < 59) {
                        delays[j] += 1;
                        int h = TM.getHours24(times[j]);
                        int m = TM.getMinute(times[j]);
                        if (m == 59) {
                            m = 0;
                            if (h < 23) h = h + 1;
                            else h = 0;
                        } else m = m + 1;
                        times[j] = h > 9 ? Integer.toString(h) : "0" + Integer.toString(h);
                        times[j] += m > 9 ? ":" + Integer.toString(m) : ":0" + Integer.toString(m);
                        ((TextView) ad.findViewById(R.id.adjust_time_widget_text)).setText(
                                getResources().getStringArray(R.array.prayer_time)[j] + "  " + times[j]);
                        ((TextView) ad.findViewById(R.id.adjust_time_widget_minutes_number)).setText(
                                Integer.toString(delays[j])
                        );

                    }
                }
            });
            ((ImageButton) adjustTimeLayout.findViewById(R.id.adjust_time_widget_decrease)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(delays[j] > -59) {
                        delays[j] -= 1;
                        int h = TM.getHours24(times[j]);
                        int m = TM.getMinute(times[j]);
                        if (m == 0) {
                            m = 59;
                            if (h > 0) h = h - 1;
                            else h = 23;
                        } else m = m - 1;
                        times[j] = h > 9 ? Integer.toString(h) : "0" + Integer.toString(h);
                        times[j] += m > 9 ? ":" + Integer.toString(m) : ":0" + Integer.toString(m);
                        ((TextView) ad.findViewById(R.id.adjust_time_widget_text)).setText(
                                getResources().getStringArray(R.array.prayer_time)[j] + "  " + times[j]);
                        ((TextView) ad.findViewById(R.id.adjust_time_widget_minutes_number)).setText(
                                Integer.toString(delays[j])
                        );
                    }
                }
            });

        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
