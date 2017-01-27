package com.androidnerdcolony.idlefactory.ui.adapters;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnerdcolony.idlefactory.R;
import com.androidnerdcolony.idlefactory.datalayout.FactoryLine;
import com.androidnerdcolony.idlefactory.module.ConvertNumber;
import com.androidnerdcolony.idlefactory.module.FactoryPreferenceManager;
import com.androidnerdcolony.idlefactory.module.FactoryWorking;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import static android.R.attr.level;
import static com.androidnerdcolony.idlefactory.R.string.balance;
import static com.androidnerdcolony.idlefactory.R.string.open;
import static com.androidnerdcolony.idlefactory.R.string.truck;

/**
 * Created by tiger on 1/23/2017.
 */

public class FactoryLineAdapter extends FirebaseListAdapter<FactoryLine> {

    private Context context;
    private List<FactoryLine> lines;
    private DatabaseReference mUserDataRef;

    int workTimeInterval = 100 ;
    Handler workHandler;
    public FactoryLineAdapter(Activity activity, Class modelClass, Query ref) {
        super(activity, modelClass, R.layout.factory_line, ref);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            mUserDataRef = database.getReference(user.getUid());
        }
        context = activity;
        lines = new ArrayList<>();
    }


    @Override
    protected void populateView(View v, final FactoryLine line, final int position) {
        lines.add(position, line);
        final String key = getRef(position).getKey();
        final ViewHolder holder = new ViewHolder(v);
        if (line.isOpen()) {
            holder.factoryLineOpenButton.setVisibility(View.GONE);
            holder.factoryLineUpgradeButton.setVisibility(View.VISIBLE);
        }
        double openCost = line.getOpenCost();

        String openCostString = ConvertNumber.numberToString(openCost);
        String openString = context.getString(open) + "\n" + openCostString;

        holder.factoryLineOpenButton.setText(openString);
        holder.lineLevelView.setText(String.valueOf(line.getLevel()));

        holder.factoryLineOpenButton.setTag(position);
        holder.factoryLineUpgradeButton.setTag(position);
        holder.factoryLineOpenButton.setEnabled(false);

        double lineCost = line.getLineCost();
        int level = line.getLevel();

        for (int i = 0; i < level; i++) {
            lineCost = lineCost + (lineCost * 0.1);
        }

        String lineCostString = context.getString(R.string.upgrade) + "\n" + ConvertNumber.numberToString(lineCost);
        holder.factoryLineUpgradeButton.setText(lineCostString);
        final double finalLineCost = lineCost;
        mUserDataRef.child(context.getString(R.string.user_states)).child(context.getString(R.string.db_balance)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    double balance = dataSnapshot.getValue(Double.class);
                    if (balance < line.getOpenCost()) {
                        holder.factoryLineOpenButton.setEnabled(false);
                    } else {
                        holder.factoryLineOpenButton.setEnabled(true);
                    }
                    if (balance < finalLineCost) {
                        holder.factoryLineUpgradeButton.setEnabled(false);
                    } else {
                        holder.factoryLineUpgradeButton.setEnabled(true);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        holder.factoryLineOpenButton.setOnClickListener(new ClickHandler(line, key));
        holder.factoryLineUpgradeButton.setOnClickListener(new ClickHandler(line, key));
        if (line.isOpen()) {
            holder.factoryLineOpenButton.setVisibility(View.GONE);
            holder.factoryLineUpgradeButton.setVisibility(View.VISIBLE);



        } else {
            holder.factoryLineOpenButton.setVisibility(View.VISIBLE);
            holder.factoryLineUpgradeButton.setVisibility(View.GONE);
        }


        //repeat working process
        workTimeInterval = 100 * line.getConfigTime();

        WorkingProgress workingProgress = new WorkingProgress(holder);
        holder.workingProgressBar.setMax(workTimeInterval);
        workHandler = new Handler();

        final Runnable workChecker = new Runnable() {
            @Override
            public void run() {
                Timber.d(key + " working");
                workHandler.postDelayed(this, workTimeInterval);
            }
        };
        if (line.isOpen()) {
            workHandler.postDelayed(workChecker, workTimeInterval);
        }else{
            workHandler.removeCallbacks(workChecker);
        }
    }

    private void workDone(FactoryLine line, ViewHolder holder) {
        double workProfit = line.getWorkCapacity();
        String workProfitString = ConvertNumber.numberToString(workProfit);
        holder.workProfitView.setVisibility(View.VISIBLE);
        holder.workProfitView.setText(workProfitString);
        holder.workProfitView.setAnimation(AnimationUtils.loadAnimation(context, R.anim.work_profit));
        holder.workProfitView.animate();


    }


    private class WorkingProgress extends AsyncTask<Integer, Integer, Double> {

        ViewHolder holder;
        public WorkingProgress(ViewHolder holder){
            this.holder = holder;
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            int progress = values[0];
            if (progress < 100) {
                holder.workingProgressBar.setProgress(progress);
            }
        }

        @Override
        protected void onPostExecute(Double profit) {
            super.onPostExecute(profit);
            holder.workingProgressBar.setVisibility(View.INVISIBLE);
            holder.workingProgressBar.setProgress(0);

            String profitString = ConvertNumber.numberToString(profit);
            holder.workProfitView.setText(profitString);
            Animation profitAni = AnimationUtils.loadAnimation(context, R.anim.work_profit);
            holder.workProfitView.startAnimation(profitAni);

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            holder.workingProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Double doInBackground(Integer... params) {

            double profit = 100;
                for (int i = 0; i < params[0]; i++) {
                    try {
                        Thread.sleep(100);
                        publishProgress(i);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            return profit;
        }
    };

    private class ClickHandler implements View.OnClickListener {

        FactoryLine line;
        double balance;
        String key;
        DatabaseReference factoryRef;

        ClickHandler(FactoryLine line, String key) {
            this.line = line;
            this.key = key;
        }

        @Override
        public void onClick(View view) {
//        mClickHandler.onClick(view);
            int id = view.getId();
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

            String PrefBalance = FactoryPreferenceManager.getPrefBalance(context);
            balance = Double.valueOf(PrefBalance);
            String factoryName = FactoryPreferenceManager.getPrefFactoryName(context);
            factoryRef = mUserDataRef.child(context.getString(R.string.factories)).child(factoryName).child(key);

            switch (id) {
                case R.id.factory_line_open_button:
                    Timber.d("Line = " + line.getLineCost());
                    double openCost = line.getOpenCost();
                    //need to get line information.
                    Timber.d("onClick Balance = " + balance);
                    if (balance < openCost){
                        Toast.makeText(context, "balance is not enough", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    balance = balance - openCost;
                    mUserDataRef.child(context.getString(R.string.user_states)).child(context.getString(R.string.db_balance)).setValue(balance);
                    mUserDataRef.child(context.getString(R.string.db_factories)).child(factoryName).child(key).child("open").setValue(true);


                    break;
                case R.id.factory_line_upgrade_button:
                    Timber.d("Line = " + line.getLineCost());
                    double lineCost = line.getLineCost();
                    int level = FactoryPreferenceManager.getPrefLevel(context, key);
                    double upgradeCost = lineCost;
                    for (int i = 0; i < level; i++) {
                        upgradeCost = upgradeCost + (upgradeCost * 0.1);
                    }

                    if (balance < upgradeCost ){
                        Toast.makeText(context, "balance is not enough", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    balance = balance - upgradeCost;
                    mUserDataRef.child(context.getString(R.string.user_states)).child(context.getString(R.string.db_balance)).setValue(balance);
                    Timber.d("level before: " + level);
                    level = level + 1;
                    factoryRef.child("level").setValue(level);
                    Timber.d("level after: " + level);
                    FactoryPreferenceManager.setPrefLevel(context, level, key);


                    //need to get line information..
                    break;

            }
        }
    }

    public class ViewHolder {

        @BindView(R.id.worker)
        ImageView workerView;
        @BindView(R.id.manager)
        ImageView managerView;
        @BindView(R.id.factory_line_box)
        ImageView factoryLineBoxView;
        @BindView(R.id.factory_line_open_button)
        Button factoryLineOpenButton;
        @BindView(R.id.factory_line_upgrade_button)
        Button factoryLineUpgradeButton;
        @BindView(R.id.working_progress)
        ProgressBar workingProgressBar;
        @BindView(R.id.work_profits)
        TextView workProfitView;
        @BindView(R.id.line_level)
                TextView lineLevelView;

        View view;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
            this.view = view;

        }
    }
}
