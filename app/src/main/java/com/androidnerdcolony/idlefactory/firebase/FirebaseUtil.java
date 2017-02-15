package com.androidnerdcolony.idlefactory.firebase;

import android.content.Context;
import android.support.annotation.NonNull;

import com.androidnerdcolony.idlefactory.R;
import com.androidnerdcolony.idlefactory.datalayout.FactoryLine;
import com.androidnerdcolony.idlefactory.datalayout.UserStates;
import com.androidnerdcolony.idlefactory.module.FactoryPreferenceManager;
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
 * Created by tiger on 2/8/2017.
 */

public class FirebaseUtil implements GoogleApiClient.OnConnectionFailedListener {

    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static FirebaseUser mUser = mAuth.getCurrentUser();
    private static FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();

    public FirebaseUtil() {
    }

    public static DatabaseReference getUserState(Context context) {
        return mDatabase.getReference(mUser.getUid()).child(context.getString(R.string.user_states));
    }

    public static DatabaseReference getFactoryLineState(Context context, int lineNumber) {
        DatabaseReference factoryLineState = getFactory(context);
        return factoryLineState.child(String.valueOf(lineNumber));
    }
    public static DatabaseReference getFactoryLineState(Context context, String key) {
        DatabaseReference factoryLineState = getFactory(context);
        return factoryLineState.child(key);
    }
    public static DatabaseReference getFactory(Context context){
        String factoryName = FactoryPreferenceManager.getPrefFactoryName(context);
        return getFactoryLines(context, factoryName);
    }
    public static DatabaseReference getFactoryLines(Context context, String factoryName) {
        return mDatabase.getReference(mUser.getUid()).child(context.getString(R.string.factories)).child(factoryName);
    }

    public static void setUserState(Context context, UserStates userState) {
        DatabaseReference userStateRef = getUserState(context);
        userStateRef.setValue(userState);
    }

    public static void setFactoryLine(Context context, FactoryLine line, int lineNumber) {
        DatabaseReference factoryRef = getFactoryLineState(context, lineNumber);
        factoryRef.setValue(line);
    }

    public static void setBalance(Context context, double balance) {
        DatabaseReference userState = getUserState(context);
        FactoryPreferenceManager.setPrefBalance(context, balance);
        userState.child(context.getString(R.string.db_balance)).setValue(balance);
    }

    public static void OpenLine(Context context, String lineNumber) {
        DatabaseReference factoryRef = getFactoryLineState(context, lineNumber);
        factoryRef.child("open").setValue(true);
    }
    public static void OpenLine(Context context, int lineNumber) {
        DatabaseReference factoryRef = getFactoryLineState(context, lineNumber);
        factoryRef.child("open").setValue(true);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public static void setWorkDate(Context context, long workDate, String key) {
        DatabaseReference factoryLineRef = getFactoryLineState(context, key);
        Timber.d("setWorkDate: factoryLIneRef: " + factoryLineRef.toString());
        Timber.d("key is = " + key);
        factoryLineRef.child("workDate").setValue(workDate);
    }

    public static void setLevel(Context context, int level, String key) {
        DatabaseReference factoryLineRef = getFactoryLineState(context, key);
        factoryLineRef.child("level").setValue(level);
    }
}
