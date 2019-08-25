package com.alhadara.omar.azan;

import android.app.Activity;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.example.omar.azanapkmostafa.R;


public class TimePoint extends LinearLayout {

    Activity mContext;
    private TextView text;
    private TextView time;
    private TextView ampm;
    private int index;

    public TimePoint(Activity context) {
        super(context);
        LinearLayout l = (LinearLayout) inflate(context, R.layout.time_point, this);
        mContext = context;
        text = l.findViewById(R.id.time_point_text);
        time = l.findViewById(R.id.time_point_time);
        ampm = l.findViewById(R.id.time_point_ampm);
        ampm.setText("AM");

    }

    public void setAttributes(int i, String times[]) {
        index = i;
        text.setText(Constants.alias[i]);

        String hours = times[i].substring(0, 2);
        int h = Integer.parseInt(hours);
        if (h > 12) {
            h = h - 12;
            ampm.setText("PM");
            hours = "0" + Integer.toString(h);
        }
        String tm = hours + times[i].substring(2, 5);
        time.setText(tm);
    }



    public void meComming(boolean is) {
    }

    public void setCounterText(int h, int m, int s) {
      /*  LinearLayout l = (LinearLayout) ((FrameLayout)(this).findViewById(R.id.time_point_count_layout)).getChildAt(0);
        ((TextView)(l.getChildAt(0))).setText(Integer.toString(h));
        ((TextView)(l.getChildAt(2))).setText(Integer.toString(m));
        ((TextView)(l.getChildAt(4))).setText(Integer.toString(s));*/
    }
}
