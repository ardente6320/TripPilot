package com.trippilot.trippilot;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

/**
 * Created by arden on 2018-09-11.
 */

public class PlanListViewHolder extends RecyclerView.ViewHolder {
    RelativeLayout plan_list_item;
    TextView plan_name;
    Switch scope;
    public PlanListViewHolder(View itemView) {
        super(itemView);
        plan_list_item = (RelativeLayout)itemView.findViewById(R.id.plan_list_item);
        plan_name = (TextView)itemView.findViewById(R.id.plan_name);
        scope = (Switch)itemView.findViewById(R.id.scope);
    }
}
