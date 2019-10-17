package com.doitandroid.mybeta.itemclass;

import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class UserItem implements Serializable {

    String username, userID, fullName, userPhoto;

    ArrayList<UserItem> followList;

    // 여기서 followList 가 null 이면 리퀘스트로 찾는다. 아니면 이미 찾은 셈. 이런식으로 너무 계속 뻗어나가는 것을 방지.
    // 그리고 누가 a를 팔로우 하는지를 나타내고, a가 팔로우하는 사람까지 표시하는 것은 그림이 좋지 않은 것 같다.


    public UserItem(String username, String userID, String fullName, String userPhoto, ArrayList<UserItem> followList) {
        this.username = username;
        this.userID = userID;
        this.fullName = fullName;
        this.userPhoto = userPhoto;
        this.followList = followList;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public ArrayList<UserItem> getFollowList() {
        return followList;
    }

    public void setFollowList(ArrayList<UserItem> followList) {
        this.followList = followList;
    }
}
