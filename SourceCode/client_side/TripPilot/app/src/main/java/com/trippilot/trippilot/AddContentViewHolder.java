package com.trippilot.trippilot;

import android.media.Image;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by arden on 2018-11-19.
 */

public class AddContentViewHolder extends RecyclerView.ViewHolder {
    CardView added_content_card;
    ImageView added_content_img;
    TextView added_content_name,added_content_area;
    ImageButton delete_added_content_btn;
    public AddContentViewHolder(View itemView) {
        super(itemView);
        added_content_card = (CardView)itemView.findViewById(R.id.added_content_card);
        added_content_img = (ImageView)itemView.findViewById(R.id.added_content_img);
        added_content_name = (TextView) itemView.findViewById(R.id.added_content_name);
        added_content_area = (TextView)itemView.findViewById(R.id.added_content_area);
        delete_added_content_btn = (ImageButton)itemView.findViewById(R.id.delete_added_content_btn);
    }
}
