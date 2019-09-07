package com.doitandroid.mybeta.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;

import com.doitandroid.mybeta.R;

public class SearchFragment extends Fragment implements View.OnClickListener {


    AppCompatImageView setting_btn;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_search, container, false);
        setting_btn = rootView.findViewById(R.id.user_fragment_setting_btn);

        return rootView;
    }

    @Override
    public void onClick(View v) {

    }
}
