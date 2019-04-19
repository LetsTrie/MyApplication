package com.sakib23.myapplication;

public class UserPost {
    String myUserID;
    String myUserName;
    String myPostID;
    String myPostTitle;
    String myPostDes;
    String myPostTime;
    String myPostURL;
    String myPostExt;

    public UserPost(){
        myPostExt = myPostDes = myPostID = myUserID = myUserName = myPostTime = myPostTitle = myPostURL = myPostTime = "";
    }

    public UserPost(String myUserID, String myUserName, String myPostID, String myPostTitle, String myPostDes, String myPostTime) {
        this.myUserID = myUserID;
        this.myUserName = myUserName;
        this.myPostID = myPostID;
        this.myPostTitle = myPostTitle;
        this.myPostDes = myPostDes;
        this.myPostTime = myPostTime;
        this.myPostURL = null;
        this.myPostExt = null;
    }

    public UserPost(String myUserID, String myUserName, String myPostID, String myPostTitle, String myPostDes, String myPostTime, String myPostURL, String myPostExt) {
        this.myUserID = myUserID;
        this.myUserName = myUserName;
        this.myPostID = myPostID;
        this.myPostTitle = myPostTitle;
        this.myPostDes = myPostDes;
        this.myPostTime = myPostTime;
        this.myPostURL = myPostURL;
        this.myPostExt = myPostExt;
    }

    public String getMyUserID() {
        return myUserID;
    }

    public void setMyUserID(String myUserID) {
        this.myUserID = myUserID;
    }

    public String getMyUserName() {
        return myUserName;
    }

    public void setMyUserName(String myUserName) {
        this.myUserName = myUserName;
    }

    public String getMyPostID() {
        return myPostID;
    }

    public void setMyPostID(String myPostID) {
        this.myPostID = myPostID;
    }

    public String getMyPostTitle() {
        return myPostTitle;
    }

    public void setMyPostTitle(String myPostTitle) {
        this.myPostTitle = myPostTitle;
    }

    public String getMyPostDes() {
        return myPostDes;
    }

    public void setMyPostDes(String myPostDes) {
        this.myPostDes = myPostDes;
    }

    public String getMyPostTime() {
        return myPostTime;
    }

    public void setMyPostTime(String myPostTime) {
        this.myPostTime = myPostTime;
    }

    public String getMyPostURL() {
        return myPostURL;
    }

    public void setMyPostURL(String myPostURL) {
        this.myPostURL = myPostURL;
    }

    public String getMyPostExt() {
        return myPostExt;
    }

    public void setMyPostExt(String myPostExt) {
        this.myPostExt = myPostExt;
    }
}
