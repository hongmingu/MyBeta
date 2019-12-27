package com.doitandroid.mybeta.fragment;

import android.content.Context;
import android.content.SharedPreferences;
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
import com.doitandroid.mybeta.ConstantStrings;
import com.doitandroid.mybeta.ContentActivity;
import com.doitandroid.mybeta.R;
import com.doitandroid.mybeta.itemclass.FeedItem;
import com.doitandroid.mybeta.itemclass.UserItem;
import com.doitandroid.mybeta.rest.APIInterface;
import com.doitandroid.mybeta.rest.ConstantREST;
import com.doitandroid.mybeta.rest.LoggedInAPIClient;
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

import static android.content.Context.MODE_PRIVATE;

public class ContentUserFragment extends Fragment {

    private static final String TAG = "HomeFollowFragTAG";

    InitializationOnDemandHolderIdiom singleton = InitializationOnDemandHolderIdiom.getInstance();

    APIInterface apiInterface;

    ViewGroup rootView;
    UserItem userItem;

    CircleImageView user_photo_civ, related_follower_1_civ, related_follower_2_civ, related_follower_3_civ;
    AppCompatImageView follow_iv;
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
            follow_iv.setBackground(getResources().getDrawable(R.drawable.bg_darkblue_border_radius4dp));
        } else {
            // not following
            follow_iv.setBackground(getResources().getDrawable(R.drawable.bg_skyblue));

        }

        userItem.setOnUserItemChangedListener(new UserItem.OnUserItemChangedCallback() {
            @Override
            public void onItemChanged(UserItem userItem) {

                if (userItem.isFollowed()) {
                    follow_iv.setBackground(getResources().getDrawable(R.drawable.bg_darkblue_border_radius4dp));

                } else {
                    follow_iv.setBackground(getResources().getDrawable(R.drawable.bg_skyblue));

                }

            }
        });

        follow_iv.setOnClickListener(new View.OnClickListener() {
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


        if(userItem.isSameUserItem(singleton.getUserItemFromSingletonByUserID(singleton.profileUserID))){
            follow_iv.setVisibility(View.INVISIBLE);
            related_follower_wrapper_cl.setVisibility(View.INVISIBLE);
        }
        //todo: 이제 fully updated 가 아니면 받아오는 코드.

        return rootView;
    }


    public void follow_user() {
        RequestBody userID = RequestBody.create(MediaType.parse("multipart/form-data"), userItem.getUserID());
        Call<JsonObject> call = apiInterface.follow(userID);
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
                        if (jsonObject.get("content").getAsBoolean()) {
                            // follow
                            follow_iv.setBackground(getResources().getDrawable(R.drawable.bg_darkblue_border_radius4dp));

                            userItem.setFollowed(true);
                            singleton.updateUserList(userItem, false);

                        } else {
                            follow_iv.setBackground(getResources().getDrawable(R.drawable.bg_skyblue));

                            userItem.setFollowed(false);
                            singleton.updateUserList(userItem, false);


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

    public static boolean containsUserItem(ArrayList<UserItem> c, UserItem item) {
        for (UserItem o : c) {
            if (o != null && o.getUserID().equals(item.getUserID())) {
                return true;
            }
        }
        return false;
    }

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


                Glide.with(getActivity().getApplicationContext())
                        .load((ConstantREST.URL_HOME).substring(0, ConstantREST.URL_HOME.length() - 1) + relatedUserItemArrayList.get(0).getUserPhoto())
                        .into(related_follower_1_civ);
                break;
            case 2:
                related_follower_wrapper_cl.setVisibility(View.VISIBLE);
                Glide.with(getActivity().getApplicationContext())
                        .load((ConstantREST.URL_HOME).substring(0, ConstantREST.URL_HOME.length() - 1) + relatedUserItemArrayList.get(0).getUserPhoto())
                        .into(related_follower_1_civ);
                Glide.with(getActivity().getApplicationContext())
                        .load((ConstantREST.URL_HOME).substring(0, ConstantREST.URL_HOME.length() - 1) + relatedUserItemArrayList.get(1).getUserPhoto())
                        .into(related_follower_2_civ);
                break;
            case 3:
                related_follower_wrapper_cl.setVisibility(View.VISIBLE);
                Glide.with(getActivity().getApplicationContext())
                        .load((ConstantREST.URL_HOME).substring(0, ConstantREST.URL_HOME.length() - 1) + relatedUserItemArrayList.get(0).getUserPhoto())
                        .into(related_follower_1_civ);
                Glide.with(getActivity().getApplicationContext())
                        .load((ConstantREST.URL_HOME).substring(0, ConstantREST.URL_HOME.length() - 1) + relatedUserItemArrayList.get(1).getUserPhoto())
                        .into(related_follower_2_civ);
                Glide.with(getActivity().getApplicationContext())
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
        follow_iv = rootView.findViewById(R.id.fragment_content_user_follow_iv);

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
    public void init_feed() {


        Call<JsonObject> call = apiInterface.get_follow_feed();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject jsonObject = response.body();
                    if (jsonObject != null) {
                        int rc = jsonObject.get("rc").getAsInt();
                        JsonArray content = jsonObject.get("content").getAsJsonArray();

                        if (rc != ConstantIntegers.SUCCEED_RESPONSE) {
                            // sign up 실패
                            call.cancel();
                            return;
                        }

                        // todo: 이제 feedItem 만들기. inflater 를 이용해야 할 것 같다.
                        Log.d(TAG, content.toString());
                        for (JsonElement feedElement : content) {
                            JsonObject feedObject = feedElement.getAsJsonObject();
                            FeedItem feedItem = new FeedItem(feedObject);

                            boolean isExist = false;
                            for (FeedItem item : singleton.followFeedList) {
                                if (feedItem.getPostID().equals(item.getPostID())) {
                                    isExist = true;
                                }
                            }
                            if (!isExist) {
                                singleton.followFeedList.add(feedItem);
                                Log.d(TAG, "called");
                            }

                            Log.d(TAG, singleton.followFeedList.size() + "");


                        }
                        singleton.homeFollowAdapter.notifyDataSetChanged();

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


    public ContentUserFragment(UserItem userItem) {
        this.userItem = userItem;
    }
}
