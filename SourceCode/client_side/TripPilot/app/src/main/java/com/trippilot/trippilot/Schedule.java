package com.trippilot.trippilot;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.List;

/**
 * Created by arden on 2018-09-12.
 */

public class Schedule implements Parcelable{
    private String schedule_id;
    private List<Location> content;
    private int date;

    public Schedule(String schedule_id,List<Location> content, int date){
        this.schedule_id = schedule_id;
        this.content = content;
        this.date = date;
    }

    protected Schedule(Parcel in) {
        schedule_id = in.readString();
        content = in.readArrayList(Location.class.getClassLoader());
        date = in.readInt();
    }

    public static final Creator<Schedule> CREATOR = new Creator<Schedule>() {
        @Override
        public Schedule createFromParcel(Parcel in) {
            return new Schedule(in);
        }

        @Override
        public Schedule[] newArray(int size) {
            return new Schedule[size];
        }
    };

    public void setSchedule_id(String schedule_id){this.schedule_id = schedule_id;}
    public void setContent(List<Location> content){this.content=content;}
    public void setDate(int date){this.date=date;}
    public String getSchedule_id(){return schedule_id;}
    public List<Location> getContent(){return content;}
    public int getDate(){return date;}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(schedule_id);
        parcel.writeList(content);
        parcel.writeInt(date);
    }
}
