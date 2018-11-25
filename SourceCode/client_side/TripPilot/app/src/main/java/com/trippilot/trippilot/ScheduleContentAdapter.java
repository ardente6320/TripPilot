package com.trippilot.trippilot;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.bumptech.glide.Glide;

import java.util.Collections;
import java.util.List;

/**
 * Created by arden on 2018-09-12.
 */

public class ScheduleContentAdapter extends RecyclerView.Adapter<ScheduleContentViewHolder> {
    private Context context;
    private List<Location> list_location;
    private int p_position; //일정 번호
    private int type;
    private OnDataChangedListener onDataChangedListener;
    interface OnDataChangedListener{
        void dataChanged(List<Location> list_location,Location location ,int position);
    }

    public  ScheduleContentAdapter(Context context, List<Location> list_location, OnDataChangedListener onDataChangedListener,int p_position,int type){
        this.context = context;
        this.list_location = list_location;
        this.onDataChangedListener = onDataChangedListener;
        this.p_position = p_position;
        this.type = type;
    }

    @Override
    public ScheduleContentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.schedulecontent_itemcard_view,null);
        return new ScheduleContentViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ScheduleContentViewHolder holder, final int position) {
        if(list_location.get(position).getImgurl()!=null)
            Glide.with(context).load(list_location.get(position).getImgurl()).into(holder.content_img);
        if(type ==1) {
            holder.delete_content_btn.setVisibility(View.GONE);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, LocationDetailView.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("location",list_location.get(position));
                    intent.putExtra("lc_bundle",bundle);
                    context.startActivity(intent);
                }
            });
        }
        else if(type ==2) {
            holder.delete_content_btn.setVisibility(View.VISIBLE);
            holder.delete_content_btn.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Location temp = list_location.get(position);
                    list_location.remove(position);
                    int size = list_location.size();
                    for(int i =position; i<size; i++){
                        Location loc = list_location.get(i);
                        loc.setOrder(loc.getOrder()-1);
                        list_location.set(i,loc);
                    }
                    onDataChangedListener.dataChanged(list_location,temp,p_position);
                    notifyDataSetChanged();
                }
            });
        }
        holder.content_name.setText(list_location.get(position).getName());
        int area = list_location.get(position).getArea_code();
        switch (area){
            case 1:
                holder.content_area.setText("지역 : 서울");break;
            case 2:
                holder.content_area.setText("지역 : 인천");break;
            case 3:
                holder.content_area.setText("지역 : 대전");break;
            case 4:
                holder.content_area.setText("지역 : 대구");break;
            case 5:
                holder.content_area.setText("지역 : 광주");break;
            case 6:
                holder.content_area.setText("지역 : 부산");break;
            case 7:
                holder.content_area.setText("지역 : 울산");break;
            case 8:
                holder.content_area.setText("지역 : 세종특별자치시");break;
            case 31:
                holder.content_area.setText("지역 : 경기도");break;
            case 32:
                holder.content_area.setText("지역 : 강원도");break;
            case 33:
                holder.content_area.setText("지역 : 충청북도");break;
            case 34:
                holder.content_area.setText("지역 : 충청남도");break;
            case 35:
                holder.content_area.setText("지역 : 경상북도");break;
            case 36:
                holder.content_area.setText("지역 : 경상남도");break;
            case 37:
                holder.content_area.setText("지역 : 전라북도");break;
            case 38:
                holder.content_area.setText("지역 : 전라남도");break;
            case 39:
                holder.content_area.setText("지역 : 제주도");break;
        }
    }

    @Override
    public int getItemCount() {
        return list_location.size();
    }
}