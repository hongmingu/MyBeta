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
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.doitandroid.mybeta.ConstantIntegers;
import com.doitandroid.mybeta.ConstantStrings;
import com.doitandroid.mybeta.MainActivity;
import com.doitandroid.mybeta.R;
import com.doitandroid.mybeta.adapter.NotiAdapter;
import com.doitandroid.mybeta.itemclass.FeedItem;
import com.doitandroid.mybeta.itemclass.NotiItem;
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

public class NotiFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "NotiFragmentTAG";
    APIInterface apiInterface;
    String endID;
    InitializationOnDemandHolderIdiom singleton = InitializationOnDemandHolderIdiom.getInstance();

    ViewGroup rootView;
    RecyclerView recyclerView;

    int retry;
    boolean isLoading = false;
    Call<JsonObject> call;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_noti, container, false);


        // todo: view 레이아웃 꾸민다. 아래 함수 만들고 겟 할때 서버에서 체크드 표시하기. 6h 등등까찌 표시한 후
        //  그 후엔 노티피케이션 클릭시 특수액티비티, 노티피케이션 알림은 같은채널에 중첩되게, 하나만 클릭해도 다 보이니까
        //  그리고 정규액티비티로 노티 화면, 근데 다른 팔로잉의 글을 볼 떄는 중첩하지 말고 뜨게 하되 누를 떄마다 뷰 액티비티에 따로 뜨게
        //  하자. 그런 후 거기서 컨텐트 액티비티로 넘어갈 수 있게 하면 될 것 같다.


        apiInterface = singleton.notiApiInterface;

        recyclerView = rootView.findViewById(R.id.fragment_noti_recyclerview);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        // 어댑터를 연결시킨다.
        singleton.notiAdapter = new NotiAdapter(singleton.notiList, ((MainActivity) getContext()));
        // 리사이클러뷰에 연결한다.
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(singleton.notiAdapter);

        recyclerView.setNestedScrollingEnabled(false);

//        setOnScrollListener(recyclerView);

        return rootView;
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
                        if(singleton.notiList.size() == 1){
                            //현재 아무것도 없음.
//                            loadMore();
//                        } else if (singleton.notiList.get(singleton.notiList.size() - 1 - 1) != null && !singleton.notiList.get(singleton.notiList.size() - 1 - 1).isEnd()) {
                            //현재 아무것도 없진 않은데 끝은 아닌 상태
//                            loadMore();
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
    public void loadMore() {
        isLoading = true;
        singleton.notiList.add(null);
        singleton.notiAdapter.notifyItemInserted(singleton.notiList.size() - 1);
        retry = 0;
        if (apiInterface == null){
            apiInterface = singleton.notiApiInterface;
        }

        if (singleton.notiList.size() - 1 - 1 < 0) {
            // 아무것도 없는 상태
            call = apiInterface.getNotice();
        } else if (singleton.notiList.get(singleton.notiList.size() - 1 - 1) != null && !singleton.notiList.get(singleton.notiList.size() - 1 - 1).isEnd()) {
            // 아무것도 없는건 아닌데 끝은 아닌 상태
            RequestBody requestEndID = RequestBody.create(MediaType.parse("multipart/form-data"), singleton.notiList.get(singleton.notiList.size() - 1 - 1).getNoticeID());
            call = apiInterface.getNotice(requestEndID);
        } else {
            // 널값이 있는 상태
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

    @Override
    public void onClick(View v) {

    }

    public void clearCallAndList(){
        singleton.notiList.clear();
        singleton.notiAdapter.notifyDataSetChanged();
        loadMore();
    }
/*

    public void getNotice(){
        Log.d(TAG, "getnotice");

        if(mainCall != null){
            if(!mainCall.isCanceled()){
                mainCall.cancel();
            }
            mainCall = null;
        }
        Log.d(TAG, "getnotice");
        if(singleton.notiApiInterface != null){
            singleton.setNotiApiInterface(null);
            singleton.setNotiApiInterface(((MainActivity) getActivity()).getApiInterface());
            apiInterface = singleton.notiApiInterface;
        }

//        ((MainActivity) getActivity()).progressON((MainActivity) getActivity(), "loading...");


        singleton.notiList.clear();

        if(singleton.notiAdapter != null){
            singleton.notiAdapter.notifyDataSetChanged();
        }

        if (fragment_noti_loading_cl.getVisibility() != View.VISIBLE){
            fragment_noti_loading_cl.setVisibility(View.VISIBLE);
            if (!fragment_noti_loading_lav.isAnimating()) {
                fragment_noti_loading_lav.setMinAndMaxProgress(0f, 1f);
                fragment_noti_loading_lav.loop(true);
                fragment_noti_loading_lav.playAnimation();
            }
        }

        if (endID != null){
            RequestBody requestEndID = RequestBody.create(MediaType.parse("multipart/form-data"), endID);
            mainCall = apiInterface.getNotice(requestEndID);
        } else {
            mainCall = apiInterface.getNotice();
        }
        retry = 0;

        mainCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "onsuccessful");
                    JsonObject jsonObject = response.body();
                    if (jsonObject != null) {
                        int rc = jsonObject.get("rc").getAsInt();
                        if (rc != ConstantIntegers.SUCCEED_RESPONSE) {
                            mainCall.cancel();
                            // sign up 실패
                            call.cancel();
                            return;
                        }

                        JsonArray contentArray = jsonObject.get("content").getAsJsonArray();

                        for (JsonElement notiElement: contentArray){
                            JsonObject itemObject = notiElement.getAsJsonObject();
                            NotiItem notiItem = new NotiItem(itemObject);

                            boolean isExist = false;
                            for(NotiItem item : singleton.notiList){
                                if(notiItem.getNoticeID().equals(item.getNoticeID())){
                                    isExist = true;
                                }
                            }
                            if(!isExist){
                                singleton.notiList.add(notiItem);
                            }



                        }
                        singleton.notiAdapter.notifyDataSetChanged();


                        // 접속 성공.
                    }
                } else {

                }

                fragment_noti_loading_cl.setVisibility(View.GONE);
                fragment_noti_loading_lav.setMinAndMaxProgress(0f, 1f);
                fragment_noti_loading_lav.pauseAnimation();
                Log.d(TAG, "onresponse end");
                mainCall.cancel();

//                ((MainActivity) getActivity()).progressOFF();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

                if (retry < 4){
                    call.clone().enqueue(this);
                    retry++;
                } else {
                    call.cancel();
                }
                Log.d(TAG, "onFailure");
                Log.d(TAG, t.toString());
                call.clone().enqueue(this);

//                mainCall.cancel();
//                call.cancel();
//
//
//                getNotice();

            }
        });
    }
*/


}
