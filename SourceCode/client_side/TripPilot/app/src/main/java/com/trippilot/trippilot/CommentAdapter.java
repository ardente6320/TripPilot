package com.trippilot.trippilot;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v7.widget.ForwardingListener;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
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

/**
 * Created by arden on 2018-09-12.
 */

public class CommentAdapter extends RecyclerView.Adapter<CommentViewHolder>{
    private Context context;
    private List<Comment> comment_list;
    private DataSetChangedListener dataSetChangedListener;
    private ApplicationInfo appInfo;
    private String id;
    public CommentAdapter(Context context,List<Comment>comment_list,DataSetChangedListener dataSetChangedListener){
        this.context = context;
        this.comment_list = comment_list;
        this.dataSetChangedListener =dataSetChangedListener;
        SharedPreferences pref = context.getSharedPreferences("TripPilotAutoLogin", Activity.MODE_PRIVATE);
        id = pref.getString("TripPilotID",null);
        try {
            appInfo = this.context.getPackageManager().getApplicationInfo(this.context.getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_itemcard_view,null);
        return new CommentViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CommentViewHolder holder, final int position) {
        holder.comment_writer.setText(comment_list.get(position).getWriter());
        holder.comment_star_rating.setRating(comment_list.get(position).getScore());
        holder.comment_score.setText(Float.toString(comment_list.get(position).getScore()));
        holder.comment_contents.setText(comment_list.get(position).getContent());
        holder.delete_comment_btn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("댓글 삭제");
                builder.setMessage("댓글을 삭제합니다.\n댓글에 대한 정보가 사라집니다. 삭제하시겠습니까?");
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new ServerConnector().execute(appInfo.metaData.getString("delete_comment"),"comment_id",comment_list.get(position).getComment_id());
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

        holder.modify_comment_btn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog customDialog = new Dialog(context);
                customDialog.setContentView(R.layout.add_comment_dialog);
                EditText edt = (EditText)customDialog.findViewById(R.id.add_comment_txt);
                edt.setText(comment_list.get(position).getContent());
                RatingBar ratingbar = (RatingBar)customDialog.findViewById(R.id.add_comment_star_rating);
                final TextView score = (TextView)customDialog.findViewById(R.id.add_comment_scoring);
                ratingbar.setRating(comment_list.get(position).getScore());
                score.setText(Float.toString(comment_list.get(position).getScore()));
                ratingbar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                        score.setText(Float.toString(v));
                    }
                });
                ((Button)customDialog.findViewById(R.id.add_comment_confirm)).setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String comment = ((EditText)customDialog.findViewById(R.id.add_comment_txt)).getText().toString();
                        String scoring = ((TextView)customDialog.findViewById(R.id.add_comment_scoring)).getText().toString();
                        Calendar today = Calendar.getInstance();
                        String createdtime = today.get(Calendar.YEAR) + "." + Integer.toString(today.get(Calendar.MONTH) + 1) + "."
                                + today.get(Calendar.DATE);
                        if(appInfo.metaData !=null) {
                            new ServerConnector().execute(appInfo.metaData.getString("update_comment"), "comment_id",comment_list.get(position).getComment_id(), "content", comment, "star_rating", scoring,"createdtime",createdtime);
                        }
                        dataSetChangedListener.dataChanged(true);
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
        });

        if(comment_list.get(position).getWriter().equals(id))
            holder.modify_comment_visibility.setVisibility(View.VISIBLE);
        else
            holder.modify_comment_visibility.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return comment_list.size();
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
