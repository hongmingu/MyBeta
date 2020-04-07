package com.doitandroid.mybeta.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.doitandroid.mybeta.ConstantIntegers;
import com.doitandroid.mybeta.ConstantStrings;
import com.doitandroid.mybeta.ContentActivity;
import com.doitandroid.mybeta.R;
import com.doitandroid.mybeta.ReactActivity;
import com.doitandroid.mybeta.itemclass.ReactItem;
import com.doitandroid.mybeta.itemclass.UserItem;
import com.doitandroid.mybeta.rest.APIInterface;
import com.doitandroid.mybeta.rest.ConstantREST;
import com.doitandroid.mybeta.rest.LoggedInAPIClient;
import com.doitandroid.mybeta.utils.InitializationOnDemandHolderIdiom;
import com.google.gson.JsonObject;

import java.util.ArrayList;

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
            case ConstantIntegers.OPT_LOADING:
                view = inflater.inflate(R.layout.item_loading, parent, false);
                viewHolder = new LoadingViewHolder(view);
                break;

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
        final UserItem userItem;
        if (reactItem != null) {
            userItem = singleton.getUserItemFromSingletonByUserItem(reactItem.getUser());
        } else {
            userItem = null;
        }
        switch (getItemViewType(position)){
            case ConstantIntegers.OPT_LOADING:
                break;

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

                reactViewHolder.react_user_photo_civ.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, ContentActivity.class);
                        intent.putExtra(ConstantStrings.INTENT_CONTENT_START, ConstantStrings.INTENT_CONTENT_USER);

                        intent.putExtra("userID", userItem.getUserID());
                        ((ReactActivity) context).startActivityForResult(intent, ConstantIntegers.REQUEST_CONTENT);
                        ((ReactActivity) context).overridePendingTransition(0, 0); //

                    }
                });
                if (userItem.isFollowed()){
                    reactViewHolder.react_follow_tv.setBackground(context.getResources().getDrawable(R.drawable.bg_black40_radius4dp));
                    ((AppCompatTextView) reactViewHolder.react_follow_tv).setText(context.getResources().getString(R.string.following));
                } else {
                    reactViewHolder.react_follow_tv.setBackground(context.getResources().getDrawable(R.drawable.bg_darkblue_radius4dp));
                    ((AppCompatTextView) reactViewHolder.react_follow_tv).setText(context.getResources().getString(R.string.follow));

                }
                userItem.setOnUserItemChangedListener(new UserItem.OnUserItemChangedCallback() {
                    @Override
                    public void onItemChanged(UserItem userItem) {
                        Log.d(TAG, "follow from list follower: "+ userItem.isFollowed()+ this.getClass().toString());

                        Toast.makeText(context, "follower: "+ userItem.isFollowed()+ this.getClass().getSimpleName(), Toast.LENGTH_SHORT).show();
                        if (userItem.isFollowed()){
                            reactViewHolder.react_follow_tv.setBackground(context.getResources().getDrawable(R.drawable.bg_black40_radius4dp));
                            ((AppCompatTextView) reactViewHolder.react_follow_tv).setText(context.getResources().getString(R.string.following));
                        } else {
                            reactViewHolder.react_follow_tv.setBackground(context.getResources().getDrawable(R.drawable.bg_darkblue_radius4dp));
                            ((AppCompatTextView) reactViewHolder.react_follow_tv).setText(context.getResources().getString(R.string.follow));
                        }

                    }
                });

                reactViewHolder.react_follow_tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        follow_user(userItem.getUserID(), reactViewHolder.react_follow_tv);
                    }
                });

                if(userItem.isSameUserItem(singleton.getUserItemFromSingletonByUserID(singleton.profileUserID))){
                    reactViewHolder.react_follow_tv.setVisibility(View.INVISIBLE);
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        return reactItemArrayList == null ? 0 : reactItemArrayList.size();
    }


    private class ReactViewHolder extends RecyclerView.ViewHolder {
        AppCompatTextView react_full_name_tv;
        AppCompatTextView react_follow_tv;
        CircleImageView react_user_photo_civ;

        public ReactViewHolder(View view) {
            super(view);
            react_full_name_tv = view.findViewById(R.id.item_react_full_name_tv);
            react_follow_tv = view.findViewById(R.id.item_react_follow_tv);
            react_user_photo_civ = view.findViewById(R.id.item_react_user_photo_civ);

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
        return reactItemArrayList.get(position) == null ? ConstantIntegers.OPT_LOADING : super.getItemViewType(position);
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

    public void follow_user(final String userID, final View follow_tv){
        RequestBody requestUserID = RequestBody.create(MediaType.parse("multipart/form-data"), userID);
        RequestBody requestBoolean;
        final UserItem userItem = singleton.getUserItemFromSingletonByUserID(userID);
        if (userItem.isFollowed()){
            requestBoolean = RequestBody.create(MediaType.parse("multipart/form-data"), "false");
            userItem.setFollowed(false);
            singleton.updateUserList(userItem, false);
            follow_tv.setBackground(context.getResources().getDrawable(R.drawable.bg_darkblue_radius4dp));
            ((AppCompatTextView)follow_tv).setText(context.getResources().getString(R.string.follow));
            // 일단 팔로우취소해

        } else {
            requestBoolean = RequestBody.create(MediaType.parse("multipart/form-data"), "true");
            userItem.setFollowed(true);
            singleton.updateUserList(userItem, true);
            follow_tv.setBackground(context.getResources().getDrawable(R.drawable.bg_black40_radius4dp));
            ((AppCompatTextView)follow_tv).setText(context.getResources().getString(R.string.following));
        }


        Call<JsonObject> call = apiInterface.followBoolean(requestUserID, requestBoolean);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject jsonObject = response.body();
                    if (jsonObject != null) {
                        int rc = jsonObject.get("rc").getAsInt();

                        if (rc != ConstantIntegers.SUCCEED_RESPONSE) {
                            // sign up 실패
                            if (userItem.isFollowed()){
                                userItem.setFollowed(false);
                                singleton.updateUserList(userItem, false);
                                follow_tv.setBackground(context.getResources().getDrawable(R.drawable.bg_darkblue_radius4dp));
                                ((AppCompatTextView)follow_tv).setText(context.getResources().getString(R.string.follow));
                                // 일단 팔로우취소해

                            } else {
                                userItem.setFollowed(true);
                                singleton.updateUserList(userItem, true);
                                follow_tv.setBackground(context.getResources().getDrawable(R.drawable.bg_black40_radius4dp));
                                ((AppCompatTextView)follow_tv).setText(context.getResources().getString(R.string.following));
                            }
                            call.cancel();
                            return;
                        }


                        // 접속 성공.

                    }

                } else {
                    if (userItem.isFollowed()){
                        userItem.setFollowed(false);
                        singleton.updateUserList(userItem, false);
                        follow_tv.setBackground(context.getResources().getDrawable(R.drawable.bg_darkblue_radius4dp));
                        ((AppCompatTextView)follow_tv).setText(context.getResources().getString(R.string.follow));
                        // 일단 팔로우취소해

                    } else {
                        userItem.setFollowed(true);
                        singleton.updateUserList(userItem, true);
                        follow_tv.setBackground(context.getResources().getDrawable(R.drawable.bg_black40_radius4dp));
                        ((AppCompatTextView)follow_tv).setText(context.getResources().getString(R.string.following));
                    }
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

                call.cancel();

                if (userItem.isFollowed()){
                    userItem.setFollowed(false);
                    singleton.updateUserList(userItem, false);
                    follow_tv.setBackground(context.getResources().getDrawable(R.drawable.bg_darkblue_radius4dp));
                    ((AppCompatTextView)follow_tv).setText(context.getResources().getString(R.string.follow));
                    // 일단 팔로우취소해

                } else {
                    userItem.setFollowed(true);
                    singleton.updateUserList(userItem, true);
                    follow_tv.setBackground(context.getResources().getDrawable(R.drawable.bg_black40_radius4dp));
                    ((AppCompatTextView)follow_tv).setText(context.getResources().getString(R.string.following));
                }
            }
        });
    }
}
