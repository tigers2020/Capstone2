package com.androidnerdcolony.idlefactory.module;

import android.content.Intent;
import android.os.AsyncTask;

import java.util.Map;

/**
 * Created by tiger on 1/24/2017.
 */

public class FactoryWorking extends AsyncTask<Map, Integer, Double> {

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Double aDouble) {
        super.onPostExecute(aDouble);
    }

    @Override
    protected Double doInBackground(Map... maps) {
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }
}
