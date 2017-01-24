package com.androidnerdcolony.idlefactory.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.androidnerdcolony.idlefactory.R;
import com.androidnerdcolony.idlefactory.datalayout.FactoryLine;
import com.androidnerdcolony.idlefactory.module.ConvertNumber;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

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

        if (user != null) {
            mUserDataRef = database.getReference(user.getUid());
        }
        context = activity;
        lines = new ArrayList<>();
    }


    @Override
    protected void populateView(View v, final FactoryLine line, final int position) {
        lines.add(position, line);
        final ViewHolder holder = new ViewHolder(v);
        if (line.isOpen()) {
            holder.factoryLineOpenButton.setVisibility(View.GONE);
            holder.factoryLineUpgradeButton.setVisibility(View.VISIBLE);
        }
        double openCost = line.getOpenCost();

        String openCostString = ConvertNumber.numberToString(openCost);
        String openString = context.getString(R.string.open) + "\n" + openCostString;

        holder.factoryLineOpenButton.setText(openString);

        holder.factoryLineOpenButton.setTag(position);
        holder.factoryLineUpgradeButton.setTag(position);
        holder.factoryLineOpenButton.setEnabled(false);

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
                }
            }

            @Override

            public void onCancelled(DatabaseError databaseError) {

            }
        });


        holder.factoryLineOpenButton.setOnClickListener(new ClickHandler(line));
        holder.factoryLineUpgradeButton.setOnClickListener(new ClickHandler(line));
        if (line.isOpen()) {
            holder.factoryLineOpenButton.setVisibility(View.GONE);
            holder.factoryLineUpgradeButton.setVisibility(View.VISIBLE);
        } else {
            holder.factoryLineOpenButton.setVisibility(View.VISIBLE);
            holder.factoryLineUpgradeButton.setVisibility(View.GONE);
        }
    }

    private class ClickHandler implements View.OnClickListener {

        FactoryLine line;
        double balance;

        public ClickHandler(FactoryLine line) {
            this.line = line;
        }

        @Override
        public void onClick(View view) {
//        mClickHandler.onClick(view);
            int id = view.getId();

            switch (id) {
                case R.id.factory_line_upgrade_button:
                    Toast.makeText(context, "Upgrade Clicked", Toast.LENGTH_SHORT).show();
                    Timber.d("Line = " + line.getLineCost());
                    //need to get line information.
                    break;
                case R.id.factory_line_open_button:
                    Toast.makeText(context, "Open Clicked", Toast.LENGTH_SHORT).show();
                    Timber.d("Line = " + line.getOpenCost());


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

        View view;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
            this.view = view;

        }
    }
}
