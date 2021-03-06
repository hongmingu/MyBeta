package com.doitandroid.mybeta.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.doitandroid.mybeta.ConstantIntegers;
import com.doitandroid.mybeta.ContentActivity;
import com.doitandroid.mybeta.R;
import com.doitandroid.mybeta.itemclass.UserItem;
import com.doitandroid.mybeta.rest.APIInterface;
import com.doitandroid.mybeta.rest.ConstantREST;
import com.doitandroid.mybeta.utils.InitializationOnDemandHolderIdiom;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContentUserFragment extends Fragment {

    private static final String TAG = "ContentUserFragment";

    Activity activity;

    InitializationOnDemandHolderIdiom singleton = InitializationOnDemandHolderIdiom.getInstance();

    APIInterface apiInterface;

    ViewGroup rootView;
    UserItem userItem;

    CircleImageView user_photo_civ, related_follower_1_civ, related_follower_2_civ, related_follower_3_civ;
    AppCompatTextView follow_tv;
    AppCompatTextView full_name_tv, username_tv;
    CoordinatorLayout following_cl, follower_cl, related_follower_wrapper_cl;

    CopyOnWriteArrayList<UserItem> relatedUserItemArrayList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_content_user, container, false);

        apiInterface = singleton.apiInterface;
//        ((ContentActivity)getActivity()).addTestFragment();

        setViews();

        // set user photo
        Glide.with(getActivity().getApplicationContext())
                .load((ConstantREST.URL_HOME).substring(0, ConstantREST.URL_HOME.length() - 1) + userItem.getUserPhoto())
                .into(user_photo_civ);

        full_name_tv.setText(userItem.getFullName());
        username_tv.setText(userItem.getUsername());

        if (userItem.isFollowed()) {
            // is following
            follow_tv.setBackground(getResources().getDrawable(R.drawable.bg_black40_radius4dp));
            ((AppCompatTextView) follow_tv).setText(getActivity().getResources().getString(R.string.following));
        } else {
            // not following
            follow_tv.setBackground(getResources().getDrawable(R.drawable.bg_darkblue_radius4dp));
            ((AppCompatTextView)follow_tv).setText(getActivity().getResources().getString(R.string.follow));
        }

        userItem.setOnUserItemChangedListener(new UserItem.OnUserItemChangedCallback() {
            @Override
            public void onItemChanged(UserItem userItem) {

                if (userItem.isFollowed()) {
                    follow_tv.setBackground(getResources().getDrawable(R.drawable.bg_black40_radius4dp));
                    ((AppCompatTextView) follow_tv).setText(getActivity().getResources().getString(R.string.following));
                } else {
                    follow_tv.setBackground(getResources().getDrawable(R.drawable.bg_darkblue_radius4dp));
                    ((AppCompatTextView)follow_tv).setText(getActivity().getResources().getString(R.string.follow));
                }

            }
        });

        follow_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                follow_user();
            }
        });

        userFullyUpdate(userItem);


        following_cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ContentActivity) getActivity()).addListFragment(userItem, true);
            }
        });
        follower_cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ContentActivity) getActivity()).addListFragment(userItem, false);

            }
        });

        related_follower_wrapper_cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ContentActivity) getActivity()).addListFragment(userItem, false);
            }
        });


        if (userItem.isSameUserItem(singleton.getUserItemFromSingletonByUserID(singleton.profileUserID))) {
            follow_tv.setVisibility(View.INVISIBLE);
            related_follower_wrapper_cl.setVisibility(View.INVISIBLE);
        }
        //todo: 이제 fully updated 가 아니면 받아오는 코드.

        return rootView;
    }


    public void follow_user() {
        RequestBody userID = RequestBody.create(MediaType.parse("multipart/form-data"), userItem.getUserID());
        RequestBody requestBoolean;
        if (userItem.isFollowed()) {
            requestBoolean = RequestBody.create(MediaType.parse("multipart/form-data"), "false");
            userItem.setFollowed(false);
            singleton.updateUserList(userItem, false);
            follow_tv.setBackground(getResources().getDrawable(R.drawable.bg_darkblue_radius4dp));
            ((AppCompatTextView)follow_tv).setText(getActivity().getResources().getString(R.string.follow));
            // 일단 팔로우취소해

        } else {
            requestBoolean = RequestBody.create(MediaType.parse("multipart/form-data"), "true");
            userItem.setFollowed(true);
            singleton.updateUserList(userItem, true);
            follow_tv.setBackground(getResources().getDrawable(R.drawable.bg_black40_radius4dp));
            ((AppCompatTextView) follow_tv).setText(getActivity().getResources().getString(R.string.following));
        }


        Call<JsonObject> call = apiInterface.followBoolean(userID, requestBoolean);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (response.isSuccessful()) {
                    JsonObject jsonObject = response.body();
                    if (jsonObject != null) {
                        int rc = jsonObject.get("rc").getAsInt();

                        if (rc != ConstantIntegers.SUCCEED_RESPONSE) {
                            Toast.makeText(getContext(), "failed", Toast.LENGTH_SHORT).show();

                            if (userItem.isFollowed()) {
                                userItem.setFollowed(false);
                                singleton.updateUserList(userItem, false);
                                follow_tv.setBackground(getResources().getDrawable(R.drawable.bg_darkblue_radius4dp));
                                ((AppCompatTextView)follow_tv).setText(getActivity().getResources().getString(R.string.follow));
                                // 일단 팔로우취소해

                            } else {
                                userItem.setFollowed(true);
                                singleton.updateUserList(userItem, true);
                                follow_tv.setBackground(getResources().getDrawable(R.drawable.bg_black40_radius4dp));
                                ((AppCompatTextView) follow_tv).setText(getActivity().getResources().getString(R.string.following));
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
                        follow_tv.setBackground(getResources().getDrawable(R.drawable.bg_darkblue_radius4dp));
                        ((AppCompatTextView)follow_tv).setText(getActivity().getResources().getString(R.string.follow));
                        // 일단 팔로우취소해

                    } else {
                        userItem.setFollowed(true);
                        singleton.updateUserList(userItem, true);
                        follow_tv.setBackground(getResources().getDrawable(R.drawable.bg_black40_radius4dp));
                        ((AppCompatTextView) follow_tv).setText(getActivity().getResources().getString(R.string.following));
                    }

                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

                Toast.makeText(getContext(), "failed", Toast.LENGTH_SHORT).show();
                if (userItem.isFollowed()) {
                    userItem.setFollowed(false);
                    singleton.updateUserList(userItem, false);
                    follow_tv.setBackground(getResources().getDrawable(R.drawable.bg_darkblue_radius4dp));
                    ((AppCompatTextView)follow_tv).setText(getActivity().getResources().getString(R.string.follow));
                    // 일단 팔로우취소해

                } else {
                    userItem.setFollowed(true);
                    singleton.updateUserList(userItem, true);
                    follow_tv.setBackground(getResources().getDrawable(R.drawable.bg_black40_radius4dp));
                    ((AppCompatTextView) follow_tv).setText(getActivity().getResources().getString(R.string.following));
                }

                call.cancel();

            }
        });
    }

    public static boolean containsUserItem(ArrayList<UserItem> c, UserItem item) {
        for (UserItem o : c) {
            if (o != null && o.getUserID().equals(item.getUserID())) {
                return true;
            }
        }
        return false;
    }


