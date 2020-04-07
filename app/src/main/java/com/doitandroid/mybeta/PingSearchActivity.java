package com.doitandroid.mybeta;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialog;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import com.doitandroid.mybeta.Cropper.CropImage;
import com.doitandroid.mybeta.fragment.UserFragment;
import com.doitandroid.mybeta.itemclass.FeedItem;
import com.doitandroid.mybeta.ping.PingShownItem;
import com.doitandroid.mybeta.rest.APIInterface;
import com.doitandroid.mybeta.rest.LoggedInAPIClient;
import com.doitandroid.mybeta.utils.InitializationOnDemandHolderIdiom;
import com.doitandroid.mybeta.utils.UtilsCollection;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
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

    InitializationOnDemandHolderIdiom singleton = InitializationOnDemandHolderIdiom.getInstance();

    Boolean currentPingIsPressed;
    ProgressBar search_pb;
    AppCompatDialog progressDialog;
    ProgressThread progressThread;
    Activity activity = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ping_search);
        init_display();
        apiInterface = getApiInterface();
        context = this;
        currentPingShownItem = null;
        currentPingIsPressed = false;

        ping_search_ll = findViewById(R.id.ping_search_ll);
        ping_search_preview_tv = findViewById(R.id.ping_search_preview_tv);
        search_pb = findViewById(R.id.ping_search_pb);
        findViewById(R.id.ping_search_instant_send_btn).setOnClickListener(this);
        findViewById(R.id.ping_search_addpost).setOnClickListener(this);

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
                            //todo: 이제 addpost 눌러서 보낼시에 제대로 작동하게
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
                                List<PingItem> pingConstant = ConstantPings.defaultPingList;
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
                                            pingShownItem.getLottieAnimationView().setOnTouchListener(new View.OnTouchListener() {
                                                @Override
                                                public boolean onTouch(View v, MotionEvent event) {
                                                    if (event.getAction() == event.ACTION_DOWN) {
                                                        // 처음 누를 때
                                                        Log.d(TAG, "ACTION_DOWN");
                                                        currentPingIsPressed = true;

                                                        progressThread = new ProgressThread();
                                                        if (pingShownItem.getIsClicked()) {
                                                            progressThread.start();
                                                        }
                                                    } else if (event.getAction() == event.ACTION_MOVE) {
                                                        // 움직일 떄
                                                    } else if (event.getAction() == event.ACTION_UP) {
                                                        // 떨어질 때
                                                        currentPingIsPressed = false;

                                                        if (progressThread != null) {
                                                            progressThread.stopThread();

                                                        }
                                                        progressThread = null;
                                                    }

                                                    return false;
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
    public void finishWithClosing(){
        Intent result_intent = new Intent();
        result_intent.putExtra(ConstantStrings.INTENT_CLOSE_DIM_WRAPPER, ConstantIntegers.RESULT_SUCCESS);
        setResult(RESULT_OK, result_intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case ConstantIntegers.REQUEST_SEARCH_RESULT:
                    if (data.getIntExtra(ConstantStrings.INTENT_CLOSE_DIM_WRAPPER, ConstantIntegers.RESULT_CANCELED)
                            == ConstantIntegers.RESULT_SUCCESS) {
                        finishWithClosing();
                        //close dim wrapper
                    } else {
                        //not changed
                    }

                case ConstantIntegers.REQUEST_ADD_POST:
                    if (data.getIntExtra(ConstantStrings.INTENT_ADD_POST_INFO, ConstantIntegers.RESULT_CANCELED) == ConstantIntegers.RESULT_SUCCESS) {
                        // 글이 올라감.
                        // singleton.homeFollowAdapter.notifyDataSetChanged();
                        finishWithClosing();
                    }
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
            case (R.id.ping_search_instant_send_btn):
                sendInstantPing(currentPingShownItem.getPingID());

                break;
            case R.id.ping_search_addpost:
                Intent intent_add_post = new Intent(this, AddPostActivity.class);

                intent_add_post.putExtra(ConstantStrings.INTENT_REQUEST_CODE, ConstantIntegers.REQUEST_ADD_POST);
                if (currentPingShownItem != null) {
                    intent_add_post.putExtra(ConstantStrings.INTENT_PING_SHOWN_ITEM_ID, currentPingShownItem.getPingID());
                } else {
                    intent_add_post.putExtra(ConstantStrings.INTENT_PING_SHOWN_ITEM_ID, ConstantStrings.INTENT_NO_PING);
                }

                startActivityForResult(intent_add_post, ConstantIntegers.REQUEST_ADD_POST);

                overridePendingTransition(R.anim.slide_up, R.anim.stay);
                break;
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



    class ProgressThread extends Thread {

        boolean stopped;
        int i = 0;

        public ProgressThread() {
            this.stopped = false;
        }

        public void stopThread() {
            Message message = progressHandler.obtainMessage();
//                 메시지 ID 설정
            message.what = ConstantIntegers.STOP_PROGRESS;
//                 메시지 내용 설정
//
            progressHandler.sendMessage(message);
            this.stopped = true;
        }

        @Override
        public void run() {
            super.run();

            while (stopped == false) {
                i++;
                if (i >= 1001) {
                    stopThread();
                    Message message = sendPingHandler.obtainMessage();
                    message.what = ConstantIntegers.SEND_INSTANT_PING;
                    sendPingHandler.sendMessage(message);
                    return;
                }
//                 메시지 얻어오기
                Message message = progressHandler.obtainMessage();
//                 메시지 ID 설정
                message.what = ConstantIntegers.SEND_PROGRESS;
//                 메시지 내용 설정
                message.arg1 = i;
//                 메시지 내용 설정
                String information = i + " x 100 밀리초 째 Thread 동작 중입니다.";
                message.obj = information;
//                 메시지 전달
                progressHandler.sendMessage(message);
                try {
//                 1초 씩 딜레이 부여 1초: 1000millis
                    sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


        }
    }

    public Handler progressHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ConstantIntegers.TOUCH_DOWN:
                    Toast.makeText(getApplicationContext(), "Touch down", Toast.LENGTH_SHORT).show();
                    //원하는 기능
                    break;
                case ConstantIntegers.SEND_PROGRESS:
                    search_pb.setProgress(msg.arg1);

                    break;
                case ConstantIntegers.STOP_PROGRESS:
                    Log.d(TAG, "STOP_PROGRESS");
                    search_pb.setProgress(0);
                    Log.d(TAG, "STOP_PROGRESS_COMPLETE");
                    break;

            }
        }
    };

    public Handler sendPingHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ConstantIntegers.SEND_INSTANT_PING:
                    sendInstantPing(currentPingShownItem.getPingID());
                    //원하는 기능
                    break;

            }
        }
    };

    public void sendInstantPing(String pingID) {
/*

        final MyDialog dialog = new MyDialog(this, "뒤로가기", "작업중인 내용이 있다", "뒤로갈래잉", "안갈래잉");
        dialog.setDialogListener(new MyDialogListener() {
            @Override
            public void onPositiveClicked() {
            }

            @Override
            public void onNegativeClicked() {

            }
        });
        dialog.show();

        dialog.dismiss();

*/

        progressON(activity, "sending...");

        RequestBody requestPingID = RequestBody.create(MediaType.parse("multipart/form-data"), pingID);

        Call<JsonObject> call = apiInterface.send_instant_ping(requestPingID);
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
                            progressOFF();

                            return;
                        }

                        JsonObject contentObject = jsonObject.get("content").getAsJsonObject();

                        FeedItem feedItem = new FeedItem(contentObject);
                        singleton.followFeedList.add(0, feedItem);
                        for(FeedItem item: singleton.followFeedList){
                            if (item.getOpt() == ConstantIntegers.OPT_EMPTY){
                                singleton.followFeedList.remove(item);
                            }
                        }
                        singleton.homeFollowAdapter.notifyDataSetChanged();

                        // 접속 성공.

                    }

                }
                progressOFF();
                finishWithClosing();

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                call.cancel();
                progressOFF();
                finish();


            }
        });


    }


    public void progressON(Activity activity, String message) {

        if (activity == null || activity.isFinishing()) {
            return;
        }


        if (progressDialog != null && progressDialog.isShowing()) {
            progressSET(message);
        } else {

            progressDialog = new AppCompatDialog(activity);
            progressDialog.setCancelable(false);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.progress_loading);
            progressDialog.show();

        }


        final ImageView img_loading_frame = (ImageView) progressDialog.findViewById(R.id.iv_frame_loading);
        final AnimationDrawable frameAnimation = (AnimationDrawable) img_loading_frame.getBackground();
        img_loading_frame.post(new Runnable() {
            @Override
            public void run() {
                frameAnimation.start();
            }
        });

        TextView tv_progress_message = (TextView) progressDialog.findViewById(R.id.tv_progress_message);
        if (!TextUtils.isEmpty(message)) {
            tv_progress_message.setText(message);
        }


    }

    public void progressSET(String message) {

        if (progressDialog == null || !progressDialog.isShowing()) {
            return;
        }


        TextView tv_progress_message = (TextView) progressDialog.findViewById(R.id.tv_progress_message);
        if (!TextUtils.isEmpty(message)) {
            tv_progress_message.setText(message);
        }

    }

    public void progressOFF() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

}
