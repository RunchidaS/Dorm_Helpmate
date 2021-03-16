package com.example.dormhelpmate.CommentData;

public class CommentData {
    public  String username,date,time,comment;
    public  String profile_pic;

    public CommentData() {

    }

    public CommentData(String profile_pic, String username, String date, String time, String comment) {
        this.profile_pic = profile_pic;
        this.username = username;
        this.date = date;
        this.time = time;
        this.comment = comment;
    }


    public String getName() {
        return username;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getComment() {
        return comment;
    }

    public String getProfile_pic() {
        return profile_pic;
    }

}
