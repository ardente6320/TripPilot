package com.trippilot.trippilot;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by arden on 2018-09-20.
 */
interface DataAddListener{
    public void addContent(Location location);
}
public class LocationListAdapter extends RecyclerView.Adapter<LocationViewHolder> {
    private Context context;
    private List<Location> location_list;
    private int type; //1이면 검색창 2면 팝업 검색창
    private DataAddListener dataAddListener;
    public LocationListAdapter(Context context, List<Location> location_list, int type, DataAddListener dataAddListener){
        this.context = context;
        this.location_list = location_list;
        this.type = type;
        this.dataAddListener = dataAddListener;
    }

    @Override
    public LocationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_list_item,null);
        return new LocationViewHolder(v);
}

    @Override
    public void onBindViewHolder(final LocationViewHolder holder, final int position) {
        if(location_list.get(position).getImgurl()!=null)
            Glide.with(context).load(location_list.get(position).getImgurl()).into(holder.location_image); //썸네일 등록
        holder.location_name.setText(location_list.get(position).getName());
        holder.location_star_rating.setRating(Float.parseFloat(Double.toString(location_list.get(position).getScore())));
        if(type==2) {
            holder.add_to_schedule_btn.setVisibility(View.VISIBLE);
            holder.add_to_schedule_btn.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dataAddListener.addContent(location_list.get(position));

                    /*location_list.get(position).setChecked(!location_list.get(position).getChecked());
                    if(location_list.get(position).getChecked()){
                        holder.search_list_item.setBackgroundColor(Color.rgb(230,230,230));
                    }
                    else{
                        holder.search_list_item.setBackgroundColor(Color.rgb(255,255,255));
                    }*/
                }
            });
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//해당 플랜 화면으로 이동
                Intent intent = new Intent(context,LocationDetailView.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Bundle bundle = new Bundle();
                bundle.putSerializable("location",location_list.get(position));
                intent.putExtra("lc_bundle",bundle);
                context.startActivity(intent);// 다른 액티비티로 넘어감
            }
        });
    }
    @Override
    public int getItemCount() {
        return location_list.size();
    }
}
