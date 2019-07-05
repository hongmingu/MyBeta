package com.doitandroid.mybeta.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.doitandroid.mybeta.R;

public class MyFragment4 extends Fragment {
    //app 버전?


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment4, container, false);
        //return super.onCreateView(inflater, container, savedInstanceState);
    }
}
