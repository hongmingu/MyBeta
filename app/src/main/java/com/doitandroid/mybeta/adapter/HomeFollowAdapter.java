package com.doitandroid.mybeta.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.doitandroid.mybeta.ConstantIntegers;
import com.doitandroid.mybeta.R;
import com.doitandroid.mybeta.itemclass.FeedItem;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFollowAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
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
        RecyclerView.ViewHolder viewHolder = null;
        switch (getItemViewType(position)){
            case ConstantIntegers.OPT_DEFAULT_PING:
                viewHolder = (HomeFollowDefaultPingViewHolder) holder;

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
        AppCompatTextView default_ping_item_full_name_tv, default_ping_item_text_tv;
        CircleImageView default_ping_item_user_photo_civ, default_ping_item_profile_photo_civ;
        LottieAnimationView default_ping_item_ping_lav, default_ping_item_react_btn_lav;
        RelativeLayout default_ping_item_react_content_rl, default_ping_item_comment_content_rl;

        public HomeFollowDefaultPingViewHolder(View view) {
            super(view);
            default_ping_item_full_name_tv = view.findViewById(R.id.default_ping_item_full_name_tv);
            default_ping_item_user_photo_civ = view.findViewById(R.id.default_ping_item_user_photo_civ);
            default_ping_item_ping_lav = view.findViewById(R.id.default_ping_item_ping_lav);
            default_ping_item_text_tv = view.findViewById(R.id.default_ping_item_text_tv);
            default_ping_item_react_content_rl = view.findViewById(R.id.default_ping_item_react_content_rl);
            default_ping_item_react_btn_lav = view.findViewById(R.id.default_ping_item_react_btn_lav);
            default_ping_item_comment_content_rl = view.findViewById(R.id.default_ping_item_comment_content_rl);
            default_ping_item_profile_photo_civ = view.findViewById(R.id.default_ping_item_profile_photo_civ);

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
}
