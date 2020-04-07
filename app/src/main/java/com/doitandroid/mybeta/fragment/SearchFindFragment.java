package com.doitandroid.mybeta.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.doitandroid.mybeta.ConstantIntegers;
import com.doitandroid.mybeta.ConstantStrings;
import com.doitandroid.mybeta.R;
import com.doitandroid.mybeta.adapter.ContentListFollowerAdapter;
import com.doitandroid.mybeta.adapter.SearchResultAdapter;
import com.doitandroid.mybeta.itemclass.NotiItem;
import com.doitandroid.mybeta.itemclass.UserItem;
import com.doitandroid.mybeta.rest.APIInterface;
import com.doitandroid.mybeta.rest.ConstantREST;
import com.doitandroid.mybeta.rest.LoggedInAPIClient;
import com.doitandroid.mybeta.utils.InitializationOnDemandHolderIdiom;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class SearchFindFragment extends Fragment {

    private final static String TAG = "SearchFindFragment";

    InitializationOnDemandHolderIdiom singleton = InitializationOnDemandHolderIdiom.getInstance();

    APIInterface apiInterface;

    ArrayList<UserItem> userItemArrayList;
    SearchResultAdapter adapter;
    RecyclerView fragment_search_find_rv;

    View rootView;
    int retry;
    boolean isLoading = false;
    public static SearchFindFragment newInstance(){
        return new SearchFindFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_search_find, container, false);

        fragment_search_find_rv = rootView.findViewById(R.id.fragment_search_find_rv);
        userItemArrayList = new ArrayList<>();

        apiInterface = singleton.apiInterface;
        return rootView;
    }

    public void search(String searchWord){
        userItemArrayList.clear();

        if (adapter != null){
            adapter.notifyDataSetChanged();
        }
        RequestBody requestSearchWord = RequestBody.create(MediaType.parse("multipart/form-data"), searchWord);

        Call<JsonObject> call = apiInterface.search(requestSearchWord);
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
                        Log.d(TAG, contentArray.toString());

                        for(JsonElement jsonElement: contentArray){
                            JsonObject item = jsonElement.getAsJsonObject();
                            UserItem userItem = singleton.getUserItemFromSingletonByJsonObject(item);
                            userItemArrayList.add(userItem);

                        }

                        Log.d(TAG, "size: " + userItemArrayList.size() + "");


                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
                        // 어댑터를 연결시킨다.
                        adapter = new SearchResultAdapter(userItemArrayList, getActivity());

                        // 리사이클러뷰에 연결한다.
                        fragment_search_find_rv.setLayoutManager(layoutManager);
                        fragment_search_find_rv.setAdapter(adapter);

                        fragment_search_find_rv.setNestedScrollingEnabled(false);

                        adapter.notifyDataSetChanged();


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

    public void follow_user(String userID, final ImageView follow){
        RequestBody requestUserID = RequestBody.create(MediaType.parse("multipart/form-data"), userID);
        Call<JsonObject> call = apiInterface.follow(requestUserID);
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

                        // todo: 이제 feedItem 만들기. inflater 를 이용해야 할 것 같다.
                        if (jsonObject.get("content").getAsBoolean()){
                            // follow
                            follow.setBackground(getResources().getDrawable(R.drawable.bg_skyblue));

                        } else {
                            follow.setBackground(getResources().getDrawable(R.drawable.bg_darkblue_border_radius4dp));



                        }

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
