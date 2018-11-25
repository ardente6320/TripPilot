package com.trippilot.trippilot;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by arden on 2018-10-08.
 */

public class AccountBookViewHolder extends RecyclerView.ViewHolder {
    RelativeLayout accountBookItem;
    TextView ABName;
    TextView ABBalance;
    public AccountBookViewHolder(final View itemView) {
        super(itemView);
        accountBookItem = (RelativeLayout) itemView.findViewById(R.id.accountBookItem);
        ABName = (TextView) itemView.findViewById(R.id.ABName);
        ABBalance = (TextView)itemView.findViewById(R.id.ABbalance);

    }
}
