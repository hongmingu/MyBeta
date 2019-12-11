package com.doitandroid.mybeta.itemclass;

import com.doitandroid.mybeta.utils.InitializationOnDemandHolderIdiom;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class UserItem implements Serializable {

    String username, userID, fullName, userPhoto;

    boolean isFollowed, isFullyUpdated;

    ArrayList<UserItem> relatedFollowingList, relatedFollowerList;


    // followlist 는 이 유저가 팔로우 하고 있는 사람들.

    // 여기서 followList 가 null 이면 리퀘스트로 찾는다. 아니면 이미 찾은 셈. 이런식으로 너무 계속 뻗어나가는 것을 방지.
    // 그리고 누가 a를 팔로우 하는지를 나타내고, a가 팔로우하는 사람까지 표시하는 것은 그림이 좋지 않은 것 같다.


    public UserItem(String username,
                    String userID,
                    String fullName,
                    String userPhoto,
                    ArrayList<UserItem> relatedFollowerList,
                    ArrayList<UserItem> relatedFollowingList,
                    boolean isFullyUpdated,
                    boolean isFollowed,
                    boolean followUpdate) {

        this.username = username;
        this.userID = userID;
        this.fullName = fullName;
        this.userPhoto = userPhoto;

        this.isFullyUpdated = isFullyUpdated;

        this.relatedFollowerList = relatedFollowerList;
        this.relatedFollowingList = relatedFollowingList;
        this.isFollowed = isFollowed;

        InitializationOnDemandHolderIdiom singleton = InitializationOnDemandHolderIdiom.getInstance();
        singleton.updateUserList(this, followUpdate);
    }

    public UserItem(JsonObject jsonObject) {
        this.username = jsonObject.get("username").getAsString();
        this.userID = jsonObject.get("user_id").getAsString();
        this.fullName = jsonObject.get("full_name").getAsString();
        this.userPhoto = jsonObject.get("user_photo").getAsString();
        this.isFollowed = jsonObject.get("is_followed").getAsBoolean();

        relatedFollowerList = new ArrayList<>();
        relatedFollowingList = new ArrayList<>();

        if (jsonObject.get("follow_update").getAsBoolean()) {
            JsonArray followerJsonArray = jsonObject.get("related_follower_list").getAsJsonArray();
            for (JsonElement followerJsonElement : followerJsonArray) {
                JsonObject followerJsonItem = followerJsonElement.getAsJsonObject();
                UserItem followerUserItem = new UserItem(followerJsonItem);
                relatedFollowerList.add(followerUserItem);

            }

            JsonArray followingJsonArray = jsonObject.get("related_following_list").getAsJsonArray();
            for (JsonElement followingJsonElement : followingJsonArray) {
                JsonObject followingJsonItem = followingJsonElement.getAsJsonObject();
                UserItem followingUserItem = new UserItem(followingJsonItem);
                relatedFollowingList.add(followingUserItem);
            }

            this.isFullyUpdated = jsonObject.get("follow_update").getAsBoolean();
            // 여긴 그 유저에 딸린 관련 팔로우정보를 입력하는 것이므로 싱글톤에 관여하지 않는다.
            // 그러므로 add로 처리.
        } else {
            this.isFullyUpdated = jsonObject.get("follow_update").getAsBoolean();
        }


        InitializationOnDemandHolderIdiom singleton = InitializationOnDemandHolderIdiom.getInstance();
        singleton.updateUserList(this, jsonObject.get("follow_update").getAsBoolean());
        // 전체 유저 리스트를 업데이트 해야하므로 중복검사를 해야한다.

    }


    public boolean isSameUserItem(UserItem userItem){

        if(this.userID.equals(userItem.getUserID())){
            return true;
        } else {
            return false;
        }
    }

    public void updateItem(UserItem userItem, boolean followUpdate){
        if (followUpdate){
            this.userPhoto = userItem.getUserPhoto();
            this.username = userItem.getUsername();
            this.fullName = userItem.getFullName();
            this.isFollowed = userItem.isFollowed();
            this.relatedFollowingList = userItem.getRelatedFollowingList();
            this.relatedFollowerList = userItem.getRelatedFollowerList();

            this.isFullyUpdated = true;

        } else {
            this.userPhoto = userItem.getUserPhoto();
            this.username = userItem.getUsername();
            this.fullName = userItem.getFullName();
            this.isFollowed = userItem.isFollowed();

        }
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


    public boolean isFollowed() {
        return isFollowed;
    }

    public void setFollowed(boolean followed) {
        isFollowed = followed;
    }

    public ArrayList<UserItem> getRelatedFollowingList() {
        return relatedFollowingList;
    }

    public void setRelatedFollowingList(ArrayList<UserItem> relatedFollowingList) {
        this.relatedFollowingList = relatedFollowingList;
    }

    public ArrayList<UserItem> getRelatedFollowerList() {
        return relatedFollowerList;
    }

    public void setRelatedFollowerList(ArrayList<UserItem> relatedFollowerList) {
        this.relatedFollowerList = relatedFollowerList;
    }
}
