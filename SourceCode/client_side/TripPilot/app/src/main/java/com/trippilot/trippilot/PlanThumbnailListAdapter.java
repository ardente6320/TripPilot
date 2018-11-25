package com.trippilot.trippilot;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by arden on 2018-09-11.
 */

public class PlanThumbnailListAdapter extends RecyclerView.Adapter<PlanThumbnailViewHolder>{
    private Context context;
    private List<Plan>plan_list;
    private String id;
    public PlanThumbnailListAdapter(Context context, List<Plan>plan_list,String id){
        this.context = context;
        this.plan_list = plan_list;
        this.id = id;
    }
    @Override
    public PlanThumbnailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.thumb_plan_item_view,null);
        return new PlanThumbnailViewHolder(v);
    }

    @Override
    public void onBindViewHolder(PlanThumbnailViewHolder holder, final int position) {
        if(plan_list.get(position).getImgurl()!=null)
            Glide.with(context).load(plan_list.get(position).getImgurl()).into(holder.thumbnail); //썸네일 등록
        holder.thumbnail_title.setText(plan_list.get(position).getTitle());
        holder.writer.setText("작성자 : "+plan_list.get(position).getMember_id());
        holder.thumbnailcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//해당 플랜 화면으로 이동
                Intent intent = new Intent(context,PlanView.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("plan",plan_list.get(position));
                if(id.equals(plan_list.get(position).getMember_id()))
                    intent.putExtra("plan_type",1);//나의 계획임을 알린다.
                else
                    intent.putExtra("plan_type",2);//다른 사람 계획임을 알린다.
                context.startActivity(intent);// 다른 액티비티로 넘어감
            }
        });
    }

    @Override
    public int getItemCount() {
        return plan_list.size();
    }
}
