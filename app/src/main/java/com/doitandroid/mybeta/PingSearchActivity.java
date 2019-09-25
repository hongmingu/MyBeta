package com.doitandroid.mybeta;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;

import android.content.Context;
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

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PingSearchActivity extends AppCompatActivity {

    final static private String TAG = "PingSearchAtyTAG";
    APIInterface apiInterface;
    ArrayList<PingShownItem> allPingShownItemArrayList;
    ArrayList<LinearLayoutCompat> linearLayoutCompatArrayList;

    LinearLayoutCompat ping_search_ll;
    Context context;

    AppCompatTextView ping_search_preview_tv;
    PingShownItem currentPingShownItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ping_search);
        init_display();
        apiInterface = getApiInterface();
        context = this;
        currentPingShownItem = null;

        ping_search_ll = findViewById(R.id.ping_search_ll);
        ping_search_preview_tv = findViewById(R.id.ping_search_preview_tv);

        allPingShownItemArrayList = new ArrayList<>();

        refreshSearchContentPings();
    }
    private void refreshSearchContentPings(){
        Call<JsonObject> call = apiInterface.refresh_search_content_pings();
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

                        for (JsonElement jsonElement: contentArray){
                            JsonObject item = jsonElement.getAsJsonObject();
                            String optName = item.get("opt").toString();

                            LinearLayoutCompat linearLayoutCompat = new LinearLayoutCompat(context);
                            LinearLayoutCompat.LayoutParams params = new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            linearLayoutCompat.setOrientation(LinearLayoutCompat.VERTICAL);
                            linearLayoutCompat.setLayoutParams(params);

                            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            View view = inflater.inflate(R.layout.opt_text_item, linearLayoutCompat, false);
                            AppCompatTextView opt_tv = view.findViewById(R.id.opt_text_item_tv);
                            opt_tv.setText(optName);

                            //opt tv 넣고
                            linearLayoutCompat.addView(view);

                            JsonArray pingArray = item.getAsJsonArray("pings");

                            ArrayList<LinearLayoutCompat> linearLayoutCompatArrayList = new ArrayList<>();
                            int pingIndex = 0;

                            for (JsonElement pingJsonElement: pingArray){
                                String gotPingID = pingJsonElement.getAsString();
                                List<PingItem> pingConstant = ConstantAnimations.pingList;
                                // Constant 리스트에서 정보를 파악함.
                                pingIndex ++;
                                if ((pingIndex - 1)%5 == 0){
                                    LinearLayoutCompat pingLinearLayout = new LinearLayoutCompat(context);

                                    Log.d(TAG, pingIndex+" here");
                                    LinearLayoutCompat.LayoutParams pingLayoutParams = new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                    pingLinearLayout.setLayoutParams(pingLayoutParams);
                                    pingLinearLayout.setOrientation(LinearLayoutCompat.HORIZONTAL);
                                    pingLinearLayout.setGravity(Gravity.CENTER_HORIZONTAL);
                                    linearLayoutCompatArrayList.add(pingLinearLayout);
                                }
                                for (PingItem pingItem : pingConstant) {
                                    Log.d(TAG, pingIndex+"");

                                    if (pingItem.getPingID().equals(gotPingID)) {
                                        final PingShownItem pingShownItem = new PingShownItem(gotPingID);

                                        allPingShownItemArrayList.add(pingShownItem);
                                        Log.d(TAG, pingIndex+" and size: " + (linearLayoutCompatArrayList.size() - 1));

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
                                                ping_search_preview_tv.setText(pingShownItem.getPingText());
                                                currentPingShownItem = pingShownItem;
                                            }
                                        });

                                        gotLayout.addView(pingView);
                                        gotLayout.setOrientation(LinearLayoutCompat.HORIZONTAL);



                                    }
                                }
                            }
                            for (LinearLayoutCompat linearLayoutItem: linearLayoutCompatArrayList){
                                linearLayoutCompat.addView(linearLayoutItem);
                            }

                            ping_search_ll.addView(linearLayoutCompat);

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


    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    private void init_display() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        UtilsCollection utilsCollection = new UtilsCollection(this);
        utilsCollection.makeStatusBarColor(ConstantStrings.TRAY_COLOR);
    }

    private APIInterface getApiInterface(){
        SharedPreferences sp = getSharedPreferences(ConstantStrings.INIT_APP, MODE_PRIVATE);
        String auth_token = sp.getString(ConstantStrings.TOKEN, ConstantStrings.REMOVE_TOKEN);
        APIInterface apiInterface = LoggedInAPIClient.getClient(auth_token).create(APIInterface.class);
        return apiInterface;
    }


    public void removeAllClicked() {
        for (PingShownItem item: allPingShownItemArrayList){
            item.setIsClicked(false);
        }
    }
}
