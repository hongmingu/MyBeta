package com.doitandroid.mybeta.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import com.doitandroid.mybeta.itemclass.NotiItem;
import com.doitandroid.mybeta.rest.APIInterface;
import com.doitandroid.mybeta.rest.LoggedInAPIClient;
import com.doitandroid.mybeta.utils.InitializationOnDemandHolderIdiom;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

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

    RecyclerView recyclerView;
    CoordinatorLayout fragment_noti_loading_cl;
    LottieAnimationView fragment_noti_loading_lav;

    Call<JsonObject> mainCall;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_noti, container, false);


        // todo: view 레이아웃 꾸민다. 아래 함수 만들고 겟 할때 서버에서 체크드 표시하기. 6h 등등까찌 표시한 후
        //  그 후엔 노티피케이션 클릭시 특수액티비티, 노티피케이션 알림은 같은채널에 중첩되게, 하나만 클릭해도 다 보이니까
        //  그리고 정규액티비티로 노티 화면, 근데 다른 팔로잉의 글을 볼 떄는 중첩하지 말고 뜨게 하되 누를 떄마다 뷰 액티비티에 따로 뜨게
        //  하자. 그런 후 거기서 컨텐트 액티비티로 넘어갈 수 있게 하면 될 것 같다.

        fragment_noti_loading_lav = rootView.findViewById(R.id.fragment_noti_loading_lav);
        fragment_noti_loading_cl = rootView.findViewById(R.id.fragment_noti_loading_cl);


        apiInterface = singleton.notiApiInterface;

        recyclerView = rootView.findViewById(R.id.fragment_noti_recyclerview);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        // 어댑터를 연결시킨다.
        singleton.notiAdapter = new NotiAdapter(singleton.notiList, ((MainActivity) getContext()));
        // 리사이클러뷰에 연결한다.
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(singleton.notiAdapter);

        recyclerView.setNestedScrollingEnabled(false);
        return rootView;
    }

    @Override
    public void onClick(View v) {

    }

    public void getNotice(){
        Log.d(TAG, "getnotice");

        if(mainCall != null){
            if(!mainCall.isCanceled()){
                return;
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
        if (mainCall.isCanceled()){
            getNotice();
            return;
        }

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

                Log.d(TAG, "onFailure");
                Log.d(TAG, t.toString());
                mainCall.cancel();
                call.cancel();


                getNotice();

            }
        });
    }


    public void cancelCall(){
    }

}
