package com.trippilot.trippilot;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by arden on 2018-09-12.
 */

public class ScheduleViewHolder extends RecyclerView.ViewHolder {
    RelativeLayout schedule_item_layout,add_content_btn;
    TextView day,EmptySchedule;
    RecyclerView schedules;
    public ScheduleViewHolder(View itemView) {
        super(itemView);
        schedule_item_layout = (RelativeLayout) itemView.findViewById(R.id.schedule_item_layout) ;
        day = (TextView)itemView.findViewById(R.id.day);
        add_content_btn = (RelativeLayout) itemView.findViewById(R.id.add_content_btn);
        schedules = (RecyclerView)itemView.findViewById(R.id.schedules);
        EmptySchedule = (TextView)itemView.findViewById(R.id.EmptySchedule);
    }
}
