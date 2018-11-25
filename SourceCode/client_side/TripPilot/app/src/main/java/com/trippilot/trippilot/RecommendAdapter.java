package com.trippilot.trippilot;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arden on 2018-11-08.
 */
//스와이프 구현 해야함
public class RecommendAdapter extends RecyclerView.Adapter<RecommendViewHolder>{
    private int size;
    private Context context;
    private List<String> recommend_area = new ArrayList<String>(); //추천 지역
    private List<Integer> area_code = new ArrayList<Integer>();
    private List<String> recommend_sigungu = new ArrayList<String>(); // 추천 시군구
    private List<Integer>city_code = new ArrayList<Integer>();
    private List<Integer> selected_area = new ArrayList<Integer>();
    private int[] sarea = new int[2];
    private ApplicationInfo appInfo;
    private ArrayAdapter<String> area_adapter;
    public RecommendAdapter(Context context,int size,ApplicationInfo appInfo){
        this.size = size;
        this.context = context;
        this.appInfo = appInfo;
        getAreaData();
        getCityData(area_code.get(0));
        area_adapter = new ArrayAdapter<String>(context,R.layout.spinner_item,recommend_area);
        area_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }
    @Override
    public RecommendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recommend_item,null);
        return new RecommendViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final RecommendViewHolder holder, final int position) {
        holder.title.setText("지역"+(position+1));
        holder.area_list.setAdapter(area_adapter);
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.accountbook_longclick_dialog);
                ((TextView)dialog.findViewById(R.id.longclick_title)).setText(holder.title.getText().toString());
                String[] str = {"삭제"};
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1,str);
                ListView list = (ListView)dialog.findViewById(R.id.ac_longclick_dialog);
                list.setAdapter(adapter);
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        switch (i){
                            case 0:
                                if(size <=1)
                                    break;
                                size--;
                                notifyDataSetChanged();
                                break;
                        }
                        dialog.dismiss();
                    }
                });
                dialog.show();
                return false;
            }
        });
        holder.area_list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                sarea[position] = area_code.get(i);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

    @Override
    public int getItemCount() {
        return size;
    }

    public List<Integer> getSelectedArea(){
        for(int i =0; i<size;i++)
            selected_area.add(sarea[i]);
        return selected_area;
    }

    public void setSize(int size){
        this.size = size;
        notifyDataSetChanged();
    }

    private void getAreaData() {
        JSONObject json_data = null;
        try {
            if (appInfo!= null) {
                json_data = new APIConnector(context).execute(appInfo.metaData.getString("areaCode"),"numOfRows","17").get();
            }
            json_data = (JSONObject) json_data.get("response");
            json_data = (JSONObject) json_data.get("body");
            json_data = (JSONObject) json_data.get("items");
            JSONArray listArray = (JSONArray) json_data.get("item");
            int size = listArray.length();
            for(int i =0; i<size; i++){
                json_data = listArray.getJSONObject(i);
                area_code.add(json_data.getInt("code"));
                recommend_area.add(json_data.getString("name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getCityData(int code) {
        try {
            city_code.clear();
            recommend_sigungu.clear();
            JSONObject city_data = new APIConnector(context).execute(appInfo.metaData.getString("areaCode"), "areaCode", Integer.toString(code),"numOfRows","50").get();
            city_data = (JSONObject) city_data.get("response");
            city_data = (JSONObject) city_data.get("body");
            city_data = (JSONObject) city_data.get("items");
            JSONArray listArray = (JSONArray) city_data.get("item");
            int size = listArray.length();
            for(int i =0; i<size; i++){
                city_data = listArray.getJSONObject(i);
                city_code.add(city_data.getInt("code"));
                recommend_sigungu.add(city_data.getString("name"));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
