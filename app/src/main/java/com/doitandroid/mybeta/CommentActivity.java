package com.doitandroid.mybeta;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.doitandroid.mybeta.rest.APIInterface;
import com.doitandroid.mybeta.rest.ConstantREST;
import com.doitandroid.mybeta.rest.LoggedInAPIClient;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "CommentActivity";

    Intent gotIntent;
    AppCompatImageView comment_send_iv;
    AppCompatEditText comment_et;
    CircleImageView comment_profile_photo;

    CoordinatorLayout comment_content_cl;

    APIInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        comment_send_iv = findViewById(R.id.comment_send_iv);
        comment_send_iv.setOnClickListener(this);
        comment_et =findViewById(R.id.comment_et);

        comment_profile_photo = findViewById(R.id.comment_profile_photo_civ);

        comment_content_cl = findViewById(R.id.comment_content_cl);

        SharedPreferences sp = getSharedPreferences(ConstantStrings.SP_INIT_APP, MODE_PRIVATE);
        String profilePhoto = sp.getString(ConstantStrings.SP_ARG_PROFILE_PHOTO, ConstantStrings.SP_ARG_REMOVE_TOKEN);

        Glide.with(this)
                .load((ConstantREST.URL_HOME).substring(0, ConstantREST.URL_HOME.length()-1) + profilePhoto)
                .into(comment_profile_photo);

        apiInterface = getApiInterface();


        gotIntent = getIntent();

        getComment(gotIntent.getStringExtra(ConstantStrings.INTENT_POST_ID));

        // 이제 버튼 클릭시 애드코멘트 하는 거 추가.

    }

    @Override
    public void finish() {
        super.finish();
//        overridePendingTransition(R.anim.stay, R.anim.slide_down);
        overridePendingTransition(0, 0);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.comment_send_iv:
                String commentText = comment_et.getText().toString();

                addComment(gotIntent.getStringExtra(ConstantStrings.INTENT_POST_ID), commentText);
                break;
            default:
                break;
        }
    }

    public void addComment(String postID, String commentText){
        RequestBody requestPostID = RequestBody.create(MediaType.parse("multipart/form-data"), postID);
        RequestBody requestCommentText = RequestBody.create(MediaType.parse("multipart/form-data"), commentText);
        Call<JsonObject> call = apiInterface.addComment(requestPostID, requestCommentText);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject jsonObject = response.body();
                    if (jsonObject != null) {
                        int rc = jsonObject.get("rc").getAsInt();
                        if (rc != ConstantIntegers.SUCCEED_RESPONSE) {
                            // sign up 실패
                            call.cancel();
                            return;
                        }

                        JsonObject contentObject = jsonObject.get("content").getAsJsonObject();

                        Log.d(TAG, contentObject.toString());




                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                call.cancel();

            }
        });
    }


    private APIInterface getApiInterface(){
        SharedPreferences sp = getSharedPreferences(ConstantStrings.SP_INIT_APP, MODE_PRIVATE);
        String auth_token = sp.getString(ConstantStrings.SP_ARG_TOKEN, ConstantStrings.SP_ARG_REMOVE_TOKEN);
        APIInterface apiInterface = LoggedInAPIClient.getClient(auth_token).create(APIInterface.class);
        return apiInterface;
    }

    public void getComment(String postID){
        RequestBody requestPostID = RequestBody.create(MediaType.parse("multipart/form-data"), postID);
        Call<JsonObject> call = apiInterface.getComment(requestPostID);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject jsonObject = response.body();
                    if (jsonObject != null) {
                        int rc = jsonObject.get("rc").getAsInt();
                        if (rc != ConstantIntegers.SUCCEED_RESPONSE) {
                            // sign up 실패
                            call.cancel();
                            return;
                        }

                        JsonArray contentArray = jsonObject.get("content").getAsJsonArray();

                        Log.d(TAG, contentArray.toString());


                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                call.cancel();

            }
        });

    }
}
