package com.doitandroid.mybeta.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.doitandroid.mybeta.R;

import java.util.ArrayList;

public class SearchFragment extends Fragment implements View.OnClickListener {

    AppCompatImageView fragment_search_search_btn_iv;
    FragmentManager childFragmentManager;

    SearchDefaultFragment searchDefaultFragment;
    SearchFindFragment searchFindFragment;

    ArrayList<Fragment> fragments;
    boolean now;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_search, container, false);

        fragment_search_search_btn_iv = rootView.findViewById(R.id.fragment_search_search_btn);

        now = true;

        childFragmentManager = getChildFragmentManager();

        searchDefaultFragment = new SearchDefaultFragment();
        searchFindFragment = new SearchFindFragment();

        childFragmentManager.beginTransaction().add(R.id.fragment_search_child_container, searchFindFragment).commit();
        childFragmentManager.beginTransaction().add(R.id.fragment_search_child_container, searchDefaultFragment).commit();

        if (searchDefaultFragment == null) {
            searchDefaultFragment = new SearchDefaultFragment();
            childFragmentManager.beginTransaction().add(R.id.fragment_search_child_container, searchDefaultFragment).commit();
        }

        if (searchDefaultFragment != null) {
            childFragmentManager.beginTransaction().show(searchDefaultFragment).commit();
        }

        if (searchFindFragment!= null) {
            childFragmentManager.beginTransaction().hide(searchFindFragment).commit();
        }
        fragment_search_search_btn_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!now){
                    if (searchFindFragment == null) {
                        searchFindFragment = new SearchFindFragment();
                        childFragmentManager.beginTransaction().add(R.id.fragment_search_child_container, searchFindFragment).commit();
                    }
                    fragments = (ArrayList<Fragment>) childFragmentManager.getFragments();
                    for (Fragment fragment : fragments) {
                        if (fragment != null && fragment.isVisible()) {
                            childFragmentManager.beginTransaction().hide(fragment).commit();

                        }
                    }
                    if (searchFindFragment != null) {
                        childFragmentManager.beginTransaction().show(searchFindFragment).commit();
                    }
                    now = true;
                }
                else {
                    if (searchDefaultFragment == null) {
                        searchDefaultFragment = new SearchDefaultFragment();
                        childFragmentManager.beginTransaction().add(R.id.fragment_search_child_container, searchDefaultFragment).commit();
                    }
                    fragments = (ArrayList<Fragment>) childFragmentManager.getFragments();
                    for (Fragment fragment : fragments) {
                        if (fragment != null && fragment.isVisible()) {
                            childFragmentManager.beginTransaction().hide(fragment).commit();

                        }
                    }
                    if (searchFindFragment != null) {
                        childFragmentManager.beginTransaction().show(searchDefaultFragment).commit();
                    }
                    now = false;
                }

            }
        });

        return rootView;
    }

    @Override
    public void onClick(View v) {

    }

    private void setChildFragment(Fragment child) {
        FragmentTransaction childFt = getChildFragmentManager().beginTransaction();

        if (!child.isAdded()) {
            childFt.replace(R.id.fragment_search_child_container, child);
            childFt.addToBackStack(null);
            childFt.commit();
        }
    }
}
