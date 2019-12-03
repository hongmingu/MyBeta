package com.doitandroid.mybeta.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.widget.TextViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.doitandroid.mybeta.ConstantIntegers;
import com.doitandroid.mybeta.ConstantStrings;
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

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

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

        apiInterface = getApiInterface();




        childFragmentManager = getChildFragmentManager();

        contentListFollowerFragment = new ContentListFollowerFragment(apiInterface);
        contentListFollowingFragment = new ContentListFollowingFragment(apiInterface);

        childFragmentManager.beginTransaction().add(R.id.fragment_content_list_child_container, contentListFollowerFragment).commit();
        childFragmentManager.beginTransaction().add(R.id.fragment_content_list_child_container, contentListFollowingFragment).commit();


        showChild(initFollowing);

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

        contentListFollowerFragment.getUser(userItem.getUserID());
        contentListFollowingFragment.getUser(userItem.getUserID());


        return rootView;
    }



    private APIInterface getApiInterface(){
        SharedPreferences sp = getActivity().getSharedPreferences(ConstantStrings.SP_INIT_APP, MODE_PRIVATE);
        String auth_token = sp.getString(ConstantStrings.SP_ARG_TOKEN, ConstantStrings.SP_ARG_REMOVE_TOKEN);
        APIInterface apiInterface = LoggedInAPIClient.getClient(auth_token).create(APIInterface.class);
        return apiInterface;
    }



    public ContentListFragment(UserItem userItem, boolean initFollowing) {

        this.userItem = userItem;
        this.initFollowing = initFollowing;
    }

    public void showChild(boolean initFollowing){
        if (initFollowing) {

            if (contentListFollowingFragment == null) {
                contentListFollowingFragment = new ContentListFollowingFragment(apiInterface);
                childFragmentManager.beginTransaction().add(R.id.fragment_search_child_container, contentListFollowingFragment).commit();
            }

            if (contentListFollowingFragment != null) {
                childFragmentManager.beginTransaction().show(contentListFollowingFragment).commit();
            }

            if (contentListFollowerFragment != null) {
                childFragmentManager.beginTransaction().hide(contentListFollowerFragment).commit();
            }

        } else {
            if (contentListFollowerFragment== null) {
                contentListFollowerFragment= new ContentListFollowerFragment(apiInterface);
                childFragmentManager.beginTransaction().add(R.id.fragment_search_child_container, contentListFollowerFragment).commit();
            }

            if (contentListFollowerFragment != null) {
                childFragmentManager.beginTransaction().show(contentListFollowerFragment).commit();
            }

            if (contentListFollowingFragment != null) {
                childFragmentManager.beginTransaction().hide(contentListFollowingFragment).commit();
            }
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


}
