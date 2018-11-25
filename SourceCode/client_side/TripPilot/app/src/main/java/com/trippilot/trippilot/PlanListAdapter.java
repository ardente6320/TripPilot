package com.trippilot.trippilot;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
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

/**
 * Created by arden on 2018-09-11.
 */

public class PlanListAdapter extends RecyclerView.Adapter<PlanListViewHolder>{
    private Context context;
    private List<Plan>plan_list;
    ApplicationInfo appinfo;
    DataSetChangedListener dataSetChangedListener;
    public PlanListAdapter(Context context,List<Plan>plan_list,DataSetChangedListener dataSetChangedListener){
        this.context = context;
        this.plan_list = plan_list;
        this.dataSetChangedListener = dataSetChangedListener;
        try {
            appinfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
    @Override
    public PlanListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.plan_itemcard_view,null);
        return new PlanListViewHolder(v);
    }

    @Override
    public void onBindViewHolder(PlanListViewHolder holder, final int position) {
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                final Dialog longclick_dialog = new Dialog(context);
                longclick_dialog.setContentView(R.layout.accountbook_longclick_dialog);
                ((TextView)longclick_dialog.findViewById(R.id.longclick_title)).setText(plan_list.get(position).getTitle());
                String[] arr = {"제목 수정","삭제"};
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1,arr);
                ListView listView = (ListView)longclick_dialog.findViewById(R.id.ac_longclick_dialog);
                listView.setAdapter(arrayAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        switch (i){
                            case 0:
                                longclick_dialog.dismiss();
                                final Dialog dialog = new Dialog(context);
                                dialog.setContentView(R.layout.modify_plan_title_dialog);
                                ((EditText)dialog.findViewById(R.id.edit_plan_title)).setText(plan_list.get(position).getTitle());
                                ((Button)dialog.findViewById(R.id.confirm_modify_plan_title)).setOnClickListener(new Button.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        String title = ((EditText)dialog.findViewById(R.id.edit_plan_title)).getText().toString();
                                        if(title.replace(" ","").equals("")){
                                            errorHandle("제목을 입력하지 않았습니다.");
                                            return;
                                        }
                                        new ServerConnector().execute(appinfo.metaData.getString("update_plan_title"),"plan_id",plan_list.get(position).getPlan_id()
                                                ,"title",title);
                                        dataSetChangedListener.dataChanged(true);
                                        dialog.dismiss();
                                    }
                                });

                                ((Button)dialog.findViewById(R.id.modify_plan_title_cancel)).setOnClickListener(new Button.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialog.dismiss();
                                    }
                                });
                                dialog.show();
                                break;
                            case 1:
                                longclick_dialog.dismiss();
                                new AlertDialog.Builder(context)
                                        .setTitle("계획 삭제")
                                        .setMessage("계획을 삭제합니다.\n계획에 대한 정보가 모두 사라집니다. 삭제하시겠습니까?")

                                        // set three option buttons
                                        .setPositiveButton("삭제",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog,int whichButton) {
                                                        new ServerConnector().execute(appinfo.metaData.getString("delete_plan"), "plan_id", plan_list.get(position).getPlan_id());
                                                        dataSetChangedListener.dataChanged(true);
                                                        dialog.dismiss();
                                                    }
                                                })// setPositiveButton

                                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                dialog.dismiss();
                                            }
                                        })// setNegativeButton
                                        .create().show();
                                break;
                        }
                    }
                });
                longclick_dialog.show();
                return true;
            }
        });
        holder.plan_name.setText(plan_list.get(position).getTitle());
        holder.scope.setChecked(plan_list.get(position).getScope());
        holder.scope.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                plan_list.get(position).setScope(b);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        new ServerConnector().execute(appinfo.metaData.getString("update_plan_scope")
                                , "scope", Boolean.toString(plan_list.get(position).getScope()), "plan_id", plan_list.get(position).getPlan_id());
                    }
                }).start();
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,PlanView.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("plan",plan_list.get(position));
                intent.putExtra("plan_type",1);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return plan_list.size();
    }

    public void errorHandle(String msg){
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
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
    }
}
