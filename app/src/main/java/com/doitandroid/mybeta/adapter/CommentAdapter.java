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

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.doitandroid.mybeta.CommentActivity;
import com.doitandroid.mybeta.ConstantIntegers;
import com.doitandroid.mybeta.ConstantStrings;
import com.doitandroid.mybeta.ContentActivity;
import com.doitandroid.mybeta.MainActivity;
import com.doitandroid.mybeta.R;
import com.doitandroid.mybeta.itemclass.CommentItem;
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

public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "CommentAdapterTAG";
    ArrayList<CommentItem> commentItemArrayList;

    // todo: CommentItem 만든다.
    //  comment는 그때그때 리스트 만들어서 준다.

    Context context;

    APIInterface apiInterface;

    public CommentAdapter(ArrayList<CommentItem> commentItemArrayList, Context context) {
        this.commentItemArrayList = commentItemArrayList;
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
            default:
                view = inflater.inflate(R.layout.item_comment, parent, false);
                viewHolder = new CommentViewHolder(view);
                break;
        }

        Log.d(TAG, viewHolder.toString());

        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final CommentItem commentItem = commentItemArrayList.get(position);

        switch (getItemViewType(position)){

            default:
                CommentViewHolder commentViewHolder = ((CommentViewHolder) holder);
                commentViewHolder.comment_full_name_tv.setText(commentItem.getUser().getFullName());

                commentViewHolder.comment_full_name_tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, ContentActivity.class);
                        intent.putExtra(ConstantStrings.INTENT_CONTENT_START, ConstantStrings.INTENT_CONTENT_USER);

                        Bundle bundle = new Bundle();
                        bundle.putSerializable("userItem", commentItem.getUser());
                        intent.putExtras(bundle);

                        ((CommentActivity) context).startActivityForResult(intent, ConstantIntegers.REQUEST_CONTENT);
                        ((CommentActivity) context).overridePendingTransition(0, 0); //

                    }
                });
                commentViewHolder.comment_text_tv.setText(commentItem.getCommentText());
                Glide.with(context)
                        //.load(feeditem.getUser().getUserPhoto())
                        .load((ConstantREST.URL_HOME).substring(0, ConstantREST.URL_HOME.length()-1) + commentItem.getUser().getUserPhoto())
                        .into(commentViewHolder.comment_user_photo_civ);


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
        return commentItemArrayList.size();
    }


    private class CommentViewHolder extends RecyclerView.ViewHolder {
        AppCompatTextView comment_full_name_tv, comment_text_tv;
        CircleImageView comment_user_photo_civ;

        public CommentViewHolder(View view) {
            super(view);
            comment_full_name_tv = view.findViewById(R.id.item_comment_full_name_tv);
            comment_text_tv = view.findViewById(R.id.item_comment_text_tv);
            comment_user_photo_civ = view.findViewById(R.id.item_comment_user_photo_civ);

        }
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
        //return commentItemArrayList.get(position).getNoticeKind();
    }

    @Override
    public long getItemId(int position) {
//        return super.getItemId(position);

        return position;
    }


    private APIInterface getApiInterface(){
        SharedPreferences sp = context.getSharedPreferences(ConstantStrings.SP_INIT_APP, Context.MODE_PRIVATE);
        String auth_token = sp.getString(ConstantStrings.SP_ARG_TOKEN, ConstantStrings.SP_ARG_REMOVE_TOKEN);
        APIInterface apiInterface = LoggedInAPIClient.getClient(auth_token).create(APIInterface.class);
        return apiInterface;
    }
}
