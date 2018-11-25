package com.trippilot.trippilot;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by arden on 2018-09-11.
 */

public class PlanListView extends Fragment implements View.OnClickListener,DataSetChangedListener{
    private LinearLayout add_plan_btn;
    private RecyclerView plan_list;
    private TextView empty_plan_list;
    private ProgressBar progressing_plan_list;
    private List<Plan> my_plan_list;
    private PlanListAdapter adapter;
    private String id;
    private FragmentActivity activity;
    private ApplicationInfo appInfo;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.plan_list_view,container,false);
        add_plan_btn = (LinearLayout)v.findViewById(R.id.add_plan_btn);
        plan_list = (RecyclerView)v.findViewById(R.id.plan_list);
        empty_plan_list = (TextView)v.findViewById(R.id.empty_my_plan_list);
        progressing_plan_list = (ProgressBar)v.findViewById(R.id.progressing_my_plan_list);
        add_plan_btn.setOnClickListener(this);

        activity = getActivity();
        LinearLayoutManager layoutmanager = new LinearLayoutManager(getActivity());
        layoutmanager.setOrientation(LinearLayoutManager.VERTICAL);
        plan_list.setHasFixedSize(true);
        plan_list.setLayoutManager(layoutmanager);

        my_plan_list = new ArrayList<Plan>();
        adapter = new PlanListAdapter(getActivity(), my_plan_list, this);
        plan_list.setAdapter(adapter);
        try {
            appInfo = activity.getPackageManager().getApplicationInfo(activity.getPackageName(), PackageManager.GET_META_DATA);
            SharedPreferences pref = getActivity().getSharedPreferences("TripPilotAutoLogin", Activity.MODE_PRIVATE);
            id = pref.getString("TripPilotID",null);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        new ServerConnector(1).execute(appInfo.metaData.getString("select_plan"),"member_id",id);
        return v;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.add_plan_btn:
                showAddDialogBox();
                break;
        }
    }

    private void showAddDialogBox(){
        final Dialog customDialog = new Dialog(getActivity());

        customDialog.setContentView(R.layout.add_plan_dialog);
        ((Button)customDialog.findViewById(R.id.add_p_Dbtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String plan_id = null;
                String title = ((EditText)customDialog.findViewById(R.id.pd_edt)).getText().toString();
                if(title.replace(" ","").equals("")){
                    errorHandle("제목을 입력하지 않았습니다.");
                    return;
                }
                Date d = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
                String createdtime = sdf.format(d);
                if( appInfo.metaData!=null) {
                    try {
                        JSONObject json = new ServerConnector(2).execute(appInfo.metaData.getString("insert_plan"),
                                "member_id",id,
                                "title",title,"scope",Boolean.toString(true),
                                "createdtime",createdtime).get();
                        plan_id = json.getString("plan_id");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                new ServerConnector(1).execute(appInfo.metaData.getString("select_plan"),"member_id",id);
                Intent intent = new Intent(getContext(), ModifyPlanView.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("plan_id", plan_id);
                intent.putExtra("plan_title", title);
                intent.putExtra("type",1);
                startActivity(intent);
                customDialog.dismiss();
            }
        });
        ((Button) customDialog.findViewById(R.id.cancel_p_Dbtn))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        customDialog.dismiss();
                    }
                });
        customDialog.show();
    }

    @Override
    public void dataChanged(boolean isChanged) {
        if(isChanged)
            new ServerConnector(1).execute(appInfo.metaData.getString("select_plan"),"member_id",id);
    }
    public void errorHandle(String msg){
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getContext());
        builder.setTitle("오류");
        builder.setMessage(msg);
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
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
            if(jsonObject!=null) {
                if (type == 1){
                    my_plan_list.clear();
                    try {
                        JSONObject json_data = jsonObject;
                        JSONArray listArray = (JSONArray) json_data.get("list");
                        for (int i = 0; i < listArray.length(); i++) {
                            JSONObject obj = (JSONObject) listArray.get(i);
                            my_plan_list.add(new Plan(obj.getString("plan_id"), obj.getString("member_id"), null, obj.getString("title"),
                                    obj.getBoolean("scope"), Float.parseFloat(obj.getString("star_rating"))
                                    , obj.getString("createdtime")));
                        }
                        progressing_plan_list.setVisibility(View.GONE);
                        if (my_plan_list.size() == 0)
                            empty_plan_list.setVisibility(View.VISIBLE);
                        else
                            empty_plan_list.setVisibility(View.GONE);
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (adapter != null)
                                    adapter.notifyDataSetChanged();
                            }
                        });

                    }catch (Exception e) {
                        e.printStackTrace();
                        progressing_plan_list.setVisibility(View.GONE);
                        if (my_plan_list.size() == 0)
                            empty_plan_list.setVisibility(View.VISIBLE);
                        else
                            empty_plan_list.setVisibility(View.GONE);
                        Log.e("error!!!!! ","Error in Reading Data...In PlanListView");
                    }
                }
            }
        }
    }
}
