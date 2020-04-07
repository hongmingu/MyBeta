package com.doitandroid.mybeta.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.doitandroid.mybeta.ConstantIntegers;
import com.doitandroid.mybeta.MainActivity;
import com.doitandroid.mybeta.R;
import com.doitandroid.mybeta.adapter.HomeReceivedAdapter;
import com.doitandroid.mybeta.itemclass.FeedItem;
import com.doitandroid.mybeta.itemclass.UserItem;
import com.doitandroid.mybeta.rest.APIInterface;
import com.doitandroid.mybeta.utils.InitializationOnDemandHolderIdiom;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeReceivedFragment extends Fragment {
    private static final String TAG = "HomeReceivedFragTAG";

    InitializationOnDemandHolderIdiom singleton = InitializationOnDemandHolderIdiom.getInstance();

    SwipeRefreshLayout home_received_srl;
    int retry;

    APIInterface apiInterface;

    ArrayList<UserItem> usingUserItemArrayList;
    RecyclerView recyclerView;
    ViewGroup rootView;
    boolean isLoading = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_home_received, container, false);

        home_received_srl = rootView.findViewById(R.id.home_received_srl);

        home_received_srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                clearHomeReceivedFeed();
                loadMore();
                //home_received_srl.setEnabled(true);
            }
        });

        apiInterface = singleton.apiInterface;

//        init_feed();


        recyclerView = rootView.findViewById(R.id.home_received_recyclerview);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        // 어댑터를 연결시킨다.
        singleton.homeReceivedAdapter = new HomeReceivedAdapter(singleton.receivedFeedList, ((MainActivity) getContext()));

        Log.d(TAG, singleton.receivedFeedList.size() + "");
        // 리사이클러뷰에 연결한다.
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(singleton.homeReceivedAdapter);

        recyclerView.setNestedScrollingEnabled(false);
