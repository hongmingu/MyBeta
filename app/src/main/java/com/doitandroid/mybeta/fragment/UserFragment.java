package com.doitandroid.mybeta.fragment;

import android.app.Activity;
import android.content.Context;
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
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.doitandroid.mybeta.ConstantStrings;
import com.doitandroid.mybeta.Cropper.CropImage;
import com.doitandroid.mybeta.Cropper.CropImageView;
import com.doitandroid.mybeta.MainActivity;
import com.doitandroid.mybeta.R;
import com.doitandroid.mybeta.rest.ConstantREST;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "UserFragment";

    AppCompatImageView user_fragment_setting_btn_iv, user_fragment_change_photo_iv;
    CircleImageView user_fragment_profile_photo_civ;

    TextView user_fragment_profile_username_tv, user_fragment_profile_full_name_tv, user_fragment_profile_email_tv;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_user, container, false);
        user_fragment_setting_btn_iv = rootView.findViewById(R.id.fragment_user_setting_btn);
        user_fragment_setting_btn_iv.setOnClickListener(this);

        user_fragment_change_photo_iv = rootView.findViewById(R.id.fragment_user_change_photo_iv);
        user_fragment_change_photo_iv.setOnClickListener(this);

        user_fragment_profile_full_name_tv = rootView.findViewById(R.id.fragment_user_full_name_tv);
        user_fragment_profile_username_tv = rootView.findViewById(R.id.fragment_user_username_tv);
        user_fragment_profile_email_tv = rootView.findViewById(R.id.fragment_user_email_tv);



        user_fragment_profile_photo_civ = rootView.findViewById(R.id.fragment_user_photo_civ);


        setProfile();

        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fragment_user_setting_btn:
                Toast.makeText(getActivity(), "toast", Toast.LENGTH_SHORT).show();


                Activity activity = getActivity();
                // 그리드뷰로 만든다.
                ((MainActivity) activity).openSettingActivity();
                break;
            case R.id.fragment_user_change_photo_iv:

                // 그리드뷰로 만든다.
                ((MainActivity) getActivity()).getCroppedImage();
                break;

            default:
                break;
        }
    }

    public void changePhoto(String profilePhoto){
        Glide.with(this)
                .load((ConstantREST.URL_HOME).substring(0, ConstantREST.URL_HOME.length()-1) + profilePhoto)
                .into(user_fragment_profile_photo_civ);
    }

    public void setProfile() {


        SharedPreferences sp = getActivity().getSharedPreferences(ConstantStrings.SP_INIT_APP, Context.MODE_PRIVATE);

        String profilePhoto = sp.getString(ConstantStrings.SP_ARG_PROFILE_PHOTO, ConstantStrings.SP_ARG_NONE);
        String profileUsername = sp.getString(ConstantStrings.SP_ARG_PROFILE_USERNAME, ConstantStrings.SP_ARG_NONE);
        String profileFullName= sp.getString(ConstantStrings.SP_ARG_PROFILE_FULLNAME, ConstantStrings.SP_ARG_NONE);
        String profileEmail= sp.getString(ConstantStrings.SP_ARG_PROFILE_EMAIL, ConstantStrings.SP_ARG_NONE);

        Log.d(TAG, profilePhoto);
        Glide.with(this)
                .load((ConstantREST.URL_HOME).substring(0, ConstantREST.URL_HOME.length()-1) + profilePhoto)

                .into(user_fragment_profile_photo_civ);

        user_fragment_profile_username_tv.setText(profileUsername);
        user_fragment_profile_full_name_tv.setText(profileFullName);
        user_fragment_profile_email_tv.setText(profileEmail);

        // todo: 이제 포토 올리는거, 나머지 셋텍스트.
    }
}
