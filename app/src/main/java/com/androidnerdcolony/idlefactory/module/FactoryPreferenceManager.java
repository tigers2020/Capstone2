package com.androidnerdcolony.idlefactory.module;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;

import com.androidnerdcolony.idlefactory.R;
import com.androidnerdcolony.idlefactory.datalayout.FactoryLine;

/**
 * Created by tiger on 1/24/2017.
 */

public class FactoryPreferenceManager {


    public static void setPrefBalance(Context context, double balance){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor= preferences.edit();
        editor.putString(context.getString(R.string.db_balance), String.valueOf(balance));
        editor.apply();

    }
    public static String getPrefBalance(Context context) {
        String balance;
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(context);
        balance = preference.getString(context.getString(R.string.db_balance), "");

        return balance;
    }

    public static void setPrefFactoryName(Context context, String factoryName){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(context.getString(R.string.db_factories), factoryName);

        editor.apply();
    }
    public static String getPrefFactoryName(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        return preferences.getString(context.getString(R.string.db_factories), context.getString(R.string.db_factory_1));

    }
    public static void setPrefLevel(Context context, int lineLevel, String key){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();

        String lineLevelString = key + "_level";
        editor.putInt(lineLevelString, lineLevel);
        editor.apply();
    }

    public static int getPrefLevel(Context context, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String lineLevel = key + "_level";
        return preferences.getInt(lineLevel, 1);
    }

    public static void setDefaultFactoryLine(FactoryLine defaultFactoryLine, int i, Context context) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putBoolean("line_"+i+"_isOpen", defaultFactoryLine.isOpen());
        editor.putBoolean("line_"+i+"_isWorking", defaultFactoryLine.isWorking());
        editor.putString("line_"+i+"_workCapacity", String.valueOf(defaultFactoryLine.getWorkCapacity()));


    }
}
