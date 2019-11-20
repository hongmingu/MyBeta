package com.doitandroid.mybeta.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.doitandroid.mybeta.ConstantIntegers;
import com.doitandroid.mybeta.ConstantStrings;
import com.doitandroid.mybeta.R;
import com.doitandroid.mybeta.itemclass.NotiItem;
import com.doitandroid.mybeta.rest.APIInterface;
import com.doitandroid.mybeta.rest.ConstantREST;
import com.doitandroid.mybeta.rest.LoggedInAPIClient;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotiAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "NotiAdapterTAG";
    ArrayList<NotiItem> notiItemArrayList;

    Context context;

    APIInterface apiInterface;

    public NotiAdapter(ArrayList<NotiItem> notiItemArrayList, Context context) {
        this.notiItemArrayList = notiItemArrayList;
        this.context = context;
        apiInterface = getApiInterface();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RecyclerView.ViewHolder viewHolder = null;
        View view = null;
        switch (viewType){
            case ConstantIntegers.NOTICE_FOLLOW:
                view = inflater.inflate(R.layout.item_noti_follow, parent, false);
                viewHolder = new NotiFollowViewHolder(view);
                break;
            case ConstantIntegers.NOTICE_POST_COMMENT:
                view = inflater.inflate(R.layout.item_noti_post_comment, parent, false);
                viewHolder = new NotiPostCommentViewHolder(view);

                break;

            case ConstantIntegers.NOTICE_POST_REACT:
                view = inflater.inflate(R.layout.item_noti_post_react, parent, false);
                viewHolder = new NotiPostReactViewHolder(view);
                break;

            case ConstantIntegers.NOTICE_TIMEBAR_QUARTER_DAY:
                view = inflater.inflate(R.layout.item_noti_timebar_quarter_day, parent, false);
                viewHolder = new NotiTimeBarQuarterDayViewHolder(view);
                break;

            case ConstantIntegers.NOTICE_TIMEBAR_DAY:
                view = inflater.inflate(R.layout.item_noti_timebar_day, parent, false);
                viewHolder = new NotiTimeBarDayViewHolder(view);
                break;

            //todo: 노티리스트에 추가할 때 타임바 아이템을 따로 넣어서
            // 작업하도록 notifragment에서 jsonarray처리
            default:
                break;
        }

        Log.d(TAG, viewHolder.toString());

        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        NotiItem notiItem = notiItemArrayList.get(position);

        switch (getItemViewType(position)){

            case ConstantIntegers.NOTICE_FOLLOW:
                NotiFollowViewHolder followHolder = ((NotiFollowViewHolder) holder);
                followHolder.follow_name_tv.setText(notiItem.getUser().getFullName());
                Glide.with(context)
                        //.load(feeditem.getUser().getUserPhoto())
                        .load((ConstantREST.URL_HOME).substring(0, ConstantREST.URL_HOME.length()-1) + notiItem.getUser().getUserPhoto())
                        .into(followHolder.follow_user_photo_civ);
                Log.d(TAG, "post text: " + notiItem.getCommentText());

                break;
            case ConstantIntegers.NOTICE_POST_COMMENT:
                NotiPostCommentViewHolder postCommentHolder = ((NotiPostCommentViewHolder) holder);
                postCommentHolder.post_comment_text_tv.setText(notiItem.getUser().getFullName());
                Glide.with(context)
                        //.load(feeditem.getUser().getUserPhoto())
                        .load((ConstantREST.URL_HOME).substring(0, ConstantREST.URL_HOME.length()-1) + notiItem.getUser().getUserPhoto())
                        .into(postCommentHolder.post_comment_user_photo_civ);

                postCommentHolder.post_comment_text_tv.setText(notiItem.getCommentText());

                Log.d(TAG, "post text: " + notiItem.getCommentText());
                break;
            case ConstantIntegers.NOTICE_POST_REACT:
                NotiPostReactViewHolder postReactHolder = ((NotiPostReactViewHolder) holder);
                postReactHolder.post_react_name_tv.setText(notiItem.getUser().getFullName());
                Glide.with(context)
                        //.load(feeditem.getUser().getUserPhoto())
                        .load((ConstantREST.URL_HOME).substring(0, ConstantREST.URL_HOME.length()-1) + notiItem.getUser().getUserPhoto())
                        .into(postReactHolder.post_react_user_photo_civ);
                break;
            case ConstantIntegers.NOTICE_TIMEBAR_QUARTER_DAY:
                break;

            case ConstantIntegers.NOTICE_TIMEBAR_DAY:
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
        return notiItemArrayList.size();
    }


    private class NotiFollowViewHolder extends RecyclerView.ViewHolder {
        AppCompatTextView follow_name_tv;
        CircleImageView follow_user_photo_civ;

        public NotiFollowViewHolder(View view) {
            super(view);
            follow_name_tv = view.findViewById(R.id.item_noti_follow_name_tv);
            follow_user_photo_civ = view.findViewById(R.id.item_noti_follow_user_photo_civ);

        }
    }
    private class NotiPostCommentViewHolder extends RecyclerView.ViewHolder {
        AppCompatTextView post_comment_name_tv, post_comment_text_tv;
        CircleImageView post_comment_user_photo_civ;


        public NotiPostCommentViewHolder(View view) {
            super(view);
            post_comment_name_tv = view.findViewById(R.id.item_noti_post_comment_name_tv);
            post_comment_text_tv = view.findViewById(R.id.item_noti_post_comment_text_tv);
            post_comment_user_photo_civ = view.findViewById(R.id.item_noti_post_comment_user_photo_civ);

        }
    }
    private class NotiPostReactViewHolder extends RecyclerView.ViewHolder {
        AppCompatTextView post_react_name_tv;
        CircleImageView post_react_user_photo_civ;


        public NotiPostReactViewHolder(View view) {
            super(view);
            post_react_name_tv = view.findViewById(R.id.item_noti_post_react_name_tv);
            post_react_user_photo_civ = view.findViewById(R.id.item_noti_post_react_user_photo_civ);

        }
    }
    private class NotiTimeBarQuarterDayViewHolder extends RecyclerView.ViewHolder {
        public NotiTimeBarQuarterDayViewHolder(View view) {
            super(view);
        }
    }

    private class NotiTimeBarDayViewHolder extends RecyclerView.ViewHolder {
        public NotiTimeBarDayViewHolder(View view) {
            super(view);
        }
    }
    @Override
    public int getItemViewType(int position) {
//        return super.getItemViewType(position);
        return notiItemArrayList.get(position).getNoticeKind();
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

    private APIInterface getApiInterface(){
        SharedPreferences sp = context.getSharedPreferences(ConstantStrings.SP_INIT_APP, Context.MODE_PRIVATE);
        String auth_token = sp.getString(ConstantStrings.SP_ARG_TOKEN, ConstantStrings.SP_ARG_REMOVE_TOKEN);
        APIInterface apiInterface = LoggedInAPIClient.getClient(auth_token).create(APIInterface.class);
        return apiInterface;
    }
}
