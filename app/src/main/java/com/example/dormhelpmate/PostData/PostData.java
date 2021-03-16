package com.example.dormhelpmate.PostData;

public class PostData {

    public  String username,date,time,description;
    public  String profile_pic, post_pic;

    public PostData() {

    }

    public PostData(String profile_pic, String username, String date, String time, String description, String post_pic) {
        this.profile_pic = profile_pic;
        this.username = username;
        this.date = date;
        this.time = time;
        this.description = description;
        this.post_pic = post_pic;
    }

    public String getProfile_pic() {
        return profile_pic;
    }

    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }

    public String getName() {
        return username;
    }

    public void setName(String name) {
        this.username = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPost_pic() {
        return post_pic;
    }

    public void setPost_pic(String post_pic) {
        this.post_pic = post_pic;

    }


}
