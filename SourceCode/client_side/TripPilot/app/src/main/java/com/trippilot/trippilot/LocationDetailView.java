package com.trippilot.trippilot;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by arden on 2018-09-21.
 */

public class LocationDetailView extends AppCompatActivity implements View.OnClickListener,DataSetChangedListener,LocationListener{
    private TextView title, overview, score;
    private ImageView location_img;
    private RatingBar star_rating;
    private ImageButton nav_btn, map_btn, comment_btn;
    private RecyclerView comment_listview;
    private TextView empty_comment_list;
    private ProgressBar progressing_comment_list;
    private ApplicationInfo appInfo;
    private Location location;
    private List<Comment> comment_list;
    private CommentAdapter comment_adapter;
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
        setContentView(R.layout.location_detail_view);
        title = (TextView) findViewById(R.id.location_title);
        overview = (TextView) findViewById(R.id.location_detail_overview);
        score = (TextView) findViewById(R.id.location_detail_score);
        location_img = (ImageView) findViewById(R.id.location_detail_img);
        star_rating = (RatingBar) findViewById(R.id.location_detail_star_rating);
        nav_btn = (ImageButton) findViewById(R.id.location_detail_nav_btn);
        map_btn = (ImageButton) findViewById(R.id.location_detail_map_btn);
        comment_btn = (ImageButton) findViewById(R.id.location_detail_comment_btn);
        comment_listview = (RecyclerView) findViewById(R.id.location_detail_commentList);
        progressing_comment_list = (ProgressBar)findViewById(R.id.progressing_loc_comment);
        empty_comment_list = (TextView)findViewById(R.id.empty_loc_comment_list);
        activity = this;
        nav_btn.setOnClickListener(this);
        map_btn.setOnClickListener(this);
        comment_btn.setOnClickListener(this);

        overview.setMovementMethod(new ScrollingMovementMethod());

        LinearLayoutManager layoutmanager = new LinearLayoutManager(this);
        layoutmanager.setOrientation(LinearLayoutManager.VERTICAL);
        comment_listview.setLayoutManager(layoutmanager);
        Intent intent = getIntent();
        location = (Location) intent.getBundleExtra("lc_bundle").getSerializable("location");

        try {
            appInfo = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            getData();

            comment_list = new ArrayList<Comment>();
            comment_adapter = new CommentAdapter(this, comment_list,this);
            comment_listview.setAdapter(comment_adapter);
            String content_id = location.getContent_id();
            new ServerConnector().execute(appInfo.metaData.getString("select_comment"), "content_id", content_id);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void getData() {
        JSONObject json_data = null;
        try {
            json_data = new APIConnector(this).execute(appInfo.metaData.getString("detailCommon"),
                    "contentId", location.getContent_id(), "overviewYN", "Y").get();
            json_data = (JSONObject) json_data.get("response");
            json_data = (JSONObject) json_data.get("body");
            json_data = (JSONObject) json_data.get("items");
            json_data = (JSONObject) json_data.get("item");
            String ovStr = json_data.getString("overview");
            ovStr = ovStr.replace("<br />","");
            ovStr = ovStr.replace("<br>","");
            ovStr = ovStr.replace("<strong>","");
            ovStr = ovStr.replace("</strong>","");
            ovStr = ovStr.replace("&nbsp;","");
            location.setContent(ovStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        star_rating.setRating(location.getScore());
        score.setText(Float.toString(location.getScore()));
        String img = location.getImgurl();
        if (img != null)
            Glide.with(this).load(img).into(location_img); //썸네일 등록
        overview.setText(location.getContent());
        title.setText(location.getName());
    }

    @Override
    public void onClick(View view) {
        int btn_id = view.getId();
        switch (btn_id) {
            case R.id.location_detail_nav_btn:
                getLocation();
                showNavDialog();
                break;
            case R.id.location_detail_map_btn:
                showMapDialog();
                break;
            case R.id.location_detail_comment_btn:
                showCommentDialog();
                break;
        }
    }

    private void showNavDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.location_nav_dialog);
        String[] nav_data = {"자동차", "대중교통", "도보"};
        ((TextView)dialog.findViewById(R.id.textView12)).setText("현재위치로 부터 길찾기");
        final Spinner nav_type = (Spinner) dialog.findViewById(R.id.location_nav_type);
        ArrayAdapter<String> nav_adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, nav_data);
        nav_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        nav_type.setAdapter(nav_adapter);
        ((Button) dialog.findViewById(R.id.loc_nav_confirm)).setOnClickListener(new Button.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View view) {
                Intent startLink = activity.getPackageManager().getLaunchIntentForPackage("net.daum.android.map");
                if(startLink==null)
                {
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "net.daum.android.map")));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" +"net.daum.android.map")));
                    }
                }
                else {
                    String ntype = (String) nav_type.getSelectedItem();
                    Uri uri = null;
                    Intent intent = null;
                    String str_url = "daummaps://route?sp=" + lat + "," + lon + "&ep=" + location.getMapy() + "," + location.getMapx() + "&by=";
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
                    stopUsingGPS();
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

    private void showMapDialog() {
        List<Location> list = new ArrayList<Location>();
        list.add(location);
        final MapDialog dialog = new MapDialog();
        dialog.setLocation(list);
        dialog.show(this.getSupportFragmentManager(), "location_map");
    }
    private void showCommentDialog(){
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
                SharedPreferences pref = getSharedPreferences("TripPilotAutoLogin", Activity.MODE_PRIVATE);
                String comment = ((EditText)customDialog.findViewById(R.id.add_comment_txt)).getText().toString();
                String scoring = ((TextView)customDialog.findViewById(R.id.add_comment_scoring)).getText().toString();
                String id = pref.getString("TripPilotID",null);
                Date d = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
                String createdtime = sdf.format(d);
                if(appInfo.metaData !=null) {
                    new ServerConnector().execute(appInfo.metaData.getString("insert_comment"), "content_type", "1", "writer_id", id,
                            "content_id", location.getContent_id(), "content", comment, "star_rating", scoring, "createdtime", createdtime);
                    new ServerConnector().execute(appInfo.metaData.getString("select_comment"), "content_id", location.getContent_id());
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

    @Override
    public void dataChanged(boolean ischanged) {
        if(ischanged)
            new ServerConnector().execute(appInfo.metaData.getString("select_comment"), "content_id", location.getContent_id());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public android.location.Location getLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(LocationDetailView.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION);
        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(
                        LocationDetailView.this, android.Manifest.permission.ACCESS_FINE_LOCATION )
                        != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                        LocationDetailView.this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
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
                        if (location != null) {
                            // 위도 경도 저장
                            lat = gpsLocation.getLatitude();
                            lon = gpsLocation.getLongitude();
                        }
                    }
                }

                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        if (locationManager != null) {
                            gpsLocation = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
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
            locationManager.removeUpdates(LocationDetailView.this);
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
            if (jsonObject != null) {
                comment_list.clear();
                JSONObject json_data = jsonObject;
                try {
                    String content_id = location.getContent_id();
                    float loc_score = Float.parseFloat(json_data.getString("average"));
                    star_rating.setRating(loc_score);
                    score.setText(Float.toString(loc_score));
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
                    comment_adapter.notifyDataSetChanged();
            /*===========================================================================*/
                } catch (Exception e) {
                    progressing_comment_list.setVisibility(View.GONE);
                    if(comment_list.size()==0)
                        empty_comment_list.setVisibility(View.VISIBLE);
                    else
                        empty_comment_list.setVisibility(View.GONE);
                    comment_adapter.notifyDataSetChanged();
                    e.printStackTrace();
                }
            }
        }
    }
}

