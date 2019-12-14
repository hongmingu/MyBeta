package com.doitandroid.mybeta;

import androidx.annotation.Nullable;
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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.Toast;

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

public class PingSearchActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener {

    final static private String TAG = "PingSearchAtyTAG";
    APIInterface apiInterface;
    ArrayList<PingShownItem> allPingShownItemArrayList;
    ArrayList<LinearLayoutCompat> linearLayoutCompatArrayList;

    LinearLayoutCompat ping_search_ll;
    Context context;

    AppCompatTextView ping_search_preview_tv;
    AppCompatEditText ping_search_et;

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

        ping_search_et = findViewById(R.id.ping_search_et);
        ping_search_et.setOnKeyListener(this);

        refreshSearchContentPings();
    }

    private void refreshSearchContentPings() {
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

                        for (JsonElement jsonElement : contentArray) {
                            JsonObject item = jsonElement.getAsJsonObject();
                            String optName = item.get("opt").toString();

                            LinearLayoutCompat upLinearLayoutCompat = new LinearLayoutCompat(context);
                            LinearLayoutCompat.LayoutParams params = new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            upLinearLayoutCompat.setOrientation(LinearLayoutCompat.VERTICAL);
                            upLinearLayoutCompat.setLayoutParams(params);

                            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            View view = inflater.inflate(R.layout.opt_text_item, upLinearLayoutCompat, false);
                            AppCompatTextView opt_tv = view.findViewById(R.id.opt_text_item_tv);
                            opt_tv.setText(optName);

                            //opt tv 넣고
                            upLinearLayoutCompat.addView(view);

                            JsonArray pingArray = item.getAsJsonArray("pings");
                            int pingArraySize = pingArray.size();
                            for (int i = 0; i < (5 - (pingArraySize % 5)) % 5; i++) {
                                pingArray.add("nonePing");
                                Log.d(TAG, "pingArray by: "+ (5 - (pingArray.size() % 5))% 5);

                            }
                            Log.d(TAG, "pingArray: "+ pingArray.toString());

                            ArrayList<LinearLayoutCompat> linearLayoutCompatArrayList = new ArrayList<>();
                            int pingIndex = 0;

                            for (JsonElement pingJsonElement : pingArray) {
                                String gotPingID = pingJsonElement.getAsString();
                                List<PingItem> pingConstant = ConstantAnimations.pingList;
                                // Constant 리스트에서 정보를 파악함.
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

                                if (gotPingID.equals("nonePing")){
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


                                        if (pingItem.getPingID().equals(gotPingID)) {
                                            final PingShownItem pingShownItem = new PingShownItem(gotPingID);

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
                                                    ping_search_preview_tv.setText(pingShownItem.getPingText());
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
                                upLinearLayoutCompat.addView(linearLayoutItem);
                                Space space = new Space(getApplicationContext());
                                space.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.default12)));
                                upLinearLayoutCompat.addView(space);
                            }

                            ping_search_ll.addView(upLinearLayoutCompat);

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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);

        /*switch (requestCode) {

            case ConstantIntegers.REQUEST_SETTING_ACTIVITY:
                switch (resultCode){
                    case ConstantIntegers.RESULT_SUCCESS:
                        break;
                    default:
                        break;
                }

                if (data.getIntExtra(ConstantStrings.INTENT_LOGOUT_INFO, ConstantIntegers.RESULT_CANCELED) == ConstantIntegers.RESULT_LOGGED_OUT) {
                    //logout됨
                    logout();
                    Toast.makeText(this, "is logout", Toast.LENGTH_SHORT).show();

                } else {
                    //logout 안됨
                }


                break;
            default:
                break;
        }*/
        if (resultCode == RESULT_OK) {
            switch (requestCode) {

                case ConstantIntegers.REQUEST_SETTING_ACTIVITY:

                    break;
                default:
                    break;
            }
        }
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

    private APIInterface getApiInterface() {
        SharedPreferences sp = getSharedPreferences(ConstantStrings.SP_INIT_APP, MODE_PRIVATE);
        String auth_token = sp.getString(ConstantStrings.SP_ARG_TOKEN, ConstantStrings.SP_ARG_REMOVE_TOKEN);
        APIInterface apiInterface = LoggedInAPIClient.getClient(auth_token).create(APIInterface.class);
        return apiInterface;
    }


    public void removeAllClicked() {
        for (PingShownItem item : allPingShownItemArrayList) {
            item.setIsClicked(false);
        }
    }

    public void openSearchResultActivity(String search_word){
        String trimmed_search_word = search_word.trim();
        Intent search_result_intent = new Intent(this, PingSearchResultActivity.class);
        search_result_intent.putExtra(ConstantStrings.INTENT_PING_SEARCH_WORD, trimmed_search_word);
        startActivityForResult(search_result_intent, ConstantIntegers.REQUEST_SEARCH_RESULT);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            default:
                break;
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        switch (v.getId()){
            case R.id.ping_search_et:
                if (event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    switch (keyCode)
                    {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:

                            openSearchResultActivity(ping_search_et.getEditableText().toString());
                            Toast.makeText(getApplicationContext(), "enter", Toast.LENGTH_SHORT).show();
                            return true;
                        default:
                            break;
                    }
                }
                break;
            default:
                break;
        }

        return false;
    }


}
