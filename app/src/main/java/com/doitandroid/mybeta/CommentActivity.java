package com.doitandroid.mybeta;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.doitandroid.mybeta.rest.APIInterface;
import com.doitandroid.mybeta.rest.LoggedInAPIClient;
import com.google.gson.JsonObject;

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
    APIInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        comment_send_iv = findViewById(R.id.comment_send_iv);
        comment_send_iv.setOnClickListener(this);
        comment_et =findViewById(R.id.comment_et);

        apiInterface = getApiInterface();


        gotIntent = getIntent();


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
        SharedPreferences sp = getSharedPreferences(ConstantStrings.INIT_APP, MODE_PRIVATE);
        String auth_token = sp.getString(ConstantStrings.TOKEN, ConstantStrings.REMOVE_TOKEN);
        APIInterface apiInterface = LoggedInAPIClient.getClient(auth_token).create(APIInterface.class);
        return apiInterface;
    }
}
