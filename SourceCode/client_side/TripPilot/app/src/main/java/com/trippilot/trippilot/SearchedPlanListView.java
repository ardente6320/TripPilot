package com.trippilot.trippilot;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by arden on 2018-09-11.
 */
public class SearchedPlanListView extends android.support.v4.app.Fragment{
    RecyclerView search_plan_list;
    SearchedPlanListAdapter adapter;
    private ImageButton search_btn;
    private ProgressBar searched_plan_progressbar;
    private Spinner category_area, category_thema,category_city;
    private EditText search_plan_edt;
    private TextView no_search_plan_item;
    List<Plan> plan_list;
    private ArrayAdapter<String> city_adapter;
    private List<Integer> code, content_type,city_code;
    private List<String> area,city;
    ApplicationInfo appInfo;
    String id;
    int type;
    int totalCount = 0;
    int pageNo = 1;
    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 10;
    int firstVisibleItem, visibleItemCount, totalItemCount;
    Activity activity;
    int areaNo=0,cityNo=0;
    public void setType(int type){
        this.type = type;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.search_plan_view,container,false);
        activity = this.getActivity();
        searched_plan_progressbar = (ProgressBar)v.findViewById(R.id.searched_plan_progressbar);
        search_plan_list = (RecyclerView)v.findViewById(R.id.searched_plan_list);
        search_btn = (ImageButton)v.findViewById(R.id.search_plan_btn);
        no_search_plan_item = (TextView)v.findViewById(R.id.no_search_plan_item);
        search_btn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                plan_list.clear();
                pageNo = 1;
                loading = true;
                previousTotal = 0;
                getData(pageNo);
            }
        });
        search_plan_edt = (EditText)v.findViewById(R.id.search_plan_edt);
        SharedPreferences pref = getActivity().getSharedPreferences("TripPilotAutoLogin", Activity.MODE_PRIVATE);
        id = pref.getString("TripPilotID",null);

        final LinearLayoutManager layoutmanager = new LinearLayoutManager(getContext());
        layoutmanager.setOrientation(LinearLayoutManager.VERTICAL);
        search_plan_list.setHasFixedSize(true);
        search_plan_list.setLayoutManager(layoutmanager);

        code = new ArrayList<Integer>();
        city_code = new ArrayList<Integer>();
        content_type = new ArrayList<Integer>();
        area = new ArrayList<String>();
        city = new ArrayList<String>();
        plan_list = new ArrayList<Plan>();
        category_area = (Spinner)v.findViewById(R.id.category_plan_area);
        category_thema = (Spinner)v.findViewById(R.id.category_plan_thema);
        category_city = (Spinner)v.findViewById(R.id.category_plan_city);
        category_city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                cityNo = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
        String[] category = {"관광지","문화시설","축제/공연/행사","레포츠","쇼핑","맛집"};
        content_type.add(12);content_type.add(14);content_type.add(15);content_type.add(28);content_type.add(38);content_type.add(39);
        ArrayAdapter<String> category_adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_dropdown_item,category);
        category_thema.setAdapter(category_adapter);
        city_adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, city);
        category_city.setAdapter(city_adapter);
        adapter = new SearchedPlanListAdapter(getActivity(),plan_list);
        search_plan_list.setAdapter(adapter);
        category_area.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                getCityData(code.get(i));
                areaNo = i;
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
        search_plan_list.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                visibleItemCount = search_plan_list.getChildCount();
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
                ArrayAdapter<String> area_adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_dropdown_item,area);
                category_area.setAdapter(area_adapter);
                category_area.setSelection(0);
            }
        });
    }

    private void getCityData(int code) {
        try {
            city_code.clear();
            city.clear();
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
    private void getData(int cnt) {
        String area = Integer.toString(code.get(category_area.getSelectedItemPosition()));
        String sigungu = Integer.toString(city_code.get(category_city.getSelectedItemPosition()));
        String contentType = Integer.toString(content_type.get(category_thema.getSelectedItemPosition()));
        String keyword = search_plan_edt.getText().toString();
        if (keyword.replace(" ", "").equals(""))
            keyword = "N";
        new ServerConnector().execute(appInfo.metaData.getString("search_plan"),"member_id",id,"area_code",area,"sigungu_code",sigungu,"content_type",contentType, "keyword",keyword,"cnt",Integer.toString(cnt));

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

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (jsonObject != null) {
                try {
                    JSONObject json_data = jsonObject;
                    totalCount = json_data.getInt("totalCount");
                    JSONArray listArray = (JSONArray) json_data.get("list");
                    int size = listArray.length();
                    if (size != 0) {
                        no_search_plan_item.setVisibility(View.GONE);
                        searched_plan_progressbar.setVisibility(View.GONE);
                    } else {
                        no_search_plan_item.setVisibility(View.VISIBLE);
                        searched_plan_progressbar.setVisibility(View.GONE);
                    }

                    for (int i = 0; i < size; i++) {
                        json_data = (JSONObject) listArray.get(i);
                        String content_id = json_data.getString("img");

                        try {
                            if (content_id.equals("N"))
                                throw new Exception();

                            JSONObject temp = new APIConnector(getContext()).execute(appInfo.metaData.getString("detailCommon"), "contentId", content_id, "firstImageYN", "Y").get();
                            temp = temp.getJSONObject("response");
                            temp = temp.getJSONObject("body");
                            temp = temp.getJSONObject("items");
                            temp = temp.getJSONObject("item");
                            plan_list.add(new Plan(json_data.getString("plan_id"), json_data.getString("member_id"), temp.getString("firstimage2"), json_data.getString("title")
                                    , Boolean.parseBoolean(json_data.getString("scope")), Float.parseFloat(json_data.getString("star_rating"))
                                    , json_data.getString("createdtime")));
                        } catch (Exception e) {
                            plan_list.add(new Plan(json_data.getString("plan_id"), json_data.getString("member_id"), null, json_data.getString("title")
                                    , Boolean.parseBoolean(json_data.getString("scope")), Float.parseFloat(json_data.getString("star_rating"))
                                    , json_data.getString("createdtime")));
                        }
                    }
                    searched_plan_progressbar.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                } catch (Exception e) {
                    if(pageNo==1) {
                        plan_list.clear();
                        searched_plan_progressbar.setVisibility(View.GONE);
                        adapter.notifyDataSetChanged();
                        no_search_plan_item.setVisibility(View.VISIBLE);
                    }
                    e.printStackTrace();
                }
            }
        }
    }
}
