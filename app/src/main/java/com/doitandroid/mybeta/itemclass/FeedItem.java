package com.doitandroid.mybeta.itemclass;

import android.util.Log;

import com.doitandroid.mybeta.ConstantPings;
import com.doitandroid.mybeta.ConstantIntegers;
import com.doitandroid.mybeta.PingItem;
import com.doitandroid.mybeta.utils.InitializationOnDemandHolderIdiom;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;

public class FeedItem {
    JsonObject jsonObject;
    InitializationOnDemandHolderIdiom singleton = InitializationOnDemandHolderIdiom.getInstance();

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

    ArrayList<UserItem> recentUserList;
    String recentDate;

    boolean isEmptyFlag;
    boolean isRecentUserExist;
    boolean isList;

    public boolean isList() {
        return isList;
    }

    public void setList(boolean list) {
        isList = list;
    }

    public String getRecentDate() {
        return recentDate;
    }

    public void setRecentDate(String recentDate) {
        this.recentDate = recentDate;
    }

    public boolean isRecentUserExist() {
        return isRecentUserExist;
    }

    public void setRecentUserExist(boolean recentUserExist) {
        isRecentUserExist = recentUserExist;
    }

    public FeedItem(JsonObject jsonObject) {

        this.jsonObject = jsonObject;

        if(!jsonObject.get("is_list").getAsBoolean()){

            this.isList = false;
            JsonObject item = jsonObject.get("con").getAsJsonObject();
            this.opt = getOptFromFeedJsonObject(item);

            switch (opt) {
                case ConstantIntegers.OPT_DEFAULT_PING:


                    this.created = item.get("created").getAsString();
                    this.postID = item.get("post_id").getAsString();

                    this.pingID = item.get("ping_id").isJsonNull() ? null : item.get("ping_id").getAsString();
                    this.pingRes = pingID != null ? getPingResByPingID(pingID) : null;

                    if (item.get("ping_text").isJsonNull()){
                        for (PingItem pingConstantItem: ConstantPings.defaultPingList){
                            if (pingConstantItem.getPingID().equals(pingID)){
                                this.pingText = pingConstantItem.getPingText();
                            }
                        }
                    } else {
                        this.pingText = item.get("ping_text").getAsString();
                    }



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
        } else {

            this.isList = true;
            this.recentUserList = new ArrayList<>();


            JsonArray recentArray = jsonObject.get("con").getAsJsonArray();
            this.recentDate = jsonObject.get("date").getAsString();

            if(jsonObject.get("date").getAsString().equals("empty")){
                this.isEmptyFlag = jsonObject.get("date").getAsString().equals("empty");
                this.opt = ConstantIntegers.OPT_EMPTY;
            } else {
                this.isEmptyFlag = true;
                this.opt = ConstantIntegers.OPT_LIST;
            }


            for (JsonElement element: recentArray){
                this.recentUserList.add(singleton.getUserItemFromSingletonByJsonObject(element.getAsJsonObject()));
            }

            this.isRecentUserExist = recentArray.size() != 0;
        }

        // 여기서 이제 jsonObject 에 따라서 init 한다.
    }
    //todo: 장고에서 주는 JsonResponse 정하고 그거에 맞춰서 클래스 init 정리.

    public Integer getPingResByPingID(String pingID){
        for (PingItem pingConstantItem: ConstantPings.defaultPingList){
            if (pingConstantItem.getPingID().equals(pingID)){
                return pingConstantItem.getPingRes();
            }
        }
        return null;

    }

    public int getOptFromFeedJsonObject(JsonObject item){
        String pingID = item.get("ping_id").isJsonNull() ? null : item.get("ping_id").getAsString();

        if (pingID == null){
            return ConstantIntegers.OPT_DEFAULT_PING;
        } else {
            if (pingID.startsWith("de")){
                return ConstantIntegers.OPT_DEFAULT_PING;

            }
        }
        return ConstantIntegers.OPT_DEFAULT_PING;

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

    public ArrayList<UserItem> getRecentUserList() {
        return recentUserList;
    }

    public void setRecentUserList(ArrayList<UserItem> recentUserList) {
        this.recentUserList = recentUserList;
    }



}
