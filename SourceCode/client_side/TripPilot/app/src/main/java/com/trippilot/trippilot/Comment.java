package com.trippilot.trippilot;

import java.util.Date;

/**
 * Created by arden on 2018-09-12.
 */

public class Comment {
    private String comment_id;
    private String writer;
    private float score;
    private String content;
    private String createdtime;

    public Comment(String comment_id, String writer, float score, String content, String createdtime) {
        this.comment_id = comment_id;
        this.writer = writer;
        this.score = score;
        this.content = content;
        this.createdtime = createdtime;
    }

    public void setComment_id(String comment_id) {this.comment_id = comment_id;}
    public void setWriter(String writer) {this.writer = writer;}
    public void setScore(float score) {this.score = score;}
    public void setContent(String content) {this.content = content;}
    public void setCreatedtime(String createdtime) {this.createdtime = createdtime;}
    public String getComment_id() {return comment_id;}
    public String getWriter() {return writer;}
    public float getScore() {return score;}
    public String getContent() {return content;}
    public String getCreatedtime() {return createdtime;}
}
