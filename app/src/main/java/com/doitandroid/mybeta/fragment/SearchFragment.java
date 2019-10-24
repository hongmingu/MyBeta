package com.doitandroid.mybeta.fragment;

import android.app.Service;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.doitandroid.mybeta.R;

import java.util.ArrayList;

public class SearchFragment extends Fragment implements View.OnClickListener {

    AppCompatImageView fragment_search_search_iv, fragment_search_back_iv;
    AppCompatEditText fragment_search_et;
    FragmentManager childFragmentManager;

    SearchDefaultFragment searchDefaultFragment;
    SearchFindFragment searchFindFragment;


    InputMethodManager inputMethodManager;

    ArrayList<Fragment> fragments;
    boolean now;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_search, container, false);

        fragment_search_search_iv = rootView.findViewById(R.id.fragment_search_search_iv);
        fragment_search_back_iv = rootView.findViewById(R.id.fragment_search_back_iv);
        fragment_search_et = rootView.findViewById(R.id.fragment_search_et);

        fragment_search_search_iv.setOnClickListener(this);
        fragment_search_back_iv.setOnClickListener(this);


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

        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fragment_search_search_iv:
                fragment_search_back_iv.setVisibility(View.VISIBLE);
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
                inputMethodManager = (InputMethodManager)getActivity().getSystemService(Service.INPUT_METHOD_SERVICE);
                // imm.showSoftInput(fragment_search_et, 0);
                inputMethodManager.hideSoftInputFromWindow(fragment_search_et.getWindowToken(), 0);

                searchFindFragment.search(fragment_search_et.getText().toString());

                break;
            case R.id.fragment_search_back_iv:

                fragment_search_back_iv.setVisibility(View.GONE);

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

                inputMethodManager = (InputMethodManager)getActivity().getSystemService(Service.INPUT_METHOD_SERVICE);
                // imm.showSoftInput(fragment_search_et, 0);
                inputMethodManager.hideSoftInputFromWindow(fragment_search_et.getWindowToken(), 0);
                break;

            default:
                break;
        }

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
