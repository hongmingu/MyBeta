package com.doitandroid.mybeta.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.doitandroid.mybeta.ConstantIntegers;
import com.doitandroid.mybeta.ConstantPings;
import com.doitandroid.mybeta.MainActivity;
import com.doitandroid.mybeta.PingItem;
import com.doitandroid.mybeta.R;
import com.doitandroid.mybeta.itemclass.CommentItem;
import com.doitandroid.mybeta.rest.APIInterface;
import com.doitandroid.mybeta.utils.InitializationOnDemandHolderIdiom;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import com.doitandroid.mybeta.ConstantStrings;
import com.google.gson.JsonObject;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BetaFirebaseService extends FirebaseMessagingService {
    private static final String TAG = "BetaFirebaseServiceTAG";

    InitializationOnDemandHolderIdiom singleton = InitializationOnDemandHolderIdiom.getInstance();
    APIInterface apiInterface;

    // [START receive_message]
    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages
        // are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data
        // messages are the type
        // traditionally used with GCM. Notification messages are only received here in
        // onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated
        // notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages
        // containing both notification
        // and data payloads are treated as notification messages. The Firebase console always
        // sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            Log.d(TAG, "Message data payload: " + remoteMessage.getData().get("opt"));

            String opt = remoteMessage.getData().get("opt");
            // Also if you intend on generating your own notifications as a result of a received FCM
            // message, here is where that should be initiated. See sendNotification method below.
            //화면을 깨운다.(이방법은 Deprecated 되었다고 한다. 당장 작동은 되지만 나중에 어떻게 될지 모른다?)
            PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
            PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "myfcm:powermanager");
            wakeLock.acquire(1000);
            wakeLock.release();

            //String title = remoteMessage.getNotification().getTitle();
            //String body = remoteMessage.getNotification().getBody();
            Handler mHandler = new Handler(Looper.getMainLooper());
            switch (opt) {
                case ConstantStrings.FCM_OPT_NOTICE_FOLLOW:

                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // 사용하고자 하는 코드

                            sendNotification("SallyPing",
                                    remoteMessage.getData().get("full_name") + " started follow you",
                                    ConstantIntegers.NOTIFICATION_FOLLOW,
                                    remoteMessage.getData().get("full_name"));
                            Log.d(TAG, "FOLLOW");

                        }
                    }, 0);
                    break;
                case ConstantStrings.FCM_OPT_NOTICE_COMMENT:
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // 사용하고자 하는 코드
                            sendNotification(remoteMessage.getData().get("full_name") + " added comment", remoteMessage.getData().get("text"),
                                    ConstantIntegers.NOTIFICATION_POST_COMMENT,
                                    remoteMessage.getData().get("instance_id"));
                            Log.d(TAG, "COMMENT");

                        }
                    }, 0);

                    //todo: 왜 쌓이질 않을까. 쌓아야한다.
                    break;
                case ConstantStrings.FCM_OPT_NOTICE_REACT:

                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // 사용하고자 하는 코드
                            sendNotification("SallyPing",
                                    remoteMessage.getData().get("full_name") + " reacted to you",
                                    ConstantIntegers.NOTIFICATION_POST_REACT,
                                    remoteMessage.getData().get("instance_id"));

                        }
                    }, 0);
                    break;
                case ConstantStrings.FCM_OPT_POST:
                    String text = "some ping";

                    if (remoteMessage.getData().get("text").equals("need_ping_text")) {
                        for (PingItem pingConstantItem : ConstantPings.defaultPingList) {
                            if (pingConstantItem.getPingID().equals(remoteMessage.getData().get("ping_id"))) {
                                text = pingConstantItem.getPingText();
                            }
                        }
                    } else {
                        text = remoteMessage.getData().get("text");
                    }
                    final String finalText = text;
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // 사용하고자 하는 코드
                            sendNotification(remoteMessage.getData().get("full_name") + " added new ping",
                                    finalText,
                                    ConstantIntegers.NOTIFICATION_POST,
                                    remoteMessage.getData().get("instance_id"));

                        }
                    }, 0);
                    break;
                default:
                    break;

            }


            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use WorkManager.
                // scheduleJob();
            } else {
                // Handle message within 10 seconds
                // handleNow();
            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            // 노티피케이션 메시지가 없으면 작동하지 않는다. 장고에서 message_body 가 없으니 작동하지 않았다.
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    // [END receive_message]
    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token);
    }


    public void fcmPush(String token) {

        RequestBody requestToken = RequestBody.create(MediaType.parse("multipart/form-data"), token);
        if (apiInterface == null) {
            apiInterface = singleton.apiInterface;
        }

        Call<JsonObject> call = apiInterface.fcmPush(requestToken);
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

    public void sendRegistrationToServer(String token) {
        //todo: 여기 만들자.
        fcmPush(token);

        // 로그인 된 상태, 로그인 안 된 상태 분기/
    }

    private void sendNotification(String title, String messageBody, int notificationOption, String instanceID) {


        if (title == null) {
            //제목이 없는 payload이면
            title = "SallyPing"; //기본제목을 적어 주자.
        }
        //전달된 액티비티에 따라 분기하여 해당 액티비티를 오픈하도록 한다.
//        Intent intent;
//        if (click_action.equals("MainActivity")) {
//            intent = new Intent(this, MainActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        } else if(click_action.equals("NotiActivity")){//이런 액티비티 이름을 잘못 타이핑했네.ㅋ
//            intent = new Intent(this, NotiActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        }
//        else if (click_action.equals("NewsActivity")){
//            intent = new Intent(this, NewsActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        } else {
//            intent = new Intent(this, MainActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        }
//
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
//                PendingIntent.FLAG_ONE_SHOT);

        String channelID;
        String channelName;

        switch (notificationOption) {
            case (ConstantIntegers.NOTIFICATION_FOLLOW):
                channelID = instanceID;
                channelName = "sallyFollow";
                break;
            case (ConstantIntegers.NOTIFICATION_POST_COMMENT):
                channelID = instanceID;
                channelName = "sallyComment";
                break;
            case (ConstantIntegers.NOTIFICATION_POST_REACT):
                channelID = instanceID;
                channelName = "sallyReact";
                break;
            case (ConstantIntegers.NOTIFICATION_POST):
                channelID = instanceID;
                channelName = "sallyPost";
                break;

            default:
                channelID = instanceID;
                channelName = "sallyPing";
                break;
        }


        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder notificationBuilder;


        Intent notifyIntent = new Intent(this, MainActivity.class);
        notifyIntent.putExtra("notification", "fromNotification");

        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent notifyPendingIntent = PendingIntent.getActivity(this, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            int notificationImportance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(channelID, channelName, notificationImportance);

            // 중요도로 상단에 팝업이 어떻게 처리될지 결정한다고 한다.
            notificationManager.createNotificationChannel(notificationChannel);

            // 높은 버전
            // 채널은 처음 임포턴스만 설정가능하고 나머지는 언인스톨후 해야한다.
            notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), channelID);

            notificationBuilder
                    .setSmallIcon(R.drawable.ic_add_post)
                    .setContentTitle(title)
                    .setContentText(messageBody)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setVibrate(new long[]{1000, 1000})
                    .setLights(Color.BLUE, 1, 1)
                    .setShowWhen(true)

                    .setContentIntent(notifyPendingIntent);
            notificationManager.notify("do_not1", (int) (Math.random() * 10000000)/* ID of notification */, notificationBuilder.build());
        } else {

            // 낮은 버전
            notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), channelID);

            notificationBuilder
                    .setSmallIcon(R.drawable.ic_add_post)
                    .setContentTitle(title)
                    .setContentText(messageBody)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setVibrate(new long[]{1000, 1000})
                    .setLights(Color.BLUE, 1, 1)
                    .setPriority(NotificationManager.IMPORTANCE_HIGH)
                    .setContentIntent(notifyPendingIntent);


            notificationManager.notify("do_not1", (int) (Math.random() * 10000000)/* ID of notification */, notificationBuilder.build());

        }


//        notificationManager.notify("do_not1",0 /* ID of notification */, notificationBuilder.build());
    }

}

/*
glide가 로드가 안되면 largeicon에도 안먹히는 애로가 있다.



        String url = "https://i.pinimg.com/originals/9f/12/f6/9f12f63a998677db51f8e0c63198fbd4.jpg";

        final Bitmap[] bitmap = {null};
        Glide.with(this)
                .asBitmap()
                .load(url)
                .error(R.drawable.ic_add_post)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        bitmap[0] = resource;
                    }
                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) { }
                });

        if (bitmap[0] != null){

            notificationBuilder
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(title)
                    .setContentText(messageBody)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setVibrate(new long[]{1000, 1000})
                    .setLights(Color.BLUE, 1,1)
                    .setLargeIcon(bitmap[0])
                    .setContentIntent(notifyPendingIntent);
            notificationManager.notify("do_not1",0 *//* ID of notification *//*, notificationBuilder.build());

        }*/