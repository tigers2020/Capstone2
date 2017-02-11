package com.androidnerdcolony.idlefactory.module;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.androidnerdcolony.idlefactory.R;
import com.androidnerdcolony.idlefactory.datalayout.FactoryLine;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by tiger on 1/24/2017.
 */

public class FactoryPreferenceManager implements GoogleApiClient.OnConnectionFailedListener {

    static FirebaseAuth auth;
    static FirebaseUser user;
    static FirebaseDatabase mDatabase;
    static DatabaseReference mUserDataRef;

    public FactoryPreferenceManager() {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();
        mUserDataRef = mDatabase.getReference(user.getUid());
    }

    public static void setPrefBalance(Context context, double balance) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(context.getString(R.string.db_balance), String.valueOf(balance));
        editor.apply();
        mUserDataRef.child(context.getString(R.string.db_user_states))
                .child(context.getString(R.string.db_balance))
                .setValue(balance);

    }

    public static String getPrefBalance(Context context) {
        String balance;
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(context);
        balance = preference.getString(context.getString(R.string.db_balance), "");

        return balance;
    }

    public static void setPrefFactoryName(Context context, String factoryName) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(context.getString(R.string.db_factories), factoryName);

        editor.apply();
    }

    public static String getPrefFactoryName(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        return preferences.getString(context.getString(R.string.db_factories), context.getString(R.string.db_factory_1));

    }

    public static void setPrefLevel(Context context, int lineLevel, String key) {
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

        editor.putBoolean("line_" + i + "_isOpen", defaultFactoryLine.isOpen());
        editor.putBoolean("line_" + i + "_isWorking", defaultFactoryLine.isWorking());
        editor.putString("line_" + i + "_workCapacity", String.valueOf(defaultFactoryLine.getWorkCapacity()));


    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public static boolean getPrefWorking(Context context, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isWorking = preferences.getBoolean(key + "_" + context.getString(R.string.db_is_working), context.getResources().getBoolean(R.bool.default_boolean));
        return isWorking;
    }
    public static void  setPrefWorking(Context context, String key, boolean isWorking){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key + "_" + context.getString(R.string.db_is_working), isWorking);
        mUserDataRef.child(context.getString(R.string.db_factories))
                .child(getPrefFactoryName(context))
                .child(key).child("working").setValue(isWorking);
        editor.apply();


    }

    public static FactoryLine getFactoryLine(Context context, String lineNumber) {
        final FactoryLine[] line = {new FactoryLine()};
        mUserDataRef.child(context.getString(R.string.db_factories)).child(getPrefFactoryName(context))
                .child(lineNumber).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                line[0] = dataSnapshot.getValue(FactoryLine.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return line[0];
    }
}
