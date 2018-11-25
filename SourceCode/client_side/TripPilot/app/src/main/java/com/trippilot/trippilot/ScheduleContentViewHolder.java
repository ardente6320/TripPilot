package com.trippilot.trippilot;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by arden on 2018-09-12.
 */

public class ScheduleContentViewHolder extends RecyclerView.ViewHolder {
    CardView schedule_content;
    ImageView content_img;
    ImageButton delete_content_btn;
    TextView content_name,content_area;
    public ScheduleContentViewHolder(View itemView) {
        super(itemView);
        schedule_content = (CardView)itemView.findViewById(R.id.schedule_content_card);
        content_img = (ImageView)itemView.findViewById(R.id.content_img);
        delete_content_btn = (ImageButton)itemView.findViewById(R.id.delete_content_btn);
        content_name = (TextView)itemView.findViewById(R.id.content_name);
        content_area = (TextView)itemView.findViewById(R.id.content_area);
    }
}
