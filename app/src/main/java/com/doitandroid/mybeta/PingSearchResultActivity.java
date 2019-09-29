package com.doitandroid.mybeta;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Space;

import com.doitandroid.mybeta.ping.PingShownItem;
import com.doitandroid.mybeta.rest.APIInterface;
import com.doitandroid.mybeta.rest.LoggedInAPIClient;
import com.doitandroid.mybeta.utils.UtilsCollection;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PingSearchResultActivity extends AppCompatActivity {
    final static private String TAG = "PingSearchResultAty";

    Context context;
    Intent gotIntent;
    APIInterface apiInterface;
    ArrayList<PingShownItem> allPingShownItemArrayList;

    LinearLayoutCompat ping_search_result_ll;

    AppCompatTextView ping_search_result_preview_tv;
    AppCompatEditText ping_search_result_et;

    PingShownItem currentPingShownItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ping_search_result);
        init_display();

        apiInterface = getApiInterface();

        ping_search_result_et = findViewById(R.id.ping_search_result_et);
        ping_search_result_preview_tv = findViewById(R.id.ping_search_result_preview_tv);
        ping_search_result_ll = findViewById(R.id.ping_search_result_ll);


        context = this;
        currentPingShownItem = null;

        allPingShownItemArrayList = new ArrayList<>();

        gotIntent = getIntent();

        String gotPingSearchWord = gotIntent.getStringExtra(ConstantStrings.INTENT_PING_SEARCH_WORD);

        refreshPingSearchResult(gotPingSearchWord);
    }

    private void refreshPingSearchResult(String pingSearchWord) {

        RequestBody requestPingSearchWord = RequestBody.create(MediaType.parse("multipart/form-data"), pingSearchWord);

        Call<JsonObject> call = apiInterface.refresh_ping_search_result(requestPingSearchWord);
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
                        ArrayList<String> pingIDArrayList = new ArrayList<>();

                        for (JsonElement jsonElement : contentArray) {
                            String gotPingID = jsonElement.getAsString();
                            pingIDArrayList.add(gotPingID);
                        }

                        int pingIDArrayListSize = pingIDArrayList.size();
                        for (int i = 0; i < (5 - (pingIDArrayListSize % 5)) % 5; i++) {
                            pingIDArrayList.add("nonePing");
                            Log.d(TAG, "pingArray by: "+ (5 - (pingIDArrayList.size() % 5))% 5);
                        }


                        int pingIndex = 0;

                        List<PingItem> pingConstant = ConstantAnimations.pingList;
                        ArrayList<LinearLayoutCompat> linearLayoutCompatArrayList = new ArrayList<>();

                        for (String pingIDItem: pingIDArrayList) {
                            pingIndex++;

                            if ((pingIndex - 1) % 5 == 0) {
                                LinearLayoutCompat pingLinearLayout = new LinearLayoutCompat(context);

                                Log.d(TAG, pingIndex + " here");
                                LinearLayoutCompat.LayoutParams pingLayoutParams = new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                pingLinearLayout.setLayoutParams(pingLayoutParams);
                                pingLinearLayout.setOrientation(LinearLayoutCompat.HORIZONTAL);
                                pingLinearLayout.setGravity(Gravity.CENTER_HORIZONTAL);
                                linearLayoutCompatArrayList.add(pingLinearLayout);
                            }

                            if (pingIDItem.equals("nonePing")){
                                LinearLayoutCompat gotLayout = linearLayoutCompatArrayList.get(linearLayoutCompatArrayList.size() - 1);
                                Space fakePing = new Space(getApplicationContext());
                                fakePing.setLayoutParams(new LinearLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.home_ping_width), getResources().getDimensionPixelSize(R.dimen.home_ping_width)));
                                gotLayout.addView(fakePing);
                                if (pingIndex % 5 != 0) {
                                    Space space = new Space(getApplicationContext());
                                    space.setLayoutParams(new LinearLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.home_ping_space), ViewGroup.LayoutParams.MATCH_PARENT));
                                    gotLayout.addView(space);
                                }
                            } else {
                                for (PingItem pingItem : pingConstant) {
                                    Log.d(TAG, pingIndex + "");


                                    if (pingItem.getPingID().equals(pingIDItem)) {
                                        final PingShownItem pingShownItem = new PingShownItem(pingIDItem);

                                        allPingShownItemArrayList.add(pingShownItem);
                                        Log.d(TAG, pingIndex + " and size: " + (linearLayoutCompatArrayList.size() - 1));

                                        LinearLayoutCompat gotLayout = linearLayoutCompatArrayList.get(linearLayoutCompatArrayList.size() - 1);

                                        LayoutInflater pingInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                        View pingView = pingInflater.inflate(R.layout.ping_recyclerview_item, gotLayout, false);

                                        pingShownItem.setView(pingView);
                                        pingShownItem.getLottieAnimationView().setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                removeAllClicked();
                                                pingShownItem.setIsClicked(true);
                                                pingShownItem.playAnimation();
                                                ping_search_result_preview_tv.setText(pingShownItem.getPingText());
                                                currentPingShownItem = pingShownItem;
                                            }
                                        });

                                        gotLayout.addView(pingView);

                                        if (pingIndex % 5 != 0) {
                                            Space space = new Space(getApplicationContext());
                                            space.setLayoutParams(new LinearLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.home_ping_space), ViewGroup.LayoutParams.MATCH_PARENT));
                                            gotLayout.addView(space);
                                        }


                                    }
                                }
                            }

                        }


                        for (LinearLayoutCompat linearLayoutItem : linearLayoutCompatArrayList) {
                            ping_search_result_ll.addView(linearLayoutItem);
                            Space space = new Space(getApplicationContext());
                            space.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.default12)));
                            ping_search_result_ll.addView(space);
                        }

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
    private void init_display() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        UtilsCollection utilsCollection = new UtilsCollection(this);
        utilsCollection.makeStatusBarColor(ConstantStrings.TRAY_COLOR);
    }

    private APIInterface getApiInterface() {
        SharedPreferences sp = getSharedPreferences(ConstantStrings.INIT_APP, MODE_PRIVATE);
        String auth_token = sp.getString(ConstantStrings.TOKEN, ConstantStrings.REMOVE_TOKEN);
        APIInterface apiInterface = LoggedInAPIClient.getClient(auth_token).create(APIInterface.class);
        return apiInterface;
    }

    public void removeAllClicked() {
        for (PingShownItem item : allPingShownItemArrayList) {
            item.setIsClicked(false);
        }
    }
}
