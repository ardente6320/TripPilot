package com.trippilot.trippilot;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by arden on 2018-10-08.
 */

public class DetailListAdapter extends RecyclerView.Adapter<DetailViewHolder> {
    Context context;
    List<AccountBookDate> date_list;
    DataSetChangedListener dataSetChangedListener;
    public  DetailListAdapter(Context context,List<AccountBookDate> date_list,DataSetChangedListener dataSetChangedListener)
    {
        this.context = context;
        this.date_list = date_list;
        this.dataSetChangedListener = dataSetChangedListener;
    }
    @Override
    public DetailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.day_accountbook_content,null);
        return new DetailViewHolder(v);
    }

    @Override
    public void onBindViewHolder(DetailViewHolder holder, int position) {
        holder.ABDate.setText(date_list.get(position).getDate().toString());
        LinearLayoutManager lastlayout = new LinearLayoutManager(context);
        lastlayout.setOrientation(LinearLayoutManager.VERTICAL);
        holder.detailListRecycle.setLayoutManager(lastlayout);
        SubDetailAdapter adapter = new SubDetailAdapter(context,date_list.get(position).getUsagehistory_list(),dataSetChangedListener);
        holder.detailListRecycle.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return date_list.size();
    }
}