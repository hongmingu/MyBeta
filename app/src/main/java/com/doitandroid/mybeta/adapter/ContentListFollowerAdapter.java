package com.doitandroid.mybeta.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.doitandroid.mybeta.ConstantIntegers;
import com.doitandroid.mybeta.R;
import com.doitandroid.mybeta.fragment.ContentListFragment;
import com.doitandroid.mybeta.itemclass.UserItem;
import com.doitandroid.mybeta.rest.APIInterface;
import com.doitandroid.mybeta.rest.ConstantREST;
import com.doitandroid.mybeta.utils.InitializationOnDemandHolderIdiom;
import com.google.gson.JsonObject;

import java.util.concurrent.CopyOnWriteArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContentListFollowerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "CLFollowerAdapterTAG";
    CopyOnWriteArrayList<UserItem> userItemArrayList;

    Fragment parentFragment;


    Context context;
    InitializationOnDemandHolderIdiom singleton = InitializationOnDemandHolderIdiom.getInstance();

    APIInterface apiInterface;

    public ContentListFollowerAdapter(CopyOnWriteArrayList<UserItem> userItemArrayList, Context context, Fragment parentFragment) {
        this.userItemArrayList = userItemArrayList;
        this.parentFragment = parentFragment;

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
        switch (viewType) {
            case ConstantIntegers.OPT_LOADING:
                view = inflater.inflate(R.layout.item_loading, parent, false);
                viewHolder = new LoadingViewHolder(view);
                break;

            default:
                view = inflater.inflate(R.layout.item_follower, parent, false);
                viewHolder = new UserViewHolder(view);
                break;
        }

        Log.d(TAG, viewHolder.toString());

        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case ConstantIntegers.OPT_LOADING:
                break;

            default:
                final UserItem userItem = singleton.getUserItemFromSingletonByUserID(userItemArrayList.get(position).getUserID());
                final UserViewHolder userViewHolder = ((UserViewHolder) holder);
                userViewHolder.full_name_tv.setText(userItem.getFullName());

                Glide.with(context.getApplicationContext())
                        //.load(feeditem.getUser().getUserPhoto())
                        .load((ConstantREST.URL_HOME).substring(0, ConstantREST.URL_HOME.length() - 1) + userItem.getUserPhoto())
                        .into(userViewHolder.user_photo_civ);

                setStartUserFragment(userItem, userViewHolder.full_name_tv);
                setStartUserFragment(userItem, userViewHolder.user_photo_civ);
                if (userItem.isFollowed()) {
                    userViewHolder.follow_tv.setBackground(context.getResources().getDrawable(R.drawable.bg_black40_radius4dp));
                    ((AppCompatTextView) userViewHolder.follow_tv).setText(context.getResources().getString(R.string.following));
                } else {
                    userViewHolder.follow_tv.setBackground(context.getResources().getDrawable(R.drawable.bg_darkblue_radius4dp));
                    ((AppCompatTextView) userViewHolder.follow_tv).setText(context.getResources().getString(R.string.follow));
                }
                userViewHolder.follow_tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        follow_user(userItem.getUserID(), userViewHolder.follow_tv);
                    }
                });

                userItem.setOnUserItemChangedListener(new UserItem.OnUserItemChangedCallback() {
                    @Override
                    public void onItemChanged(UserItem userItem) {
                        Log.d(TAG, "follow from list follower: " + userItem.isFollowed() + this.getClass().toString());

                        Toast.makeText(context, "follower: " + userItem.isFollowed() + this.getClass().getSimpleName(), Toast.LENGTH_SHORT).show();
                        if (userItem.isFollowed()) {
                            userViewHolder.follow_tv.setBackground(context.getResources().getDrawable(R.drawable.bg_black40_radius4dp));
                            ((AppCompatTextView) userViewHolder.follow_tv).setText(context.getResources().getString(R.string.following));
                        } else {
                            userViewHolder.follow_tv.setBackground(context.getResources().getDrawable(R.drawable.bg_darkblue_radius4dp));
                            ((AppCompatTextView) userViewHolder.follow_tv).setText(context.getResources().getString(R.string.follow));
                        }

                    }
                });
                if (userItem.isSameUserItem(singleton.getUserItemFromSingletonByUserID(singleton.profileUserID))) {
                    userViewHolder.follow_tv.setVisibility(View.INVISIBLE);
                }

                break;
        }
    }

    @Override
    public int getItemCount() {
        return userItemArrayList == null ? 0 : userItemArrayList.size();
    }


    private class UserViewHolder extends RecyclerView.ViewHolder {
        AppCompatTextView full_name_tv;
        AppCompatTextView follow_tv;
        CircleImageView user_photo_civ;

        public UserViewHolder(View view) {
            super(view);
            full_name_tv = view.findViewById(R.id.item_following_full_name_tv);
            follow_tv = view.findViewById(R.id.item_following_follow_tv);
            user_photo_civ = view.findViewById(R.id.item_following_user_photo_civ);

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
        return userItemArrayList.get(position) == null ? ConstantIntegers.OPT_LOADING : super.getItemViewType(position);

    }

    @Override
    public long getItemId(int position) {
//        return super.getItemId(position);
        return position;
    }


    public void follow_user(final String userID, final View follow_tv) {
        RequestBody requestUserID = RequestBody.create(MediaType.parse("multipart/form-data"), userID);
        RequestBody requestBoolean;
        final UserItem userItem = singleton.getUserItemFromSingletonByUserID(userID);
        if (userItem.isFollowed()) {
            requestBoolean = RequestBody.create(MediaType.parse("multipart/form-data"), "false");
            userItem.setFollowed(false);
            singleton.updateUserList(userItem, false);
            follow_tv.setBackground(context.getResources().getDrawable(R.drawable.bg_darkblue_radius4dp));
            ((AppCompatTextView) follow_tv).setText(context.getResources().getString(R.string.follow));

            // 일단 팔로우취소해

        } else {
            requestBoolean = RequestBody.create(MediaType.parse("multipart/form-data"), "true");
            userItem.setFollowed(true);
            singleton.updateUserList(userItem, true);
            follow_tv.setBackground(context.getResources().getDrawable(R.drawable.bg_black40_radius4dp));
            ((AppCompatTextView) follow_tv).setText(context.getResources().getString(R.string.following));
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

                            if (userItem.isFollowed()) {
                                userItem.setFollowed(false);
                                singleton.updateUserList(userItem, false);
                                follow_tv.setBackground(context.getResources().getDrawable(R.drawable.bg_darkblue_radius4dp));
                                ((AppCompatTextView) follow_tv).setText(context.getResources().getString(R.string.follow));

                                // 일단 팔로우취소해

                            } else {
                                userItem.setFollowed(true);
                                singleton.updateUserList(userItem, true);
                                follow_tv.setBackground(context.getResources().getDrawable(R.drawable.bg_black40_radius4dp));
                                ((AppCompatTextView) follow_tv).setText(context.getResources().getString(R.string.following));
                            }

                            call.cancel();
                            return;
                        }

                        // todo: 이제 feedItem 만들기. inflater 를 이용해야 할 것 같다.


                        // 접속 성공.

                    }

                } else {
                    if (userItem.isFollowed()) {
                        userItem.setFollowed(false);
                        singleton.updateUserList(userItem, false);
                        follow_tv.setBackground(context.getResources().getDrawable(R.drawable.bg_darkblue_radius4dp));
                        ((AppCompatTextView) follow_tv).setText(context.getResources().getString(R.string.follow));
                        // 일단 팔로우취소해

                    } else {
                        userItem.setFollowed(true);
                        singleton.updateUserList(userItem, true);
                        follow_tv.setBackground(context.getResources().getDrawable(R.drawable.bg_black40_radius4dp));
                        ((AppCompatTextView) follow_tv).setText(context.getResources().getString(R.string.following));
                    }
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                call.cancel();


                if (userItem.isFollowed()) {
                    userItem.setFollowed(false);
                    singleton.updateUserList(userItem, false);
                    follow_tv.setBackground(context.getResources().getDrawable(R.drawable.bg_darkblue_radius4dp));
                    ((AppCompatTextView) follow_tv).setText(context.getResources().getString(R.string.follow));
                    // 일단 팔로우취소해

                } else {
                    userItem.setFollowed(true);
                    singleton.updateUserList(userItem, true);
                    follow_tv.setBackground(context.getResources().getDrawable(R.drawable.bg_black40_radius4dp));
                    ((AppCompatTextView) follow_tv).setText(context.getResources().getString(R.string.following));
                }
            }
        });
    }


    public void setStartUserFragment(final UserItem userItem, View view) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ContentListFragment) parentFragment).addUserFragment(userItem);
            }
        });

    }
}
