package com.example.videostreaming;

public class CommentModel {
    String date,time,uId,userImage,userMsg,userName;

    public CommentModel() {
    }

    public CommentModel(final String date, final String time, final String uId, final String userImage, final String userMsg, final String userName) {
        this.date = date;
        this.time = time;
        this.uId = uId;
        this.userImage = userImage;
        this.userMsg = userMsg;
        this.userName = userName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(final String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(final String time) {
        this.time = time;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(final String uId) {
        this.uId = uId;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(final String userImage) {
        this.userImage = userImage;
    }

    public String getUserMsg() {
        return userMsg;
    }

    public void setUserMsg(final String userMsg) {
        this.userMsg = userMsg;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(final String userName) {
        this.userName = userName;
    }
}
