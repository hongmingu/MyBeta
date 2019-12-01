package com.doitandroid.mybeta.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.doitandroid.mybeta.R;

public class ContentListFollowingFragment extends Fragment {

    public static ContentListFollowingFragment newInstance(){
        return new ContentListFollowingFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_content_list_following, container, false);

        return view;
    }
}
