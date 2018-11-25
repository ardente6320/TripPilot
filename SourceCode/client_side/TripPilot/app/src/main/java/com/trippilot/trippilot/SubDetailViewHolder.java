package com.trippilot.trippilot;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by arden on 2018-10-08.
 */

public class SubDetailViewHolder  extends RecyclerView.ViewHolder{
    TextView ABUseTitle,ABUseMoney,NBTypetv,NBUseTypetv,NBSpendMoneytv;
    ImageButton ABshowDetail;
    RelativeLayout setVisible,detail_accountbook_item;
    Button delete_detail_accountbook_item;
    Button modify_detail_accountbook_item;
    View detail_ab_line;
    TextView won;
    public SubDetailViewHolder(View itemView) {
        super(itemView);
        detail_accountbook_item = (RelativeLayout)itemView.findViewById(R.id.detail_accountbook_item);
        setVisible = (RelativeLayout)itemView.findViewById(R.id.setVisible);
        ABUseTitle = (TextView)itemView.findViewById(R.id.ABUseTitle);
        ABUseMoney = (TextView)itemView.findViewById(R.id.ABUseMoney);
        won = (TextView)itemView.findViewById(R.id.won);
        NBTypetv = (TextView)itemView.findViewById(R.id.NBTypetv);
        NBUseTypetv = (TextView)itemView.findViewById(R.id.NBUseTypetv);
        NBSpendMoneytv = (TextView)itemView.findViewById(R.id.NBSpendMoneytv);
        ABshowDetail = (ImageButton)itemView.findViewById(R.id.ABshowDetail);
        delete_detail_accountbook_item = (Button)itemView.findViewById(R.id.delete_detail_accountbook_item);
        modify_detail_accountbook_item = (Button)itemView.findViewById(R.id.modify_detail_accountbook_item);
        detail_ab_line = (View)itemView.findViewById(R.id.detail_ab_line);
    }
}
