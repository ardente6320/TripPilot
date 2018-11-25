package com.trippilot.trippilot;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * Created by arden on 2018-09-11.
 */

public class PlanThumbnailViewHolder extends RecyclerView.ViewHolder {
    CardView thumbnailcard;
    ImageView thumbnail;
    TextView thumbnail_title,writer;
    public PlanThumbnailViewHolder(View itemView) {
        super(itemView);
        thumbnailcard = (CardView)itemView.findViewById(R.id.thumbnailcard);
        thumbnail = (ImageView)itemView.findViewById(R.id.thumbnail);
        thumbnail_title = (TextView)itemView.findViewById(R.id.thumbnail_title);
        writer = (TextView)itemView.findViewById(R.id.thumbnail_writer);
    }
}
