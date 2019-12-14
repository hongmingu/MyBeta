package com.doitandroid.mybeta.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_noti, container, false);


        // todo: view 레이아웃 꾸민다. 아래 함수 만들고 겟 할때 서버에서 체크드 표시하기. 6h 등등까찌 표시한 후
        //  그 후엔 노티피케이션 클릭시 특수액티비티, 노티피케이션 알림은 같은채널에 중첩되게, 하나만 클릭해도 다 보이니까
        //  그리고 정규액티비티로 노티 화면, 근데 다른 팔로잉의 글을 볼 떄는 중첩하지 말고 뜨게 하되 누를 떄마다 뷰 액티비티에 따로 뜨게
        //  하자. 그런 후 거기서 컨텐트 액티비티로 넘어갈 수 있게 하면 될 것 같다.


        apiInterface = singleton.apiInterface;

        getNotice();

        RecyclerView recyclerView = rootView.findViewById(R.id.fragment_noti_recyclerview);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        // 어댑터를 연결시킨다.
        singleton.notiAdapter = new NotiAdapter(singleton.notiList, ((MainActivity) getContext()));

        Log.d(TAG, singleton.notiList.size() + "");
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
        singleton.notiList.clear();
        Call<JsonObject> call;
        if (endID != null){
            RequestBody requestEndID = RequestBody.create(MediaType.parse("multipart/form-data"), endID);
            call = apiInterface.getNotice(requestEndID);
        } else {
            call = apiInterface.getNotice();
        }

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

                        Log.d(TAG, "contentArray: " + contentArray.toString());
                        for (JsonElement notiElement: contentArray){
                            Log.d(TAG, "notiElement: "+notiElement.toString());
                            JsonObject itemObject = notiElement.getAsJsonObject();
                            Log.d(TAG, "itemObject: "+notiElement.toString());
                            NotiItem notiItem = new NotiItem(itemObject);

                            boolean isExist = false;
                            for(NotiItem item : singleton.notiList){
                                if(notiItem.getNoticeID().equals(item.getNoticeID())){
                                    isExist = true;
                                }
                            }
                            if(!isExist){
                                singleton.notiList.add(notiItem);
                                Log.d(TAG, "called");
                            }

                            Log.d(TAG, singleton.notiList.size() + "");


                        }
                        singleton.notiAdapter.notifyDataSetChanged();


                        // 접속 성공.
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                call.cancel();

            }
        });
    }


}
