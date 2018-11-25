package com.trippilot.trippilot;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by arden on 2018-10-08.
 */

public class DetailViewHolder extends RecyclerView.ViewHolder {
    LinearLayout detailaccountBook;
    TextView totalMoney;
    TextView ABDate;
    RecyclerView detailRecyclerView,detailListRecycle;

    public DetailViewHolder(final View itemView) {
        super(itemView);
        detailaccountBook = (LinearLayout)itemView.findViewById(R.id.detail_accountbook_item);
        ABDate = (TextView) itemView.findViewById(R.id.ABDate);
        detailRecyclerView = (RecyclerView)itemView.findViewById(R.id.dtABList);
        detailListRecycle = (RecyclerView)itemView.findViewById(R.id.detailListRecycle);
        totalMoney = (TextView)itemView.findViewById(R.id.totalMoney);
    }
}

