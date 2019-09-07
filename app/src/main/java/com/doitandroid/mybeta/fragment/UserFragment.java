package com.doitandroid.mybeta.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;

import com.doitandroid.mybeta.MainActivity;
import com.doitandroid.mybeta.PingSearchActivity;
import com.doitandroid.mybeta.R;
import com.doitandroid.mybeta.SettingActivity;

public class UserFragment extends Fragment implements View.OnClickListener {


    AppCompatImageView setting_btn;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_user, container, false);
        setting_btn = rootView.findViewById(R.id.user_fragment_setting_btn);
        setting_btn.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.user_fragment_setting_btn:
                Toast.makeText(getActivity(), "toast", Toast.LENGTH_SHORT).show();


                Activity activity = getActivity();
                // 그리드뷰로 만든다.
                ((MainActivity) activity).openSettingActivity();
                break;
            default:
                break;
        }
    }
}
