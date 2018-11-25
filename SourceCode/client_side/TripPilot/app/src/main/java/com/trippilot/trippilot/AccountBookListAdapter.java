package com.trippilot.trippilot;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.widget.Toast;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by arden on 2018-10-08.
 */
public class AccountBookListAdapter extends RecyclerView.Adapter<AccountBookViewHolder>{
    Activity activity;
    List<AccountBook> accountBookList;
    String id;
    ApplicationInfo appInfo;
    DataSetChangedListener dataSetChangedListener;
    public AccountBookListAdapter(Activity activity,List<AccountBook> accountBookList,String id,DataSetChangedListener dataSetChangedListener )
    {
        this.activity = activity;
        this.accountBookList = accountBookList;
        this.id = id;
        this.dataSetChangedListener = dataSetChangedListener;
        try {
            appInfo = this.activity.getPackageManager().getApplicationInfo(this.activity.getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
    @Override
    public AccountBookViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.accounbook_list_item,null);
        return new AccountBookViewHolder(v);
    }

    @Override
    public void onBindViewHolder(AccountBookViewHolder holder, final int position) {
        holder.ABName.setText(accountBookList.get(position).getTitle());
        final List<AccountBookDate> date_list= accountBookList.get(position).getDate_list();
        int balance = 0;
        int total = 0;
        int minus = 0;
        for(AccountBookDate abd : date_list){
            List<UsageHistory> uh_list = abd.getUsagehistory_list();
            for(UsageHistory uh : uh_list){
                if(uh.getType().equals("지출"))
                    minus+=uh.getMoney();
                else
                    total += uh.getMoney();
            }
        }
        total -= minus;
        if(total<0)
            total = 0;
        holder.ABBalance.setText(Integer.toString(total));
        holder.accountBookItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity,AccountBookDetailView.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                Bundle bundle = new Bundle();
                bundle.putString("member_id",id);
                bundle.putParcelable("accountbook",accountBookList.get(position));
                intent.putExtra("ac_bundle",bundle);
                activity.startActivity(intent);
            }
        });
        holder.accountBookItem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                final Dialog dialog = new Dialog(activity);
                dialog.setContentView(R.layout.accountbook_longclick_dialog);
                ListView listview = (ListView)dialog.findViewById(R.id.ac_longclick_dialog);
                ((TextView)dialog.findViewById(R.id.longclick_title)).setText(accountBookList.get(position).getTitle());
                String[] temp_list= {"제목 수정","가계부 삭제"};
                ArrayAdapter<String> dialog_adapter = new ArrayAdapter<String>(activity,android.R.layout.simple_list_item_1,temp_list);
                listview.setAdapter(dialog_adapter);
                listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        if(i==0){
                            dialog.dismiss();
                            final Dialog customDialog = new Dialog(activity);
                            customDialog.setTitle("가계부 이름 수정");

                            customDialog.setContentView(R.layout.add_accountbook_dialog);
                            final EditText accountBookName  = (EditText)customDialog.findViewById(R.id.accountBookName);
                            ((Button)customDialog.findViewById(R.id.NameOk)).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if(accountBookName.getText().toString().replace(" ","").equals("")){
                                        errorHandle("가계부 이름을 입력하지 않으셨습니다.");
                                    }
                                    else {
                                        new ServerConnector().execute(appInfo.metaData.getString("update_accountbook")
                                                ,"accountbook_id",accountBookList.get(position).getAccountBookId()
                                                ,"title",accountBookName.getText().toString());
                                        dataSetChangedListener.dataChanged(true);
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
                        else if(i==1){
                            dialog.dismiss();
                            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(activity);
                            builder.setTitle("가계부 삭제");
                            builder.setMessage("가계부를 삭제합니다.\n가계부 정보가 모두 사라집니다. 삭제하시겠습니까?");
                            builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    new ServerConnector().execute(appInfo.metaData.getString("delete_accountbook"),"accountbook_id",accountBookList.get(position).getAccountBookId());
                                    dataSetChangedListener.dataChanged(true);
                                    dialogInterface.dismiss();
                                }
                            });
                            builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                            builder.show();
                        }
                    }
                });
                dialog.show();
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return accountBookList.size();
    }

    class ServerConnector extends AsyncTask<String,Void,JSONObject>{

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

    public void errorHandle(String msg) {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(activity);
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
}
