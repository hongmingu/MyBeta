package com.doitandroid.mybeta.rest;

import com.google.gson.JsonObject;

import java.util.ArrayList;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface APIInterface {
//
//    @FormUrlEncoded
//    @Multipart
//    @POST("u/test_json/")
//    Call<ArrayList<TestResource>> getTestJson(@Part MultipartBody.Part file, @Field("field1") String field1);
//
//    @Multipart
//    @POST("u/test_json/")
//    Call<ArrayList<TestResource>> getTestJson(@Part MultipartBody.Part file, @Part("description") RequestBody description);

    @Multipart
    @POST("u/test_json/")
    Call<ArrayList<JsonObject>> getTestJson(@Part MultipartBody.Part file, @Part("description") RequestBody description, @Part("second") RequestBody second);

    //google의 json

    @Multipart
    @POST("u/test_token/")
    Call<ArrayList<JsonObject>> getTestToken(@Part("description") RequestBody description);

/*
    @Multipart
    @POST("u/test_datum/")
    Call<ArrayList<TestResource>> getTestDatum(@Part MultipartBody.Part file, @Part("description") RequestBody description);
*/

    @Multipart
    @POST("u/test_json_dic/")
    Call<ArrayList<JsonObject>> getTestJsonText(@Part("description") RequestBody description, @Part("title") RequestBody title);

    /* Sign Up */
    @Multipart
    @POST("r/rest/sign_up/")
    Call<JsonObject> sign_up(@Part("full_name") RequestBody full_name, @Part("email") RequestBody email, @Part("password") RequestBody password);

    /* Log In */

    @Multipart
    @POST("r/rest/log_in/")
    Call<JsonObject> log_in(@Part("account") RequestBody account, @Part("password") RequestBody password);

    /* refresh for you Pings */

    @POST("r/rest/refresh_for_you_pings/")
    Call<JsonObject> refresh_for_you_pings();

    /* refresh recommend Pings */

    @POST("r/rest/refresh_recommend_pings/")
    Call<JsonObject> refresh_recommend_pings();

    /* refresh search content pings */

    @POST("r/rest/refresh_search_content_pings/")
    Call<JsonObject> refresh_search_content_pings();

    /* refresh ping search result */
    @Multipart
    @POST("r/rest/refresh_ping_search_result/")
    Call<JsonObject> refresh_ping_search_result(@Part("search_word") RequestBody search_word);


    /* send instant ping */
    @Multipart
    @POST("r/rest/send_instant_ping/")
    Call<JsonObject> send_instant_ping(@Part("ping_id") RequestBody ping_id);

    /* get follow feed */
    @POST("r/rest/get_follow_feed/")
    Call<JsonObject> get_follow_feed();


    /* follow */
    @Multipart
    @POST("r/rest/follow/")
    Call<JsonObject> follow(@Part("user_id") RequestBody user_id);


    /* get_related_follower */
    @Multipart
    @POST("r/rest/get_related_follower/")
    Call<JsonObject> get_related_follower(@Part("user_id") RequestBody user_id);

    /* search */
    @Multipart
    @POST("r/rest/search/")
    Call<JsonObject> search(@Part("search_word") RequestBody search_word);


    /* add post */
    @Multipart
    @POST("r/rest/add_post/")
    Call<JsonObject> addPost(@Part("ping_id") RequestBody ping_id,
                             @Part("ping_text") RequestBody ping_text,
                             @Part("post_text") RequestBody post_text);


    /* add comment */
    @Multipart
    @POST("r/rest/add_comment/")
    Call<JsonObject> addComment(@Part("post_id") RequestBody post_id,
                                @Part("comment_text") RequestBody comment_text);

    /* react */
    @Multipart
    @POST("r/rest/react/")
    Call<JsonObject> react(@Part("post_id") RequestBody post_id);


}
