package com.doitandroid.mybeta.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.doitandroid.mybeta.CommentActivity;
import com.doitandroid.mybeta.ConstantAnimations;
import com.doitandroid.mybeta.ConstantIntegers;
import com.doitandroid.mybeta.ConstantStrings;
import com.doitandroid.mybeta.ContentActivity;
import com.doitandroid.mybeta.MainActivity;
import com.doitandroid.mybeta.PingItem;
import com.doitandroid.mybeta.R;
import com.doitandroid.mybeta.itemclass.FeedItem;
import com.doitandroid.mybeta.itemclass.UserItem;
import com.doitandroid.mybeta.rest.APIInterface;
import com.doitandroid.mybeta.rest.ConstantREST;
import com.doitandroid.mybeta.rest.LoggedInAPIClient;
import com.doitandroid.mybeta.utils.InitializationOnDemandHolderIdiom;
import com.google.gson.JsonObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFollowAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "HomeFollowAdapterTAG";
    ArrayList<FeedItem> feedItemArrayList;

    Context context;

    APIInterface apiInterface;

    InitializationOnDemandHolderIdiom singleton = InitializationOnDemandHolderIdiom.getInstance();

    public HomeFollowAdapter(ArrayList<FeedItem> feedItemArrayList, Context context) {
        this.feedItemArrayList = feedItemArrayList;
        this.context = context;

        apiInterface = singleton.apiInterface;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RecyclerView.ViewHolder viewHolder = null;
        View view = null;
        switch (viewType){
            case ConstantIntegers.OPT_DEFAULT_PING:
                view = inflater.inflate(R.layout.item_default_ping, parent, false);
                viewHolder = new HomeFollowDefaultPingViewHolder(view);
                break;
            case ConstantIntegers.OPT_TO_CLICK:
                view = inflater.inflate(R.layout.home_following_recyclerview_item, parent, false);
                viewHolder = new HomeFollowDefaultPingViewHolder(view);

                break;
            default:
                break;
        }
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)){
            case ConstantIntegers.OPT_DEFAULT_PING:
                final FeedItem feeditem = feedItemArrayList.get(position);
                final UserItem userItem = singleton.getUserItemFromSingletonByUserID(feeditem.getUser().getUserID());
                ((HomeFollowDefaultPingViewHolder) holder).dpi_full_name_tv.setText(userItem.getFullName());
                ((HomeFollowDefaultPingViewHolder) holder).dpi_full_name_tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, ContentActivity.class);
                        intent.putExtra(ConstantStrings.INTENT_CONTENT_START, ConstantStrings.INTENT_CONTENT_USER);

                        intent.putExtra("userID", userItem.getUserID());

                        ((MainActivity) context).startActivityForResult(intent, ConstantIntegers.REQUEST_CONTENT);
                        ((MainActivity) context).overridePendingTransition(0, 0); //

                    }
                });

                for (PingItem pingConstantItem: ConstantAnimations.pingList){
                    if (pingConstantItem.getPingID().equals(feeditem.getPingID())){
                        ((HomeFollowDefaultPingViewHolder) holder).dpi_ping_lav.setAnimation(feeditem.getPingRes());
                        ((HomeFollowDefaultPingViewHolder) holder).dpi_ping_lav.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        });

                        if (feeditem.getPingText() == null){
                            ((HomeFollowDefaultPingViewHolder) holder).dpi_ping_text_tv.setText(pingConstantItem.getPingText());
                            Log.d(TAG, "this is ping text" + pingConstantItem.getPingText());
                        } else {
                            ((HomeFollowDefaultPingViewHolder) holder).dpi_ping_text_tv.setText(feeditem.getPingText());
                            Log.d(TAG, "this is feed text" + feeditem.getPingText());

                        }
                    }
                }

                Glide.with(context)
                        //.load(feeditem.getUser().getUserPhoto())
                        .load((ConstantREST.URL_HOME).substring(0, ConstantREST.URL_HOME.length()-1) + userItem.getUserPhoto())

                        .into(((HomeFollowDefaultPingViewHolder) holder).dpi_user_photo_civ);

                Log.d(TAG, ConstantREST.URL_HOME + userItem.getUserPhoto());
                switch (getAdjustedTimeDifference(feeditem.getCreated())){
                    case ConstantIntegers.TIME_DEFAULT:
                        ((HomeFollowDefaultPingViewHolder) holder).dpi_time_indicator_iv.setBackground(context.getResources().getDrawable(R.drawable.ic_bluelogo));
                        break;
                    case ConstantIntegers.TIME_OVER_TWENTEY:
                        ((HomeFollowDefaultPingViewHolder) holder).dpi_time_indicator_iv.setBackground(context.getResources().getDrawable(R.drawable.bg_skyblue));
                        break;
                    case ConstantIntegers.TIME_OVER_TWENTEY_NINE:
                        ((HomeFollowDefaultPingViewHolder) holder).dpi_time_indicator_iv.setBackground(context.getResources().getDrawable(R.drawable.bg_green_radius4dp));
                        break;
                    default:
                        break;
                }

                if (feeditem.getPingID() == null){
                    ((HomeFollowDefaultPingViewHolder) holder).dpi_ping_wrapper_ll.setVisibility(View.GONE);
                } else {
                    ((HomeFollowDefaultPingViewHolder) holder).dpi_ping_wrapper_ll.setVisibility(View.VISIBLE);

                    ((HomeFollowDefaultPingViewHolder) holder).dpi_ping_text_tv.setText(feeditem.getPingText());
                    ((HomeFollowDefaultPingViewHolder) holder).dpi_ping_lav.setAnimation(feeditem.getPingRes());

                }

                ((HomeFollowDefaultPingViewHolder) holder).dpi_text_tv.setText(feeditem.getPostText());

                ((HomeFollowDefaultPingViewHolder) holder).dpi_comment_content_rl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((MainActivity) context).startCommentActivity(feeditem.getPostID(), false);
                    }
                });

                Log.d(TAG, "isReacted" + feeditem.getReacted());
                if (feeditem.getReacted()){
                    ((HomeFollowDefaultPingViewHolder) holder).dpi_react_btn_lav.setMinAndMaxProgress(0f, 1f);
                    ((HomeFollowDefaultPingViewHolder) holder).dpi_react_btn_lav.setProgress(1f);
                } else {
                    ((HomeFollowDefaultPingViewHolder) holder).dpi_react_btn_lav.setMinAndMaxProgress(0f, 1f);
                    ((HomeFollowDefaultPingViewHolder) holder).dpi_react_btn_lav.setProgress(0f);

                }


                ((HomeFollowDefaultPingViewHolder) holder).dpi_react_btn_lav.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        react(feeditem.getPostID(), ((HomeFollowDefaultPingViewHolder)holder).dpi_react_btn_lav, feeditem.getReacted(), feeditem);
                    }
                });

                ((HomeFollowDefaultPingViewHolder) holder).dpi_comment_chat_rl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((MainActivity) context).startCommentActivity(feeditem.getPostID(), true);
                    }
                });

                ((HomeFollowDefaultPingViewHolder) holder).dpi_react_content_rl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((MainActivity) context).startReactActivity(feeditem.getPostID());
                    }
                });



                break;
            case ConstantIntegers.OPT_TO_CLICK:
                break;
            default:
                break;
        }
    }