//        clearHomeReceivedFeed();
//        loadMore();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                Log.d(TAG, "onScrolled");
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                // Toast.makeText(getActivity().getApplicationContext(), "Last", Toast.LENGTH_SHORT).show();

                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == singleton.receivedFeedList.size() - 1) {
//                            singleton.homeReceivedAdapter.notifyDataSetChanged();
//                        clearHomeReceivedFeed();

//                        isLoading = true;
                    }
                }
            }
        });

        clearHomeReceivedFeed();
        loadMore();


        return rootView;

        // return inflater.inflate(R.layout.fragment1, container, false);
        //return super.onCreateView(inflater, container, savedInstanceState);


    }


    public void clearHomeReceivedFeed() {
        singleton.receivedFeedList.clear();
        home_received_srl.setRefreshing(false);

        if (singleton.homeReceivedAdapter != null) {
            singleton.homeReceivedAdapter.notifyDataSetChanged();
        }
    }


    public void loadMore(){
        isLoading = true;

        retry = 0;
        Call<JsonObject> call;
        singleton.receivedFeedList.add(null);
        singleton.homeReceivedAdapter.notifyItemInserted(singleton.receivedFeedList.size() - 1);

        /*if (singleton.receivedFeedList.get(singleton.receivedFeedList.size()-1-1) != null && !singleton.receivedFeedList.get(singleton.receivedFeedList.size()-1-1).isList()){
            RequestBody requestEndID = RequestBody.create(MediaType.parse("multipart/form-data"), singleton.receivedFeedList.get(singleton.receivedFeedList.size()-1-1).getPostID());
            call = apiInterface.get_follow_feed(requestEndID);
        } else {
            call = apiInterface.get_follow_feed();
        }*/
        call = apiInterface.get_received_feed();

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                singleton.receivedFeedList.remove(singleton.receivedFeedList.size() - 1);
                int scrollPosition = singleton.receivedFeedList.size();
                singleton.homeReceivedAdapter.notifyItemRemoved(scrollPosition);
//                singleton.homeReceivedAdapter.notifyDataSetChanged();

                if (response.isSuccessful()) {
                    JsonObject jsonObject = response.body();
                    if (jsonObject != null) {
                        int rc = jsonObject.get("rc").getAsInt();
                        JsonArray content = jsonObject.get("content").getAsJsonArray();

                        if (rc != ConstantIntegers.SUCCEED_RESPONSE) {
                            // sign up 실패
                            call.cancel();
                            return;
                        }

                        // todo: 이제 feedItem 만들기. inflater 를 이용해야 할 것 같다.
                        Log.d(TAG, content.toString());
                        for (JsonElement feedElement: content){
                            JsonObject feedObject = feedElement.getAsJsonObject();
                            FeedItem feedItem = new FeedItem(feedObject);

                            boolean isExist = false;
                            for(FeedItem item : singleton.receivedFeedList){
                                if(!feedItem.isList()){
                                    if(feedItem.getPostID().equals(item.getPostID())){
                                        isExist = true;
                                    }
                                }
                            }
                            if(!isExist){
                                singleton.receivedFeedList.add(feedItem);
                                Log.d(TAG, "called");
                            }

                            Log.d(TAG, singleton.receivedFeedList.size() + "");


                        }
                        singleton.homeReceivedAdapter.notifyDataSetChanged();


                        // 접속 성공.

                    }

                } else {
                    Toast.makeText(getActivity(), "not is successful", Toast.LENGTH_SHORT).show();

                }
                isLoading = false;


            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(getActivity(), "onFailure", Toast.LENGTH_SHORT).show();


                if (retry < 4){
                    call.clone().enqueue(this);
                    retry ++;

                } else {
                    call.cancel();
                    isLoading = false;

                }
//                call.cancel();
//                init_feed();
//                ((MainActivity) getActivity()).progressOFF();

            }
        });

    }

    public void init_feed() {

        ((MainActivity) getActivity()).progressON((MainActivity) getActivity(), "loading...");
        singleton.receivedFeedList.clear();
        home_received_srl.setRefreshing(false);

        if (singleton.homeReceivedAdapter != null) {
            singleton.homeReceivedAdapter.notifyDataSetChanged();
        }


        //todo: retry 세팅 안되어있음 이쪽은.
        Call<JsonObject> call = apiInterface.get_received_feed();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject jsonObject = response.body();
                    if (jsonObject != null) {
                        int rc = jsonObject.get("rc").getAsInt();
                        JsonArray content = jsonObject.get("content").getAsJsonArray();

                        if (rc != ConstantIntegers.SUCCEED_RESPONSE) {
                            // sign up 실패

                            call.cancel();
                            home_received_srl.setRefreshing(false);
                            Toast.makeText(getActivity(), "not SUCCEEDRESPONSE", Toast.LENGTH_SHORT).show();

                            ((MainActivity) getActivity()).progressOFF();

                            return;
                        }

                        // todo: 이제 feedItem 만들기. inflater 를 이용해야 할 것 같다.
                        Log.d(TAG, content.toString());
                        for (JsonElement feedElement : content) {
                            JsonObject feedObject = feedElement.getAsJsonObject();
                            FeedItem feedItem = new FeedItem(feedObject);

                            boolean isExist = false;
                            for (FeedItem item : singleton.receivedFeedList) {
                                if (!feedItem.isList()) {
                                    if (feedItem.getPostID().equals(item.getPostID())) {
                                        isExist = true;
                                    }
                                }
                            }
                            if (!isExist) {
                                singleton.receivedFeedList.add(feedItem);
                                Log.d(TAG, "called");
                            }

                            Log.d(TAG, singleton.receivedFeedList.size() + "");


                        }
                        singleton.homeReceivedAdapter.notifyDataSetChanged();

                        // 접속 성공.

                    }

                } else {
                    Toast.makeText(getActivity(), "not is successful", Toast.LENGTH_SHORT).show();

                }
                ((MainActivity) getActivity()).progressOFF();


            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(getActivity(), "onFailure", Toast.LENGTH_SHORT).show();
                call.cancel();
                init_feed();
//                ((MainActivity) getActivity()).progressOFF();

            }
        });
    }

}
