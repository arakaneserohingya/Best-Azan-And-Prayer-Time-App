package com.alnamaa.engineering.azan.Display;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;


import com.alnamaa.engineering.azan.R;

import java.text.NumberFormat;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;


public class _DisplaySET {
    public final static String displayFile = "display";
    public static final int THEME_WHITE = 0,THEME_BLACK=1;


    public static void setLanguagePreferences(Context context) {
        firstTime(context);
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
    public static NumberFormat getNumberFormat(Context context){
        SharedPreferences pref = context.getSharedPreferences(displayFile,Context.MODE_PRIVATE);
        switch (pref.getInt("numbers_language",0)){
            case 0: return NumberFormat.getInstance(new Locale("en"));
            case 1: return NumberFormat.getInstance(new Locale("ar","EG"));
            default: if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    return NumberFormat.getInstance(context.getResources().getConfiguration().getLocales().get(0));
                } else return NumberFormat.getInstance(context.getResources().getConfiguration().locale);
        }
    }

    public static void firstTime(Context context){
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

    public static void clear(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(displayFile,MODE_PRIVATE).edit();
        editor.clear();
        editor.commit();
        firstTime(context);
    }

    public static int getAppTheme(Context context) {
        return context.getSharedPreferences(displayFile,MODE_PRIVATE).getInt("theme",THEME_WHITE);
    }

    public static int getPrimaryColor(Context context) {
         return getAppTheme(context) == THEME_WHITE?R.color.colorPrimaryWhite:R.color.colorPrimaryBlack;
    }

    public static String formatStringNumbers(Context context,String str) {
        NumberFormat nf = getNumberFormat(context);
        String s = "";
        for(int i=0;i<str.length();i++){
            if(str.charAt(i) >= '0' && str.charAt(i) <= '9') s+= nf.format(Integer.parseInt(str.substring(i,i+1)));
            else s+= str.charAt(i);
        }
        return s;
    }
}
