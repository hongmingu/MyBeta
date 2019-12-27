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
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.doitandroid.mybeta.CommentActivity;
import com.doitandroid.mybeta.ConstantIntegers;
import com.doitandroid.mybeta.ConstantStrings;
import com.doitandroid.mybeta.ContentActivity;
import com.doitandroid.mybeta.R;
import com.doitandroid.mybeta.ReactActivity;
import com.doitandroid.mybeta.itemclass.CommentItem;
import com.doitandroid.mybeta.itemclass.ReactItem;
import com.doitandroid.mybeta.itemclass.UserItem;
import com.doitandroid.mybeta.rest.APIInterface;
import com.doitandroid.mybeta.rest.ConstantREST;
import com.doitandroid.mybeta.rest.LoggedInAPIClient;
import com.doitandroid.mybeta.utils.InitializationOnDemandHolderIdiom;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReactAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "ReactAdapterTAG";
    ArrayList<ReactItem> reactItemArrayList;
    InitializationOnDemandHolderIdiom singleton = InitializationOnDemandHolderIdiom.getInstance();

    // todo: CommentItem 만든다.
    //  comment는 그때그때 리스트 만들어서 준다.

    Context context;

    APIInterface apiInterface;

    public ReactAdapter(ArrayList<ReactItem> reactItemArrayList, Context context) {
        this.reactItemArrayList = reactItemArrayList;
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
            default:
                view = inflater.inflate(R.layout.item_react, parent, false);
                viewHolder = new ReactViewHolder(view);
                break;
        }

        Log.d(TAG, viewHolder.toString());

        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final ReactItem reactItem = reactItemArrayList.get(position);
        final UserItem userItem = singleton.getUserItemFromSingletonByUserItem(reactItem.getUser());

        switch (getItemViewType(position)){

            default:
                final ReactViewHolder reactViewHolder = ((ReactViewHolder) holder);
                reactViewHolder.react_full_name_tv.setText(userItem.getFullName());

                reactViewHolder.react_full_name_tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, ContentActivity.class);
                        intent.putExtra(ConstantStrings.INTENT_CONTENT_START, ConstantStrings.INTENT_CONTENT_USER);

                        intent.putExtra("userID", userItem.getUserID());
                        ((ReactActivity) context).startActivityForResult(intent, ConstantIntegers.REQUEST_CONTENT);
                        ((ReactActivity) context).overridePendingTransition(0, 0); //

                    }
                });
                Glide.with(context)
                        //.load(feeditem.getUser().getUserPhoto())
                        .load((ConstantREST.URL_HOME).substring(0, ConstantREST.URL_HOME.length()-1) + userItem.getUserPhoto())
                        .into(reactViewHolder.react_user_photo_civ);

                reactViewHolder.react_follow_iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        follow_user(userItem.getUserID(), reactViewHolder.react_follow_iv);
                    }
                });


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
        return reactItemArrayList.size();
    }


    private class ReactViewHolder extends RecyclerView.ViewHolder {
        AppCompatTextView react_full_name_tv;
        AppCompatImageView react_follow_iv;
        CircleImageView react_user_photo_civ;

        public ReactViewHolder(View view) {
            super(view);
            react_full_name_tv = view.findViewById(R.id.item_react_full_name_tv);
            react_follow_iv = view.findViewById(R.id.item_react_follow_iv);
            react_user_photo_civ = view.findViewById(R.id.item_react_user_photo_civ);

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

    public void follow_user(String userID, final View follow_iv){
        RequestBody requestUserID = RequestBody.create(MediaType.parse("multipart/form-data"), userID);
        Call<JsonObject> call = apiInterface.follow(requestUserID);
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

                        // todo: 이제 feedItem 만들기. inflater 를 이용해야 할 것 같다.
                        if (jsonObject.get("content").getAsBoolean()){
                            // follow
                            follow_iv.setBackground(context.getResources().getDrawable(R.drawable.bg_skyblue));
                        } else {
                            // not follow
                            follow_iv.setBackground(context.getResources().getDrawable(R.drawable.bg_darkblue_border_radius4dp));
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
}
