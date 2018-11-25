package com.trippilot.trippilot;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

/**
 * Created by arden on 2018-09-21.
 */

public class SearchedPlanViewHolder extends RecyclerView.ViewHolder {
    ImageView searched_plan_image;
    TextView searched_plan_name;
    RatingBar searched_plan_star_rating;
    public SearchedPlanViewHolder(View itemView) {
        super(itemView);
        searched_plan_name = (TextView)itemView.findViewById(R.id.searched_name);
        searched_plan_star_rating = (RatingBar)itemView.findViewById(R.id.searched_star_rating);
        searched_plan_image = (ImageView)itemView.findViewById(R.id.searched_image);
    }
}
