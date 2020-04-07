package com.doitandroid.mybeta.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.doitandroid.mybeta.ConstantIntegers;
import com.doitandroid.mybeta.ConstantStrings;
import com.doitandroid.mybeta.MainActivity;
import com.doitandroid.mybeta.R;
import com.doitandroid.mybeta.adapter.HomeFollowAdapter;
import com.doitandroid.mybeta.itemclass.FeedItem;
import com.doitandroid.mybeta.itemclass.UserItem;
import com.doitandroid.mybeta.rest.APIInterface;
import com.doitandroid.mybeta.rest.LoggedInAPIClient;
import com.doitandroid.mybeta.utils.InitializationOnDemandHolderIdiom;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class HomeFollowFragment extends Fragment {

    private static final String TAG = "HomeFollowFragTAG";

    InitializationOnDemandHolderIdiom singleton = InitializationOnDemandHolderIdiom.getInstance();

    SwipeRefreshLayout home_follow_srl;
    int retry;

    APIInterface apiInterface;

    ArrayList<UserItem> usingUserItemArrayList;

    ViewGroup rootView;

    RecyclerView recyclerView;
    boolean isLoading = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_home_follow, container, false);

        home_follow_srl = rootView.findViewById(R.id.home_follow_srl);

        home_follow_srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                clearHomeFollowFeed();

                loadMore();
                Toast.makeText(getContext(), "swipe refresh layout", Toast.LENGTH_SHORT).show();
                //home_received_srl.setEnabled(true);
            }
        });

        apiInterface = singleton.apiInterface;

        // init_feed();


        recyclerView = rootView.findViewById(R.id.home_follow_recyclerview);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        // 어댑터를 연결시킨다.
        singleton.homeFollowAdapter = new HomeFollowAdapter(singleton.followFeedList, ((MainActivity) getContext()));

        Log.d(TAG, singleton.followFeedList.size() + "");
        // 리사이클러뷰에 연결한다.
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(singleton.homeFollowAdapter);

        recyclerView.setNestedScrollingEnabled(false);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                // Toast.makeText(getActivity().getApplicationContext(), "Last", Toast.LENGTH_SHORT).show();

                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == singleton.followFeedList.size() - 1) {
//                        loadMore();
/*                        if(singleton.followFeedList.size()>0 && !singleton.followFeedList.get(singleton.followFeedList.size()-1).isList()){
                            //bottom of list!
                            singleton.homeFollowAdapter.notifyItemInserted(singleton.followFeedList.size() - 1);
//                            singleton.homeFollowAdapter.notifyDataSetChanged();



                        } else {
                            Toast.makeText(getActivity().getApplicationContext(), "Last", Toast.LENGTH_SHORT).show();

                        }*/

//                        isLoading = true;
                    }
                }
            }
        });
        clearHomeFollowFeed();
        loadMore();
        return rootView;
        // return inflater.inflate(R.layout.fragment1, container, false);
        //return super.onCreateView(inflater, container, savedInstanceState);


    }

    public void clearHomeFollowFeed() {
        singleton.followFeedList.clear();
        home_follow_srl.setRefreshing(false);

        if (singleton.homeFollowAdapter != null) {
            singleton.homeFollowAdapter.notifyDataSetChanged();
        }
    }


    public void loadMore(){
        isLoading = true;

        retry = 0;
        Call<JsonObject> call;
        singleton.followFeedList.add(null);
        singleton.homeFollowAdapter.notifyItemInserted(singleton.followFeedList.size() - 1);

        /*if (singleton.followFeedList.get(singleton.followFeedList.size()-1-1) != null && !singleton.followFeedList.get(singleton.followFeedList.size()-1-1).isList()){
            RequestBody requestEndID = RequestBody.create(MediaType.parse("multipart/form-data"), singleton.followFeedList.get(singleton.followFeedList.size()-1-1).getPostID());
            call = apiInterface.get_follow_feed(requestEndID);
        } else {
            call = apiInterface.get_follow_feed();
        }*/
        call = apiInterface.get_follow_feed();

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                singleton.followFeedList.remove(singleton.followFeedList.size() - 1);
                int scrollPosition = singleton.followFeedList.size();
                singleton.homeFollowAdapter.notifyItemRemoved(scrollPosition);
//                singleton.homeFollowAdapter.notifyDataSetChanged();

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
                            for(FeedItem item : singleton.followFeedList){
                                if(!feedItem.isList()){
                                    if(feedItem.getPostID().equals(item.getPostID())){
                                        isExist = true;
                                    }
                                }
                            }
                            if(!isExist){
                                singleton.followFeedList.add(feedItem);
                                Log.d(TAG, "called");
                            }

                            Log.d(TAG, singleton.followFeedList.size() + "");


                        }


                        singleton.homeFollowAdapter.notifyDataSetChanged();



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
    /**
        final Activity activity = (MainActivity)getActivity();
        Button button = rootView.findViewById(R.id.fragment1_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) activity).noti_btn_clicked();
            }
        });
     */
    public void init_feed(){

        ((MainActivity) getActivity()).progressON((MainActivity) getActivity(), "loading...");
        clearHomeFollowFeed();


        retry = 0;
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
                            home_follow_srl.setRefreshing(false);
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
                            for(FeedItem item : singleton.followFeedList){
                                if(!feedItem.isList()){
                                    if(feedItem.getPostID().equals(item.getPostID())){
                                        isExist = true;
                                    }
                                }
                            }
                            if(!isExist){
                                singleton.followFeedList.add(feedItem);
                                Log.d(TAG, "called");
                            }

                            Log.d(TAG, singleton.followFeedList.size() + "");


                        }
                        singleton.homeFollowAdapter.notifyDataSetChanged();

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


                if (retry < 4){
                    call.clone().enqueue(this);
                    retry ++;

                } else {
                    call.cancel();
                }
//                call.cancel();
//                init_feed();
//                ((MainActivity) getActivity()).progressOFF();

            }
        });
    }

    public void scrollToTop(){
        recyclerView.smoothScrollToPosition(0);
    }
    private APIInterface getApiInterface(){
        SharedPreferences sp = getActivity().getSharedPreferences(ConstantStrings.SP_INIT_APP, MODE_PRIVATE);
        String auth_token = sp.getString(ConstantStrings.SP_ARG_TOKEN, ConstantStrings.SP_ARG_REMOVE_TOKEN);
        APIInterface apiInterface = LoggedInAPIClient.getClient(auth_token).create(APIInterface.class);
        return apiInterface;
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
