package com.doitandroid.mybeta.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.doitandroid.mybeta.ConstantAnimations;
import com.doitandroid.mybeta.ConstantIntegers;
import com.doitandroid.mybeta.ConstantStrings;
import com.doitandroid.mybeta.ContentActivity;
import com.doitandroid.mybeta.MainActivity;
import com.doitandroid.mybeta.PingItem;
import com.doitandroid.mybeta.R;
import com.doitandroid.mybeta.itemclass.FeedItem;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFollowAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "HomeFollowAdapterTAG";
    ArrayList<FeedItem> feedItemArrayList;
    Context context;

    public HomeFollowAdapter(ArrayList<FeedItem> feedItemArrayList, Context context) {
        this.feedItemArrayList = feedItemArrayList;
        this.context = context;
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
                view = inflater.inflate(R.layout.default_ping_item, parent, false);
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
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)){
            case ConstantIntegers.OPT_DEFAULT_PING:
                final FeedItem feeditem = feedItemArrayList.get(position);
                ((HomeFollowDefaultPingViewHolder) holder).dpi_full_name_tv.setText(feeditem.getFullName());
                ((HomeFollowDefaultPingViewHolder) holder).dpi_full_name_tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, ContentActivity.class);
                        intent.putExtra(ConstantStrings.INTENT_CONTENT_START, ConstantStrings.INTENT_CONTENT_USER);
                        intent.putExtra(ConstantStrings.INTENT_USER_ID, feeditem.getUserID());
                        ((MainActivity) context).startActivityForResult(intent, ConstantIntegers.REQUEST_CONTENT);
                    }
                });

                for (PingItem pingConstantItem: ConstantAnimations.pingList){
                    if (pingConstantItem.getPingID().equals(feeditem.getPingID())){
                        ((HomeFollowDefaultPingViewHolder) holder).dpi_ping_lav.setAnimation(feeditem.getPingID());
                        ((HomeFollowDefaultPingViewHolder) holder).dpi_ping_lav.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        });
                    }
                }

                Glide.with(context)
                        .load(feeditem.getUserPhoto())
                        .into(((HomeFollowDefaultPingViewHolder) holder).dpi_user_photo_civ);

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

                ((HomeFollowDefaultPingViewHolder) holder).dpi_ping_text_tv.setText(feeditem.getPingText());
                ((HomeFollowDefaultPingViewHolder) holder).dpi_ping_lav.setAnimation(feeditem.getPingRes());

                ((HomeFollowDefaultPingViewHolder) holder).dpi_text_tv.setText(feeditem.getPostText());
                Log.d(TAG, "dpi sets");

                break;
            case ConstantIntegers.OPT_TO_CLICK:
                break;
            default:
                break;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads);
        }else {
            for (Object payload : payloads) {
                if (payload instanceof String) {
                    String type = (String) payload;
                    if (TextUtils.equals(type, "color_red")) {
/*                        LinearLayout layout = ((HomeFollowingViewHolder) holder).home_layout;
                        View view = layout.findViewWithTag("idis_"+holder.getAdapterPosition());
                        Context context = layout.getContext();
                        view.setBackgroundColor(context.getResources().getColor(R.color.red));*/

                    }
                }
            }
        }
    }


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
        AppCompatTextView dpi_full_name_tv, dpi_ping_text_tv, dpi_text_tv;
        CircleImageView dpi_user_photo_civ, dpi_profile_photo_civ;
        AppCompatImageView dpi_time_indicator_iv;
        LottieAnimationView dpi_ping_lav, dpi_react_btn_lav;
        RelativeLayout dpi_react_content_rl, dpi_comment_content_rl;

        public HomeFollowDefaultPingViewHolder(View view) {
            super(view);
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



}
