package com.trippilot.trippilot;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Created by arden on 2018-10-25.
 */

public class OptionView extends Fragment {
    private ListView option_list;
    private String[] str_list = {"정보 수정","로그아웃"};
    private ArrayAdapter<String> adapter;
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.option_view,container,false);
        option_list = (ListView)v.findViewById(R.id.option_list);
        adapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,str_list);
        option_list.setAdapter(adapter);
        option_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = null;
                SharedPreferences pref = getContext().getSharedPreferences("TripPilotAutoLogin", Activity.MODE_PRIVATE);
                switch (i){
                    case 0:
                        intent = new Intent(getContext(), MemberView.class);
                        String id = pref.getString("TripPilotID",null);
                        intent.putExtra("member_id",id);
                        intent.putExtra("type",1);
                        startActivityForResult(intent,100);
                        break;
                    case 1:
                        SharedPreferences.Editor editor = pref.edit();
                        editor.clear();
                        editor.commit();
                        intent = new Intent(getContext(), LoginView.class);
                        startActivity(intent);
                        getActivity().finish();
                        break;
                }
            }
        });
        return v;
    }
}
