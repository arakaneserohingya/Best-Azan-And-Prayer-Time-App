package com.alhadara.omar.azan.Backup;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.alhadara.omar.azan.Alarms._AlarmSET;
import com.alhadara.omar.azan.Constants;
import com.alhadara.omar.azan.Display._DisplaySET;
import com.alhadara.omar.azan.Locations._LocationSET;
import com.alhadara.omar.azan.Settings._SET;
import com.alhadara.omar.azan.Times._TimesSET;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class _BackupSET {
    public final static int TYPE_SETTINGS = 1,TYPE_LOCATIONS = 2;
    private static final int ACCESS_SD_PERMISSION = 1 ;
    private static final String BACKUP_FOLDER = "/" + Constants.APP_NAME + "Backup";

    public static void checkForPermissions(Activity activity){
        if(ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    ACCESS_SD_PERMISSION);
        }
        if(ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    ACCESS_SD_PERMISSION);
        }
    }
    public static String[] listSaved(int type){
        File dir = new File(Environment.getExternalStorageDirectory().getPath()+ BACKUP_FOLDER + (type==TYPE_SETTINGS?"/svdsettings":"/svdlocations"));
        return dir.list();
    }
    public static void backup(Context context,String dstName,int type){
        if(type == TYPE_SETTINGS){
            saveSharedPreferencesToFile(context,"/svdsettings/" + dstName, _AlarmSET.azanFile);
            saveSharedPreferencesToFile(context,"/svdsettings/" + dstName, _AlarmSET.iqamaFile);
            saveSharedPreferencesToFile(context,"/svdsettings/" + dstName, _AlarmSET.sahoorFile);
            saveSharedPreferencesToFile(context,"/svdsettings/" + dstName, _AlarmSET.silentFile);
            saveSharedPreferencesToFile(context,"/svdsettings/" + dstName, _DisplaySET.displayFile);
            saveSharedPreferencesToFile(context,"/svdsettings/" + dstName, _SET.settingsFile);
            saveSharedPreferencesToFile(context,"/svdsettings/" + dstName, _TimesSET.adjustTimesFile);
            saveSharedPreferencesToFile(context,"/svdsettings/" + dstName, _TimesSET.prayersFile);
        } else {
            saveSharedPreferencesToFile(context,"/svdlocations/" + dstName, _LocationSET.currentLocation);
            saveSharedPreferencesToFile(context,"/svdlocations/" + dstName, _LocationSET.locationsFile);
            SharedPreferences locs = context.getSharedPreferences(_LocationSET.locationsFile,MODE_PRIVATE);
            for(int i=1;i<=locs.getInt("locationsnumber",0);i++) {
                String name = locs.getString("location"+i,"");
                saveSharedPreferencesToFile(context,"/svdlocations/" + dstName, name);
            }
        }
    }
    public static boolean saveSharedPreferencesToFile(Context context, String dstName ,String prefName) {
        boolean res = false;
        File dir = new File(Environment.getExternalStorageDirectory().getPath() + BACKUP_FOLDER +  dstName);
        dir.mkdirs();
        File dst = new File(dir,prefName);
        ObjectOutputStream output = null;
        try {
            output = new ObjectOutputStream(new FileOutputStream(dst));
            SharedPreferences pref =
                    context.getSharedPreferences(prefName, MODE_PRIVATE);
            output.writeObject(pref.getAll());

            res = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if (output != null) {
                    output.flush();
                    output.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return res;
    }

    public static void restore(Context context, String dstName, int type) {
        if(type == TYPE_SETTINGS){
            loadSharedPreferencesFromFile(context,"/svdsettings/" + dstName, _AlarmSET.azanFile);
            loadSharedPreferencesFromFile(context,"/svdsettings/" + dstName, _AlarmSET.iqamaFile);
            loadSharedPreferencesFromFile(context,"/svdsettings/" + dstName, _AlarmSET.sahoorFile);
            loadSharedPreferencesFromFile(context,"/svdsettings/" + dstName, _AlarmSET.silentFile);
            loadSharedPreferencesFromFile(context,"/svdsettings/" + dstName, _DisplaySET.displayFile);
            loadSharedPreferencesFromFile(context,"/svdsettings/" + dstName, _SET.settingsFile);
            loadSharedPreferencesFromFile(context,"/svdsettings/" + dstName, _TimesSET.adjustTimesFile);
            loadSharedPreferencesFromFile(context,"/svdsettings/" + dstName, _TimesSET.prayersFile);
        } else {
            loadSharedPreferencesFromFile(context,"/svdlocations/" + dstName, _LocationSET.currentLocation);
            loadSharedPreferencesFromFile(context,"/svdlocations/" + dstName, _LocationSET.locationsFile);
            SharedPreferences locs = context.getSharedPreferences(_LocationSET.locationsFile,MODE_PRIVATE);
            for(int i=1;i<=locs.getInt("locationsnumber",0);i++) {
                String name = locs.getString("location"+i,"");
                loadSharedPreferencesFromFile(context,"/svdlocations/" + dstName, name);
            }
        }
    }

    @SuppressWarnings({ "unchecked" })
    public static boolean loadSharedPreferencesFromFile(Context context, String dstName, String prefName) {
        boolean res = false;
        ObjectInputStream input = null;
        try {
            File dir = new File(Environment.getExternalStorageDirectory().getPath() + BACKUP_FOLDER +  dstName);
            File src = new File(dir,prefName);
            input = new ObjectInputStream(new FileInputStream(src));
            SharedPreferences.Editor prefEdit = context.getSharedPreferences(prefName, MODE_PRIVATE).edit();
            prefEdit.clear();
            Map<String, ?> entries = (Map<String, ?>) input.readObject();
            for (Map.Entry<String, ?> entry : entries.entrySet()) {
                Object v = entry.getValue();
                String key = entry.getKey();

                if (v instanceof Boolean)
                    prefEdit.putBoolean(key, ((Boolean) v).booleanValue());
                else if (v instanceof Float)
                    prefEdit.putFloat(key, ((Float) v).floatValue());
                else if (v instanceof Integer)
                    prefEdit.putInt(key, ((Integer) v).intValue());
                else if (v instanceof Long)
                    prefEdit.putLong(key, ((Long) v).longValue());
                else if (v instanceof String)
                    prefEdit.putString(key, ((String) v));
            }
            prefEdit.commit();
            res = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }finally {
            try {
                if (input != null) {
                    input.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return res;
    }

    public static void clear(Context context,int type) {
        if(type == TYPE_SETTINGS){
            _AlarmSET.clear(context);
            _DisplaySET.clear(context);
            _SET.clear(context);
            _TimesSET.clear(context);
        }
    }
}
