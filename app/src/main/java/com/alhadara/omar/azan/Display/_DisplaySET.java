package com.alhadara.omar.azan.Display;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import com.alhadara.omar.azan.Activities.MainActivity;
import com.alhadara.omar.azan.Locations._LocationSET;


import java.util.Locale;


public class _DisplaySET {
    public static String displayFile = "display.txt";


    public static void setLanguagePreferences(Context context) {
        SharedPreferences pref = context.getSharedPreferences(displayFile,Context.MODE_PRIVATE);
        Configuration configuration = context.getResources().getConfiguration();
        Locale locale = new Locale(pref.getString("language","en"));
        configuration.setLocale(locale);
        configuration.setLayoutDirection(locale);
        context.getResources().updateConfiguration(configuration, context.getResources().getDisplayMetrics());
    }

}
