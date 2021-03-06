package com.doitandroid.mybeta.fragment;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.doitandroid.mybeta.ConstantIntegers;
import com.doitandroid.mybeta.ContentActivity;
import com.doitandroid.mybeta.R;
import com.doitandroid.mybeta.itemclass.UserItem;
import com.doitandroid.mybeta.rest.APIInterface;
import com.doitandroid.mybeta.utils.InitializationOnDemandHolderIdiom;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContentListFragment extends Fragment {

    private static final String TAG = "ContentListFragTAG";

    InitializationOnDemandHolderIdiom singleton = InitializationOnDemandHolderIdiom.getInstance();

    APIInterface apiInterface;

    ViewGroup rootView;
    UserItem userItem;
    boolean initFollowing;

    CircleImageView user_photo_civ;
    AppCompatImageView follow_iv;
    AppCompatTextView full_name_tv, username_tv;



    FragmentManager childFragmentManager;

    AppCompatTextView content_list_following_tv, content_list_follower_tv;

    ContentListFollowingFragment contentListFollowingFragment;
    ContentListFollowerFragment contentListFollowerFragment;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_content_list, container, false);

        apiInterface = singleton.apiInterface;


        userFullyUpdateAndSetAdapter(userItem);

        childFragmentManager = getChildFragmentManager();

        contentListFollowerFragment = new ContentListFollowerFragment(userItem.getFollowerList());
        contentListFollowingFragment = new ContentListFollowingFragment(userItem.getFollowingList());

        childFragmentManager.beginTransaction().add(R.id.fragment_content_list_child_container, contentListFollowerFragment).commit();
        childFragmentManager.beginTransaction().add(R.id.fragment_content_list_child_container, contentListFollowingFragment).commit();


        full_name_tv = rootView.findViewById(R.id.fragment_content_list_full_name_tv);
        full_name_tv.setText(userItem.getFullName());

        content_list_following_tv = rootView.findViewById(R.id.fragment_content_list_following_tv);

        content_list_follower_tv = rootView.findViewById(R.id.fragment_content_list_follower_tv);

        content_list_following_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChild(true);

            }
        });
        content_list_follower_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChild(false);
            }
        });

        showChild(initFollowing);

        return rootView;
    }




    public ContentListFragment(UserItem userItem, boolean initFollowing) {

        this.userItem = userItem;
        this.initFollowing = initFollowing;
    }

    public void showChild(boolean initFollowing){
        if (initFollowing) {

            if (contentListFollowingFragment == null) {
                contentListFollowingFragment = new ContentListFollowingFragment(userItem.getFollowingList());
                childFragmentManager.beginTransaction().add(R.id.fragment_search_child_container, contentListFollowingFragment).commit();
            }

            if (contentListFollowingFragment != null) {
                childFragmentManager.beginTransaction().show(contentListFollowingFragment).commit();
            }

            if (contentListFollowerFragment != null) {
                childFragmentManager.beginTransaction().hide(contentListFollowerFragment).commit();
            }
            content_list_following_tv.setTextColor(Color.parseColor("#202020"));
            content_list_following_tv.setBackgroundColor(getResources().getColor(R.color.skyblue_4));
            content_list_follower_tv.setTextColor(Color.parseColor("#636363"));
            content_list_follower_tv.setBackgroundColor(getResources().getColor(R.color.transparent));
        } else {
            if (contentListFollowerFragment== null) {
                contentListFollowerFragment= new ContentListFollowerFragment(userItem.getFollowerList());
                childFragmentManager.beginTransaction().add(R.id.fragment_search_child_container, contentListFollowerFragment).commit();
            }

            if (contentListFollowerFragment != null) {
                childFragmentManager.beginTransaction().show(contentListFollowerFragment).commit();
            }

            if (contentListFollowingFragment != null) {
                childFragmentManager.beginTransaction().hide(contentListFollowingFragment).commit();
            }

            content_list_following_tv.setTextColor(Color.parseColor("#636363"));
            content_list_follower_tv.setTextColor(Color.parseColor("#202020"));
            content_list_following_tv.setBackgroundColor(getResources().getColor(R.color.transparent));
            content_list_follower_tv.setBackgroundColor(getResources().getColor(R.color.skyblue_4));


        }

    }


    public void addUserFragment(UserItem userItem){
        ((ContentActivity) getActivity()).addUserFragment(userItem);

    }



    public void userFullyUpdateAndSetAdapter(final UserItem userItem){
        if (!userItem.isFullyUpdated()){

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

                            for(JsonElement jsonFollowerElement: contentFollowerArray){
                                JsonObject followerItem = jsonFollowerElement.getAsJsonObject();
                                UserItem followerUserItem = singleton.getUserItemFromSingletonByJsonObject(followerItem);
                                userItem.addFollower(followerUserItem);

                            }
                            JsonArray contentFollowingArray= jsonObject.get("content_following").getAsJsonArray();


                            for(JsonElement jsonFollowingElement: contentFollowingArray){
                                JsonObject followingItem= jsonFollowingElement.getAsJsonObject();
                                UserItem followingUserItem = singleton.getUserItemFromSingletonByJsonObject(followingItem);
                                userItem.addFollowing(followingUserItem);

                            }


                            userItem.setFullyUpdated(true);
                            contentListFollowerFragment.setAdapter();
                            contentListFollowingFragment.setAdapter();


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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState");

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
        Log.d(TAG, "onAttach");

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");

    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView");


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");

    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach");


    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.d(TAG, "onHiddenChanged");

    }

    @Override
    public void onAttachFragment(Fragment childFragment) {
        super.onAttachFragment(childFragment);
        Log.d(TAG, "onAttachFragment");

    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        Log.d(TAG, "onViewStateRestored");

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult");

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult");

    }

    @NonNull
    @Override
    public LayoutInflater onGetLayoutInflater(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onGetLayoutInflater");
        return super.onGetLayoutInflater(savedInstanceState);

    }

    @Override
    public void onInflate(Context context, AttributeSet attrs, Bundle savedInstanceState) {
        super.onInflate(context, attrs, savedInstanceState);
        Log.d(TAG, "onInflate");
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        Log.d(TAG, "onCreateAnimation");
        return super.onCreateAnimation(transit, enter, nextAnim);
    }

    @Override
    public Animator onCreateAnimator(int transit, boolean enter, int nextAnim) {
        Log.d(TAG, "onCreateAnimator");
        return super.onCreateAnimator(transit, enter, nextAnim);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated");
    }

    @Override
    public void onMultiWindowModeChanged(boolean isInMultiWindowMode) {
        super.onMultiWindowModeChanged(isInMultiWindowMode);
        Log.d(TAG, "onMultiWindowModeChanged");
    }

    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode);
        Log.d(TAG, "onPictureInPictureModeChanged");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d(TAG, "onConfigurationChanged");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.d(TAG, "onLowMemory");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        Log.d(TAG, "onCreateOptionsMenu");
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        Log.d(TAG, "onPrepareOptionsMenu");
    }

    @Override
    public void onDestroyOptionsMenu() {
        super.onDestroyOptionsMenu();
        Log.d(TAG, "onDestroyOptionsMenu");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected");
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onOptionsMenuClosed(Menu menu) {
        super.onOptionsMenuClosed(menu);
        Log.d(TAG, "onOptionsMenuClosed");
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        Log.d(TAG, "onCreateContextMenu");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Log.d(TAG, "onContextItemSelected");
        return super.onContextItemSelected(item);
    }
}
