package com.doitandroid.mybeta.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.doitandroid.mybeta.R;

public class SearchDefaultFragment extends Fragment {

    public static SearchDefaultFragment newInstance(){
        return new SearchDefaultFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_default, container, false);

        return view;
    }
}
