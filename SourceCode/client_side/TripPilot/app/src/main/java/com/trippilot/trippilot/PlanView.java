package com.trippilot.trippilot;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by arden on 2018-09-11.
 */

public class PlanView extends AppCompatActivity implements View.OnClickListener,DataSetChangedListener, LocationListener{
    private TextView plan_title,plan_score;
    private Button plan_modify_Btn,plan_bring_btn;
    private ImageButton plan_nav_btn,plan_map_btn,plan_comment_btn;
    private RatingBar plan_star_rating;
    private RecyclerView plan_scheduleList,plan_commentList;
    private TextView empty_schedule_list,empty_comment_list;
    private ProgressBar progressing_schedule_list, progressing_comment_list;
    private ScheduleAdapter schedule_adapter;
    private CommentAdapter comment_adapter;
    private Plan plan;
    private List<Comment>comment_list;
    private List<Schedule>schedule_list;
    private List<String> loc_list;
    private ApplicationInfo appInfo;
    private SharedPreferences pref;
    private Activity activity;

    private final int REQUEST_FINE_LOCATION = 1234;
    boolean isGPSEnabled = false; //현재 GPS 사용유무
    boolean isNetworkEnabled = false; //네트워크 사용유무
    boolean isGetLocation = false; // GPS상태값
    private android.location.Location gpsLocation;
    double lat; // 위도
    double lon; // 경도
    // 최소 GPS 정보 업데이트 거리 10미터
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    // 최소 GPS 정보 업데이트 시간 밀리세컨이므로 1분
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;
    protected LocationManager locationManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.plan_view);
        activity =  this;
        try {
            appInfo = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        plan_title = (TextView)findViewById(R.id.plan_title);
        plan_modify_Btn = (Button)findViewById(R.id.plan_modify_btn);
        plan_bring_btn = (Button)findViewById(R.id.plan_bring_btn);
        plan_scheduleList = (RecyclerView)findViewById(R.id.plan_scheduleList);
        plan_nav_btn = (ImageButton) findViewById(R.id.plan_nav_btn);
        plan_map_btn = (ImageButton) findViewById(R.id.plan_map_btn);
        plan_comment_btn = (ImageButton) findViewById(R.id.plan_comment_btn);
        plan_star_rating = (RatingBar)findViewById(R.id.plan_star_rating);
        plan_score = (TextView)findViewById(R.id.plan_score);
        plan_commentList = (RecyclerView)findViewById(R.id.plan_commentList);
        empty_schedule_list = (TextView)findViewById(R.id.empty_schedule_list);
        empty_comment_list = (TextView)findViewById(R.id.empty_plan_comment);
        progressing_schedule_list = (ProgressBar) findViewById(R.id.progressing_schedule_list);
        progressing_comment_list = (ProgressBar) findViewById(R.id.progressing_comment_list);
        plan_modify_Btn.setOnClickListener(this);
        plan_bring_btn.setOnClickListener(this);
        plan_nav_btn.setOnClickListener(this);
        plan_map_btn.setOnClickListener(this);
        plan_comment_btn.setOnClickListener(this);

        Intent intent = getIntent();
        plan = (Plan) intent.getSerializableExtra("plan");
        plan_title.setText(plan.getTitle());
        int plan_type = intent.getIntExtra("plan_type",1);
        float star_rating = plan.getScore();
        plan_score.setText(Float.toString(star_rating));
        plan_star_rating.setRating(star_rating);

        switch(plan_type){ //플랜 타입에 따라서 보이는 버튼이 달라진다.
            case 1:
                plan_bring_btn.setVisibility(View.GONE);
                plan_modify_Btn.setVisibility(View.VISIBLE);
                break;
            case 2:
                plan_bring_btn.setVisibility(View.VISIBLE);
                plan_modify_Btn.setVisibility(View.GONE);
                break;
            default: //ERROR check
        }

        LinearLayoutManager schedulelayoutmanager = new LinearLayoutManager(getApplicationContext());
        schedulelayoutmanager.setOrientation(LinearLayoutManager.VERTICAL);
        LinearLayoutManager commentlayoutmanager = new LinearLayoutManager(getApplicationContext());
        commentlayoutmanager.setOrientation(LinearLayoutManager.VERTICAL);
        plan_scheduleList.setHasFixedSize(true);
        plan_scheduleList.setLayoutManager(schedulelayoutmanager);
        plan_commentList.setHasFixedSize(true);
        plan_commentList.setLayoutManager(commentlayoutmanager);

        comment_list = new ArrayList<Comment>();
        schedule_list = new ArrayList<Schedule>();
        comment_adapter = new CommentAdapter(this, comment_list, this);
        plan_commentList.setAdapter(comment_adapter);
        schedule_adapter = new ScheduleAdapter(this, schedule_list, getSupportFragmentManager(), 1);
        plan_scheduleList.setAdapter(schedule_adapter);
        new ServerConnector(1).execute(appInfo.metaData.getString("select_schedule"), "plan_id", plan.getPlan_id());
        new ServerConnector(2).execute(appInfo.metaData.getString("select_comment"), "content_id", plan.getPlan_id());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            switch (requestCode) {
                case 3000:
                    new ServerConnector(1).execute(appInfo.metaData.getString("select_schedule"), "plan_id", plan.getPlan_id());
                    break;
                case 2000:
                    Plan p = (Plan) data.getSerializableExtra("plan");
                    Intent intent = new Intent(this, PlanView.class);
                    intent.putExtra("plan",p);
                    intent.putExtra("plan_type",1);
                    startActivity(intent);
                    finish();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.plan_modify_btn :
                Intent intent = new Intent(this,ModifyPlanView.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                Bundle bundle  = new Bundle();
                bundle.putParcelableArrayList("schedule_list", (ArrayList<? extends Parcelable>) schedule_list);
                intent.putExtra("type",2);
                intent.putExtra("schedule_bundle",bundle);
                intent.putExtra("plan_id",plan.getPlan_id());
                intent.putExtra("plan_title",plan_title.getText().toString());
                startActivityForResult(intent,3000);
                break;
            case R.id.plan_bring_btn :
                showBringDialogBox();
                break;
            case R.id.plan_nav_btn :
                getLocation();
                showNavDialogBox();
                break;
            case R.id.plan_map_btn :
                showMapDialogBox();
                break;
            case R.id.plan_comment_btn :
                showCommentDialogBox();
                break;
        }
    }

    /*가져오기(플랜 검색 시에 대한 가져오기)*/
    private void showBringDialogBox(){
        final Dialog customDialog = new Dialog(this);
        customDialog.setTitle("플랜 생성");
        customDialog.setContentView(R.layout.add_plan_dialog);
        ((Button)customDialog.findViewById(R.id.add_p_Dbtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pref = getSharedPreferences("TripPilotAutoLogin", Activity.MODE_PRIVATE);
                String id = pref.getString("TripPilotID",null);
                String title = ((EditText) customDialog.findViewById(R.id.pd_edt)).getText().toString();
                if(title.replace(" ","").equals("")) {
                    errorHandle("제목을 입력하지 않았습니다.");
                    return;
                }
                Calendar today = Calendar.getInstance();
                String plan_id = "";
                String createdtime = today.get(Calendar.YEAR) + "." + Integer.toString(today.get(Calendar.MONTH) + 1) + "."
                        + today.get(Calendar.DATE);
                if(appInfo.metaData !=null) {
                    try {
                        JSONObject json = new ServerConnector(1).execute(appInfo.metaData.getString("insert_plan"),
                                "member_id", id,
                                "title", title, "scope", Boolean.toString(true),
                                "createdtime", createdtime).get();
                        plan_id = json.getString("plan_id");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Bundle bundle  = new Bundle();
                    List<Schedule> bring_schedule_list = new ArrayList<Schedule>();
                    int length = schedule_list.size();
                    for(int i =0; i<length; i++){
                        Schedule sch = schedule_list.get(i);
                        sch.setSchedule_id("N");
                        List<Location>loc = sch.getContent();
                        int size = loc.size();
                        for(int j = 0; j< size; j++){
                            Location temp = loc.get(j);
                            temp.setLocation_id("N");
                            loc.set(j,temp);
                        }
                        sch.setContent(loc);
                        bring_schedule_list.add(sch);
                    }
                    bundle.putParcelableArrayList("schedule_list", (ArrayList<? extends Parcelable>) bring_schedule_list);
                    Intent intent = new Intent(getApplicationContext(), ModifyPlanView.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("plan_id", plan_id);
                    intent.putExtra("plan_title", title);
                    intent.putExtra("type",2);
                    intent.putExtra("schedule_bundle",bundle);
                    startActivityForResult(intent,2000);
                }
                customDialog.dismiss();
            }
        });
        ((Button)customDialog.findViewById(R.id.cancel_p_Dbtn)).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialog.dismiss();
            }
        });
        customDialog.show();
    }

    private void showNavDialogBox(){
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.location_nav_dialog);
        String[] nav_data = {"자동차", "대중교통", "도보"};
        ((RelativeLayout)dialog.findViewById(R.id.nav_dialog_visibility)).setVisibility(View.VISIBLE);
        final Spinner nav_type = (Spinner) dialog.findViewById(R.id.location_nav_type);
        final Spinner day_spinner = (Spinner)dialog.findViewById(R.id.day_spinner);
        List<String> day_list = new ArrayList<String>();
        for(Schedule sch : schedule_list)
            day_list.add(Integer.toString(sch.getDate()));
        ArrayAdapter<String> day_adapter = new ArrayAdapter<String>(dialog.getContext(),R.layout.spinner_item,day_list);
        day_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        day_spinner.setAdapter(day_adapter);

        List<Location> loc = schedule_list.get(0).getContent();
        loc_list = new ArrayList<String>();
        for(Location l : loc)
            loc_list.add(l.getName());
        loc_list.add("현재 위치");
        final ArrayAdapter<String> source_adapter = new ArrayAdapter<String>(dialog.getContext(),R.layout.spinner_item,loc_list);
        source_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        final Spinner source_loc = (Spinner)dialog.findViewById(R.id.source_spinner);
        source_loc.setAdapter(source_adapter);
        final ArrayAdapter<String> destination_adapter = new ArrayAdapter<String>(dialog.getContext(),R.layout.spinner_item,loc_list);
        destination_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        final Spinner destination_loc = (Spinner)dialog.findViewById(R.id.destination_spinner);
        destination_loc.setAdapter(destination_adapter);

        ArrayAdapter<String> nav_adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, nav_data);
        nav_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        nav_type.setAdapter(nav_adapter);

        day_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long L) {
                loc_list.clear();
                List<Location> loc = schedule_list.get(i).getContent();
                for(Location l : loc)
                    loc_list.add(l.getName());
                loc_list.add("현재 위치");
                source_adapter.notifyDataSetChanged();
                destination_adapter.notifyDataSetChanged();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
        ((Button) dialog.findViewById(R.id.loc_nav_confirm)).setOnClickListener(new Button.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View view) {
                Intent startLink = activity.getPackageManager().getLaunchIntentForPackage("net.daum.android.map");
                if (startLink == null) {
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "net.daum.android.map")));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + "net.daum.android.map")));
                    }
                } else {
                    String ntype = (String) nav_type.getSelectedItem();
                    Uri uri = null;
                    Intent intent = null;
                    List<Location> llist = schedule_list.get(day_spinner.getSelectedItemPosition()).getContent();
                    String sname = source_loc.getSelectedItem().toString(), dname = destination_loc.getSelectedItem().toString();
                    Location source = null, destination = null;
                    String str_url = "daummaps://route?sp=";
                    if (sname.equals("현재 위치")) {
                        str_url+=lat+","+lon+"&ep=";
                    }
                    else{
                        for (Location l : llist) {
                            if (l.getName().equals(sname))
                                source = l;
                        }
                        str_url+=source.getMapy() + "," + source.getMapx() + "&ep=";
                    }
                    if (dname.equals("현재 위치")) {
                        str_url+=lat+","+lon+"&by=";

                    } else {
                        for (Location l : llist) {
                            if (l.getName().equals(dname))
                                destination = l;
                        }
                          str_url+= destination.getMapy() + "," + destination.getMapx() + "&by=";
                    }
                    switch (ntype) {
                        case "자동차":
                            uri = Uri.parse(str_url + "CAR");
                            intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);
                            dialog.dismiss();
                            break;
                        case "대중교통":
                            uri = Uri.parse(str_url + "PUBLICTRANSIT");
                            intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);
                            dialog.dismiss();
                            break;
                        case "도보":
                            uri = Uri.parse(str_url + "FOOT");
                            intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);
                            dialog.dismiss();
                            break;
                    }
                }
            }
        });
        ((Button)dialog.findViewById(R.id.loc_nav_cancel)).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    /*지도 보기 다이얼로그*/
    private void showMapDialogBox(){
        List<Location> temp = new ArrayList<Location>();
        for(Schedule sch : schedule_list){
            List<Location> loc = sch.getContent();
            for(Location l :loc)
                temp.add(l);
        }
        final MapDialog dialog = new MapDialog();
        dialog.setLocation(temp);
        dialog.show(this.getSupportFragmentManager(), "location_map");
    }

    /*댓글 등록 다이얼로그*/
    public void showCommentDialogBox(){
        final Dialog customDialog = new Dialog(this);
        customDialog.setTitle("댓글 달기");
        customDialog.setContentView(R.layout.add_comment_dialog);
        ((RatingBar)customDialog.findViewById(R.id.add_comment_star_rating)).setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                ((TextView)customDialog.findViewById(R.id.add_comment_scoring)).setText(Float.toString(v));
            }
        });
        ((Button)customDialog.findViewById(R.id.add_comment_confirm)).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                pref = getSharedPreferences("TripPilotAutoLogin", Activity.MODE_PRIVATE);

                String comment = ((EditText)customDialog.findViewById(R.id.add_comment_txt)).getText().toString();
                if(comment.replace(" ","").equals("")){
                    errorHandle("댓글을 입력하지 않았습니다.");
                    return;
                }
                String scoring = ((TextView)customDialog.findViewById(R.id.add_comment_scoring)).getText().toString();
                if(scoring.equals("0.0")){
                    errorHandle("별점을 등록하지 않았습니다.");
                    return;
                }
                String id = pref.getString("TripPilotID",null);
                Date d = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
                String createdtime = sdf.format(d);
                if(appInfo.metaData !=null) {
                    new ServerConnector(2).execute(appInfo.metaData.getString("insert_comment"), "content_type", "0", "writer_id", id,
                            "content_id", plan.getPlan_id(), "content", comment, "star_rating", scoring, "createdtime", createdtime);
                    new ServerConnector(2).execute(appInfo.metaData.getString("select_comment"), "content_id", plan.getPlan_id());
                }
                customDialog.dismiss();
            }
        });
        ((Button)customDialog.findViewById(R.id.add_comment_cancel)).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialog.dismiss();
            }
        });
        customDialog.show();
    }

    private void errorHandle(String msg){
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
    public void dataChanged(boolean ischanged) {
        if(ischanged)
            new ServerConnector(2).execute(appInfo.metaData.getString("select_comment"), "content_id", plan.getPlan_id());
    }

    public android.location.Location getLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(PlanView.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION);
        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(
                        PlanView.this, android.Manifest.permission.ACCESS_FINE_LOCATION )
                        != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                        PlanView.this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return null;
        }

        try {
            locationManager = (LocationManager) this
                    .getSystemService(LOCATION_SERVICE);

            // GPS 정보 가져오기
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // 현재 네트워크 상태 값 알아오기
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // GPS 와 네트워크사용이 가능하지 않을때 소스 구현
            } else {
                this.isGetLocation = true;
                // 네트워크 정보로 부터 위치값 가져오기
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (locationManager != null) {
                        gpsLocation = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (gpsLocation != null) {
                            // 위도 경도 저장
                            lat = gpsLocation.getLatitude();
                            lon = gpsLocation.getLongitude();
                        }
                    }
                }

                if (isGPSEnabled) {
                    if (gpsLocation == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        if (locationManager != null) {
                            gpsLocation = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (gpsLocation != null) {
                                lat = gpsLocation.getLatitude();
                                lon = gpsLocation.getLongitude();
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return gpsLocation;
    }

    /**
     * GPS 종료
     * */
    public void stopUsingGPS(){
        if(locationManager != null){
            locationManager.removeUpdates(PlanView.this);
        }
    }
    /**
     * GPS 나 wife 정보가 켜져있는지 확인합니다.
     * */
    public boolean isGetLocation() {
        return this.isGetLocation;
    }

    @Override
    public void onLocationChanged(android.location.Location location) {}
    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {}
    @Override
    public void onProviderEnabled(String s) {}
    @Override
    public void onProviderDisabled(String s) {}
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("gps", "Location permission granted");
                    try {
                        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                        locationManager.requestLocationUpdates("gps", 0, 0, this);
                    } catch (SecurityException ex) {
                        Log.d("gps", "Location permission did not work!");
                    }
                }
                break;
        }
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
                if (type == 1) {
                    schedule_list.clear();
                    JSONObject obj = null;
                    String plan_id = plan.getPlan_id();
                    try {
                        JSONObject json_data = jsonObject;

            /*===================계획에 대한 일정 데이터 추출============================================================================*/
                        int size = json_data.getInt("total_day");
                        JSONArray list = json_data.getJSONArray("list");
                        for (int i = 0; i < size; i++) {
                            json_data = list.getJSONObject(i);
                            JSONArray day_list = json_data.getJSONArray(Integer.toString(i + 1));
                            String schedule_id = null;
                            List<String> location_id_list = new ArrayList<String>();
                            List<String> content_id_list = new ArrayList<String>();
                            List<Integer> sequence_list = new ArrayList<Integer>();
                            List<Integer> area_code_list = new ArrayList<Integer>();
                            List<Integer> sigungu_code_list = new ArrayList<Integer>();
                            List<Integer> content_type_list = new ArrayList<Integer>();
                            List<Float> star_rating_list = new ArrayList<Float>();
                            schedule_id = json_data.getString("schedule_id");
                            if (json_data.getString("isEmpty").equals("true")) {
                                schedule_list.add(new Schedule(schedule_id, new ArrayList<Location>(), i + 1));
                                continue;
                            }
                            for (int j = 0; j < day_list.length(); j++) {
                                obj = (JSONObject) day_list.get(j);
                                location_id_list.add(obj.getString("location_id"));
                                content_id_list.add(obj.getString("content_id"));
                                sequence_list.add(Integer.parseInt(obj.getString("sequence")));
                                area_code_list.add(Integer.parseInt(obj.getString("area_code")));
                                sigungu_code_list.add(Integer.parseInt(obj.getString("sigungu_code")));
                                content_type_list.add(Integer.parseInt(obj.getString("content_type")));
                                star_rating_list.add(Float.parseFloat(obj.getString("star_rating")));
                            }
                            List<Location> content_list = new ArrayList<Location>();
                            for (int j = 0; j < content_id_list.size(); j++) {
                                if (appInfo.metaData != null) {
                                    obj = new APIConnector(activity).execute(appInfo.metaData.getString("detailCommon"),
                                            "contentId", content_id_list.get(j), "defaultYN", "Y", "firstImageYN", "Y", "mapinfoYN", "Y", "overviewYN", "Y").get();
                                }
                                obj = (JSONObject) obj.get("response");
                                obj = (JSONObject) obj.get("body");
                                obj = (JSONObject) obj.get("items");
                                obj = (JSONObject) obj.get("item");
                                Location tempLoc = null;
                                try {
                                    tempLoc = new Location(location_id_list.get(j), obj.getString("contentid"), sequence_list.get(j), obj.getString("firstimage"),
                                            obj.getString("title"), obj.getString("overview"), area_code_list.get(j), sigungu_code_list.get(j), content_type_list.get(j)
                                            , star_rating_list.get(j), Double.parseDouble(obj.getString("mapx")), Double.parseDouble(obj.getString("mapy")));
                                } catch (Exception e) {
                                    tempLoc = new Location(location_id_list.get(j), obj.getString("contentid"), sequence_list.get(j), null,
                                            obj.getString("title"), obj.getString("overview"), area_code_list.get(j), sigungu_code_list.get(j), content_type_list.get(j)
                                            , star_rating_list.get(j), Double.parseDouble(obj.getString("mapx")), Double.parseDouble(obj.getString("mapy")));
                                }
                                content_list.add(tempLoc);
                            }
                            schedule_list.add(new Schedule(schedule_id, content_list, i + 1));
                        }
                        progressing_schedule_list.setVisibility(View.GONE);
                        if(schedule_list.size()==0)
                            empty_schedule_list.setVisibility(View.VISIBLE);
                        else
                            empty_schedule_list.setVisibility(View.GONE);
                        schedule_adapter.notifyDataSetChanged();
            /*====================================================================================================================*/
                    } catch (Exception e) {
                        progressing_schedule_list.setVisibility(View.GONE);
                        if(schedule_list.size()==0)
                            empty_schedule_list.setVisibility(View.VISIBLE);
                        else
                            empty_schedule_list.setVisibility(View.GONE);
                        schedule_adapter.notifyDataSetChanged();
                        e.printStackTrace();
                    }
                } else if (type == 2) {
                    comment_list.clear();
                    JSONObject json_data = jsonObject;
                    try {
                        final float loc_score = Float.parseFloat(json_data.getString("average"));
                        JSONArray comment_array = (JSONArray) json_data.get("list");
                        for (int i = 0; i < comment_array.length(); i++) {
                            json_data = (JSONObject) comment_array.get(i);
                            Comment cmt = new Comment(json_data.getString("comment_id"), json_data.getString("writer_id"), Float.parseFloat(json_data.getString("star_rating"))
                                    , json_data.getString("content"), json_data.getString("createdtime"));
                            comment_list.add(cmt);
                        }
                        progressing_comment_list.setVisibility(View.GONE);
                        if(comment_list.size()==0)
                            empty_comment_list.setVisibility(View.VISIBLE);
                        else
                            empty_comment_list.setVisibility(View.GONE);
                        plan_star_rating.setRating(loc_score);
                        plan_score.setText(Float.toString(loc_score));
                        comment_adapter.notifyDataSetChanged();

            /*===========================================================================*/
                    } catch (Exception e) {
                        e.printStackTrace();
                        progressing_comment_list.setVisibility(View.GONE);
                        if(comment_list.size()==0)
                            empty_comment_list.setVisibility(View.VISIBLE);
                        else
                            empty_comment_list.setVisibility(View.GONE);
                        comment_adapter.notifyDataSetChanged();
                    }
                }
            }
       }
   }
}