package com.doitandroid.mybeta.itemclass;

import com.doitandroid.mybeta.utils.InitializationOnDemandHolderIdiom;
import com.google.gson.JsonObject;

public class ReactItem {
    JsonObject jsonObject;


    /**
    *     serialized = {
     *         'user_id': user.username,
     *         'username': user.userusername.username,
     *         'full_name': user.userfullname.full_name,
     *         'user_photo': user.userphoto.file_300_url(),
     *         'related_follower_list': get_related_follower_list(user, user_who_read),
     *         'is_followed': get_is_followed(user, user_who_read),
     *         'comment_id': item.uuid,
     *         'created': item.created,
     *         'comment_text': item.text
     *
     *     }
    *
    * */
    // post info
    String created;

    // creator
    UserItem user;


    public ReactItem(JsonObject jsonObject) {


        // todo: 이제 서버에서 노티스 받아와서 처리해야함, 유저아이템 유저리스트에 넣는 것 등 잊지마.
        // todo: 아마 apiinterfcae랑 url처리하고 진행하면 될 거야.
        this.jsonObject = jsonObject;
        JsonObject item = jsonObject;


        this.created = item.get("created").getAsString();

        InitializationOnDemandHolderIdiom singleton = InitializationOnDemandHolderIdiom.getInstance();

        this.user = singleton.getUserItemFromSingletonByJsonObject(item.get("user").getAsJsonObject());

        if (item.get("is_followed").getAsBoolean()) {
            // singletone follow 리스트에 추가.
        }
        // todo: 여기서부터 notice 아이템 꾸미고 그 다음 노티스 아이템 엔드 아이디 널 처리해서 보내고 받아와서 뿌려주는 것 처리한다. created 비교해서 6h 등등 나누고.

    }
    //todo: 장고에서 주는 JsonResponse 정하고 그거에 맞춰서 클래스 init 정리.


    public JsonObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JsonObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public UserItem getUser() {
        return user;
    }

    public void setUser(UserItem user) {
        this.user = user;
    }
}
