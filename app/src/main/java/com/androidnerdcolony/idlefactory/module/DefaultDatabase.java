package com.androidnerdcolony.idlefactory.module;

import android.content.Context;

import com.androidnerdcolony.idlefactory.R;
import com.androidnerdcolony.idlefactory.datalayout.FactoryLine;
import com.androidnerdcolony.idlefactory.datalayout.UserStates;
import com.google.firebase.database.DatabaseReference;

import timber.log.Timber;

/**
 * Created by tiger on 1/23/2017.
 */
public class DefaultDatabase {

    public static void SetNewDatabase(Context context, DatabaseReference userDataRef) {
        Timber.d("initialize SetNewDatabase");

        DatabaseReference factoryRef = userDataRef.child(context.getString(R.string.factories)).child(context.getString(R.string.db_factory_1));

        FactoryLine defaultFactoryLine = setDefaultFactoryLine(context);

        for (int i = 0; i < 10; i++) {
            factoryRef.child(String.valueOf(i)).setValue(setFactoryLines(defaultFactoryLine, i));
            FactoryPreferenceManager.setDefaultFactoryLine(defaultFactoryLine, i, context);

        }
        UserStates userStates = setDefaultUserState(context);
        userDataRef.child(context.getString(R.string.user_states)).setValue(userStates);

    }

    private static UserStates setDefaultUserState(Context context) {
        UserStates defaultState = new UserStates();

        defaultState.setBalance(500);
        defaultState.setExp(0);
        defaultState.setLevel(1);
        defaultState.setPrestige(0);
        return defaultState;
    }

    private static FactoryLine setFactoryLines(FactoryLine defaultFactoryLine, int i) {
        FactoryLine currentLine = defaultFactoryLine;
        double openCost = defaultFactoryLine.getOpenCost();
        double lineCost = defaultFactoryLine.getLineCost();
        double workCapacity = defaultFactoryLine.getWorkCapacity();
        int defaultTime = defaultFactoryLine.getConfigTime();
        for (int j = 0; j < i; j++) {
            openCost = openCost * (14 + (j*2));
            lineCost = openCost + (openCost * (9+(j*2))/100);
            workCapacity = workCapacity * (100+(j*10));
            defaultTime = defaultTime * 2;

        }
        currentLine.setOpenCost(openCost);
        currentLine.setLineCost(lineCost);
        currentLine.setWorkCapacity(workCapacity);
        currentLine.setConfigTime(defaultTime);


        return currentLine;
    }

    private static FactoryLine setDefaultFactoryLine(Context context) {
        FactoryLine factoryLine = new FactoryLine();
        factoryLine.setIdleCash(1);
        factoryLine.setLevel(1);
        factoryLine.setConfigQuality(context.getResources().getInteger(R.integer.factory_line_default_config));
        factoryLine.setConfigTime(context.getResources().getInteger(R.integer.factory_line_default_time));
        factoryLine.setConfigValue(context.getResources().getInteger(R.integer.factory_line_default_config));
        factoryLine.setOpen(false);
        factoryLine.setWorking(false);
        factoryLine.setOpenCost(context.getResources().getInteger(R.integer.factory_line_default_open_cost));
        factoryLine.setLineCost(context.getResources().getInteger(R.integer.factory_line_default_upgrade_cost));
        factoryLine.setWorkCapacity(500);
        factoryLine.setLineNumber(1);

        return factoryLine;
    }
}
