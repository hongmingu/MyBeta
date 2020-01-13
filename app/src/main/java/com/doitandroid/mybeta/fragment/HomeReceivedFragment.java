package com.doitandroid.mybeta.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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
    TextView tv_count;

    APIInterface apiInterface;

    ArrayList<UserItem> usingUserItemArrayList;

    ViewGroup rootView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_home_received, container, false);

        home_received_srl = rootView.findViewById(R.id.home_received_srl);

        home_received_srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                init_feed();
                //home_received_srl.setEnabled(true);
            }
        });

        apiInterface = singleton.apiInterface;

        init_feed();


        RecyclerView recyclerView = rootView.findViewById(R.id.home_received_recyclerview);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        // 어댑터를 연결시킨다.
        singleton.homeReceivedAdapter = new HomeReceivedAdapter(singleton.receivedFeedList, ((MainActivity) getContext()));

        Log.d(TAG, singleton.receivedFeedList.size() + "");
        // 리사이클러뷰에 연결한다.
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(singleton.homeReceivedAdapter);

        recyclerView.setNestedScrollingEnabled(false);
        return rootView;

        // return inflater.inflate(R.layout.fragment1, container, false);
        //return super.onCreateView(inflater, container, savedInstanceState);


    }
    public void init_feed(){

        ((MainActivity) getActivity()).progressON((MainActivity) getActivity(), "loading...");
        singleton.receivedFeedList.clear();
        home_received_srl.setRefreshing(false);

        if (singleton.homeReceivedAdapter != null){
            singleton.homeReceivedAdapter.notifyDataSetChanged();
        }



        Call<JsonObject> call = apiInterface.get_follow_feed();
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
                        for (JsonElement feedElement: content){
                            JsonObject feedObject = feedElement.getAsJsonObject();
                            FeedItem feedItem = new FeedItem(feedObject);

                            boolean isExist = false;
                            for(FeedItem item : singleton.receivedFeedList){
                                if(feedItem.getPostID().equals(item.getPostID())){
                                    isExist = true;
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
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d("fragment1_STATE", "onSaveInstanceState");

/*        if (tv_count == null){
            tv_count = getView().findViewById(R.id.fragment1_counter);
        }

        int counter = Integer.parseInt(tv_count.getText().toString());
        outState.putInt("counter", counter);
        Log.d("fragment1_OSI", "outstate: "+counter);*/
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d("fragment1_STATE", "onAttach");

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("fragment1_STATE", "onCreate");

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("fragment1_STATE", "onActivityCreated");

    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("fragment1_STATE", "onStart");

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("fragment1_STATE", "onResume");

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("fragment1_STATE", "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("fragment1_STATE", "onStop");

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("fragment1_STATE", "onDestroyView");


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("fragment1_STATE", "onDestroy");

    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("fragment1_STATE", "onDetach");


    }

}
