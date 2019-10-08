package com.alhadara.omar.azan.Activities.SettingsLayouts;

import android.app.Activity;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.omar.azanapkmostafa.R;

import static android.content.Context.MODE_PRIVATE;

public class _SET {
    private final static String settingsFile = "settings.txt";
    private static SharedPreferences settingsPref;
    private static SharedPreferences.Editor settingsEditor;
    public static void startSettings(Activity settingsAc){
        settingsPref = settingsAc.getSharedPreferences(settingsFile,MODE_PRIVATE);
        settingsEditor = settingsPref.edit();
        if(settingsPref.getBoolean("firsttime",true)) firstTime();
    }


    public static boolean isActivated(View view){
        return settingsPref.getBoolean("status" + view.getId(),true);
    }
    public static void setStatus(View view){
        view.setClickable(isActivated(view));
        view.setAlpha(isActivated(view)?1.0f:0.2f);
    }
    public static void setStatus(View view,boolean bool){
        settingsEditor.putBoolean("status"+view.getId(),bool);
        settingsEditor.commit();
        view.setClickable(bool);
        view.setAlpha(bool?1.0f:0.2f);
    }
    public static boolean isChecked(View view) {
        return settingsPref.getBoolean("checked"+view.getId(),false);
    }
    public static void setCheckBox(View view) {
        CheckBox box = view.findViewById(R.id.settings_check_box_checkbox);
        box.setChecked(isChecked(view));
    }
    public static void setCheckBox(View view,boolean bool) {
        CheckBox box = view.findViewById(R.id.settings_check_box_checkbox);
        box.setChecked(bool);
        settingsEditor.putBoolean("checked"+view.getId(),bool);
        settingsEditor.commit();
    }
    public static void setDescription(ViewGroup group) {
        ((TextView) group.getChildAt(1)).setText(((TextView) group.getChildAt(1)).getText().toString() +settingsPref.getString("description"+group.getId(),""));
    }
    public static void setDescription(ViewGroup group,String description) {
        String lastDesc = settingsPref.getString("description"+group.getId(),"");
        String apex = ((TextView) group.getChildAt(1)).getText().toString().replace(lastDesc,"");
        settingsEditor.putString("description"+group.getId(),description);
        settingsEditor.commit();
        ((TextView) group.getChildAt(1)).setText(apex + description);
    }
    public static int generateViewID(int l1,int l2,int l3){
        return (l1*10000)+(l2*100)+(l3);
    }

    private static void firstTime() {
        settingsEditor.putBoolean("firsttime",false);
        settingsEditor.putBoolean("checked40200",true);
        settingsEditor.putBoolean("checked40500",false);
        settingsEditor.putBoolean("status40600",false);
        settingsEditor.putBoolean("checked40800",true);
        settingsEditor.putBoolean("checked41100",false);
        settingsEditor.putBoolean("status41200",false);
        settingsEditor.putBoolean("checked41300",false);
        settingsEditor.putBoolean("checked41400",false);
        settingsEditor.putBoolean("checked40102",true);
        settingsEditor.putBoolean("checked40104",true);
        settingsEditor.putBoolean("checked40106",true);
        settingsEditor.putBoolean("checked40108",true);
        settingsEditor.putBoolean("checked40110",true);
        settingsEditor.putBoolean("checked40112",true);
        settingsEditor.putString("description40101","0");
        settingsEditor.putString("description40103","0");
        settingsEditor.putString("description40105","0");
        settingsEditor.putString("description40107","0");
        settingsEditor.putString("description40109","0");
        settingsEditor.putString("description40111","0");
        settingsEditor.putBoolean("checked40702",true);
        settingsEditor.putBoolean("checked40704",true);
        settingsEditor.putBoolean("checked40706",true);
        settingsEditor.putBoolean("checked40708",true);
        settingsEditor.putBoolean("checked40710",true);
        settingsEditor.putBoolean("checked40712",true);
        settingsEditor.putString("description40701","0");
        settingsEditor.putString("description40703","0");
        settingsEditor.putString("description40705","0");
        settingsEditor.putString("description40707","0");
        settingsEditor.putString("description40709","0");
        settingsEditor.putString("description40711","0");
        settingsEditor.putBoolean("checked40402",false);
        settingsEditor.putBoolean("status40403",false);
        settingsEditor.putBoolean("checked40404",false);
        settingsEditor.putBoolean("status40405",false);
        settingsEditor.putBoolean("status40406",false);
        settingsEditor.putBoolean("status40407",false);
        settingsEditor.putBoolean("status40408",false);
        settingsEditor.putBoolean("status40409",false);
        settingsEditor.putBoolean("checked41002",false);
        settingsEditor.putBoolean("status41003",false);
        settingsEditor.putBoolean("checked40425",false);
        settingsEditor.putBoolean("status40435",false);
        settingsEditor.putBoolean("checked40426",false);
        settingsEditor.putBoolean("status40436",false);
        settingsEditor.putBoolean("checked40427",false);
        settingsEditor.putBoolean("status40437",false);
        settingsEditor.putBoolean("checked40428",false);
        settingsEditor.putBoolean("status40438",false);
        settingsEditor.putBoolean("checked40429",false);
        settingsEditor.putBoolean("status40439",false);
        settingsEditor.commit();
    }
}
