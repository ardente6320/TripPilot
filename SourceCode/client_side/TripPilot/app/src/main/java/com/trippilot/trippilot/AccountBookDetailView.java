package com.trippilot.trippilot;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by arden on 2018-10-08.
 */
public class AccountBookDetailView extends AppCompatActivity implements View.OnClickListener,DataSetChangedListener{
    LinearLayout plusUseContent;
    RecyclerView booklist;
    AccountBook accountbook;
    DetailListAdapter adapter;
    Spinner NBType,NBUseType;
    TextView ChangeMoney,totalMoney,dtABName;
    TextView empty_uh_list;
    ProgressBar progressing_uh_list;
    EditText NBSubtitle,NBSpendMoney;
    String[] pay={"수입","지출"};
    String[] paytype ={"현금","카드"};
    String id;
    int total,minus;
    ApplicationInfo appInfo;
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.detail_accountbook_list_view);
        plusUseContent = (LinearLayout)findViewById(R.id.plusUseContent);
        booklist = (RecyclerView)findViewById(R.id.dtABList) ;
        plusUseContent.setOnClickListener(this);
        ChangeMoney = (TextView)findViewById(R.id.changeMoney);
        totalMoney = (TextView)findViewById(R.id.totalMoney);
        dtABName = (TextView)findViewById(R.id.dtABName);
        empty_uh_list = (TextView)findViewById(R.id.empty_uh_list);
        progressing_uh_list = (ProgressBar)findViewById(R.id.progressing_uh_list);
        LinearLayoutManager recycleViewManager = new LinearLayoutManager(getApplicationContext());
        recycleViewManager.setOrientation(LinearLayoutManager.VERTICAL);
        Bundle bundle = getIntent().getBundleExtra("ac_bundle");
        id = bundle.getString("member_id");
        accountbook = bundle.getParcelable("accountbook");

        total = 0; // 총잔액
        minus = 0; // 총지출
        List<AccountBookDate> date_list = accountbook.getDate_list();
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
        dtABName.setText(accountbook.getTitle());
        ChangeMoney.setText(Integer.toString(total));
        totalMoney.setText(Integer.toString(minus));
        booklist.setLayoutManager(recycleViewManager);
        adapter = new DetailListAdapter(this,accountbook.getDate_list(),this);
        booklist.setAdapter(adapter);
        if(appInfo == null ) {
            try {
                appInfo = getApplicationContext().getPackageManager().getApplicationInfo(getApplicationContext().getPackageName(), PackageManager.GET_META_DATA);
                new ServerConnector().execute(appInfo.metaData.getString("select_accountbook"),"accountbook_id", accountbook.getAccountBookId());
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.plusUseContent)
        {
            final Dialog customDialog = new Dialog(this);
            customDialog.setTitle("가계부 사용 내역 추가");
            customDialog.setContentView(R.layout.add_detail_accountbook_dialog);
            long now = System.currentTimeMillis();
            Date date = new Date(now);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
            String time = sdf.format(date);
            final TextView dateTxt = (TextView)customDialog.findViewById(R.id.date_txt);
            dateTxt.setText(time);
            Button datepick = (Button)customDialog.findViewById(R.id.datePick);
            datepick.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Dialog dateDialog = new Dialog(customDialog.getContext());
                    dateDialog.setContentView(R.layout.datepick_dialog);
                    final DatePicker datepicker = (DatePicker)dateDialog.findViewById(R.id.date_pick);
                    ((Button)dateDialog.findViewById(R.id.add_date_Dbtn)).setOnClickListener(new Button.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dateTxt.setText(datepicker.getYear()+"."+(datepicker.getMonth()+1)+"."+datepicker.getDayOfMonth());
                            dateDialog.dismiss();
                        }
                    });
                    ((Button)dateDialog.findViewById(R.id.cancel_date_Dbtn)).setOnClickListener(new Button.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dateDialog.dismiss();
                        }
                    });
                    dateDialog.show();
                }
            });
            NBType = (Spinner)customDialog.findViewById(R.id.NBType);
            NBUseType = (Spinner)customDialog.findViewById(R.id.NBUseType);
            NBSubtitle = (EditText)customDialog.findViewById(R.id.NBSubtitle);
            NBSpendMoney = (EditText) customDialog.findViewById(R.id.NBSpendMoney);
            ArrayAdapter<String> type_dapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item,pay);
            ArrayAdapter<String> use_type_dapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item,paytype);
            NBType.setAdapter(type_dapter);
            NBUseType.setAdapter(use_type_dapter);
            ((Button)customDialog.findViewById(R.id.Checkadd)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String spendmoney = NBSpendMoney.getText().toString();
                    int money = Integer.parseInt(spendmoney);
                    String date = dateTxt.getText().toString();
                    if(NBSubtitle.getText().toString().replace(" ","").equals("")){
                        errorHandle("사용내역을 입력 해주세요.");
                        return;
                    }
                    if(NBSpendMoney.getText().toString().replace(" ","").equals("")){
                        errorHandle("금액을 입력 해주세요.");
                        return;
                    }
                    else {
                        if(appInfo == null ) {
                            try {
                                appInfo = AccountBookDetailView.this.getPackageManager().getApplicationInfo(AccountBookDetailView.this.getPackageName(), PackageManager.GET_META_DATA);
                            } catch (PackageManager.NameNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                        new ServerConnector().execute(appInfo.metaData.getString("insert_usagehistory"), "accountbook_id", accountbook.getAccountBookId()
                                , "date", date, "money", Integer.toString(money), "usage_history", NBSubtitle.getText().toString(), "type", NBType.getSelectedItem().toString()
                                , "payment_method", NBUseType.getSelectedItem().toString());
                        dataChanged(true);
                        customDialog.dismiss();
                    }
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
    }

    @Override
    public void dataChanged(boolean isChanged) {
        if(isChanged) {
            new ServerConnector().execute(appInfo.metaData.getString("select_accountbook"),"accountbook_id", accountbook.getAccountBookId());
        }
    }

    public void errorHandle(String msg){
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
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
    public void onBackPressed() {
        super.onBackPressed();
        finish();
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

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if(jsonObject!=null){
                try {
                    JSONObject json_data = jsonObject;
                    JSONArray list = json_data.getJSONArray("list");
                    int size = list.length();
                    List<AccountBookDate> abd_list = accountbook.getDate_list();
                    abd_list.clear();
                    String temp_date = null,date = null;
                    List<UsageHistory> uh_list = null;
                    for(int i =0; i<size; i++){
                        json_data = list.getJSONObject(i);
                        temp_date = json_data.getString("date");
                        if(i!=0&&!date.equals(temp_date)) {
                            abd_list.add(new AccountBookDate(date, uh_list));
                            uh_list = new ArrayList<UsageHistory>();
                            date = temp_date;
                        }
                        else if(i==0) {
                            date = temp_date;
                            uh_list = new ArrayList<UsageHistory>();
                        }
                        uh_list.add(new UsageHistory(json_data.getString("content_id"), date, json_data.getInt("money")
                                , json_data.getString("usage_history"), json_data.getString("type"), json_data.getString("payment_method")));
                    }
                    if(date.equals(temp_date))
                        abd_list.add(new AccountBookDate(date, uh_list));
                    accountbook.setDate_list(abd_list);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                total = 0;
                minus = 0;
                List<AccountBookDate> date_list = accountbook.getDate_list();
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
                ChangeMoney.setText(Integer.toString(total));
                totalMoney.setText(Integer.toString(0-minus));
                progressing_uh_list.setVisibility(View.GONE);
                if (date_list.size()==0)
                    empty_uh_list.setVisibility(View.VISIBLE);
                else
                    empty_uh_list.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
            }
        }
    }
}
