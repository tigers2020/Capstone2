package com.androidnerdcolony.idlefactory.sync;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

import com.androidnerdcolony.idlefactory.datalayout.FactoryLine;
import com.androidnerdcolony.idlefactory.firebase.FirebaseUtil;
import com.androidnerdcolony.idlefactory.module.FactoryPreferenceManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

/**
 * Created by tiger on 2/10/2017.
 */

public class FactoryWork {
    private static AsyncTask<Void, Void, Void> mWorkTask;

    private static void scheduleWork(final Context context) {
        final Handler handler = new Handler();

        handler.post(new Runnable() {
            @Override
            public void run() {
                mWorkTask = new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        calculateIdleCash(context);
                        working(context);
                        return null;
                    }
                };

                mWorkTask.execute();
                handler.postDelayed(this, 1000);
            }
        });

    }

    private static void calculateIdleCash(final Context context) {
        DatabaseReference factoryLineStateRef = FirebaseUtil.getFactory(context);
        factoryLineStateRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot factoryLineShot : dataSnapshot.getChildren()){
                    String key = factoryLineShot.getKey();
                    FactoryLine line = factoryLineShot.getValue(FactoryLine.class);
                    double workCapacity = line.getWorkCapacity();
                    long workProgressTime = line.getConfigTime();
                    double idleCash = workCapacity / (workProgressTime/100);
                    FirebaseUtil.setIdleCash(context, key, idleCash);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private static void working(final Context context) {
        DatabaseReference factoryLineStateRef = FirebaseUtil.getFactory(context);

        factoryLineStateRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                double balance = Double.valueOf(FactoryPreferenceManager.getPrefBalance(context));
                for (DataSnapshot factoryLineShot : dataSnapshot.getChildren()) {
                    String key = factoryLineShot.getKey();
                    FactoryLine line = factoryLineShot.getValue(FactoryLine.class);
                    long currentDate = new Date().getTime();
                    long workDate = line.getWorkDate();
                    if (workDate <= 0) {
                        workDate = currentDate;
                    }
                    if (line.isOpen()) {
                        if (workDate <= currentDate) {
                            double workCapacity = line.getWorkCapacity();
                            int level = line.getLevel();
                            double workProfit = workCapacity + ((workCapacity * 0.09) * level);
                            balance = balance + workProfit;
                            workDate = currentDate + line.getConfigTime();
                            FirebaseUtil.setBalance(context, balance);
                            FirebaseUtil.setWorkDate(context, workDate, key);
                        }
                    }

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    synchronized public static void initialize(Context context) {
        scheduleWork(context);
    }
}
