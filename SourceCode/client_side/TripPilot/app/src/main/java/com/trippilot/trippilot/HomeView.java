package com.trippilot.trippilot;

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
import android.widget.ProgressBar;
import android.widget.TextView;

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
import java.util.concurrent.ExecutionException;

/**
 * Created by arden on 2018-09-20.
 */

public class HomeView extends Fragment {
    private RecyclerView progressing_trip_list,popular_plan_list,recommend_plan_list;
    private PlanThumbnailListAdapter progressing_adapter, popular_adapter,recommend_adapter;
    private List<Plan> progressing_trip,popular_plan, recommend_plan;
    private TextView empty_popular_plan,empty_my_plan,empty_recommend_plan;
    private ProgressBar popular_progressbar,my_plan_progressbar,recommend_progressbar;
    private String id;
    private ApplicationInfo appInfo;
    private Activity activity;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.home_view,container,false);
        SharedPreferences pref = getActivity().getSharedPreferences("TripPilotAutoLogin", Activity.MODE_PRIVATE);
        id = pref.getString("TripPilotID", null);
        progressing_trip_list = (RecyclerView)v.findViewById(R.id.progressing_trip_list);
        popular_plan_list = (RecyclerView)v.findViewById(R.id.popular_plan_list);
        recommend_plan_list = (RecyclerView)v.findViewById(R.id.recommend_plan_list);
        empty_popular_plan = (TextView)v.findViewById(R.id.empty_popular_plan);
        empty_my_plan = (TextView)v.findViewById(R.id.empty_my_plan);
        empty_recommend_plan = (TextView)v.findViewById(R.id.empty_recommend_plan);
        popular_progressbar = (ProgressBar)v.findViewById(R.id.populer_progressbar);
        my_plan_progressbar = (ProgressBar)v.findViewById(R.id.my_plan_progressbar);
        recommend_progressbar = (ProgressBar)v.findViewById(R.id.recommend_progressbar);

         /*리스트 셋팅*/
        LinearLayoutManager progress_layoutmanager = new LinearLayoutManager(getActivity());
        progress_layoutmanager.setOrientation(LinearLayoutManager.HORIZONTAL);
        LinearLayoutManager popular_layoutmanager = new LinearLayoutManager(getActivity());
        popular_layoutmanager.setOrientation(LinearLayoutManager.HORIZONTAL);
        LinearLayoutManager recommend_layoutmanager = new LinearLayoutManager(getActivity());
        recommend_layoutmanager.setOrientation(LinearLayoutManager.HORIZONTAL);
        progressing_trip_list.setHasFixedSize(true);
        progressing_trip_list.setLayoutManager(progress_layoutmanager);
        popular_plan_list.setHasFixedSize(true);
        popular_plan_list.setLayoutManager(popular_layoutmanager);
        recommend_plan_list.setHasFixedSize(true);
        recommend_plan_list.setLayoutManager(recommend_layoutmanager);
        progressing_trip = new ArrayList<Plan>();
        popular_plan = new ArrayList<Plan>();
        recommend_plan = new ArrayList<Plan>();
        progressing_adapter = new PlanThumbnailListAdapter(getActivity(), progressing_trip, id);
        progressing_trip_list.setAdapter(progressing_adapter);
        popular_adapter = new PlanThumbnailListAdapter(getActivity(), popular_plan, id);
        popular_plan_list.setAdapter(popular_adapter);
        recommend_adapter = new PlanThumbnailListAdapter(getActivity(), recommend_plan, id);
        recommend_plan_list.setAdapter(recommend_adapter);
        activity = this.getActivity();
        try {
            appInfo = activity.getPackageManager().getApplicationInfo(activity.getPackageName(), PackageManager.GET_META_DATA);
            if (appInfo.metaData != null) {
                new ServerConnector(1).execute(appInfo.metaData.getString("select_plan"), "member_id", id);
                new ServerConnector(2).execute(appInfo.metaData.getString("select_popular_plan"));
                new ServerConnector(3).execute(appInfo.metaData.getString("select_recommend_plan"), "member_id", id);
            }
        }catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
        }
        return v;
    }

    class ServerConnector extends AsyncTask<String,Void,JSONObject>{
        int type;
        public ServerConnector(int type){
            this.type = type;
        }
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

                for(int i =1; i<strings.length;i+=2){
                    buffer.append(strings[i]).append("=").append(strings[i+1]);
                    if(i < strings.length-2)
                        buffer.append("&");
                }

                PrintWriter pw = new PrintWriter(new OutputStreamWriter(con.getOutputStream(), "UTF-8"));
                pw.write(buffer.toString());
                pw.flush();

                int response = con.getResponseCode();
                if (response >=200 && response <=300) {
                    StringBuilder builder = new StringBuilder();
                    try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(con.getInputStream(),"UTF-8"))) {
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
                if (type == 1)
                    progressing_trip.clear();
                else if (type == 2)
                    popular_plan.clear();
                else if (type == 3)
                    recommend_plan.clear();

                try {
                    JSONObject json_data = jsonObject;
                    JSONArray listArray = (JSONArray) json_data.get("list");
                    int size = listArray.length();
                    for (int i = 0; i < size; i++) {
                        JSONObject obj = (JSONObject) listArray.get(i);
                        String content_id = obj.getString("img");

                        try {
                            if (content_id.equals("N"))
                                throw new Exception();

                            json_data = new APIConnector(activity).execute(appInfo.metaData.getString("detailCommon"), "contentId", content_id, "firstImageYN", "Y").get();
                            json_data = json_data.getJSONObject("response");
                            json_data = json_data.getJSONObject("body");
                            json_data = json_data.getJSONObject("items");
                            json_data = json_data.getJSONObject("item");
                            if (type == 1)
                                progressing_trip.add(new Plan(obj.getString("plan_id"), obj.getString("member_id"), json_data.getString("firstimage2"), obj.getString("title"),
                                        obj.getBoolean("scope"), Float.parseFloat(obj.getString("star_rating"))
                                        , obj.getString("createdtime")));
                            else if (type == 2)
                                popular_plan.add(new Plan(obj.getString("plan_id"), obj.getString("member_id"), json_data.getString("firstimage2"), obj.getString("title"),
                                        obj.getBoolean("scope"), Float.parseFloat(obj.getString("star_rating"))
                                        , obj.getString("createdtime")));
                            else if (type == 3)
                                recommend_plan.add(new Plan(obj.getString("plan_id"), obj.getString("member_id"), json_data.getString("firstimage2"), obj.getString("title"),
                                        obj.getBoolean("scope"), 0
                                        , obj.getString("createdtime")));
                        } catch (Exception e) {
                            if (type == 1) {
                                progressing_trip.add(new Plan(obj.getString("plan_id"), obj.getString("member_id"), null, obj.getString("title"),
                                        obj.getBoolean("scope"), Float.parseFloat(obj.getString("star_rating"))
                                        , obj.getString("createdtime")));
                            } else if (type == 2)
                                popular_plan.add(new Plan(obj.getString("plan_id"), obj.getString("member_id"), null, obj.getString("title"),
                                        obj.getBoolean("scope"), Float.parseFloat(obj.getString("star_rating"))
                                        , obj.getString("createdtime")));
                            else if (type == 3)
                                recommend_plan.add(new Plan(obj.getString("plan_id"), obj.getString("member_id"), null, obj.getString("title"),
                                        obj.getBoolean("scope"), 0
                                        , obj.getString("createdtime")));
                        }
                    }
                    if (type == 1) {
                        progressing_adapter = new PlanThumbnailListAdapter(getActivity(), progressing_trip, id);
                        progressing_trip_list.setAdapter(progressing_adapter);
                        if (progressing_trip.size() != 0)
                            empty_my_plan.setVisibility(View.GONE);
                        else
                            empty_my_plan.setVisibility(View.VISIBLE);
                        my_plan_progressbar.setVisibility(View.GONE);
                    } else if (type == 2) {

                        popular_adapter = new PlanThumbnailListAdapter(getActivity(), popular_plan, id);
                        popular_plan_list.setAdapter(popular_adapter);
                        if (popular_plan.size() != 0)
                            empty_popular_plan.setVisibility(View.GONE);
                        else
                            empty_popular_plan.setVisibility(View.VISIBLE);
                        popular_progressbar.setVisibility(View.GONE);
                    } else if (type == 3) {

                        recommend_adapter = new PlanThumbnailListAdapter(getActivity(), recommend_plan, id);
                        recommend_plan_list.setAdapter(recommend_adapter);
                        if (recommend_plan.size() != 0)
                            empty_recommend_plan.setVisibility(View.GONE);
                        else
                            empty_recommend_plan.setVisibility(View.VISIBLE);
                        recommend_progressbar.setVisibility(View.GONE);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
