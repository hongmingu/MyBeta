package com.doitandroid.mybeta.itemclass;

import android.util.Log;

import com.doitandroid.mybeta.utils.InitializationOnDemandHolderIdiom;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.Serializable;
import java.util.concurrent.CopyOnWriteArrayList;

@SuppressWarnings("serial")
public class UserItem implements Serializable {

    private static final String TAG = "UserItem";

    String username, userID, fullName, userPhoto;

    boolean isFollowed, isFullyUpdated;

    CopyOnWriteArrayList<UserItem> FollowingList;
    CopyOnWriteArrayList<UserItem> FollowerList;

    CopyOnWriteArrayList<OnUserItemChangedCallback> onUserItemChangedCallbackArrayList;

    // followlist 는 이 유저가 팔로우 하고 있는 사람들.

    // 여기서 followList 가 null 이면 리퀘스트로 찾는다. 아니면 이미 찾은 셈. 이런식으로 너무 계속 뻗어나가는 것을 방지.
    // 그리고 누가 a를 팔로우 하는지를 나타내고, a가 팔로우하는 사람까지 표시하는 것은 그림이 좋지 않은 것 같다.


    @SuppressWarnings("serial")
    public interface OnUserItemChangedCallback extends Serializable {

        void onItemChanged(UserItem userItem);
    }




    public void setOnUserItemChangedListener(OnUserItemChangedCallback onUserItemChangedCallback){
        Log.d(TAG, "setOnUserItemChangedListener");

        onUserItemChangedCallbackArrayList.add(onUserItemChangedCallback);

    }

    public UserItem(String username,
                    String userID,
                    String fullName,
                    String userPhoto,
                    CopyOnWriteArrayList<UserItem> FollowerList,
                    CopyOnWriteArrayList<UserItem> FollowingList,
                    boolean isFullyUpdated,
                    boolean isFollowed,
                    boolean followUpdate) {

        this.username = username;
        this.userID = userID;
        this.fullName = fullName;
        this.userPhoto = userPhoto;

        this.isFullyUpdated = isFullyUpdated;

        this.FollowerList = FollowerList;
        this.FollowingList = FollowingList;
        this.isFollowed = isFollowed;

        onUserItemChangedCallbackArrayList = new CopyOnWriteArrayList<>();
        InitializationOnDemandHolderIdiom singleton = InitializationOnDemandHolderIdiom.getInstance();
        singleton.updateUserList(this, followUpdate);
    }

    public UserItem(JsonObject jsonObject) {
        this.username = jsonObject.get("username").getAsString();
        this.userID = jsonObject.get("user_id").getAsString();
        this.fullName = jsonObject.get("full_name").getAsString();
        this.userPhoto = jsonObject.get("user_photo").getAsString();
        this.isFollowed = jsonObject.get("is_followed").getAsBoolean();

        FollowerList = new CopyOnWriteArrayList<UserItem>();
        FollowingList = new CopyOnWriteArrayList<UserItem>();

        if (jsonObject.get("follow_update").getAsBoolean()) {
            JsonArray followerJsonArray = jsonObject.get("related_follower_list").getAsJsonArray();
            for (JsonElement followerJsonElement : followerJsonArray) {
                JsonObject followerJsonItem = followerJsonElement.getAsJsonObject();
                UserItem followerUserItem = new UserItem(followerJsonItem);
                FollowerList.add(followerUserItem);

            }

            JsonArray followingJsonArray = jsonObject.get("related_following_list").getAsJsonArray();
            for (JsonElement followingJsonElement : followingJsonArray) {
                JsonObject followingJsonItem = followingJsonElement.getAsJsonObject();
                UserItem followingUserItem = new UserItem(followingJsonItem);
                FollowingList.add(followingUserItem);
            }

            this.isFullyUpdated = jsonObject.get("follow_update").getAsBoolean();
            // 여긴 그 유저에 딸린 관련 팔로우정보를 입력하는 것이므로 싱글톤에 관여하지 않는다.
            // 그러므로 add로 처리.
        } else {
            this.isFullyUpdated = jsonObject.get("follow_update").getAsBoolean();
        }
        onUserItemChangedCallbackArrayList = new CopyOnWriteArrayList<>();

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
            this.FollowingList = userItem.getFollowingList();
            this.FollowerList = userItem.getFollowerList();

            this.isFullyUpdated = true;

        } else {
            this.userPhoto = userItem.getUserPhoto();
            this.username = userItem.getUsername();
            this.fullName = userItem.getFullName();
            this.isFollowed = userItem.isFollowed();

        }


        Log.d(TAG, "updatedItem");
        onUserItemChangedLoop();


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

        InitializationOnDemandHolderIdiom singleton = InitializationOnDemandHolderIdiom.getInstance();

        UserItem userItem = singleton.getUserItemFromSingletonByUserID(singleton.getProfileUserID());
        userItem.setFullyUpdated(false);

        onUserItemChangedLoop();
    }

    public boolean isFullyUpdated() {
        return isFullyUpdated;
    }

    public void setFullyUpdated(boolean fullyUpdated) {
        isFullyUpdated = fullyUpdated;
    }

    private void onUserItemChangedLoop(){

        if (onUserItemChangedCallbackArrayList.size() != 0){
            for (OnUserItemChangedCallback onUserItemChangedCallback: onUserItemChangedCallbackArrayList){
                if (onUserItemChangedCallback != null){
                    try {
                        onUserItemChangedCallback.onItemChanged(this);
                    } catch (IllegalStateException e){
                        onUserItemChangedCallbackArrayList.remove(onUserItemChangedCallback);
                        Log.d(TAG, e.toString());
                    }

                } else {
                    Log.d(TAG, "null catch");

                }
            }
            Log.d(TAG, "setFollowed");

        }
    }

    public CopyOnWriteArrayList<UserItem> getFollowingList() {
        return FollowingList;
    }

    public void setFollowingList(CopyOnWriteArrayList<UserItem> followingList) {
        this.FollowingList = followingList;
    }

    public CopyOnWriteArrayList<UserItem> getFollowerList() {
        return FollowerList;
    }

    public void setFollowerList(CopyOnWriteArrayList<UserItem> followerList) {
        this.FollowerList = followerList;
    }

    public void addFollower(UserItem userItem){

        CopyOnWriteArrayList<UserItem> changeList = this.FollowerList;


        boolean isUpdated = false;
        for (UserItem existUserItem: changeList){
            if(existUserItem.isSameUserItem(userItem)){
                existUserItem.updateItem(userItem, false);
                isUpdated =true;

            }
        }
        if(!isUpdated){
            changeList.add(userItem);
        }
    }
    public void addFollowing(UserItem userItem){
        CopyOnWriteArrayList<UserItem> changeList = this.FollowingList;

        boolean isUpdated = false;
        for (UserItem existUserItem: changeList){
            if(existUserItem.isSameUserItem(userItem)){
                existUserItem.updateItem(userItem, false);
                isUpdated =true;

            }
        }
        if(!isUpdated){
            changeList.add(userItem);
        }
    }

}
