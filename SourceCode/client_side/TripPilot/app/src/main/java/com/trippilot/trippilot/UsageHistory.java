package com.trippilot.trippilot;


import java.io.Serializable;

/**
 * Created by arden on 2018-10-09.
 */

public class UsageHistory implements Serializable{
    private String content_id;
    private String date;
    private int money;
    private String usage_history;
    private String type;
    private String payment_method;
    private boolean isChecked;
    public UsageHistory(String content_id, String date,int money, String usage_history, String type, String payment_method) {
        this.content_id = content_id;
        this.date = date;
        this.money = money;
        this.usage_history = usage_history;
        this.type = type;
        this.payment_method = payment_method;
        isChecked = false;
    }



    public void setContent_id(String content_id) {
        this.content_id = content_id;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public void setUsage_history(String usage_history) {
        this.usage_history = usage_history;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setPayment_method(String payment_method) {
        this.payment_method = payment_method;
    }
    public void setIsChecked(boolean isChecked){this.isChecked = isChecked;}
    public String getContent_id() {
        return content_id;
    }

    public String getDate() {
        return date;
    }

    public int getMoney() {
        return money;
    }

    public String getUsage_history() {
        return usage_history;
    }

    public String getType() {
        return type;
    }

    public String getPayment_method() {
        return payment_method;
    }
    public boolean getIsChecked(){
        return isChecked;
    }

}
