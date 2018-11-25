package com.trippilot.trippilot;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;


/**
 * Created by arden on 2018-09-12.
 */

public class CommentViewHolder extends RecyclerView.ViewHolder {
    LinearLayout comment_item,modify_comment_visibility;
    TextView comment_writer,comment_score,comment_contents;
    RatingBar comment_star_rating;
    Button modify_comment_btn,delete_comment_btn;
    public CommentViewHolder(View itemView) {
        super(itemView);
        comment_item = (LinearLayout) itemView.findViewById(R.id.comment_item);
        modify_comment_visibility = (LinearLayout)itemView.findViewById(R.id.modify_comment_visibility);
        comment_writer = (TextView)itemView.findViewById(R.id.comment_writer);
        comment_score = (TextView)itemView.findViewById(R.id.comment_score);
        comment_contents = (TextView)itemView.findViewById(R.id.comment_contents);
        comment_star_rating = (RatingBar)itemView.findViewById(R.id.comment_star_rating);
        modify_comment_btn = (Button)itemView.findViewById(R.id.modify_comment_btn);
        delete_comment_btn = (Button)itemView.findViewById(R.id.delete_comment_btn);
    }
}
