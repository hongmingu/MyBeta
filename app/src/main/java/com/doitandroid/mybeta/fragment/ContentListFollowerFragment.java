package com.doitandroid.mybeta.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.doitandroid.mybeta.ConstantIntegers;
import com.doitandroid.mybeta.ConstantStrings;
import com.doitandroid.mybeta.R;
import com.doitandroid.mybeta.adapter.ContentListFollowerAdapter;
import com.doitandroid.mybeta.adapter.ReactAdapter;
import com.doitandroid.mybeta.itemclass.NotiItem;
import com.doitandroid.mybeta.itemclass.ReactItem;
import com.doitandroid.mybeta.itemclass.UserItem;
import com.doitandroid.mybeta.rest.APIInterface;
import com.doitandroid.mybeta.rest.LoggedInAPIClient;
import com.doitandroid.mybeta.utils.InitializationOnDemandHolderIdiom;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContentListFollowerFragment extends Fragment {
    private static final String TAG = "CLFollowerFragmentTAG";

    APIInterface apiInterface;
    CopyOnWriteArrayList<UserItem> userItemArrayList;
    RecyclerView list_rv;

    ContentListFollowerAdapter adapter;

    InitializationOnDemandHolderIdiom singleton = InitializationOnDemandHolderIdiom.getInstance();
    int retry;
    boolean isLoading = false;
    /*public static ContentListFollowerFragment newInstance(){
        return new ContentListFollowerFragment();
    }*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_content_list_follower, container, false);
        list_rv = view.findViewById(R.id.child_fragment_content_list_follower_rv);
        setAdapter();
        return view;
    }

    public void getUser(String userID){
        RequestBody requestPostID = RequestBody.create(MediaType.parse("multipart/form-data"), userID);
        Call<JsonObject> call = apiInterface.getFollower(requestPostID);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject jsonObject = response.body();
                    if (jsonObject != null) {
                        int rc = jsonObject.get("rc").getAsInt();
                        if (rc != ConstantIntegers.SUCCEED_RESPONSE) {
                            // sign up 실패
                            call.cancel();
                            return;
                        }

                        JsonArray contentArray = jsonObject.get("content").getAsJsonArray();

                        for(JsonElement jsonElement: contentArray){
                            JsonObject item = jsonElement.getAsJsonObject();
                            UserItem userItem = singleton.getUserItemFromSingletonByJsonObject(item);

                            userItemArrayList.add(userItem);

                        }


                        setAdapter();


                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                call.cancel();

            }
        });



    }

    public void setUserItemArrayList(CopyOnWriteArrayList<UserItem> arrayList){
        this.userItemArrayList = arrayList;
    }
    public void setAdapter() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        // 어댑터를 연결시킨다.
        adapter = new ContentListFollowerAdapter(userItemArrayList, getParentFragment().getContext(), getParentFragment());

        // 리사이클러뷰에 연결한다.
        list_rv.setLayoutManager(layoutManager);
        list_rv.setAdapter(adapter);

        list_rv.setNestedScrollingEnabled(false);

        singleton.contentListFollowerAdapterList.add(adapter);

        adapter.notifyDataSetChanged();
    }

    public ContentListFollowerFragment(CopyOnWriteArrayList userItemArrayList) {
        this.apiInterface = singleton.apiInterface;
        this.userItemArrayList = userItemArrayList;
    }

    public void setOnScrollListener(RecyclerView recyclerView){
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
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == singleton.notiList.size() - 1) {
                        if(singleton.notiList.size() == 0){
                            //현재 아무것도 없음.
                            loadMore();
                        } else if (singleton.notiList.get(singleton.notiList.size() - 1 - 1) != null && !singleton.notiList.get(singleton.notiList.size() - 1 - 1).isEnd()) {
                            //현재 아무것도 없진 않은데 끝은 아닌 상태
                            loadMore();
                        } else {
                            //뭐든간 리스트 끝임
                            Toast.makeText(getActivity().getApplicationContext(), "Last", Toast.LENGTH_SHORT).show();
                        }
//                        isLoading = true;
                    }
                }
            }
        });
    }

    //todo: loadmore 처리
    public void loadMore() {
        isLoading = true;
        singleton.notiList.add(null);
        singleton.notiAdapter.notifyItemInserted(singleton.notiList.size() - 1);
        retry = 0;
        Call<JsonObject> call;
        if (singleton.notiList.size() - 1 - 1 < 0) {
            call = apiInterface.getNotice();
        } else if (singleton.notiList.get(singleton.notiList.size() - 1 - 1) != null && !singleton.notiList.get(singleton.notiList.size() - 1 - 1).isEnd()) {
            RequestBody requestEndID = RequestBody.create(MediaType.parse("multipart/form-data"), singleton.notiList.get(singleton.notiList.size() - 1 - 1).getNoticeID());
            call = apiInterface.getNotice(requestEndID);
        } else {
            call = apiInterface.getNotice();
        }
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (!singleton.notiList.isEmpty()){
                    singleton.notiList.remove(singleton.notiList.size() - 1);
                    int scrollPosition = singleton.notiList.size();
                    singleton.notiAdapter.notifyItemRemoved(scrollPosition);
                }

//                singleton.homeFollowAdapter.notifyDataSetChanged();

                if (response.isSuccessful()) {
                    JsonObject jsonObject = response.body();
                    if (jsonObject != null) {
                        int rc = jsonObject.get("rc").getAsInt();
                        if (rc != ConstantIntegers.SUCCEED_RESPONSE) {
                            // sign up 실패
                            call.cancel();
                            return;
                        }

                        JsonArray contentArray = jsonObject.get("content").getAsJsonArray();

                        for (JsonElement notiElement : contentArray) {
                            JsonObject itemObject = notiElement.getAsJsonObject();
                            NotiItem notiItem = new NotiItem(itemObject);

                            boolean isExist = false;
                            for (NotiItem item : singleton.notiList) {
                                if (notiItem.getNoticeID().equals(item.getNoticeID())) {
                                    isExist = true;
                                }
                            }
                            if (!isExist) {
                                singleton.notiList.add(notiItem);
                            }


                        }
                        singleton.notiAdapter.notifyDataSetChanged();


                        // 접속 성공.
                    }
                } else {

                    Toast.makeText(getActivity().getApplicationContext(), "failed", Toast.LENGTH_SHORT).show();
                }
                isLoading = false;


            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if (retry < 4) {
                    call.clone().enqueue(this);
                    retry++;
                } else {
                    call.cancel();
                    isLoading = false;
                }
            }
        });
    }
}
