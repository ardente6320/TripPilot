package com.trippilot.trippilot;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by arden on 2018-09-20.
 */

public class LocationViewHolder extends RecyclerView.ViewHolder {
    RelativeLayout search_list_item;
    TextView location_name;
    RatingBar location_star_rating;
    ImageButton add_to_schedule_btn;
    ImageView location_image;
    public LocationViewHolder(View itemView) {
        super(itemView);
        search_list_item = (RelativeLayout) itemView.findViewById(R.id.search_list_item);
        location_name = (TextView)itemView.findViewById(R.id.searched_name);
        location_star_rating = (RatingBar)itemView.findViewById(R.id.searched_star_rating);
        add_to_schedule_btn = (ImageButton)itemView.findViewById(R.id.add_to_schedule_btn);
        location_image = (ImageView)itemView.findViewById(R.id.searched_image);
    }
}
