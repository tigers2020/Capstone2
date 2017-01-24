package com.androidnerdcolony.idlefactory.module;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;

import com.androidnerdcolony.idlefactory.R;

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
}
