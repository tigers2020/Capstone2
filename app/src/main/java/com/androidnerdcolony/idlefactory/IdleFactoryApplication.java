package com.androidnerdcolony.idlefactory;

import android.app.Application;

import timber.log.Timber;

/**
 * Created by tiger on 1/23/2017.
 */

public class IdleFactoryApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Timber.plant(new Timber.DebugTree());
    }
}
