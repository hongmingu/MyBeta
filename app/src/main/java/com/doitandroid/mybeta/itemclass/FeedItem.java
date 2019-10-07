package com.doitandroid.mybeta.itemclass;

import com.doitandroid.mybeta.ConstantIntegers;
import com.google.gson.JsonObject;

import java.util.ArrayList;

public class FeedItem {
    JsonObject jsonObject;

    int opt;
    // post info
    String created, postText, pingID;

    // creator
    String username, userID, fullname, photo;
    Integer reactCount, commentCount;
    Boolean isReacted;

    ArrayList<UserItem> commentUserArrayList, likeUserArrayList;


    public FeedItem(JsonObject jsonObject) {

        this.jsonObject = jsonObject;
        this.opt = jsonObject.get("opt").getAsInt();
        switch (opt){
            case ConstantIntegers.OPT_DEFAULT_PING:

                [{"opt":1,"con":{"post_id":"77525228c3b0435caf83d9c387833e71","postText":null,"pint_id":"5","user_id":"900218257910401369","username":"ghdalsrn2","user_fullname":"ghdalsrn2","comment_display_user_list":[],"comment_count":6,"react_display_user_list":[],"react_count":6,"is_reacted":false}},{"opt":1,"con":{"post_id":"53bf8040da94494995a20f9fb9767c2a","postText":null,"pint_id":"13","user_id":"900218257910401369","username":"ghdalsrn2","user_fullname":"ghdalsrn2","comment_display_user_list":[],"comment_count":6,"react_display_user_list":[],"react_count":6,"is_reacted":false}},{"opt":1,"con":{"post_id":"130fe66d08fe4ce18a88797bd247c66e","postText":null,"pint_id":"12","user_id":"900218257910401369","username":"ghdalsrn2","user_fullname":"ghdalsrn2","comment_display_user_list":[],"comment_count":6,"react_display_user_list":[],"react_count":6,"is_reacted":false}}]

                JsonObject item = jsonObject.get("con").getAsJsonObject();
                this.created = item.get("created").getAsString();

                this.pingID = item.get("ping_id") != null ? item.get("ping_id").getAsString() : null;

                //todo: django 에서 오타 수정 pint_id 가 아니라 ping_id, user_fullname 이 아니라 full_name, photo 추가, 로그인한 사람 자신의 포토,

                this.username = item.get("username").getAsString();
                this.fullname = item.get("full_name").getAsString();
                this.photo = item.get("photo").getAsString();
                this.userID = item.get("user_id").getAsString();
                this.reactCount = item.get("react_count").getAsInt();
                this.commentCount = item.get("comment_count").getAsInt();

                this.postText = item.get("post_text") != null ? item.get("post_text").getAsString() : null;
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

    public int getOpt() {
        return opt;
    }
}
