package com.doitandroid.mybeta.utils;

import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.fragment.app.Fragment;

import com.doitandroid.mybeta.ConstantStrings;
import com.doitandroid.mybeta.adapter.HomeFollowAdapter;
import com.doitandroid.mybeta.adapter.HomeReceivedAdapter;
import com.doitandroid.mybeta.adapter.NotiAdapter;
import com.doitandroid.mybeta.itemclass.FeedItem;
import com.doitandroid.mybeta.itemclass.NotiItem;
import com.doitandroid.mybeta.itemclass.UserItem;
import com.doitandroid.mybeta.rest.APIInterface;
import com.doitandroid.mybeta.rest.LoggedInAPIClient;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class InitializationOnDemandHolderIdiom {

    public ArrayList<FeedItem> followFeedList, receivedFeedList;
    public ArrayList<UserItem> userList;
    public ArrayList<NotiItem> notiList;

    public ArrayList<Fragment> contentFragmentList;

    public APIInterface apiInterface, notiApiInterface;

    public HomeFollowAdapter homeFollowAdapter;
    public HomeReceivedAdapter homeReceivedAdapter;
    public NotiAdapter notiAdapter;

    public String profileUserID;


    public int accumulatedNum;

    private InitializationOnDemandHolderIdiom() {
        followFeedList = new ArrayList<>();
        receivedFeedList = new ArrayList<>();

        contentFragmentList = new ArrayList<>();
        userList = new ArrayList<>();
        notiList = new ArrayList<>();

        profileUserID = null;
        accumulatedNum = 0;
    }


    private static class Singleton {
        private static final InitializationOnDemandHolderIdiom instance = new InitializationOnDemandHolderIdiom();
    }

    public static InitializationOnDemandHolderIdiom getInstance() {
        System.out.println("create instance");
        return Singleton.instance;
    }


    public void setApiInterface(APIInterface apiInterface) {
        this.apiInterface = apiInterface;
    }
    public void setNotiApiInterface(APIInterface notiApiInterface) {
        this.notiApiInterface= notiApiInterface;
    }


    public void updateUserList(UserItem userItem, Boolean followUpdate) {
        boolean isUpdated = false;
        for (UserItem item : userList) {
            if (item.isSameUserItem(userItem)) {
                // 같은 것이 존재.
                item.updateItem(userItem, followUpdate);
                isUpdated = true;
            }
        }
        if (!isUpdated) {
            userList.add(userItem);
        }

    }

    public UserItem getUserItemFromSingletonByJsonObject(JsonObject jsonObject) {
        UserItem userItem = new UserItem(jsonObject);
        UserItem foundUserItem = findUserItemFromSingleton(userItem);
        if (foundUserItem != null) {
            return foundUserItem;
        } else {
            userList.add(userItem);
            return userItem;
        }
    }

    public UserItem getUserItemFromSingletonByUserItem(UserItem userItem) {
        UserItem foundUserItem = findUserItemFromSingleton(userItem);
        if (foundUserItem != null) {
            return foundUserItem;
        } else {
            userList.add(userItem);
            return userItem;
        }
    }


    public UserItem findUserItemFromSingleton(UserItem userItem) {
        for (UserItem item : userList) {
            if (item.isSameUserItem(userItem)) {
                updateUserList(userItem, false);
                return item;
            }
        }
        return null;
    }

    public UserItem getUserItemFromSingletonByUserID(String userID) {
        for (UserItem item : userList) {
            if (item.getUserID().equals(userID)) {
                return item;
            }
        }
        return null;
    }

    public UserItem getOrCreateUserItemFromSingletonByUserID(String userID) {
        for (UserItem item : userList) {
            if (item.getUserID().equals(userID)) {
                return item;
            }
        }

        UserItem tempUserItem = new UserItem("tempUsername",
                profileUserID,
                "tempFullName",
                "tempPhoto",
                new CopyOnWriteArrayList<UserItem>(),
                new CopyOnWriteArrayList<UserItem>(),
                false,
                false,
                false);
        return tempUserItem;
    }

    public void removeUserItemfromUserList(UserItem userItem) {
        for (UserItem item : userList) {
            if (item.isSameUserItem(userItem)) {
                userList.remove(item);
            }
        }
    }

    public String getProfileUserID() {
        return profileUserID;
    }

    public void setProfileUserID(String profileUserID) {
        this.profileUserID = profileUserID;
    }
}