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
import com.doitandroid.mybeta.itemclass.UserItem;
import com.doitandroid.mybeta.rest.APIInterface;
import com.doitandroid.mybeta.rest.ConstantREST;
import com.doitandroid.mybeta.rest.LoggedInAPIClient;
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

    APIInterface apiInterface;

    ArrayList<UserItem> userItemArrayList;
    SearchResultAdapter adapter;
    RecyclerView fragment_search_find_rv;

    View rootView;

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

        apiInterface = getApiInterface();
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
                            UserItem userItem = new UserItem(item);
                            userItemArrayList.add(userItem);

                        }

                        Log.d(TAG, "size: " + userItemArrayList.size() + "");


                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
                        // 어댑터를 연결시킨다.
                        adapter = new SearchResultAdapter(userItemArrayList, getParentFragment().getActivity());

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

    private APIInterface getApiInterface(){
        SharedPreferences sp = getActivity().getSharedPreferences(ConstantStrings.SP_INIT_APP, MODE_PRIVATE);
        String auth_token = sp.getString(ConstantStrings.SP_ARG_TOKEN, ConstantStrings.SP_ARG_REMOVE_TOKEN);
        APIInterface apiInterface = LoggedInAPIClient.getClient(auth_token).create(APIInterface.class);
        return apiInterface;
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


}
