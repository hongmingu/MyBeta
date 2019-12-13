package com.doitandroid.mybeta.itemclass;

import android.view.View;

import com.doitandroid.mybeta.ConstantAnimations;
import com.doitandroid.mybeta.ConstantIntegers;
import com.doitandroid.mybeta.PingItem;
import com.doitandroid.mybeta.utils.InitializationOnDemandHolderIdiom;
import com.google.gson.JsonObject;

import java.util.ArrayList;

public class FeedItem {
    JsonObject jsonObject;

    int opt;
    // post info
    String created, postText, pingID, pingText, postID;

    // creator
    UserItem user;

    String profilePhoto;


    Integer pingRes, reactCount, commentCount;



    Boolean isReacted;

    ArrayList<UserItem> commentUserArrayList;


    ArrayList<UserItem> likeUserArrayList;




    public FeedItem(JsonObject jsonObject) {

        this.jsonObject = jsonObject;
        this.opt = jsonObject.get("opt").getAsInt();
        switch (opt) {
            case ConstantIntegers.OPT_DEFAULT_PING:


            JsonObject item = jsonObject.get("con").getAsJsonObject();
            this.created = item.get("created").getAsString();
            this.postID = item.get("post_id").getAsString();


            this.pingID = item.get("ping_id").isJsonNull() ? null : item.get("ping_id").getAsString() ;
            this.pingRes = pingID != null ? getPingResByPingID(pingID) : null;

            //todo: django 에서 오타 수정 pint_id 가 아니라 ping_id, user_fullname 이 아니라 full_name, userPhoto 추가, 로그인한 사람 자신의 포토,

            InitializationOnDemandHolderIdiom singleton = InitializationOnDemandHolderIdiom.getInstance();

            this.user = singleton.getUserItemFromSingletonByJsonObject(item.get("user").getAsJsonObject());



            this.reactCount = item.get("react_count").getAsInt();
            this.commentCount = item.get("comment_count").getAsInt();

            this.postText = item.get("post_text").isJsonNull() ? null : item.get("post_text").getAsString();
            this.isReacted = item.get("is_reacted").getAsBoolean();


            break;
            case ConstantIntegers.OPT_TO_CLICK:
                break;
            default:
                break;
        }

        // 여기서 이제 jsonObject 에 따라서 init 한다.
    }
    //todo: 장고에서 주는 JsonResponse 정하고 그거에 맞춰서 클래스 init 정리.

    public Integer getPingResByPingID(String pingID){
        for (PingItem pingConstantItem: ConstantAnimations.pingList){
            if (pingConstantItem.getPingID().equals(pingID)){
                return pingConstantItem.getPingRes();
            }
        }
        return null;

    }

    public JsonObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JsonObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public int getOpt() {
        return opt;
    }


    public void setOpt(int opt) {
        this.opt = opt;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getPostText() {
        return postText;
    }

    public void setPostText(String postText) {
        this.postText = postText;
    }

    public String getPingID() {
        return pingID;
    }

    public void setPingID(String pingID) {
        this.pingID = pingID;
    }

    public String getPingText() {
        return pingText;
    }

    public void setPingText(String pingText) {
        this.pingText = pingText;
    }

    public Integer getPingRes() {
        return pingRes;
    }

    public void setPingRes(Integer pingRes) {
        this.pingRes = pingRes;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public Integer getReactCount() {
        return reactCount;
    }

    public void setReactCount(Integer reactCount) {
        this.reactCount = reactCount;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }

    public Boolean getReacted() {
        return isReacted;
    }

    public void setReacted(Boolean reacted) {
        isReacted = reacted;
    }

    public ArrayList<UserItem> getCommentUserArrayList() {
        return commentUserArrayList;
    }

    public void setCommentUserArrayList(ArrayList<UserItem> commentUserArrayList) {
        this.commentUserArrayList = commentUserArrayList;
    }

    public ArrayList<UserItem> getLikeUserArrayList() {
        return likeUserArrayList;
    }

    public void setLikeUserArrayList(ArrayList<UserItem> likeUserArrayList) {
        this.likeUserArrayList = likeUserArrayList;
    }


    public UserItem getUser() {
        return user;
    }

    public void setUser(UserItem user) {
        this.user = user;
    }

}
