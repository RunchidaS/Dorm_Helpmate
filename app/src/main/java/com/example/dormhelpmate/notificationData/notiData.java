package com.example.dormhelpmate.notificationData;

public class notiData {
    public String title, body, date, time;

    public notiData() {
    }

    public notiData(String title, String body, String date, String time) {
        this.title = title;
        this.body = body;
        this.date = date;
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
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
}
