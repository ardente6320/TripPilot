package com.trippilot.trippilot;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class AddDialog extends DialogFragment implements DataAddListener,DeleteContentListener{
    int type;
    List<Location> content_list;
    AddDialogListener listener;
    AddContentDialogAdapter addContentDialogAdapter;
    TextView empty_content;
    public void setType(int type){this.type = type;}
    public void setAddDialogListener(AddDialogListener listener){
        this.listener = listener;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.add_content_dialog,container,false);
        RecyclerView add_content_list= (RecyclerView)v.findViewById(R.id.add_content_list);
        empty_content = (TextView)v.findViewById(R.id.empty_add_list);
        LinearLayoutManager layoutmanager = new LinearLayoutManager(getContext());
        layoutmanager.setOrientation(LinearLayoutManager.HORIZONTAL);
        add_content_list.setHasFixedSize(true);
        add_content_list.setLayoutManager(layoutmanager);
        content_list = new ArrayList<Location>();
        addContentDialogAdapter = new AddContentDialogAdapter(AddDialog.this.getContext(),content_list,this);
        add_content_list.setAdapter(addContentDialogAdapter);
        final SearchedLocationListView locFragment = new SearchedLocationListView();
        locFragment.setData(2,this);
        getChildFragmentManager().beginTransaction().replace(R.id.add_content_frame,locFragment).commit();
        ((Button)v.findViewById(R.id.add_location_content_btn)).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Location> loc_list = addContentDialogAdapter.getData();
                listener.onClicked(loc_list);
            }
        });
        ((Button)v.findViewById(R.id.cancel_location_content_btn)).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void addContent(Location location) {
        content_list.add(location);
        if(content_list.size()!=0)
            empty_content.setVisibility(View.GONE);
        addContentDialogAdapter.notifyDataSetChanged();
    }

    @Override
    public void deleteContent(List<Location> list) {
        content_list = list;
        if(content_list.size()==0)
            empty_content.setVisibility(View.VISIBLE);
        addContentDialogAdapter.notifyDataSetChanged();
    }
}
