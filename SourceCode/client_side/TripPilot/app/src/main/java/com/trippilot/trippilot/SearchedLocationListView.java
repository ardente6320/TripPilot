package com.trippilot.trippilot;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by arden on 2018-09-11.
 */

public class SearchedLocationListView extends android.support.v4.app.Fragment implements View.OnClickListener{
    private RecyclerView search_location_list;
    private TextView no_search_item;
    private ProgressBar searched_location_progressbar;
    private LocationListAdapter adapter;
    private List<Location> location_list;
    private Spinner category_area, category_city,category_thema;
    private ImageButton search_btn;
    private List<Integer> code, content_type,city_code;
    private List<String> area,city;
    private EditText search_txt;
    String keyword;
    private ArrayAdapter<String> city_adapter;
    ArrayAdapter<String> area_adapter;
    ApplicationInfo appInfo;
    int type, pageNo=1,totalCount;
    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 10;
    int firstVisibleItem, visibleItemCount, totalItemCount;
    private Activity activity;
    private DataAddListener dataAddListener;

    public void setData(int type, DataAddListener dataAddListener){
        this.type = type;
        this.dataAddListener = dataAddListener;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.search_location_view,container,false);
        search_location_list = (RecyclerView)v.findViewById(R.id.searched_location_list);
        searched_location_progressbar = (ProgressBar)v.findViewById(R.id.searched_location_progressbar);
        no_search_item = (TextView)v.findViewById(R.id.no_search_item);
        category_area = (Spinner)v.findViewById(R.id.category_location_area);
        category_thema = (Spinner)v.findViewById(R.id.category_location_thema);
        category_city = (Spinner)v.findViewById(R.id.category_location_city);
        search_txt = (EditText)v.findViewById(R.id.search_location_edt);
        search_btn = (ImageButton)v.findViewById(R.id.search_location_btn);
        search_btn.setOnClickListener(this);
        keyword = "N";
        category_area.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                getCityData(code.get(i));
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        location_list = new ArrayList<Location>();
        final LinearLayoutManager layoutmanager = new LinearLayoutManager(getContext());
        layoutmanager.setOrientation(LinearLayoutManager.VERTICAL);
        search_location_list.setHasFixedSize(true);
        search_location_list.setLayoutManager(layoutmanager);
        code = new ArrayList<Integer>();
        city_code = new ArrayList<Integer>();
        content_type = new ArrayList<Integer>();
        area = new ArrayList<String>();
        city = new ArrayList<String>();
        String[] category = {"관광지","문화시설","축제/공연/행사","레포츠","쇼핑","맛집"};
        content_type.add(12);content_type.add(14);content_type.add(15);content_type.add(28);content_type.add(38);content_type.add(39);
        ArrayAdapter<String> category_adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_dropdown_item,category);
        category_thema.setAdapter(category_adapter);
        area_adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_dropdown_item,area);
        category_area.setAdapter(area_adapter);
        city_adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, city);
        category_city.setAdapter(city_adapter);
        adapter = new LocationListAdapter(getContext(),location_list,type,dataAddListener);
        search_location_list.setAdapter(adapter);
        search_location_list.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                visibleItemCount = search_location_list.getChildCount();
                totalItemCount = layoutmanager.getItemCount();
                firstVisibleItem = layoutmanager.findFirstVisibleItemPosition();
                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false;
                        previousTotal = totalItemCount;
                    }
                }
                if (!loading && (totalItemCount - visibleItemCount)
                        <= (firstVisibleItem + visibleThreshold)) {
                    if((double)totalCount/10>pageNo) {
                        pageNo++;
                        getData(pageNo);
                        loading = true;
                    }
                }
            }
        });
        try {
            appInfo = getActivity().getPackageManager().getApplicationInfo(getActivity().getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        activity = this.getActivity();
        new Thread(new Runnable() {
            @Override
            public void run() {
                getCategoryData();
                getCityData(code.get(0));
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getData(pageNo);
                    }
                });
            }
        }).start();
        return v;
    }
    private void getCategoryData() {
        JSONObject json_data = null;
        try {
            if (appInfo.metaData != null) {
                json_data = new APIConnector(getContext()).execute(appInfo.metaData.getString("areaCode"),"numOfRows","17").get();
            }
            json_data = (JSONObject) json_data.get("response");
            json_data = (JSONObject) json_data.get("body");
            json_data = (JSONObject) json_data.get("items");
            JSONArray listArray = (JSONArray) json_data.get("item");
            int size = listArray.length();
            for(int i =0; i<size; i++){
                json_data = listArray.getJSONObject(i);
                code.add(json_data.getInt("code"));
                area.add(json_data.getString("name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                area_adapter.notifyDataSetChanged();
                category_area.setSelection(0);
            }
        });
    }

    private void getCityData(int code) {
        try {
            city_code.clear();
            city.clear();
            APIConnector api = new APIConnector(this.getContext());
            JSONObject city_data = new APIConnector(getContext()).execute(appInfo.metaData.getString("areaCode"), "areaCode", Integer.toString(code),"numOfRows","50").get();
            city_data = (JSONObject) city_data.get("response");
            city_data = (JSONObject) city_data.get("body");
            city_data = (JSONObject) city_data.get("items");
            try {
                JSONArray listArray = (JSONArray) city_data.get("item");
                int size = listArray.length();
                for (int i = 0; i < size; i++) {
                    city_data = listArray.getJSONObject(i);
                    city_code.add(city_data.getInt("code"));
                    city.add(city_data.getString("name"));
                }
            }catch (Exception e){
                city_data = city_data.getJSONObject("item");
                city_code.add(city_data.getInt("code"));
                city.add(city_data.getString("name"));
            }
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    city_adapter.notifyDataSetChanged();
                    category_city.setSelection(0);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void getData(final int pageNo) {
        try {
            JSONObject json_data = null;
            String ctype = Integer.toString(content_type.get(category_thema.getSelectedItemPosition()));
            String acode = Integer.toString(code.get(category_area.getSelectedItemPosition()));
            String ccode = Integer.toString(city_code.get(category_city.getSelectedItemPosition()));
            if (search_txt.getText().toString().replace(" ", "").equals("")) {
                json_data = new APIConnector(getContext()).execute(appInfo.metaData.getString("areaBasedList"),
                        "contentTypeId", ctype,
                        "areaCode", acode,
                        "sigunguCode", ccode,
                        "pageNo", Integer.toString(pageNo)).get();
            } else {
                json_data = new APIConnector(getContext()).execute(appInfo.metaData.getString("searchKeyword"),
                        "contentTypeId", ctype,
                        "areaCode", acode,
                        "sigunguCode", ccode
                        , "keyword", search_txt.getText().toString(),
                        "pageNo", Integer.toString(pageNo)).get();
            }
            String content_id = null;
            Location loc = null;
            json_data = json_data.getJSONObject("response");
            json_data = json_data.getJSONObject("body");
            totalCount = json_data.getInt("totalCount");
            json_data = json_data.getJSONObject("items");
            try {
                JSONArray searchlist = json_data.getJSONArray("item");
                int size = searchlist.length();
                if (size == 0) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            no_search_item.setVisibility(View.VISIBLE);
                            searched_location_progressbar.setVisibility(View.GONE);
                        }
                    });
                } else{
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            no_search_item.setVisibility(View.GONE);
                        }
                    });
                }
                for (int i = 0; i < size; i++) {
                    json_data = searchlist.getJSONObject(i);
                    content_id = json_data.getString("contentid");
                    try {
                        loc = new Location("", content_id, 0, json_data.getString("firstimage")
                                , json_data.getString("title"), null, json_data.getInt("areacode"),
                                json_data.getInt("sigungucode"), json_data.getInt("contenttypeid"), 0, json_data.getDouble("mapx"), json_data.getDouble("mapy"));
                    } catch (Exception e) {
                        loc = new Location("", content_id, 0, null
                                , json_data.getString("title"), null, json_data.getInt("areacode"),
                                json_data.getInt("sigungucode"), json_data.getInt("contenttypeid"), 0, json_data.getDouble("mapx"), json_data.getDouble("mapy"));
                    }
                    json_data = new ServerConnector().execute(appInfo.metaData.getString("select_star_rating"), "content_id", content_id).get();
                    loc.setScore(Float.parseFloat(json_data.getString("star_rating")));
                    location_list.add(loc);
                }
            } catch (Exception e) {
                json_data = json_data.getJSONObject("item");
                content_id = json_data.getString("contentid");
                try {
                    loc = new Location("", content_id, 0, json_data.getString("firstimage")
                            , json_data.getString("title"), null, json_data.getInt("areacode"),
                            json_data.getInt("sigungucode"), json_data.getInt("contenttypeid"), 0, json_data.getDouble("mapx"), json_data.getDouble("mapy"));
                } catch (Exception e1) {
                    loc = new Location("", content_id, 0, null
                            , json_data.getString("title"), null, json_data.getInt("areacode"),
                            json_data.getInt("sigungucode"), json_data.getInt("contenttypeid"), 0, json_data.getDouble("mapx"), json_data.getDouble("mapy"));
                }
                json_data = new ServerConnector().execute(appInfo.metaData.getString("select_star_rating"), "content_id", content_id).get();
                loc.setScore(Float.parseFloat(json_data.getString("star_rating")));
                location_list.add(loc);
            }
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    searched_location_progressbar.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();

                }
            });
        } catch (Exception e) {
            if(pageNo==1) {
                location_list.clear();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        searched_location_progressbar.setVisibility(View.GONE);
                        adapter.notifyDataSetChanged();
                        no_search_item.setVisibility(View.VISIBLE);
                    }
                });
            }
            e.printStackTrace();
        }
    }

    public List<Location> getCheckedLocationList(){
        List<Location> checked_list = new ArrayList<Location>();
        for(Location loc : location_list){
            if(loc.getChecked()) {
                checked_list.add(loc);
            }
        }
        int size = location_list.size();
        for(int i =0; i<size; i++){
            Location loc = location_list.get(i);
            loc.setChecked(false);
            location_list.set(i,loc);
        }

        return checked_list;
    }
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.search_location_btn){
            searched_location_progressbar.setVisibility(View.VISIBLE);
            pageNo = 1;
            location_list.clear();
            previousTotal = 0;
            loading = true;
            getData(pageNo);
        }
    }
    class ServerConnector extends AsyncTask<String,Void,JSONObject> {

        @Override
        protected JSONObject doInBackground(String... strings) {
            HttpURLConnection con = null;
            try {
                URL myurl = new URL(strings[0]);
                con = (HttpURLConnection) myurl.openConnection();
                con.setDefaultUseCaches(false);
                con.setDoInput(true);                         // 서버에서 읽기 모드 지정
                con.setDoOutput(true);                       // 서버로 쓰기 모드 지정
                con.setRequestMethod("POST");
                con.setRequestProperty("content-type", "application/x-www-form-urlencoded; charset=UTF-8");
                StringBuffer buffer = new StringBuffer();

                for (int i = 1; i < strings.length; i += 2) {
                    buffer.append(strings[i]).append("=").append(strings[i + 1]);
                    if (i < strings.length - 2)
                        buffer.append("&");
                }

                PrintWriter pw = new PrintWriter(new OutputStreamWriter(con.getOutputStream(), "UTF-8"));
                pw.write(buffer.toString());
                pw.flush();

                int response = con.getResponseCode();
                if (response >= 200 && response <= 300) {
                    StringBuilder builder = new StringBuilder();
                    try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(con.getInputStream(), "UTF-8"))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            builder.append(line);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return new JSONObject(builder.toString());
                } else {
                    Log.e("TAG-Server-error", "Connection Error!");
                }

            } catch (Exception e) {

                e.printStackTrace();

            } finally {
                con.disconnect();
            }
            return null;
        }
    }
}
