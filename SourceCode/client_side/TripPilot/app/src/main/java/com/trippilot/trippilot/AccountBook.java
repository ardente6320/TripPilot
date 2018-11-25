package com.trippilot.trippilot;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by arden on 2018-10-08.
 */

public class AccountBook implements Parcelable{
    private String accountBookId;
    private String title;
    private List<AccountBookDate> date_list;
    private boolean ischecked;

    public AccountBook(String accountBookId, String ABName, List<AccountBookDate> date_list) {
        this.accountBookId = accountBookId;
        this.title = ABName;
        ischecked = false;
        this.date_list = date_list;
    }

    protected AccountBook(Parcel in) {
        accountBookId = in.readString();
        title = in.readString();
        date_list = in.readArrayList(AccountBookDate.class.getClassLoader());
        ischecked = in.readByte() != 0;
    }

    public static final Creator<AccountBook> CREATOR = new Creator<AccountBook>() {
        @Override
        public AccountBook createFromParcel(Parcel in) {
            return new AccountBook(in);
        }

        @Override
        public AccountBook[] newArray(int size) {
            return new AccountBook[size];
        }
    };

    public void setAccountBookId(String accountBookId) {
        this.accountBookId = accountBookId;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public void setDate_list(List<AccountBookDate> date_list) {
        this.date_list = date_list;
    }

    public void setIschecked(boolean isChecked){this.ischecked = isChecked;}
    public String getAccountBookId() {
        return accountBookId;
    }
    public String getTitle() {
        return title;
    }

    public List<AccountBookDate> getDate_list() {
        return date_list;
    }

    public boolean getIsChecked() {
        return ischecked;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(accountBookId);
        parcel.writeString(title);
        parcel.writeList(date_list);
        parcel.writeByte((byte) (ischecked ? 1 : 0));
    }
}
