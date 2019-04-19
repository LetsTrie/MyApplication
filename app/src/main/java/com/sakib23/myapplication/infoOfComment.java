package com.sakib23.myapplication;

public class infoOfComment {

    String  userName,commentDetails;
    String userUid;
    String timestamp;

    public infoOfComment() {
        userName=commentDetails = userUid =timestamp= "";
    }


    public infoOfComment(String userName, String commentDetails, String userUid, String timestamp) {
        this.userName=userName;
        this.commentDetails = commentDetails;
        this.userUid = userUid;
        this.timestamp = timestamp;
    }

    public String getcommentDetails() {
        return commentDetails;
    }

    public String getUserUid() {
        return userUid;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getUserName() {
        return userName;
    }
}
