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
import com.doitandroid.mybeta.ConstantIntegers;
import com.doitandroid.mybeta.ConstantStrings;
import com.doitandroid.mybeta.ContentActivity;
import com.doitandroid.mybeta.MainActivity;
import com.doitandroid.mybeta.R;
import com.doitandroid.mybeta.fragment.ContentListFragment;
import com.doitandroid.mybeta.itemclass.UserItem;
import com.doitandroid.mybeta.rest.APIInterface;
import com.doitandroid.mybeta.rest.ConstantREST;
import com.doitandroid.mybeta.rest.LoggedInAPIClient;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchResultAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "SearchResultAdapterTAG";
    ArrayList<UserItem> userItemArrayList;

    // todo: CommentItem 만든다.
    //  comment는 그때그때 리스트 만들어서 준다.

    Context context;

    APIInterface apiInterface;

    public SearchResultAdapter(ArrayList<UserItem> userItemArrayList, Context context) {
        this.userItemArrayList= userItemArrayList;
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
                view = inflater.inflate(R.layout.search_user_item, parent, false);
                viewHolder = new UserViewHolder(view);
                break;
        }

        Log.d(TAG, viewHolder.toString());

        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final UserItem userItem = userItemArrayList.get(position);

        switch (getItemViewType(position)){

            default:
                final UserViewHolder userViewHolder = ((UserViewHolder) holder);
                userViewHolder.full_name_tv.setText(userItem.getFullName());
                userViewHolder.full_name_tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, ContentActivity.class);
                        intent.putExtra(ConstantStrings.INTENT_CONTENT_START, ConstantStrings.INTENT_CONTENT_USER);

                        Bundle bundle = new Bundle();
                        bundle.putSerializable("userItem", userItem);
                        intent.putExtras(bundle);

                        ((MainActivity) context).startActivityForResult(intent, ConstantIntegers.REQUEST_CONTENT);
                        ((MainActivity) context).overridePendingTransition(0, 0); //
                    }
                });

                //todo: 이제 댓글, 좋아요에서 유저컨텐트로 이어지는 부분.
                Glide.with(context.getApplicationContext())
                        //.load(feeditem.getUser().getUserPhoto())
                        .load((ConstantREST.URL_HOME).substring(0, ConstantREST.URL_HOME.length()-1) + userItem.getUserPhoto())
                        .into(userViewHolder.user_photo_civ);

                userViewHolder.follow_iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        follow_user(userItem.getUserID(), userViewHolder.follow_iv);
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
        return userItemArrayList.size();
    }


    private class UserViewHolder extends RecyclerView.ViewHolder {
        AppCompatTextView full_name_tv;
        AppCompatImageView follow_iv;
        CircleImageView user_photo_civ;

        public UserViewHolder(View view) {
            super(view);
            full_name_tv = view.findViewById(R.id.search_user_item_full_name_tv);
            follow_iv = view.findViewById(R.id.search_user_item_follow_iv);
            user_photo_civ = view.findViewById(R.id.search_user_item_user_photo_civ);

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