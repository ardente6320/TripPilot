package com.trippilot.trippilot;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * Created by arden on 2018-11-08.
 */

public class RecommendViewHolder extends RecyclerView.ViewHolder {
    Spinner area_list;
    TextView title;
    public RecommendViewHolder(View itemView) {
        super(itemView);
        title = (TextView)itemView.findViewById(R.id.recommend_area);
        area_list = (Spinner)itemView.findViewById(R.id.recommend_area_spinner);
    }
}
