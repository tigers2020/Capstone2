package com.androidnerdcolony.idlefactory.module;

import android.content.Context;

/**
 * Created by tiger on 1/24/2017.
 */

public class FactoryWorking {
    public FactoryWorking(Context context){
        new FactoryPreferenceManager();
        boolean working = FactoryPreferenceManager.getPrefWorking(context);
    }
}
