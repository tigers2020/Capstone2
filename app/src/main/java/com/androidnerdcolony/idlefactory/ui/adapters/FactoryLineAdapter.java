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
import com.google.firebase.database.Query;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by tiger on 1/23/2017.
 */

public class FactoryLineAdapter extends FirebaseListAdapter<FactoryLine> implements View.OnClickListener {

    private Context context;
    private FactoryClickHandler mClickHandler;
    private List<FactoryLine> lines;
    public FactoryLineAdapter(Activity activity, Class modelClass, Query ref) {
        super(activity, modelClass, R.layout.factory_line, ref);
        context = activity;
    }
    interface FactoryClickHandler {
        void onClick(View view, FactoryLine line);
    }

    @Override
    protected void populateView(View v, FactoryLine line, int position) {
        lines.add(position, line);
        ViewHolder holder = new ViewHolder(v);
        mClickHandler = new FactoryClickHandler() {
            @Override
            public void onClick(View view, FactoryLine line) {
                int id = view.getId();
                switch (id) {
                    case R.id.factory_line_upgrade_button:
                        Toast.makeText(context, "Upgrade Clicked", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.factory_line_open_button:
                        Toast.makeText(context, "Open Clicked", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };

        if (line.isOpen()) {
            holder.factoryLineOpenButton.setVisibility(View.GONE);
            holder.factoryLineUpgradeButton.setVisibility(View.VISIBLE);
        }
        double openCost = line.getOpenCost();

        String openCostString = ConvertNumber.numberToString(openCost);
        String openString = context.getString(R.string.open) + "\n" + openCostString;

        holder.factoryLineOpenButton.setText(openString);

        holder.factoryLineOpenButton.setOnClickListener(this);
        holder.factoryLineUpgradeButton.setOnClickListener(this);

        if (line.isOpen()){
            holder.factoryLineOpenButton.setVisibility(View.GONE);
            holder.factoryLineUpgradeButton.setVisibility(View.VISIBLE);
        }else {
            holder.factoryLineOpenButton.setVisibility(View.VISIBLE);
            holder.factoryLineUpgradeButton.setVisibility(View.GONE);
        }


    }

    @Override
    public void onClick(View view) {

//        mClickHandler.onClick(view);
        int id = view.getId();
        switch (id) {
            case R.id.factory_line_upgrade_button:
                Toast.makeText(context, "Upgrade Clicked", Toast.LENGTH_SHORT).show();
                //need to get line information.
                break;
            case R.id.factory_line_open_button:
                Toast.makeText(context, "Open Clicked", Toast.LENGTH_SHORT).show();
                //need to get line information..
                break;

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
