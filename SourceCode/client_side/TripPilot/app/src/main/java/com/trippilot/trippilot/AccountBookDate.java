package com.trippilot.trippilot;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by arden on 2018-10-09.
 */

public class AccountBookDate implements Serializable{
    private String date;
    private List<UsageHistory> usagehistory_list;

    public AccountBookDate(String date, List<UsageHistory> usagehistory_list) {
        this.date = date;
        this.usagehistory_list = usagehistory_list;
    }

    protected AccountBookDate(Parcel in) {
        date = in.readString();
        usagehistory_list = new ArrayList<UsageHistory>();
        in.readList(usagehistory_list,UsageHistory.class.getClassLoader());
    }



    public void setDate(String date) {
        this.date = date;
    }

    public void setUsagehistory_list(List<UsageHistory> usagehistory_list) {
        this.usagehistory_list = usagehistory_list;
    }

    public String getDate() {
        return date;
    }

    public List<UsageHistory> getUsagehistory_list() {
        return usagehistory_list;
    }
}