/*
    Date today = new Date();

    //        java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
    java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
    String dateToStr = format.format(today);
        Log.d(TAG, dateToStr);

*/


    public void setRelatedFollower() {

        relatedUserItemArrayList = new CopyOnWriteArrayList<>();
        for (UserItem userItem : userItem.getFollowerList()) {
            if (relatedUserItemArrayList.size() == 3) {
                break;
            }
            if (userItem.isFollowed()) {
                relatedUserItemArrayList.add(userItem);
            }
        }


        switch (relatedUserItemArrayList.size()) {
            case 0:

                related_follower_wrapper_cl.setVisibility(View.INVISIBLE);

                break;
            case 1:
                related_follower_wrapper_cl.setVisibility(View.VISIBLE);


                Glide.with(activity.getApplicationContext())
                        .load((ConstantREST.URL_HOME).substring(0, ConstantREST.URL_HOME.length() - 1) + relatedUserItemArrayList.get(0).getUserPhoto())
                        .into(related_follower_1_civ);
                break;
            case 2:
                related_follower_wrapper_cl.setVisibility(View.VISIBLE);
                Glide.with(activity.getApplicationContext())
                        .load((ConstantREST.URL_HOME).substring(0, ConstantREST.URL_HOME.length() - 1) + relatedUserItemArrayList.get(0).getUserPhoto())
                        .into(related_follower_1_civ);
                Glide.with(getActivity().getApplicationContext())
                        .load((ConstantREST.URL_HOME).substring(0, ConstantREST.URL_HOME.length() - 1) + relatedUserItemArrayList.get(1).getUserPhoto())
                        .into(related_follower_2_civ);
                break;
            case 3:
                related_follower_wrapper_cl.setVisibility(View.VISIBLE);
                Glide.with(activity.getApplicationContext())
                        .load((ConstantREST.URL_HOME).substring(0, ConstantREST.URL_HOME.length() - 1) + relatedUserItemArrayList.get(0).getUserPhoto())
                        .into(related_follower_1_civ);
                Glide.with(activity.getApplicationContext())
                        .load((ConstantREST.URL_HOME).substring(0, ConstantREST.URL_HOME.length() - 1) + relatedUserItemArrayList.get(1).getUserPhoto())
                        .into(related_follower_2_civ);
                Glide.with(activity.getApplicationContext())
                        .load((ConstantREST.URL_HOME).substring(0, ConstantREST.URL_HOME.length() - 1) + relatedUserItemArrayList.get(2).getUserPhoto())
                        .into(related_follower_3_civ);
                break;
            default:
                break;
        }
    }

    public void setViews() {
        user_photo_civ = rootView.findViewById(R.id.fragment_content_user_photo_civ);
        full_name_tv = rootView.findViewById(R.id.fragment_content_user_full_name_tv);
        username_tv = rootView.findViewById(R.id.fragment_content_user_username_tv);
        follow_tv = rootView.findViewById(R.id.fragment_content_user_follow_tv);

        follower_cl = rootView.findViewById(R.id.fragment_content_user_follower_cl);
        following_cl = rootView.findViewById(R.id.fragment_content_user_following_cl);

        related_follower_wrapper_cl = rootView.findViewById(R.id.fragment_content_user_followed_by_wrapper_cl);
        related_follower_1_civ = rootView.findViewById(R.id.fragment_content_user_followed_by_photo1_civ);
        related_follower_2_civ = rootView.findViewById(R.id.fragment_content_user_followed_by_photo2_civ);
        related_follower_3_civ = rootView.findViewById(R.id.fragment_content_user_followed_by_photo3_civ);


    }

    /**
     * final Activity activity = (MainActivity)getActivity();
     * Button button = rootView.findViewById(R.id.fragment1_btn);
     * button.setOnClickListener(new View.OnClickListener() {
     *
     * @Override public void onClick(View v) {
     * ((MainActivity) activity).noti_btn_clicked();
     * }
     * });
     */

    public void userFullyUpdate(final UserItem userItem) {
        if (!userItem.isFullyUpdated()) {

            RequestBody requestPostID = RequestBody.create(MediaType.parse("multipart/form-data"), userItem.getUserID());
            Call<JsonObject> call = apiInterface.userFullyUpdate(requestPostID);
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

                            JsonArray contentFollowerArray = jsonObject.get("content_follower").getAsJsonArray();

                            for (JsonElement jsonFollowerElement : contentFollowerArray) {
                                JsonObject followerItem = jsonFollowerElement.getAsJsonObject();
                                UserItem followerUserItem = singleton.getUserItemFromSingletonByJsonObject(followerItem);
                                userItem.addFollower(followerUserItem);

                            }
                            JsonArray contentFollowingArray = jsonObject.get("content_following").getAsJsonArray();


                            for (JsonElement jsonFollowingElement : contentFollowingArray) {
                                JsonObject followingItem = jsonFollowingElement.getAsJsonObject();
                                UserItem followingUserItem = singleton.getUserItemFromSingletonByJsonObject(followingItem);
                                userItem.addFollowing(followingUserItem);

                            }


                            userItem.setFullyUpdated(true);
                            setRelatedFollower();

                        }
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    call.cancel();

                }
            });
        } else {
            setRelatedFollower();
        }

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d("fragment1_STATE", "onSaveInstanceState");

/*        if (tv_count == null){
            tv_count = getView().findViewById(R.id.fragment1_counter);
        }

        int counter = Integer.parseInt(tv_count.getText().toString());
        outState.putInt("counter", counter);
        Log.d("fragment1_OSI", "outstate: "+counter);*/
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d("fragment1_STATE", "onAttach");

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("fragment1_STATE", "onCreate");

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("fragment1_STATE", "onActivityCreated");

    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("fragment1_STATE", "onStart");

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("fragment1_STATE", "onResume");

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("fragment1_STATE", "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("fragment1_STATE", "onStop");

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("fragment1_STATE", "onDestroyView");


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("fragment1_STATE", "onDestroy");

    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("fragment1_STATE", "onDetach");


    }


    public ContentUserFragment(UserItem userItem, Activity activity) {
        this.userItem = userItem;
        this.activity = activity;
    }


}
