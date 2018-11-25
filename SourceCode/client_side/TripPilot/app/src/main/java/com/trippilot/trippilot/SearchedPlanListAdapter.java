package com.trippilot.trippilot;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by arden on 2018-09-21.
 */

public class SearchedPlanListAdapter extends RecyclerView.Adapter<SearchedPlanViewHolder> {
    private Context context;
    private List<Plan> plan_list;

    public SearchedPlanListAdapter(Context context, List<Plan> plan_list){
        this.context = context;
        this.plan_list = plan_list;
    }

    @Override
    public SearchedPlanViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_list_item,null);
        return new SearchedPlanViewHolder(v);
    }

    @Override
    public void onBindViewHolder(SearchedPlanViewHolder holder, final int position) {
        if(plan_list.get(position).getImgurl()!=null)
            Glide.with(context).load(plan_list.get(position).getImgurl()).into(holder.searched_plan_image); //썸네일 등록
        holder.searched_plan_name.setText(plan_list.get(position).getTitle());
        holder.searched_plan_star_rating.setRating(plan_list.get(position).getScore());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//해당 플랜 화면으로 이동
                Intent intent = new Intent(context,PlanView.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("plan",plan_list.get(position));
                intent.putExtra("plan_type",2);
                context.startActivity(intent);// 다른 액티비티로 넘어감
            }
        });
    }

    @Override
    public int getItemCount() {
        return plan_list.size();
    }
}
