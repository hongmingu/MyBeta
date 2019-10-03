package com.doitandroid.mybeta.itemclass;

import org.json.JSONObject;

import java.util.ArrayList;

public class PostItem {
    JSONObject jsonObject;

    // post info
    String created, text, pingID;

    // creator
    String username, userID, fullname, photo;
    Integer likeCount, commentCount;

    ArrayList<UserItem> commentUserArrayList, likeUserArrayList;


    public PostItem(JSONObject jsonObject) {

        this.jsonObject = jsonObject;
        // 여기서 이제 jsonObject 에 따라서 init 한다.
    }
    //todo: 장고에서 주는 JsonResponse 정하고 그거에 맞춰서 클래스 init 정리.
    public String getOption() {
        String option = "";
        return option;
    }

}
