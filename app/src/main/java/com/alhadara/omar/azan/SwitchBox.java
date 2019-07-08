package com.alhadara.omar.azan;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;

import com.example.omar.azanapkmostafa.R;

public class SwitchBox extends LinearLayout {
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private String indexKey;
    public ImageButton imageButton;
    SwitchCompat switchCompat;
    TextView text;
    public SwitchBox(Activity context, String filename, String key, boolean onIsDefault, String alies , boolean isActive) {
        super(context);
        LinearLayout l= (LinearLayout) inflate(context,R.layout.switch_box,this);
        pref = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        editor = pref.edit();
        switchCompat = l.findViewById(R.id.switch_box_trigger);
        text = l.findViewById(R.id.switch_box_text);
        imageButton = this.findViewById(R.id.switch_box_imagebutton);
        indexKey = key;
        switchCompat.setChecked(pref.getBoolean(indexKey,onIsDefault));
        //setPlayBox(switchCompat.isChecked());
        switchCompat.setEnabled(isActive);
        text.setText(alies);
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                editor.putBoolean(indexKey,b);
                editor.commit();
                //setPlayBox(b);
            }
        });
    }
   /* public void setPlayBox(boolean b){
        if(b) {
            imageButton.setVisibility(VISIBLE);
        }else{
            imageButton.setVisibility(GONE);
        }
    }*/
}
