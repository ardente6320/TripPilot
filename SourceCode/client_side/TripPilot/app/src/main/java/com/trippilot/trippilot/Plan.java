package com.trippilot.trippilot;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by arden on 2018-09-12.
 */

/*Plan Data Class*/
public class Plan implements Serializable{
    private String plan_id;
    private String member_id;
    private String imgurl;
    private String title;
    private boolean scope;
    private float score;
    private String createdtime;

    public Plan(String plan_id,String member_id,String imgurl,String title,boolean scope,float score,String createdtime){
        this.plan_id = plan_id;
        this.member_id = member_id;
        this.imgurl = imgurl;
        this.title = title;
        this.scope = scope;
        this.score = score;
        this.createdtime = createdtime;
    }

    public void setPlan_id(String plan_id){this.plan_id = plan_id;}
    public void setMember_id(String member_id){this.member_id = member_id;}
    public void setImgurl(String imgurl){this.imgurl=imgurl;}
    public void setTitle(String title){this.title = title;}
    public void setScope(boolean scope){this.scope = scope;}
    public void setScore(float score){this.score = score;}
    public void setCreatedtime(String createdtime){this.createdtime = createdtime;}
    public String getPlan_id(){return plan_id;}
    public String getMember_id(){return member_id;}
    public String getImgurl(){return imgurl;}
    public String getTitle(){return title;}
    public boolean getScope(){return scope;}
    public float getScore(){return score;}
    public String getCreatedtime(){return createdtime;}
}
