package com.doitandroid.mybeta.itemclass;

import android.util.Log;

import com.doitandroid.mybeta.ConstantIntegers;
import com.doitandroid.mybeta.utils.InitializationOnDemandHolderIdiom;
import com.google.gson.JsonObject;

public class NotiItem {
    JsonObject jsonObject;

    int noticeKind;
    // post info
    String created, noticeID, commentText, postID;

    // creator
    UserItem user;
    boolean isEnd;
    boolean isEmpty;

    public boolean isEmpty() {
        return isEmpty;
    }

    public void setEmpty(boolean empty) {
        isEmpty = empty;
    }

    public NotiItem(JsonObject jsonObject) {


        // todo: notiitem에 postid가 필요하다..
        this.jsonObject = jsonObject;
        JsonObject item = jsonObject;
        if (item.get("is_empty").getAsBoolean()){
            this.isEmpty = item.get("is_empty").getAsBoolean();
            this.noticeKind = ConstantIntegers.OPT_EMPTY;
            // adapter viewtype 에서 쓰임

        } else {
            this.isEmpty = item.get("is_empty").getAsBoolean();

            this.noticeKind = item.get("notice_kind").getAsInt();
            Log.d("NotiItem", item.get("notice_kind").getAsInt()+"");
            this.noticeID = item.get("notice_id").getAsString();
            if (noticeKind == ConstantIntegers.NOTICE_POST_COMMENT){
                this.commentText = item.get("comment_text").getAsString();
            }
            this.postID = item.get("post_id").getAsString();

            this.created = item.get("created").getAsString();


            InitializationOnDemandHolderIdiom singleton = InitializationOnDemandHolderIdiom.getInstance();

            this.user = singleton.getUserItemFromSingletonByJsonObject(item.get("user").getAsJsonObject());

            if (item.get("is_followed").getAsBoolean()) {
                // singletone follow 리스트에 추가.
            }
            this.isEnd = item.get("is_end").getAsBoolean();
        }


        // todo: 여기서부터 notice 아이템 꾸미고 그 다음 노티스 아이템 엔드 아이디 널 처리해서 보내고 받아와서 뿌려주는 것 처리한다. created 비교해서 6h 등등 나누고.

    }
    //todo: 장고에서 주는 JsonResponse 정하고 그거에 맞춰서 클래스 init 정리.


    public boolean isEnd() {
        return isEnd;
    }

    public void setEnd(boolean end) {
        isEnd = end;
    }

    public JsonObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JsonObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public int getNoticeKind() {
        return noticeKind;
    }

    public void setNoticeKind(int noticeKind) {
        this.noticeKind = noticeKind;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getNoticeID() {
        return noticeID;
    }

    public void setNoticeID(String noticeID) {
        this.noticeID = noticeID;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public UserItem getUser() {
        return user;
    }

    public void setUser(UserItem user) {
        this.user = user;
    }


    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }
}
