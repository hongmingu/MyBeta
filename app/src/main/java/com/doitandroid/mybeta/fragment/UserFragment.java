package com.doitandroid.mybeta.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.doitandroid.mybeta.ConstantIntegers;
import com.doitandroid.mybeta.ConstantStrings;
import com.doitandroid.mybeta.ContentActivity;
import com.doitandroid.mybeta.Cropper.CropImage;
import com.doitandroid.mybeta.Cropper.CropImageView;
import com.doitandroid.mybeta.MainActivity;
import com.doitandroid.mybeta.ProfileChangeActivity;
import com.doitandroid.mybeta.R;
import com.doitandroid.mybeta.ReactActivity;
import com.doitandroid.mybeta.itemclass.UserItem;
import com.doitandroid.mybeta.rest.ConstantREST;
import com.doitandroid.mybeta.utils.InitializationOnDemandHolderIdiom;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

public class UserFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "UserFragment";

    CoordinatorLayout user_fragment_follower_cl, user_fragment_following_cl;
    AppCompatImageView user_fragment_setting_btn_iv, user_fragment_change_photo_iv, user_fragment_change_profile_iv;
    CircleImageView user_fragment_profile_photo_civ;

    TextView user_fragment_profile_username_tv,
            user_fragment_profile_full_name_tv,
            user_fragment_profile_email_tv,
            user_fragment_profile_follower_count_tv,
            user_fragment_profile_following_count_tv;

    InitializationOnDemandHolderIdiom singleton = InitializationOnDemandHolderIdiom.getInstance();


    Activity activity;

    String profilePhoto, profileUsername, profileFullName, profileEmail, profileUserID;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_user, container, false);

        activity = getActivity();

        setViews(rootView);


        setProfile();

        return rootView;
    }

    public void setViews(ViewGroup rootView) {
        user_fragment_setting_btn_iv = rootView.findViewById(R.id.fragment_user_setting_btn);
        user_fragment_setting_btn_iv.setOnClickListener(this);

        user_fragment_change_photo_iv = rootView.findViewById(R.id.fragment_user_change_photo_iv);
        user_fragment_change_photo_iv.setOnClickListener(this);

        user_fragment_profile_full_name_tv = rootView.findViewById(R.id.fragment_user_full_name_tv);
        user_fragment_profile_username_tv = rootView.findViewById(R.id.fragment_user_username_tv);
        user_fragment_profile_email_tv = rootView.findViewById(R.id.fragment_user_email_tv);

        user_fragment_follower_cl = rootView.findViewById(R.id.fragment_user_follower_cl);
        user_fragment_follower_cl.setOnClickListener(this);
        user_fragment_following_cl = rootView.findViewById(R.id.fragment_user_following_cl);
        user_fragment_following_cl.setOnClickListener(this);

        user_fragment_profile_photo_civ = rootView.findViewById(R.id.fragment_user_photo_civ);
        user_fragment_profile_photo_civ.setOnClickListener(this);

        user_fragment_change_profile_iv = rootView.findViewById(R.id.fragment_user_change_profile_iv);
        user_fragment_change_profile_iv.setOnClickListener(this);

        user_fragment_profile_follower_count_tv = rootView.findViewById(R.id.fragment_user_follower_count_tv);
        user_fragment_profile_following_count_tv = rootView.findViewById(R.id.fragment_user_following_count_tv);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fragment_user_setting_btn:
                Toast.makeText(getActivity(), "toast", Toast.LENGTH_SHORT).show();


                // 그리드뷰로 만든다.
                ((MainActivity) activity).openSettingActivity();
                break;
            case R.id.fragment_user_change_photo_iv:

                // 그리드뷰로 만든다.
                ((MainActivity) getActivity()).getCroppedImage();
                break;
            case R.id.fragment_user_photo_civ:
                ((MainActivity) getActivity()).getCroppedImage();
                break;

            case R.id.fragment_user_follower_cl:
                Intent intent = new Intent(activity, ContentActivity.class);
                intent.putExtra(ConstantStrings.INTENT_CONTENT_START, ConstantStrings.INTENT_CONTENT_FOLLOW);


                UserItem userItem = singleton.getUserItemFromSingletonByUserItem(new UserItem(profileUsername,
                        profileUserID,
                        profileFullName,
                        profilePhoto,
                        new CopyOnWriteArrayList<UserItem>(),
                        new CopyOnWriteArrayList<UserItem>(),
                        false,
                        false,
                        false));

                intent.putExtra("userID", userItem.getUserID());

                intent.putExtra(ConstantStrings.INTENT_CONTENT_FOLLOW_BOOLEAN, false);

                //todo: 유저프래그먼트에서 팔로워 팔로잉 숫자, 팔로우 그대로 빨리 눌리는 느낌주기. 처음에 누르자마자 색 바뀌고 그다음에 요청.

                ((MainActivity) activity).startActivityForResult(intent, ConstantIntegers.REQUEST_CONTENT);
                ((MainActivity) activity).overridePendingTransition(0, 0); //


                break;
            case R.id.fragment_user_following_cl:

                Intent ingIntent = new Intent(activity, ContentActivity.class);
                ingIntent.putExtra(ConstantStrings.INTENT_CONTENT_START, ConstantStrings.INTENT_CONTENT_FOLLOW);

                Bundle ingBundle = new Bundle();

                UserItem ingUserItem = singleton.getUserItemFromSingletonByUserItem(new UserItem(profileUsername,
                        profileUserID,
                        profileFullName,
                        profilePhoto,
                        new CopyOnWriteArrayList<UserItem>(),
                        new CopyOnWriteArrayList<UserItem>(),
                        false,
                        false,
                        false));

                ingIntent.putExtra("userID", ingUserItem.getUserID());

                ingIntent.putExtra(ConstantStrings.INTENT_CONTENT_FOLLOW_BOOLEAN, true);

                ((MainActivity) activity).startActivityForResult(ingIntent, ConstantIntegers.REQUEST_CONTENT);
                ((MainActivity) activity).overridePendingTransition(0, 0); //

                break;
            case R.id.fragment_user_change_profile_iv:
                Intent proIntent = new Intent(activity, ProfileChangeActivity.class);

                proIntent.putExtra(ConstantStrings.INTENT_PROFILE_FULL_NAME, profileFullName);
                proIntent.putExtra(ConstantStrings.INTENT_PROFILE_USERNAME, profileUsername);
                proIntent.putExtra(ConstantStrings.INTENT_PROFILE_EMAIL, profileEmail);

                ((MainActivity) activity).startActivityForResult(proIntent, ConstantIntegers.REQUEST_PROFILE_CHANGE);
                ((MainActivity) activity).overridePendingTransition(0, 0); //

                break;
            default:
                break;
        }
    }

    public void changePhoto(String profilePhoto){

        SharedPreferences sp = getActivity().getSharedPreferences(ConstantStrings.SP_INIT_APP, MODE_PRIVATE);

        SharedPreferences.Editor editor = sp.edit();
        editor.putString(ConstantStrings.SP_ARG_PROFILE_PHOTO, profilePhoto);

        editor.commit();
        Glide.with(this)
                .load((ConstantREST.URL_HOME).substring(0, ConstantREST.URL_HOME.length()-1) + profilePhoto)
                .into(user_fragment_profile_photo_civ);
    }

    public void refreshProfile(){

        SharedPreferences sp = getActivity().getSharedPreferences(ConstantStrings.SP_INIT_APP, MODE_PRIVATE);

        profileUsername = sp.getString(ConstantStrings.SP_ARG_PROFILE_USERNAME, ConstantStrings.SP_ARG_NONE);
        profileFullName= sp.getString(ConstantStrings.SP_ARG_PROFILE_FULLNAME, ConstantStrings.SP_ARG_NONE);
        profileEmail= sp.getString(ConstantStrings.SP_ARG_PROFILE_EMAIL, ConstantStrings.SP_ARG_NONE);

        user_fragment_profile_username_tv.setText(profileUsername);
        user_fragment_profile_full_name_tv.setText(profileFullName);
        user_fragment_profile_email_tv.setText(profileEmail);


    }

    public void setProfile() {


        SharedPreferences sp = getActivity().getSharedPreferences(ConstantStrings.SP_INIT_APP, MODE_PRIVATE);

        profilePhoto = sp.getString(ConstantStrings.SP_ARG_PROFILE_PHOTO, ConstantStrings.SP_ARG_NONE);
        profileUsername = sp.getString(ConstantStrings.SP_ARG_PROFILE_USERNAME, ConstantStrings.SP_ARG_NONE);
        profileFullName= sp.getString(ConstantStrings.SP_ARG_PROFILE_FULLNAME, ConstantStrings.SP_ARG_NONE);
        profileEmail= sp.getString(ConstantStrings.SP_ARG_PROFILE_EMAIL, ConstantStrings.SP_ARG_NONE);
        profileUserID = sp.getString(ConstantStrings.SP_ARG_PROFILE_USERID, ConstantStrings.SP_ARG_NONE);

        Log.d(TAG, profilePhoto);
        Glide.with(this)
                .load((ConstantREST.URL_HOME).substring(0, ConstantREST.URL_HOME.length()-1) + profilePhoto)

                .into(user_fragment_profile_photo_civ);

        user_fragment_profile_username_tv.setText(profileUsername);
        user_fragment_profile_full_name_tv.setText(profileFullName);
        user_fragment_profile_email_tv.setText(profileEmail);


        // todo: 이제 포토 올리는거, 나머지 셋텍스트.
    }


    public void setFollowCount(){
        UserItem userItem = singleton.getUserItemFromSingletonByUserID(singleton.getProfileUserID());

        Log.d(TAG, "setFollowCount: + " + userItem.getFollowingList().size());


        int index = 0;
        for (UserItem item: userItem.getFollowingList()){
            if(item.isFollowed()){
                index ++;
            }
        }

        user_fragment_profile_following_count_tv.setText(index + "");
        user_fragment_profile_follower_count_tv.setText(userItem.getFollowerList().size()+"");

    }
}
