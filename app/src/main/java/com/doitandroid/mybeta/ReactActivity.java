package com.doitandroid.mybeta;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.doitandroid.mybeta.adapter.CommentAdapter;
import com.doitandroid.mybeta.adapter.ReactAdapter;
import com.doitandroid.mybeta.itemclass.CommentItem;
import com.doitandroid.mybeta.itemclass.ReactItem;
import com.doitandroid.mybeta.rest.APIInterface;
import com.doitandroid.mybeta.rest.LoggedInAPIClient;
import com.doitandroid.mybeta.utils.InitializationOnDemandHolderIdiom;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReactActivity extends AppCompatActivity {

    private static final String TAG = "ReactActivity";

    Intent gotIntent;
    APIInterface apiInterface;

    ArrayList<ReactItem> reactItemArrayList;

    RecyclerView react_content_rv;
    ReactAdapter reactAdapter;

    InitializationOnDemandHolderIdiom singleton = InitializationOnDemandHolderIdiom.getInstance();

    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_react);
        gotIntent = getIntent();

        context = this;

        reactItemArrayList = new ArrayList<>();


        apiInterface = singleton.apiInterface;
        react_content_rv = findViewById(R.id.react_content_rv);



        getReact(gotIntent.getStringExtra(ConstantStrings.INTENT_POST_ID));
        //todo: 이제 commentActivity 에서 내용 가져오고 adapter 구성한다.
    }


    public void getReact(String postID){
        RequestBody requestPostID = RequestBody.create(MediaType.parse("multipart/form-data"), postID);
        Call<JsonObject> call = apiInterface.getReact(requestPostID);
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

                        for(JsonElement jsonElement: contentArray){
                            JsonObject item = jsonElement.getAsJsonObject();
                            ReactItem reactItem = new ReactItem(item);
                            reactItemArrayList.add(reactItem);

                        }

                        Log.d(TAG, "size: " + reactItemArrayList.size() + "");


                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                        // 어댑터를 연결시킨다.
                        reactAdapter = new ReactAdapter(reactItemArrayList, context);

                        // 리사이클러뷰에 연결한다.
                        react_content_rv.setLayoutManager(layoutManager);
                        react_content_rv.setAdapter(reactAdapter);

                        react_content_rv.setNestedScrollingEnabled(false);

                        reactAdapter.notifyDataSetChanged();


                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                call.cancel();

            }
        });

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);

    }
}
