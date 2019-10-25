package com.alhadara.omar.azan.Display;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;

import androidx.core.content.res.ResourcesCompat;

import com.alhadara.omar.azan.Activities.MainActivity;
import com.alhadara.omar.azan.Locations._LocationSET;
import com.alhadara.omar.azan.Settings._SET;
import com.example.omar.azanapkmostafa.R;


import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;


public class _DisplaySET {
    public static String displayFile = "display.txt";


    public static void setLanguagePreferences(Context context) {
        firsttime(context);
        SharedPreferences pref = context.getSharedPreferences(displayFile,Context.MODE_PRIVATE);
        Configuration configuration = context.getResources().getConfiguration();
        Locale locale = new Locale(pref.getString("language","en"));
        configuration.setLocale(locale);
        configuration.setLayoutDirection(locale);
        context.getResources().updateConfiguration(configuration, context.getResources().getDisplayMetrics());
    }

    public static Typeface getTypeFace(Context context) {
        SharedPreferences pref = context.getSharedPreferences(displayFile,Context.MODE_PRIVATE);
        switch (pref.getInt("font",0)){
            case 0: return pref.getString("language","en").equals("ar")?
                    ResourcesCompat.getFont(context, R.font.general_arabic):ResourcesCompat.getFont(context, R.font.general);
            case 1: return Typeface.create("sans-serif",Typeface.NORMAL);
            case 2: return Typeface.create("sans-serif",Typeface.BOLD);
            default: return ResourcesCompat.getFont(context, R.font.general);
        }
    }

    public static void firsttime(Context context){
        SharedPreferences pref = context.getSharedPreferences(displayFile,Context.MODE_PRIVATE);
        if(!pref.getBoolean("firsttime",true)) return;
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("firsttime",false);
        editor.putString("language","en");
        editor.putInt("numbers_language",0);
        editor.putInt("theme",0);
        editor.putInt("font",0);
        for(int i=0;i<5;i++) editor.putInt("widget_theme_"+i,0);
        editor.commit();
    }

    public static boolean isTime24(Context context) {
        return context.getSharedPreferences(displayFile,MODE_PRIVATE).getBoolean("time24",false);
    }
}
