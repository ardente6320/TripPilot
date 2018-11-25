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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by arden on 2018-09-11.
 */

public class AccountBookListView extends Fragment implements View.OnClickListener,DataSetChangedListener{
    private RelativeLayout accountBookAdd;
    private RecyclerView accountbooklist;
    private TextView empty_ab_list;
    private ProgressBar progressing_ab_list;
    private List<AccountBook> accountbook_list;
    private List<UsageHistory> usage_history_list;
    private List<AccountBookDate> date_list;
    private AccountBookListAdapter adapter;
    private SharedPreferences pref;
    private String id;
    private ApplicationInfo appInfo;
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.accountbook_list_view,container,false);
        pref = getActivity().getSharedPreferences("TripPilotAutoLogin", Activity.MODE_PRIVATE);
        id = pref.getString("TripPilotID",null);

        accountBookAdd =(RelativeLayout) v.findViewById(R.id.AccountBookPlus);
        accountbooklist = (RecyclerView)v.findViewById(R.id.AccountBookRecycleView);
        empty_ab_list = (TextView)v.findViewById(R.id.empty_ab_list);
        progressing_ab_list = (ProgressBar)v.findViewById(R.id.progressing_ab_list);
        accountBookAdd.setOnClickListener(this);

        LinearLayoutManager recycleViewManager = new LinearLayoutManager(getContext().getApplicationContext());
        recycleViewManager.setOrientation(LinearLayoutManager.VERTICAL);
        accountbook_list = new ArrayList<AccountBook>();
        usage_history_list = new ArrayList<UsageHistory>();
        date_list = new ArrayList<AccountBookDate>();
        accountbooklist.setLayoutManager(recycleViewManager);
        adapter = new AccountBookListAdapter(getActivity(),accountbook_list,id,this);
        accountbooklist.setAdapter(adapter);
        try {
            appInfo = getActivity().getPackageManager().getApplicationInfo(getActivity().getPackageName(), PackageManager.GET_META_DATA);
            new ServerConnector().execute(appInfo.metaData.getString("select_accountbook_list"),"member_id", id);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return v;
    }
    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.AccountBookPlus)
        {
            final Dialog customDialog = new Dialog(getContext());
            customDialog.setTitle("가계부 이름 설정");

            customDialog.setContentView(R.layout.add_accountbook_dialog);
            final EditText accountBookName  = (EditText)customDialog.findViewById(R.id.accountBookName);
            ((Button)customDialog.findViewById(R.id.NameOk)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(accountBookName.getText().toString().replace(" ","").equals("")){
                        errorHandle("가계부 이름을 입력하지 않으셨습니다.");
                    }
                    else {
                        Calendar today = Calendar.getInstance();
                        String createdtime = today.get(Calendar.YEAR)+"년 "+Integer.toString(today.get(Calendar.MONTH)+1)+"월 "+today.get(Calendar.DATE)+"일";
                        new ServerConnector().execute(appInfo.metaData.getString("insert_accountbook"),"member_id",id
                                ,"title",accountBookName.getText().toString(),"createdtime",createdtime);
                        new ServerConnector().execute(appInfo.metaData.getString("select_accountbook_list"),"member_id", id);
                        customDialog.dismiss();
                    }
                }
            });
            ((Button)customDialog.findViewById(R.id.NmaeCancel)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    customDialog.dismiss();
                }
            });

            customDialog.show();
        }
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

    @Override
    public void dataChanged(boolean isChanged) {
        if(isChanged)
            new ServerConnector().execute(appInfo.metaData.getString("select_accountbook_list"),"member_id", id);
    }

    @Override
    public void onResume() {
        super.onResume();
        new ServerConnector().execute(appInfo.metaData.getString("select_accountbook_list"),"member_id", id);
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
            if(jsonObject !=null){
                if(accountbook_list!=null)
                    accountbook_list.clear();
                if(usage_history_list!=null)
                    usage_history_list.clear();
                if(date_list !=null)
                    date_list.clear();
                String date=null,temp_date = null;
                try {
                    JSONObject json_data = jsonObject;
                    JSONArray list = json_data.getJSONArray("list");
                    int size = list.length();
                    for(int i =0; i< size; i++){
                        usage_history_list = null;
                        date_list = new ArrayList<AccountBookDate>();
                        json_data = list.getJSONObject(i);
                        String accountbook_id = json_data.getString("accountbook_id");
                        String title = json_data.getString("title");
                        JSONArray usage_list = json_data.getJSONArray("usage_list");
                        int usage_list_size = usage_list.length();

                        for(int j = 0; j<usage_list_size; j++){
                            JSONObject obj = usage_list.getJSONObject(j);
                            temp_date = obj.getString("date");
                            if(j!=0&&!date.equals(temp_date)) {
                                date_list.add(new AccountBookDate(date, usage_history_list));
                                usage_history_list = new ArrayList<UsageHistory>();
                                date = temp_date;
                            }
                            else if(j==0) {
                                date = temp_date;
                                usage_history_list = new ArrayList<UsageHistory>();
                            }
                            usage_history_list.add(new UsageHistory(obj.getString("content_id"), date, obj.getInt("money")
                                    , obj.getString("usage_history"), obj.getString("type"), obj.getString("payment_method")));
                        }
                        if(usage_list_size!=0&&date.equals(temp_date))
                            date_list.add(new AccountBookDate(date, usage_history_list));
                        accountbook_list.add(new AccountBook(accountbook_id,title,date_list));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                progressing_ab_list.setVisibility(View.GONE);
                if(accountbook_list.size()==0)
                    empty_ab_list.setVisibility(View.VISIBLE);
                else
                    empty_ab_list.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
            }
        }
    }
}
