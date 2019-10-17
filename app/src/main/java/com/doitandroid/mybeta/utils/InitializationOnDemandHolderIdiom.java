package com.doitandroid.mybeta.utils;

import android.content.Context;

import androidx.fragment.app.Fragment;

import com.doitandroid.mybeta.adapter.HomeFollowAdapter;
import com.doitandroid.mybeta.itemclass.FeedItem;

import java.util.ArrayList;

public class InitializationOnDemandHolderIdiom {
    public ArrayList<FeedItem> followFeedList, receivedFeedList;

    public ArrayList<String> homeFollowingList;

    public ArrayList<Fragment> contentFragmentList;

    public HomeFollowAdapter homeFollowAdapter;
    public Context context;

    private InitializationOnDemandHolderIdiom() {
        followFeedList = new ArrayList<>();

        contentFragmentList = new ArrayList<>();
    }

    private static class Singleton {
        private static final InitializationOnDemandHolderIdiom instance = new InitializationOnDemandHolderIdiom();
    }

    public static InitializationOnDemandHolderIdiom getInstance() {
        System.out.println("create instance");
        return Singleton.instance;
    }
}