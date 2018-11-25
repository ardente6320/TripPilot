package com.trippilot.trippilot;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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
import java.nio.channels.ShutdownChannelGroupException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

/**
 * Created by arden on 2018-09-12.
 */
// Menu로 처리할 것인가를 생각
public class ModifyPlanView extends AppCompatActivity implements View.OnClickListener {
    private TextView mplan_title;
    private ImageButton reccomend_btn,optimization_btn;
    private Button done_btn;
    private RelativeLayout add_schedule_btn;
    private RecyclerView modify_schedule_list;
    private ScheduleAdapter adapter;
    private List<Schedule> schedule_list,dummy;//dummy -> 최적화된 스케줄을 담고 있음.
    private ApplicationInfo appInfo;
    private String plan_id, memberid;
    private int[][] dist={{0,36,161,286,372,296,393,125,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,70,160,136,160,229,340,265,348,550},//0
            {36,0,173,299,390,309,405,133,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,92,198,149,138,236,349,280,355,510},//1
            {161,173,0,152,260,168,258,40,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,151,267,72,66,99,212,192,185,435},//2
            {286,299,152,0,115,212,108,180,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,234,338,177,214,181,241,128,91,425},//3
            {372,390,260,115,0,302,57,264,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,318,442,240,195,98,45,324,181,270},//4
            {296,309,168,212,302,0,261,191,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,342,445,283,322,250,239,236,98,220},//5
            {393,405,258,108,57,261,0,287,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,318,415,262,323,288,280,432,262,230},//6
            {125,133,40,180,264,192,287,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,127,255,52,59,127,240,190,230,462},//7
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},//8
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},//9
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},//10
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},//11
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},//12
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},//13
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},//14
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},//15
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},//16
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},//17
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},//18
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},//19
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},//20
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},//21
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},//22
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},//23
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},//24
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},//25
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},//26
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},//27
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},//28
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},//29
            {70,92,151,234,318,342,318,127,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,119,101,179,250,351,576,194,285},//30
            {160,198,267,338,442,445,415,255,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,119,0,220,283,361,477,705,265,400},//31
            {136,149,72,177,240,283,262,52,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,101,220,0,111,166,275,500,181,227},//32
            {160,138,66,214,195,322,323,59,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,179,283,111,0,127,236,458,237,257},//33
            {229,236,99,181,98,250,288,127,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,250,361,166,127,0,121,347,283,151},//34
            {340,349,212,241,45,239,280,240,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,351,477,275,236,121,0,232,347,183},//35
            {265,280,192,128,324,236,432,190,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,194,265,181,237,283,347,600,0,410},//36
            {348,355,185,91,181,98,262,230,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,285,400,227,257,151,183,200,410,0},//37
            {550,510,435,425,270,220,230,462,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,576,705,500,458,347,232,0,600,200}};//38
    private int[] permArr,citynum;
    private int[] pastShortPath;
    private int cityCount,cityLength, startNum,destiNum, shortDistance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modify_plan_view);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.modify_plan_actionbar);
        actionBar.setElevation(10);
        try {
            appInfo = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        mplan_title = (TextView)findViewById(R.id.mplan_title);
        reccomend_btn = (ImageButton)findViewById(R.id.recommend_btn);
        reccomend_btn.setOnClickListener(this);
        optimization_btn = (ImageButton)findViewById(R.id.schedule_optimization_btn);
        optimization_btn.setOnClickListener(this);
        done_btn = (Button)findViewById(R.id.done_btn);
        done_btn.setOnClickListener(this);
        add_schedule_btn = (RelativeLayout) findViewById(R.id.add_schedule_btn);
        modify_schedule_list = (RecyclerView)findViewById(R.id.modify_schedule_list);
        add_schedule_btn.setOnClickListener(this);

        LinearLayoutManager layoutmanager = new LinearLayoutManager(this);
        layoutmanager.setOrientation(LinearLayoutManager.VERTICAL);
        modify_schedule_list.setLayoutManager(layoutmanager);

        SharedPreferences pref = getSharedPreferences("TripPilotAutoLogin", Activity.MODE_PRIVATE);
        memberid = pref.getString("TripPilotID", null);

        Intent intent = getIntent();
        int intent_type = intent.getExtras().getInt("type");
        if(intent_type==1)
            schedule_list = new ArrayList<Schedule>();
        else if(intent_type ==2)
            schedule_list = intent.getBundleExtra("schedule_bundle").getParcelableArrayList("schedule_list");
        plan_id = intent.getExtras().getString("plan_id");
        mplan_title.setText(intent.getExtras().getString("plan_title"));

        adapter = new ScheduleAdapter(this, schedule_list, getSupportFragmentManager(),2);
        modify_schedule_list.setAdapter(adapter);

        adapter.setDataChangedListener(new DataChangedListener() { //데이터가 바뀌었다고 통보를 받았을 때
            @Override
            public void onChanged(List<Schedule> list) {
                schedule_list = list;
                adapter.notifyDataSetChanged();
            }
        });
    }

    /*뒤로가기 버튼 클릭 하였을 때*/
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.setResult(RESULT_CANCELED,null);
        finish();
    }

    @Override
    public void onClick(View view) {
        int view_id = view.getId();
        switch (view_id){
            case R.id.add_schedule_btn: // 일정 추가 버튼
                schedule_list.add(new Schedule("N",new ArrayList<Location>(),schedule_list.size()+1));
                adapter.notifyDataSetChanged();
                break;
            case R.id.recommend_btn: //추천 버튼
                final Dialog recDialog = new Dialog(this);
                recDialog.setContentView(R.layout.recommend_dialog);
                String[] mInterest = null;

                LinearLayoutManager layoutmanager = new LinearLayoutManager(this);
                layoutmanager.setOrientation(LinearLayoutManager.VERTICAL);
                RecyclerView recommend_list = (RecyclerView)recDialog.findViewById(R.id.recommend_area_list);
                final RecommendAdapter recommendAdapter = new RecommendAdapter(recDialog.getContext(),1,appInfo);
                recommend_list.setHasFixedSize(true);
                recommend_list.setLayoutManager(layoutmanager);
                recommend_list.setAdapter(recommendAdapter);
                String[] amount_list = {"1","2","3","4","5"};
                ArrayAdapter<String> recommend_amount_adapter = new ArrayAdapter<String>(recDialog.getContext(),R.layout.spinner_item,amount_list);
                recommend_amount_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                final Spinner amount_spinner = (Spinner)recDialog.findViewById(R.id.recommend_amount_spinner);
                amount_spinner.setAdapter(recommend_amount_adapter);
                ((RelativeLayout)recDialog.findViewById(R.id.add_recommend_area)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int len = recommendAdapter.getItemCount();
                        if(len<2) {
                            recommendAdapter.setSize(++len);
                        }
                    }
                });
                /*===============사용자 관심 정보를 가지고 온다.==============================*/
                try {
                    JSONObject interest = new ServerConnector().execute(appInfo.metaData.getString("select_interest"),"member_id",memberid).get();
                    JSONArray interest_list= interest.getJSONArray("list");
                    int size = interest_list.length();
                    mInterest = new String[size];
                    for(int i =0; i<size; i++){
                        interest = interest_list.getJSONObject(i);
                        mInterest[i] = interest.getString("interest");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                final String[] tInterest = mInterest;
                /*==============================================================*/
                ((Button)recDialog.findViewById(R.id.confirm_recommend)).setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        List<Integer> selectedArea = recommendAdapter.getSelectedArea();
                        Random rand = new Random();
                        List<Location> recommend_loc = new ArrayList<Location>();
                        int recommend_size = Integer.parseInt((String) amount_spinner.getSelectedItem());
                        for (int i = 0; i < recommend_size; i++) {
                            String interest = tInterest[rand.nextInt(tInterest.length)];
                            String areaCode = Integer.toString(selectedArea.get(rand.nextInt(selectedArea.size())));
                            try {
                                JSONObject json = new APIConnector(recDialog.getContext()).execute(appInfo.metaData.getString("areaBasedList"), "contentTypeId",interest
                                        , "areaCode", areaCode,"numOfRows", "1").get();
                                json = json.getJSONObject("response");
                                json = json.getJSONObject("body");
                                int totalCount = json.getInt("totalCount");
                                int pageNo = rand.nextInt(totalCount-1)+1;
                                json = new APIConnector(recDialog.getContext()).execute(appInfo.metaData.getString("areaBasedList"), "contentTypeId",interest
                                        , "areaCode", areaCode,"numOfRows", "1","pageNo",Integer.toString(pageNo)).get();
                                json = json.getJSONObject("response");
                                json = json.getJSONObject("body");
                                json = json.getJSONObject("items");
                                json = json.getJSONObject("item");
                                if(json.has("firstimage2"))
                                    recommend_loc.add(new Location("N",json.getString("contentid"),i+1,
                                        json.getString("firstimage2"),json.getString("title"),"",json.getInt("areacode")
                                        ,json.getInt("sigungucode"),json.getInt("contenttypeid"),0,json.getDouble("mapx"),json.getDouble("mapy")));
                                else
                                    recommend_loc.add(new Location("N",json.getString("contentid"),i+1,
                                            null,json.getString("title"),"",json.getInt("areacode")
                                            ,json.getInt("sigungucode"),json.getInt("contenttypeid"),0,json.getDouble("mapx"),json.getDouble("mapy")));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        if(schedule_list.size() !=0)
                            schedule_list.add(new Schedule("N",recommend_loc,schedule_list.get(schedule_list.size()-1).getDate()+1));
                        else
                            schedule_list.add(new Schedule("N",recommend_loc,1));
                        adapter.notifyDataSetChanged();
                        recDialog.dismiss();
                    }
                });
                ((Button)recDialog.findViewById(R.id.cancel_recommend)).setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        recDialog.dismiss();
                    }
                });
                recDialog.show();
                break;
            case R.id.schedule_optimization_btn: //최적화 버튼
                final Dialog dialog = new Dialog(this);
                dialog.setContentView(R.layout.optimization_dialog);
                final int[] area_code_list= new int[39];
                for(int i =0; i< area_code_list.length;i++)
                    area_code_list[i] = 0;
                for(Schedule sch:schedule_list){
                    List<Location> loc_list = sch.getContent();
                    int area_code = loc_list.get(0).getArea_code();
                    if(area_code_list[area_code]==1)
                        continue;
                    area_code_list[area_code]++;
                }
                final List<String> area_list = new ArrayList<String>();
                for(int i=1; i<area_code_list.length;i++){
                    if(area_code_list[i]!=0){
                        switch (i){
                            case 1:area_list.add("서울");break;
                            case 2:area_list.add("인천");break;
                            case 3:area_list.add("대전");break;
                            case 4:area_list.add("대구");break;
                            case 5:area_list.add("광주");break;
                            case 6:area_list.add("부산");break;
                            case 7:area_list.add("울산");break;
                            case 8:area_list.add("세종");break;
                            case 31:area_list.add("경기도");break;
                            case 32:area_list.add("강원도");break;
                            case 33:area_list.add("충청북도");break;
                            case 34:area_list.add("충청남도");break;
                            case 35:area_list.add("경상북도");break;
                            case 36:area_list.add("경상남도");break;
                            case 37:area_list.add("전라북도");break;
                            case 38:area_list.add("전라남도");break;
                            case 39:area_list.add("제주도");break;
                        }
                    }
                }
                ArrayAdapter<String> start_adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,area_list);
                ArrayAdapter<String> destination_adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,area_list);
                final Spinner start_city = (Spinner)dialog.findViewById(R.id.start_city);
                final Spinner destination_city = (Spinner)dialog.findViewById(R.id.destination_city);
                start_city.setAdapter(start_adapter);
                destination_city.setAdapter(destination_adapter);
                ((Button)dialog.findViewById(R.id.optimize_btn)).setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        /*================거리 계산 사전 작업========================*/
                        int length =area_list.size();
                        int[][] op_dist = new int[length][length];
                        citynum = new int[length];
                        for(int i =0; i<length; i++){
                            switch (area_list.get(i)){
                                case "서울":citynum[i]=0;break;
                                case "인천":citynum[i]=1;break;
                                case "대전":citynum[i]=2;break;
                                case "대구":citynum[i]=3;break;
                                case "광주":citynum[i]=4;break;
                                case "부산":citynum[i]=5;break;
                                case "울산":citynum[i]=6;break;
                                case "세종":citynum[i]=7;break;
                                case "경기도": citynum[i]=30;break;
                                case "강원도": citynum[i]=31;break;
                                case "충청북도": citynum[i]=32;break;
                                case "충청남도":citynum[i]=33;break;
                                case "경상북도":citynum[i]=34;break;
                                case "경상남도":citynum[i]=35;break;
                                case "전라북도":citynum[i]=36;break;
                                case "전라남도":citynum[i]=37;break;
                                case "제주도":citynum[i]=38;break;
                            }
                        }
                        for(int i =0; i< length; i++){
                            for(int j = 0; j<length; j++){
                                op_dist[i][j] = dist[citynum[i]][citynum[j]];
                            }
                        }
                        /*===================거리계산 사전작업 완료===================================*/
                        startNum= start_city.getSelectedItemPosition();
                        destiNum = destination_city.getSelectedItemPosition();
                        optimization(length);
                        schedule_list.clear();
                        int size = dummy.size();
                        for(int i =0; i< size; i++)
                            schedule_list.add(dummy.get(i));
                        adapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });
                ((Button)dialog.findViewById(R.id.cancel_optimize_btn)).setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                break;
            case R.id.done_btn: //완료 버튼
                try {
                    List<Schedule> delete_schedule_list = adapter.getDelete_schedule_list();
                    for(Schedule sch : delete_schedule_list) // 삭제할 스케줄이 있을 경우
                        new ServerConnector().execute(appInfo.metaData.getString("delete_schedule"),"schedule_id",sch.getSchedule_id());

                    for (Schedule sch : schedule_list) { // 남아있는 스케줄에 대해 업데이트 또는 등록
                        List<Location> loc_list = sch.getContent();
                        String sch_id = sch.getSchedule_id();
                        if (sch_id.equals("N")) {
                            JSONObject json = new ServerConnector().execute(appInfo.metaData.getString("insert_schedule"), "master_id", plan_id, "date", Integer.toString(sch.getDate())).get();
                            sch_id = json.getString("schedule_id");
                        }
                        else{
                            new ServerConnector().execute(appInfo.metaData.getString("update_schedule"), "schedule_id", sch_id, "date", Integer.toString(sch.getDate()));
                        }
                        for (Location loc : loc_list) {
                            String loc_id = null;
                            if (loc.getLocation_id().equals(""))
                                loc_id = "N";
                            else
                                loc_id = loc.getLocation_id();
                            new ServerConnector().execute(appInfo.metaData.getString("insert_location"), "master_schedule_id", sch_id
                                    , "location_id", loc_id, "content_id", loc.getContent_id(), "sequence", Integer.toString(loc.getOrder())
                                    , "area_code", Integer.toString(loc.getArea_code()),"sigungu_code", Integer.toString(loc.getSigungu_code()),"content_type", Integer.toString(loc.getContent_type()));
                        }
                    }
                    List<Location> delete_list = adapter.getDelete_Content_list();
                    for (Location loc : delete_list) { // 삭제할 장소가 있는 겨우
                        new ServerConnector().execute(appInfo.metaData.getString("delete_location"), "location_id", loc.getLocation_id());
                    }
                    Intent intent = new Intent();
                    Plan plan = new Plan(plan_id,memberid,null,mplan_title.getText().toString(),true,0,"");
                    intent.putExtra("plan",plan);
                    this.setResult(RESULT_OK, intent); // 이전 액티비티로 결과 값을 전달한다.
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    /*=========TSP 알고리즘을 이용한 경로 최적화 함수==========================================*/
    private void optimization(int length){
        cityCount = length; // cityCount ==스피너 도시 갯수 cityLength == 같을때와 다를때의 값
        if(startNum == destiNum) {
            permArr = new int[cityCount - 1];
            pastShortPath = new int[cityCount+1];
            cityLength = cityCount-1;
        }
        else {
            permArr = new int[cityCount - 2]; // 순열 조합
            pastShortPath = new int[cityCount];
            cityLength = cityCount-2;
        }
        shortDistance=99999;
        int temp =0;
        for(int i=0;i<cityCount;i++)
        {
            if(i==startNum||i==destiNum)continue;
            else permArr[temp++]=i;
        }
        ShortPath(0);
        permArr = new int[length];
        for(int i = 0; i<length; i++){
            permArr[i] = citynum[pastShortPath[i]]+1;
        }

        int size = schedule_list.size();
        int day = 1;
        dummy = new ArrayList<Schedule>();
        for(int i =0; i<permArr.length; i++){
            for(int j=0; j<size; j++){
                Schedule sch = schedule_list.get(j);
                int area = sch.getContent().get(0).getArea_code();
                int code = permArr[i];
                if(code==area) {
                    sch.setDate(day++);
                    dummy.add(sch);
                }
            }
        }
    }

    private void ShortPath(int depth)
    {
        if(depth==cityLength) {
            findShortPath();
            return;
        }

        for(int i=depth;i<cityLength;i++) {
            swap(permArr,i,depth);
            ShortPath(depth+1);
            swap(permArr,i,depth);
        }
    }

    private void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    private void findShortPath() {
        calculate(permArr);
    }

    private void calculate(int[] arr) {
        int point=startNum;
        int tempDistance = 0;
        for(int i=0;i<cityLength;i++)
        {
            tempDistance+=dist[point][arr[i]];
            point = arr[i];
        }
        tempDistance+=dist[point][destiNum];
        if(tempDistance<shortDistance) {
            pastShortPath[0] = startNum;
            shortDistance =tempDistance;
            for(int i=1;i<cityLength+1;i++) {
                pastShortPath[i] = arr[i-1];
            }
            pastShortPath[cityLength+1]=destiNum;
        }
    }
    /*=========TSP 알고리즘을 이용한 경로 최적화 함수==========================================*/
    class ServerConnector extends AsyncTask<String,Void,JSONObject> {
        ProgressDialog pDialog;
        public ServerConnector(){
            pDialog = new ProgressDialog(ModifyPlanView.this);
        }

        @Override
        protected void onPreExecute() {
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setMessage("로딩중입니다...");
            pDialog.show();
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
                pDialog.dismiss();
                con.disconnect();
            }
            return null;
        }
    }
}