/*
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads);
        }else {
            for (Object payload : payloads) {
                if (payload instanceof String) {
                    String type = (String) payload;
                    if (TextUtils.equals(type, "color_red")) {
*//*                        LinearLayout layout = ((HomeFollowingViewHolder) holder).home_layout;
                        View view = layout.findViewWithTag("idis_"+holder.getAdapterPosition());
                        Context context = layout.getContext();
                        view.setBackgroundColor(context.getResources().getColor(R.color.red));*//*

                    }
                }
            }
        }
    }*/


    @Override
    public int getItemCount() {
        return feedItemArrayList.size();
    }

    private class HomeFollowToClickViewHolder extends RecyclerView.ViewHolder {
        LinearLayout home_layout;
        public HomeFollowToClickViewHolder (View view) {
            super(view);
            home_layout = view.findViewById(R.id.home_f_rv_i_ll);

        }
    }
    private class HomeFollowDefaultPingViewHolder extends RecyclerView.ViewHolder {
        LinearLayoutCompat dpi_ping_wrapper_ll;
        AppCompatTextView dpi_full_name_tv, dpi_ping_text_tv, dpi_text_tv;
        CircleImageView dpi_user_photo_civ, dpi_profile_photo_civ;
        AppCompatImageView dpi_time_indicator_iv;
        LottieAnimationView dpi_ping_lav, dpi_react_btn_lav;
        RelativeLayout dpi_react_content_rl, dpi_comment_content_rl, dpi_comment_chat_rl;

        public HomeFollowDefaultPingViewHolder(View view) {
            super(view);
            dpi_ping_wrapper_ll = view.findViewById(R.id.default_ping_item_ping_wrapper_ll);
            dpi_full_name_tv = view.findViewById(R.id.default_ping_item_full_name_tv);
            dpi_user_photo_civ = view.findViewById(R.id.default_ping_item_user_photo_civ);
            dpi_ping_lav = view.findViewById(R.id.default_ping_item_ping_lav);
            dpi_ping_text_tv = view.findViewById(R.id.default_ping_item_ping_text_tv);
            dpi_text_tv = view.findViewById(R.id.default_ping_item_text_tv);
            dpi_react_content_rl = view.findViewById(R.id.default_ping_item_react_content_rl);
            dpi_react_btn_lav = view.findViewById(R.id.default_ping_item_react_btn_lav);
            dpi_comment_content_rl = view.findViewById(R.id.default_ping_item_comment_content_rl);
            dpi_profile_photo_civ = view.findViewById(R.id.default_ping_item_profile_photo_civ);
            dpi_time_indicator_iv = view.findViewById(R.id.default_ping_item_time_indicator_iv);
            dpi_comment_chat_rl = view.findViewById(R.id.default_ping_item_comment_chat_rl);

        }
    }

    @Override
    public int getItemViewType(int position) {
//        return super.getItemViewType(position);
        return feedItemArrayList.get(position).getOpt();
    }

    @Override
    public long getItemId(int position) {
//        return super.getItemId(position);

        return position;
    }

    //todo: 자바에서 팔로우 요청시에 요청하는 시간을 넣어서 요청한다. 요청하는 시간을 넣어서 요청할 시에 장고에서 시간을 받은 후
    // 팔로우를 생성할 시에 그 요청시간을 넣는다. 만약 새로운 팔로우 정보가 왔는데 그것이 지난 요청시간과 비교하여 10초이내
    // 차이이며 지난 요청시간보다 이전의 요청일 경우 기각한다. 리액트도 마찬가지이다. 그리고 자바 석세스풀에선 따로 변경을 하지
    // 않는다. 후자의 요청만이 처리되었을 것이므로. 대신 콜이 실패했을 경우만 그 전 상태로 되돌린다.

    public static int getAdjustedTimeDifference(String dateString) {
        int SEC = 60;
        int MIN = 60;
        int HOUR = 24;
        int DAY = 30;
        int MONTH = 12;
        String slicedDate = dateString.substring(0, dateString.length()-1);

        java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date dateToUse = null;
        try {
            dateToUse = format.parse(slicedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        long curTime = System.currentTimeMillis();
        long regTime = dateToUse.getTime();
        long differTimeSeconds = (curTime - regTime) / 1000;

        int msg;

        if (differTimeSeconds > 60 * 29) {
            // sec
            msg = ConstantIntegers.TIME_OVER_TWENTEY_NINE;
        } else if (differTimeSeconds > 60 * 20) {
            // min
            msg = ConstantIntegers.TIME_OVER_TWENTEY;
        } else {
            msg = ConstantIntegers.TIME_DEFAULT;
        }

        return msg;
    }


    public void react(String postID, LottieAnimationView lottieAnimationView, boolean isReacted, final FeedItem feedItem){

        lottieAnimationView.pauseAnimation();
        lottieAnimationView.setMinAndMaxProgress(0f, 1f);
        Log.d(TAG, isReacted +"");
        if (!isReacted){
            lottieAnimationView.setSpeed(2.4f);
            lottieAnimationView.playAnimation();

        } else {
            lottieAnimationView.setSpeed(-2.4f);
            lottieAnimationView.playAnimation();

        }

//        lottieAnimationView.playAnimation();


        RequestBody requestPostID = RequestBody.create(MediaType.parse("multipart/form-data"), postID);
        RequestBody requestBoolean;
        if (isReacted){
            feedItem.setReacted(false);
            requestBoolean = RequestBody.create(MediaType.parse("multipart/form-data"), "false");
        } else {
            feedItem.setReacted(true);
            requestBoolean = RequestBody.create(MediaType.parse("multipart/form-data"), "true");

        }
        Call<JsonObject> call = apiInterface.reactBoolean(requestPostID, requestBoolean);
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
