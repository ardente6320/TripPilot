package com.trippilot.trippilot;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by arden on 2018-09-12.
 */
interface DataChangedListener{
    void onChanged(List<Schedule> list);
}
public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleViewHolder> implements ScheduleContentAdapter.OnDataChangedListener{
    private Context context;
    private List<Schedule> schedule_list;
    private List<Location> delete_content_list;
    private List<Schedule> delete_schedule_list;
    private int type; //1이면 조회 2이면 수정
    private DataChangedListener dataChangedListener;
    private FragmentManager fm;

    @Override
    public void dataChanged(List<Location> list_location, Location location,int position) {
        Schedule temp = schedule_list.get(position);
        temp.setContent(list_location);
        schedule_list.set(position, temp);
        delete_content_list.add(location);
        dataChangedListener.onChanged(schedule_list);
    }

    public ScheduleAdapter(Context context,List<Schedule>schedule_list,FragmentManager fm,int type){
        this.context = context;
        this.schedule_list = schedule_list;
        delete_content_list = new ArrayList<Location>();
        delete_schedule_list = new ArrayList<Schedule>();
        this.fm = fm;
        this.type = type;
    }
    public List<Schedule> getDelete_schedule_list(){
        return delete_schedule_list;
    }
    public List<Location> getDelete_Content_list(){
        return delete_content_list;
    }
    public void setDataChangedListener(DataChangedListener dataChangedListener){
        this.dataChangedListener = dataChangedListener;
    }
    @Override
    public ScheduleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.schedule_itemcard_view,null);
        return new ScheduleViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ScheduleViewHolder holder, final int position) {
        //다른 사람일때의 체킹  생각 해보기 그리고 ScheduleContentAdapter 연결하기
        holder.day.setText(Integer.toString(schedule_list.get(position).getDate())+"일차");
        LinearLayoutManager contentlayoutmanager = new LinearLayoutManager(context);
        contentlayoutmanager.setOrientation(LinearLayoutManager.HORIZONTAL);
        holder.schedules.setLayoutManager(contentlayoutmanager);
        if(schedule_list.get(position).getContent().size()!=0)
            holder.EmptySchedule.setVisibility(View.GONE);
        else
            holder.EmptySchedule.setVisibility(View.VISIBLE);
        ScheduleContentAdapter adapter = null;
            if (type == 1) { //보기 모드 일때
                holder.add_content_btn.setVisibility(View.GONE);
                adapter = new ScheduleContentAdapter(context,schedule_list.get(position).getContent(),null,position,1);
            }
            else if (type == 2) { //수정 모드 일때
                holder.schedule_item_layout.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        final Dialog customDialog = new Dialog(context);
                        customDialog.setContentView(R.layout.schedule_longclick_layout);
                        String[] data = {"날짜 수정","일정 삭제"};
                        ArrayAdapter<String> dialog_adapter = new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1,data);
                        ((TextView)customDialog.findViewById(R.id.schedule_longclick_title)).setText(schedule_list.get(position).getDate()+"일차");
                        ListView list = (ListView)customDialog.findViewById(R.id.schedule_longclick_dialog);
                        list.setAdapter(dialog_adapter);
                        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int cnt, long l) {
                                if(cnt ==0){ // 날짜 수정
                                    customDialog.dismiss();
                                    final Dialog modify_dialog = new Dialog(context);
                                    modify_dialog.setContentView(R.layout.schedule_day_change_dialog);
                                    int length = schedule_list.size();
                                    String[] days = new String[length];
                                    for(int i =0; i< length; i++)
                                        days[i] = schedule_list.get(i).getDate() + "일차";
                                    ArrayAdapter<String> day_adapter = new ArrayAdapter<String>(context,android.R.layout.simple_spinner_dropdown_item,days);
                                    final Spinner spinner = (Spinner)modify_dialog.findViewById(R.id.schedule_day_spinner);
                                    spinner.setAdapter(day_adapter);
                                    ((Button)modify_dialog.findViewById(R.id.confirm_modify_date)).setOnClickListener(new Button.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Schedule temp = schedule_list.get(position);
                                            String day = String.valueOf(spinner.getSelectedItem());
                                            day = day.replaceAll("일차","");
                                            int current_day = temp.getDate()-1;
                                            int changed_day = Integer.parseInt(day)-1;
                                            if(current_day<changed_day){
                                                for(int i = current_day+1;i<=changed_day; i++){
                                                    Schedule sch = schedule_list.get(i);
                                                    sch.setDate(i);
                                                    schedule_list.set(i-1,sch);
                                                }
                                                temp.setDate(changed_day+1);
                                                schedule_list.set(changed_day,temp);
                                            }
                                            else if(current_day>changed_day){
                                                for(int i = current_day-1;i>=changed_day; i--){
                                                    Schedule sch = schedule_list.get(i);
                                                    sch.setDate(i+2);
                                                    schedule_list.set(i+1,sch);
                                                }
                                                temp.setDate(changed_day+1);
                                                schedule_list.set(changed_day,temp);
                                            }
                                            else
                                                modify_dialog.dismiss();

                                            dataChangedListener.onChanged(schedule_list);
                                            modify_dialog.dismiss();
                                        }
                                    });
                                    ((Button)modify_dialog.findViewById(R.id.cancel_modify_date)).setOnClickListener(new Button.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            modify_dialog.dismiss();
                                        }
                                    });
                                    modify_dialog.show();
                                }
                                else if(cnt==1){ // 일정 삭제
                                    customDialog.dismiss();
                                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                            context);
                                    alertDialogBuilder.setTitle("일정 삭제");
                                    alertDialogBuilder
                                            .setMessage("일정을 삭제 하시겠습니까?\n삭제를 하면 일정에 포함된 모든 장소들이 삭제됩니다.")
                                            .setCancelable(false)
                                            .setPositiveButton("확인",
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            delete_schedule_list.add(schedule_list.get(position));
                                                            schedule_list.remove(position);
                                                            int size = schedule_list.size();
                                                            for(int i =position; i<size; i++){
                                                                Schedule temp = schedule_list.get(i);
                                                                temp.setDate(temp.getDate()-1);
                                                                schedule_list.set(i,temp);
                                                            }
                                                            dataChangedListener.onChanged(schedule_list);
                                                            dialog.dismiss();
                                                        }
                                                    })
                                            .setNegativeButton("취소",
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            dialog.dismiss();
                                                        }
                                                    });
                                    AlertDialog alertDialog = alertDialogBuilder.create();
                                    alertDialog.show();
                                }
                            }
                        });
                        customDialog.show();
                        return true;
                    }
                });
                holder.add_content_btn.setVisibility(View.VISIBLE);
                holder.add_content_btn.setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final AddDialog dialog = new AddDialog();
                        dialog.setType(type);
                        dialog.show(fm,"add_dialog");
                        dialog.setAddDialogListener(new AddDialogListener() {
                            @Override
                            public void onClicked(List<Location> list) {
                                List<Location> content_list = schedule_list.get(position).getContent();
                                int size = content_list.size();
                                int ch_size= list.size();
                                for(int i =0 ; i<ch_size; i++) {
                                    Location loc = list.get(i);
                                    loc.setOrder(size+i+1);
                                    content_list.add(loc);
                                }
                                schedule_list.get(position).setContent(content_list);
                                dataChangedListener.onChanged(schedule_list);
                                dialog.dismiss();
                            }
                        });
                    }
                });
                adapter = new ScheduleContentAdapter(context,schedule_list.get(position).getContent(),this,position,2);
        }
        holder.schedules.setAdapter(adapter);
    }
    @Override
    public int getItemCount() {
        return schedule_list.size();
    }
}

interface AddDialogListener{
    void onClicked(List<Location> list);
}

