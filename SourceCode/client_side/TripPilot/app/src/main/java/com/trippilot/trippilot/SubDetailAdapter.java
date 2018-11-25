package com.trippilot.trippilot;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;

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
 * Created by arden on 2018-10-08.
 */
public class SubDetailAdapter extends RecyclerView.Adapter<SubDetailViewHolder>{
    Context context;
    List<UsageHistory> usage_history_list;
    ApplicationInfo appInfo;
    String[] pay={"수입","지출"};
    String[] paytype ={"현금","카드"};
    DataSetChangedListener dataSetChangedListener;
    public SubDetailAdapter(Context context,List<UsageHistory> usage_history_list,DataSetChangedListener dataSetChangedListener)
    {
        this.context = context;
        this.usage_history_list = usage_history_list;
        try {
            appInfo = this.context.getPackageManager().getApplicationInfo(this.context.getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        this.dataSetChangedListener = dataSetChangedListener;
    }
    @Override
    public SubDetailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.detail_account_book_item,null);
        return new SubDetailViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final SubDetailViewHolder holder, final int position) {
        if(usage_history_list.get(position).getType().equals("수입")) {
            holder.NBUseTypetv.setText("수입");
        }
        if(usage_history_list.get(position).getType().equals("지출")){
            holder.NBTypetv.setText("지출");
        }
        if(usage_history_list.get(position).getPayment_method().equals("현금")){
            holder.NBUseTypetv.setText("현금");
        }
        if(usage_history_list.get(position).getPayment_method().equals("카드")){
            holder.NBUseTypetv.setText("카드");
        }
        String type = usage_history_list.get(position).getType();
        int money = usage_history_list.get(position).getMoney();
        if(type.equals("지출")) {
            holder.ABUseMoney.setText(Integer.toString(0 - money));
            holder.ABUseMoney.setTextColor(Color.rgb(250,88,88));
            holder.won.setTextColor(Color.rgb(250,88,88));
        }
        else {
            holder.ABUseMoney.setText(Integer.toString(money));
            holder.ABUseMoney.setTextColor(Color.rgb(0,161,255));
            holder.won.setTextColor(Color.rgb(0,161,255));
        }
        holder.NBSpendMoneytv.setText(Integer.toString(money));
        holder.ABUseTitle.setText(usage_history_list.get(position).getUsage_history());
        holder.ABshowDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.setVisible.getVisibility()==View.VISIBLE) {
                    holder.setVisible.setVisibility(View.GONE);
                    holder.ABshowDetail.setBackgroundResource(R.drawable.dropdown);
                }
                else if(holder.setVisible.getVisibility()==View.GONE){
                    TranslateAnimation animate = new TranslateAnimation(0,0,-40,view.getHeight()/2);
                    animate.setDuration(500);
                    animate.setFillAfter(true);
                    holder.setVisible.startAnimation(animate);
                    holder.setVisible.setVisibility(View.VISIBLE);
                    holder.ABshowDetail.setBackgroundResource(R.drawable.drowup);
                }
            }
        });
        holder.delete_detail_accountbook_item.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("사용 내역 삭제");
                builder.setMessage("사용 내역을 삭제합니다.\n 해당하는 사용 내역 정보가 삭제 됩니다. 삭제하시겠습니까?");
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new ServerConnector().execute(appInfo.metaData.getString("delete_usagehistory"),"content_id",usage_history_list.get(position).getContent_id());
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
        });
        holder.modify_detail_accountbook_item.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog customDialog = new Dialog(context);
                customDialog.setTitle("사용 내역 수정");
                customDialog.setContentView(R.layout.add_detail_accountbook_dialog);
                ((RelativeLayout)customDialog.findViewById(R.id.dialog_visible_layout)).setVisibility(View.GONE);
                 final Spinner NBType = (Spinner)customDialog.findViewById(R.id.NBType);
                 final Spinner NBUseType = (Spinner)customDialog.findViewById(R.id.NBUseType);
                 final EditText NBSubtitle = (EditText)customDialog.findViewById(R.id.NBSubtitle);
                 NBSubtitle.setText(usage_history_list.get(position).getUsage_history());
                 final EditText NBSpendMoney = (EditText) customDialog.findViewById(R.id.NBSpendMoney);
                NBSpendMoney.setText(Integer.toString(usage_history_list.get(position).getMoney()));
                ArrayAdapter<String> type_dapter = new ArrayAdapter<String>(context,android.R.layout.simple_spinner_dropdown_item,pay);
                final ArrayAdapter<String> use_type_dapter = new ArrayAdapter<String>(context,android.R.layout.simple_spinner_dropdown_item,paytype);
                NBType.setAdapter(type_dapter);
                NBUseType.setAdapter(use_type_dapter);
                if(pay[0].equals(usage_history_list.get(position).getType()))
                    NBType.setSelection(0);
                else
                    NBType.setSelection(1);
                if(paytype[0].equals(usage_history_list.get(position).getPayment_method()))
                    NBUseType.setSelection(0);
                else
                    NBUseType.setSelection(1);
                ((Button)customDialog.findViewById(R.id.Checkadd)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String spendmoney = NBSpendMoney.getText().toString();
                        if(NBSubtitle.getText().toString().replace(" ","").equals("")){
                            errorHandle("사용내역을 입력 해주세요.");
                            return;
                        }
                        if(spendmoney.replace(" ","").equals("")){
                            errorHandle("금액을 입력 해주세요.");
                            return;
                        }
                        int money = Integer.parseInt(spendmoney);
                        if (appInfo == null) {
                            try {
                                appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
                            } catch (PackageManager.NameNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                        new ServerConnector().execute(appInfo.metaData.getString("update_usagehistory"),"content_id",usage_history_list.get(position).getContent_id(),"money", Integer.toString(money), "usage_history", NBSubtitle.getText().toString(), "type", NBType.getSelectedItem().toString()
                                ,"payment_method", NBUseType.getSelectedItem().toString());
                        dataSetChangedListener.dataChanged(true);
                        customDialog.dismiss();
                    }
                });
                ((Button)customDialog.findViewById(R.id.Canceladd)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        customDialog.dismiss();
                    }
                });
                customDialog.show();
            }
        });
        if(position == (usage_history_list.size()-1))
            holder.detail_ab_line.setVisibility(View.INVISIBLE);
    }

    @Override
    public int getItemCount() {
        return usage_history_list.size();
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
