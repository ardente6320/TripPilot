package com.trippilot.trippilot;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by arden on 2018-11-19.
 */
interface DeleteContentListener{
    public void deleteContent(List<Location> list);
}
public class AddContentDialogAdapter extends RecyclerView.Adapter<AddContentViewHolder> {
    List<Location> list;
    Context context;
    DeleteContentListener deleteContentListener;
    public AddContentDialogAdapter(Context context,List<Location> list,DeleteContentListener deleteContentListener){
        this.list = list;
        this.context = context;
        this.deleteContentListener = deleteContentListener;
    }

    @Override
    public AddContentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.addcontent_dialog_item,null);
        return new AddContentViewHolder(v);
    }

    @Override
    public void onBindViewHolder(AddContentViewHolder holder, final int position) {
        holder.added_content_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, LocationDetailView.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Bundle bundle = new Bundle();
                bundle.putSerializable("location",list.get(position));
                intent.putExtra("lc_bundle",bundle);
                context.startActivity(intent);
            }
        });
        if(list.get(position).getImgurl()!=null)
            Glide.with(context).load(list.get(position).getImgurl()).into(holder.added_content_img);
        holder.added_content_name.setText(list.get(position).getName());
        int area = list.get(position).getArea_code();
        switch (area){
            case 1:
                holder.added_content_area.setText("지역 : 서울");break;
            case 2:
                holder.added_content_area.setText("지역 : 인천");break;
            case 3:
                holder.added_content_area.setText("지역 : 대전");break;
            case 4:
                holder.added_content_area.setText("지역 : 대구");break;
            case 5:
                holder.added_content_area.setText("지역 : 광주");break;
            case 6:
                holder.added_content_area.setText("지역 : 부산");break;
            case 7:
                holder.added_content_area.setText("지역 : 울산");break;
            case 8:
                holder.added_content_area.setText("지역 : 세종특별자치시");break;
            case 31:
                holder.added_content_area.setText("지역 : 경기도");break;
            case 32:
                holder.added_content_area.setText("지역 : 강원도");break;
            case 33:
                holder.added_content_area.setText("지역 : 충청북도");break;
            case 34:
                holder.added_content_area.setText("지역 : 충청남도");break;
            case 35:
                holder.added_content_area.setText("지역 : 경상북도");break;
            case 36:
                holder.added_content_area.setText("지역 : 경상남도");break;
            case 37:
                holder.added_content_area.setText("지역 : 전라북도");break;
            case 38:
                holder.added_content_area.setText("지역 : 전라남도");break;
            case 39:
                holder.added_content_area.setText("지역 : 제주도");break;
        }
        holder.delete_added_content_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Location temp = list.get(position);
                list.remove(position);
                int size = list.size();
                for(int i =position; i<size; i++){
                    Location loc = list.get(i);
                    loc.setOrder(loc.getOrder()-1);
                    list.set(i,loc);
                }
                deleteContentListener.deleteContent(list);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public List<Location> getData(){
        return list;
    }
}