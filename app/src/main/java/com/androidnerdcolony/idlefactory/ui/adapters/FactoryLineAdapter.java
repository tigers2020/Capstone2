package com.androidnerdcolony.idlefactory.ui.adapters;

import android.app.Activity;
import android.content.Context;
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
import com.androidnerdcolony.idlefactory.firebase.FirebaseUtil;
import com.androidnerdcolony.idlefactory.module.ConvertNumber;
import com.androidnerdcolony.idlefactory.module.FactoryPreferenceManager;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import static com.androidnerdcolony.idlefactory.R.string.open;

/**
 * Created by tiger on 1/23/2017.
 */

public class FactoryLineAdapter extends FirebaseListAdapter<FactoryLine> {

    private Context context;
    private List<FactoryLine> lines;
    private DatabaseReference mUserDataRef;
    public FactoryLineAdapter(Activity activity, Class modelClass, Query ref) {
        super(activity, modelClass, R.layout.factory_line, ref);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        new FactoryPreferenceManager();

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

        double balance = Double.valueOf(FactoryPreferenceManager.getPrefBalance(context));
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

        Animation animation = AnimationUtils.loadAnimation(context, R.anim.worker_move);
        int duration = line.getConfigTime() * 10;
        animation.setDuration(duration);
        //repeat working process
        holder.workerView.setAnimation(animation);

        holder.factoryLineOpenButton.setOnClickListener(new ClickHandler(line, key));
        holder.factoryLineUpgradeButton.setOnClickListener(new ClickHandler(line, key));
        if (line.isOpen()) {
            holder.factoryLineOpenButton.setVisibility(View.GONE);
            holder.factoryLineUpgradeButton.setVisibility(View.VISIBLE);
            holder.workerView.animate();
        } else {
            holder.factoryLineOpenButton.setVisibility(View.VISIBLE);
            holder.factoryLineUpgradeButton.setVisibility(View.GONE);
            holder.workerView.clearAnimation();
        }

    }

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
                    FirebaseUtil.setBalance(context, balance);
                    FirebaseUtil.OpenLine(context, key);
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
                    FirebaseUtil.setBalance(context, balance);
                    level = level + 1;
                    factoryRef.child("level").setValue(level);
                    FactoryPreferenceManager.setPrefLevel(context, level, key);
                    FirebaseUtil.setLevel(context, level, key);


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
