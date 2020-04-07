package com.doitandroid.mybeta.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Space;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.doitandroid.mybeta.ConstantFloat;
import com.doitandroid.mybeta.ConstantPings;
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
import com.doitandroid.mybeta.utils.InitializationOnDemandHolderIdiom;
import com.google.gson.JsonObject;

import java.text.ParseException;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.CopyOnWriteArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeReceivedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "HomeReceivedAdapterTAG";
    CopyOnWriteArrayList<FeedItem> feedItemArrayList;

    Context context;

    APIInterface apiInterface;

    InitializationOnDemandHolderIdiom singleton = InitializationOnDemandHolderIdiom.getInstance();

    public HomeReceivedAdapter(CopyOnWriteArrayList<FeedItem> feedItemArrayList, Context context) {
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
                viewHolder = new HomeDefaultPingViewHolder(view);
                break;
            case ConstantIntegers.OPT_LIST:
                view = inflater.inflate(R.layout.item_following_user_list_rv, parent, false);
                viewHolder = new HomeFollowingUserListViewHolder(view);

                break;
            case ConstantIntegers.OPT_LOADING:
                view = inflater.inflate(R.layout.item_loading, parent, false);
                viewHolder = new LoadingViewHolder(view);
                break;

            case ConstantIntegers.OPT_EMPTY:
                view = inflater.inflate(R.layout.item_empty, parent, false);
                viewHolder = new EmptyViewHolder(view);
                //todo: onbind뷰홀더 따지기
                break;
            default:
                break;
        }
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        final FeedItem feeditem = feedItemArrayList.get(position);

        switch (getItemViewType(position)){
            case ConstantIntegers.OPT_DEFAULT_PING:
                final UserItem userItem = singleton.getUserItemFromSingletonByUserID(feeditem.getUser().getUserID());
                ((HomeDefaultPingViewHolder) holder).dpi_full_name_tv.setText(userItem.getFullName());
                ((HomeDefaultPingViewHolder) holder).dpi_full_name_tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, ContentActivity.class);
                        intent.putExtra(ConstantStrings.INTENT_CONTENT_START, ConstantStrings.INTENT_CONTENT_USER);

                        intent.putExtra("userID", userItem.getUserID());

                        ((MainActivity) context).startActivityForResult(intent, ConstantIntegers.REQUEST_CONTENT);
                        ((MainActivity) context).overridePendingTransition(0, 0); //

                    }
                });

                for (PingItem pingConstantItem: ConstantPings.defaultPingList){
                    if (pingConstantItem.getPingID().equals(feeditem.getPingID())){
                        ((HomeDefaultPingViewHolder) holder).dpi_ping_lav.setAnimation(feeditem.getPingRes());
                        ((HomeDefaultPingViewHolder) holder).dpi_ping_lav.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ((HomeDefaultPingViewHolder) holder).dpi_ping_lav.setMinAndMaxProgress(0f, 1f);
                                ((HomeDefaultPingViewHolder) holder).dpi_ping_lav.setSpeed(2.4f);
                                ((HomeDefaultPingViewHolder) holder).dpi_ping_lav.playAnimation();
                            }
                        });

                        if (feeditem.getPingText() == null){
                            ((HomeDefaultPingViewHolder) holder).dpi_ping_text_tv.setText(pingConstantItem.getPingText());
                            Log.d(TAG, "this is ping text" + pingConstantItem.getPingText());
                        } else {
                            ((HomeDefaultPingViewHolder) holder).dpi_ping_text_tv.setText(feeditem.getPingText());
                            Log.d(TAG, "this is feed text" + feeditem.getPingText());

                        }
                    }
                }

                Glide.with(context)
                        //.load(feeditem.getUser().getUserPhoto())
                        .load((ConstantREST.URL_HOME).substring(0, ConstantREST.URL_HOME.length()-1) + userItem.getUserPhoto())

                        .into(((HomeDefaultPingViewHolder) holder).dpi_user_photo_civ);
                ((HomeDefaultPingViewHolder) holder).dpi_user_photo_civ.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, ContentActivity.class);
                        intent.putExtra(ConstantStrings.INTENT_CONTENT_START, ConstantStrings.INTENT_CONTENT_USER);

                        intent.putExtra("userID", userItem.getUserID());

                        ((MainActivity) context).startActivityForResult(intent, ConstantIntegers.REQUEST_CONTENT);
                        ((MainActivity) context).overridePendingTransition(0, 0); //

                    }
                });
                Log.d(TAG, ConstantREST.URL_HOME + userItem.getUserPhoto());
                switch (getAdjustedTimeDifference(feeditem.getCreated())){
                    case ConstantIntegers.TIME_DEFAULT:
                        ((HomeDefaultPingViewHolder) holder).dpi_time_indicator_iv.setBackground(context.getResources().getDrawable(R.drawable.ic_bluelogo));
                        break;
                    case ConstantIntegers.TIME_OVER_TWENTEY:
                        ((HomeDefaultPingViewHolder) holder).dpi_time_indicator_iv.setBackground(context.getResources().getDrawable(R.drawable.bg_skyblue));
                        break;
                    case ConstantIntegers.TIME_OVER_TWENTEY_NINE:
                        ((HomeDefaultPingViewHolder) holder).dpi_time_indicator_iv.setBackground(context.getResources().getDrawable(R.drawable.bg_green_radius4dp));
                        break;
                    default:
                        break;
                }

                if (feeditem.getPingID() == null){
                    ((HomeDefaultPingViewHolder) holder).dpi_ping_wrapper_ll.setVisibility(View.GONE);
                } else {
                    ((HomeDefaultPingViewHolder) holder).dpi_ping_wrapper_ll.setVisibility(View.VISIBLE);

                    ((HomeDefaultPingViewHolder) holder).dpi_ping_text_tv.setText(feeditem.getPingText());
                    ((HomeDefaultPingViewHolder) holder).dpi_ping_lav.setAnimation(feeditem.getPingRes());

                }

                ((HomeDefaultPingViewHolder) holder).dpi_text_tv.setText(feeditem.getPostText());

                ((HomeDefaultPingViewHolder) holder).dpi_comment_content_rl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((MainActivity) context).startCommentActivity(feeditem.getPostID(), false);
                    }
                });

                Log.d(TAG, "isReacted" + feeditem.getReacted());
                if (feeditem.getReacted()){
                    ((HomeDefaultPingViewHolder) holder).dpi_react_btn_lav.setMinAndMaxProgress(0f, 1f);
                    ((HomeDefaultPingViewHolder) holder).dpi_react_btn_lav.setProgress(1f);
                } else {
                    ((HomeDefaultPingViewHolder) holder).dpi_react_btn_lav.setMinAndMaxProgress(0f, 1f);
                    ((HomeDefaultPingViewHolder) holder).dpi_react_btn_lav.setProgress(0f);

                }


                ((HomeDefaultPingViewHolder) holder).dpi_react_btn_lav.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        react(feeditem.getPostID(), ((HomeDefaultPingViewHolder)holder).dpi_react_btn_lav, feeditem.getReacted(), feeditem);
                    }
                });

                ((HomeDefaultPingViewHolder) holder).dpi_comment_chat_rl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((MainActivity) context).startCommentActivity(feeditem.getPostID(), true);
                    }
                });

                ((HomeDefaultPingViewHolder) holder).dpi_react_content_rl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((MainActivity) context).startReactActivity(feeditem.getPostID());
                    }
                });



                break;
            case ConstantIntegers.OPT_LIST:

                //todo: 이제 여기에 리사이클러뷰 내 만들어주는 바인드.
                if (feeditem.getRecentUserList().size() != 0) {
                    ((HomeFollowingUserListViewHolder) holder).ful_tv.setVisibility(View.VISIBLE);
                    ((HomeFollowingUserListViewHolder) holder).ful_rv.setVisibility(View.VISIBLE);
                    ((HomeFollowingUserListViewHolder) holder).ful_ll.setVisibility(View.VISIBLE);

                    ((HomeFollowingUserListViewHolder) holder).ful_tv.setText(feeditem.getRecentDate());
                    RecyclerView recyclerView = ((HomeFollowingUserListViewHolder) holder).ful_rv;

                    RecyclerView.LayoutManager layoutManager = new GridLayoutManager(context, ConstantIntegers.GRID_SPAN);
                    // 어댑터를 연결시킨다.
                    HomeFollowingUserListAdapter homeFollowingUserListAdapter = new HomeFollowingUserListAdapter(feeditem.getRecentUserList(), context);

                    // 리사이클러뷰에 연결한다.
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(homeFollowingUserListAdapter);

                    recyclerView.setNestedScrollingEnabled(false);
                } else {
                    ((HomeFollowingUserListViewHolder) holder).ful_tv.setVisibility(View.GONE);
                    ((HomeFollowingUserListViewHolder) holder).ful_rv.setVisibility(View.GONE);
                    ((HomeFollowingUserListViewHolder) holder).ful_ll.setVisibility(View.GONE);
                    ((HomeFollowingUserListViewHolder) holder).ful_space.setVisibility(View.GONE);

                }

                if (position == feedItemArrayList.size() - 1) {
                    ((HomeFollowingUserListViewHolder) holder).ful_space.setVisibility(View.VISIBLE);
                }
                break;
            case ConstantIntegers.OPT_LOADING:

                break;
            case ConstantIntegers.OPT_EMPTY:

                break;
            default:
                Toast.makeText(context, "Last Two", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Last Feed Two");

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
        return feedItemArrayList == null ? 0 : feedItemArrayList.size();
    }

    private class HomeDefaultToClickViewHolder extends RecyclerView.ViewHolder {
        LinearLayout home_layout;
        public HomeDefaultToClickViewHolder(View view) {
            super(view);
            home_layout = view.findViewById(R.id.home_f_rv_i_ll);

        }
    }
    private class HomeDefaultPingViewHolder extends RecyclerView.ViewHolder {
        LinearLayoutCompat dpi_ping_wrapper_ll;
        AppCompatTextView dpi_full_name_tv, dpi_ping_text_tv, dpi_text_tv;
        CircleImageView dpi_user_photo_civ, dpi_profile_photo_civ;
        AppCompatImageView dpi_time_indicator_iv;
        LottieAnimationView dpi_ping_lav, dpi_react_btn_lav;
        RelativeLayout dpi_react_content_rl, dpi_comment_content_rl, dpi_comment_chat_rl;

        public HomeDefaultPingViewHolder(View view) {
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
    private class HomeFollowingUserListViewHolder extends RecyclerView.ViewHolder {
        LinearLayoutCompat ful_ll;

        RecyclerView ful_rv;
        AppCompatTextView ful_tv;
        Space ful_space;

        public HomeFollowingUserListViewHolder(View view) {
            super(view);
            ful_ll = view.findViewById(R.id.item_following_users_ll);

            ful_rv = view.findViewById(R.id.item_following_users_rv);
            ful_tv = view.findViewById(R.id.item_following_users_tv);
            ful_space = view.findViewById(R.id.item_following_users_space);

        }
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        ProgressBar loading_pb;
        public LoadingViewHolder(View view) {
            super(view);
            loading_pb = view.findViewById(R.id.item_loading_pb);

        }
    }

    private class EmptyViewHolder extends RecyclerView.ViewHolder {

        AppCompatTextView empty_tv;
        public EmptyViewHolder(View view) {
            super(view);
            empty_tv = view.findViewById(R.id.item_empty_tv);

        }
    }
    @Override
    public int getItemViewType(int position) {
//        return super.getItemViewType(position);
        return feedItemArrayList.get(position) == null ? ConstantIntegers.OPT_LOADING : feedItemArrayList.get(position).getOpt();
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
            lottieAnimationView.setSpeed(ConstantFloat.REACT);
            lottieAnimationView.playAnimation();

        } else {
            lottieAnimationView.setSpeed(ConstantFloat.REACT_REVERSE);
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
