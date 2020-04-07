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
import android.widget.ProgressBar;

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
import com.doitandroid.mybeta.itemclass.UserItem;
import com.doitandroid.mybeta.rest.APIInterface;
import com.doitandroid.mybeta.rest.ConstantREST;
import com.doitandroid.mybeta.rest.LoggedInAPIClient;
import com.doitandroid.mybeta.utils.InitializationOnDemandHolderIdiom;

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
    InitializationOnDemandHolderIdiom singleton = InitializationOnDemandHolderIdiom.getInstance();

    APIInterface apiInterface;

    public CommentAdapter(ArrayList<CommentItem> commentItemArrayList, Context context) {
        this.commentItemArrayList = commentItemArrayList;
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
            case ConstantIntegers.OPT_LOADING:
                view = inflater.inflate(R.layout.item_loading, parent, false);
                viewHolder = new LoadingViewHolder(view);
                break;

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
        final UserItem userItem;
        if (commentItem != null) {
            userItem = singleton.getUserItemFromSingletonByUserItem(commentItem.getUser());
        } else {
            userItem = null;
        }

        switch (getItemViewType(position)){
            case ConstantIntegers.OPT_LOADING:
                break;
            default:
                CommentViewHolder commentViewHolder = ((CommentViewHolder) holder);
                commentViewHolder.comment_full_name_tv.setText(userItem.getFullName());

                commentViewHolder.comment_full_name_tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, ContentActivity.class);
                        intent.putExtra(ConstantStrings.INTENT_CONTENT_START, ConstantStrings.INTENT_CONTENT_USER);

                        intent.putExtra("userID", userItem.getUserID());
                        ((CommentActivity) context).startActivityForResult(intent, ConstantIntegers.REQUEST_CONTENT);
                        ((CommentActivity) context).overridePendingTransition(0, 0); //

                    }
                });
                commentViewHolder.comment_text_tv.setText(commentItem.getCommentText());
                Glide.with(context)
                        //.load(feeditem.getUser().getUserPhoto())
                        .load((ConstantREST.URL_HOME).substring(0, ConstantREST.URL_HOME.length()-1) + userItem.getUserPhoto())
                        .into(commentViewHolder.comment_user_photo_civ);


                break;
        }
    }

    @Override
    public int getItemCount() {
        return commentItemArrayList == null ? 0 : commentItemArrayList.size();
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
    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        ProgressBar loading_pb;
        public LoadingViewHolder(View view) {
            super(view);
            loading_pb = view.findViewById(R.id.item_loading_pb);

        }
    }

    @Override
    public int getItemViewType(int position) {
        return commentItemArrayList.get(position) == null ? ConstantIntegers.OPT_LOADING : super.getItemViewType(position);

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
