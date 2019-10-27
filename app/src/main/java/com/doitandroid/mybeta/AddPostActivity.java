package com.doitandroid.mybeta;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.doitandroid.mybeta.itemclass.FeedItem;
import com.doitandroid.mybeta.ping.PingShownItem;
import com.doitandroid.mybeta.rest.APIInterface;
import com.doitandroid.mybeta.rest.ConstantREST;
import com.doitandroid.mybeta.rest.LoggedInAPIClient;
import com.doitandroid.mybeta.utils.InitializationOnDemandHolderIdiom;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddPostActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG ="AddPostActivity";
    Intent got_intent;
    Toolbar toolbar_exit;
    PingShownItem pingShownItem;
    CoordinatorLayout add_post_ping_cl, add_post_complete_cl;
    AppCompatEditText add_post_ping_et, add_post_text_et;

    InitializationOnDemandHolderIdiom singleton = InitializationOnDemandHolderIdiom.getInstance();

    APIInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        toolbar_exit = findViewById(R.id.tb_tb_exit);
        add_post_ping_cl = findViewById(R.id.add_post_ping_cl);
        add_post_ping_et = findViewById(R.id.add_post_ping_et);
        add_post_text_et = findViewById(R.id.add_post_text_et);
        add_post_complete_cl = findViewById(R.id.add_post_complete_cl);

        add_post_complete_cl.setOnClickListener(this);

        apiInterface = getApiInterface();

        setSupportActionBar(toolbar_exit);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        got_intent = getIntent();

        String gotPingID = got_intent.getStringExtra(ConstantStrings.INTENT_PING_SHOWN_ITEM_ID);


        if (gotPingID.equals(ConstantStrings.INTENT_NO_PING)){
            pingShownItem = null;
            add_post_ping_cl.setVisibility(View.GONE);

            Toast.makeText(this, "null ping shown item", Toast.LENGTH_SHORT).show();
        } else {

            List<PingItem> pingConstant = ConstantAnimations.pingList;
            // Constant 리스트에서 정보를 파악함.
            for (PingItem pingItem : pingConstant) {
                if (pingItem.getPingID().equals(gotPingID)) {
                    pingShownItem = new PingShownItem(gotPingID);
                }
            }
            add_post_ping_cl.setVisibility(View.VISIBLE);
            add_post_ping_et.setHint(pingShownItem.getPingText());

            Toast.makeText(this, pingShownItem.getPingText(), Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.stay, R.anim.slide_down);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.add_post_complete_cl:
                // assume no ping and post
                RequestBody request_ping_id = null;
                RequestBody request_ping_text = null;
                RequestBody request_post_text = null;

                if(pingShownItem != null){
                    request_ping_id = RequestBody.create(MediaType.parse("multipart/form-data"), pingShownItem.getPingID());

                    String ping_text = Objects.requireNonNull(add_post_ping_et.getText()).toString().trim();
                    if (!ping_text.equals(pingShownItem.getPingText())){
                        // ping text changed
                        request_ping_text = RequestBody.create(MediaType.parse("multipart/form-data"), ping_text);
                    }
                }

                String post_text = add_post_text_et.getText().toString().trim();

/*                if (post_text != null) {
                    Log.d(TAG, "ping_text: " + request_ping_text.toString());
                }*/
                if(!post_text.equals("")){
                    request_post_text = RequestBody.create(MediaType.parse("multipart/form-data"), post_text);
                }

                if(request_ping_id == null && request_post_text == null){
                    // 아무것도 없음.
                    Toast.makeText(this, "nothing so cant add", Toast.LENGTH_SHORT).show();
                    return;
                }

                addPost(request_ping_id, request_ping_text, request_post_text);

                if (request_ping_id != null) {
                    Log.d(TAG, "ping_id: " + request_ping_id.toString());
                }
                if (request_ping_text != null) {
                    Log.d(TAG, "ping_text: " + request_ping_text.toString());
                }
                if (request_post_text != null) {
                    Log.d(TAG, "post_text: "+ request_post_text.toString());
                }


                Intent result_intent = new Intent();
                result_intent.putExtra(ConstantStrings.INTENT_ADD_POST_INFO, ConstantIntegers.RESULT_SUCCESS);
                setResult(RESULT_OK, result_intent);


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    finishAfterTransition();
                } else finish();








                break;
            default:
                break;
        }

    }

    public void addPost(RequestBody requestPingID, RequestBody requestPingText, RequestBody requestPostText){

        Call<JsonObject> call = apiInterface.addPost(requestPingID, requestPingText, requestPostText);
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

/*
                        FeedItem feedItem = new FeedItem(contentObject);
                        Log.d(TAG, "called_feeditem_good: "+feedItem.getPostID());

                        boolean isExist = false;
                        for(FeedItem item : singleton.followFeedList){
                            if(feedItem.getPostID().equals(item.getPostID())){
                                isExist = true;
                            }
                        }
                        if(!isExist){
                            singleton.followFeedList.add(0, feedItem);
                            singleton.homeFollowAdapter.notifyDataSetChanged();

                            Log.d(TAG, "called_feeditem_good");
                        }
*/





                        // homeFollowFragment 띄우면서 맨 위에 리스트에 넣고 어댑터에 노티파이 해줘야겠다.

                        // 접속 성공.
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
