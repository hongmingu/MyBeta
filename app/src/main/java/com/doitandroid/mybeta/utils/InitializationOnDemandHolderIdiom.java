package com.doitandroid.mybeta.utils;

import com.doitandroid.mybeta.itemclass.FeedItem;

import java.util.ArrayList;

public class InitializationOnDemandHolderIdiom {
    public ArrayList<FeedItem> followFeedList, receivedFeedList;

    public ArrayList<String> homeFollowingList;

    private InitializationOnDemandHolderIdiom() {
    }

    private static class Singleton {
        private static final InitializationOnDemandHolderIdiom instance = new InitializationOnDemandHolderIdiom();
    }

    public static InitializationOnDemandHolderIdiom getInstance() {
        System.out.println("create instance");
        return Singleton.instance;
    }
}