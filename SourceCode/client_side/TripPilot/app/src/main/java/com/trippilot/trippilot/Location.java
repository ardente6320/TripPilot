package com.trippilot.trippilot;

import java.io.Serializable;

/**
 * Created by arden on 2018-09-12.
 */

public class Location implements Serializable{
    private String location_id;
    private String content_id;
    private int order;
    private String imgurl;
    private String name;
    private String content;
    private float score;
    private double mapx;
    private double mapy;
    private int area_code;
    private int sigungu_code;
    private int content_type;
    private boolean checked;
    public Location(String location_id,String content_id,int order,String imgurl,String name, String content,int area_code,int sigungu_code, int content_type, float score,double mapx, double mapy){
        this.location_id = location_id;
        this.content_id = content_id;
        this.order = order;
        this.imgurl = imgurl;
        this.name = name;
        this.content = content;
        this.area_code = area_code;
        this.sigungu_code = sigungu_code;
        this.content_type = content_type;
        this.score = score;
        this.mapx = mapx;
        this.mapy = mapy;
        this.checked = false;
    }
    public void setLocation_id(String location_id) {this.location_id = location_id;}
    public void setContent_id(String content_id) {this.content_id = content_id;}
    public void setOrder(int order){this.order = order;}
    public void setImgurl(String imgurl) {this.imgurl = imgurl;}
    public void setName(String name) {this.name = name;}
    public void setContent(String content) {this.content = content;}
    public void setArea_code(int area_code) {this.area_code = area_code;}
    public void setSigungu_code(int sigungu_code){this.sigungu_code = sigungu_code;}
    public void setContent_type(int content_type) {this.content_type = content_type;}
    public void setScore(float score) {this.score = score;}
    public void setMapx(double mapx) {this.mapx = mapx;}
    public void setMapy(double mapy) {this.mapy = mapy;}
    public void setChecked (boolean checked){this.checked = checked;}
    public String getLocation_id() {return location_id;}
    public String getContent_id(){return content_id;}
    public int getOrder(){return order;}
    public String getImgurl() {return imgurl;}
    public String getName() {return name;}
    public String getContent() {return content;}
    public int getArea_code() {return area_code;}
    public int getSigungu_code(){return sigungu_code;}
    public int getContent_type() {return content_type;}
    public float getScore() {return score;}
    public double getMapx() {return mapx;}
    public double getMapy() {return mapy;}
    public boolean getChecked(){return checked;}
}
