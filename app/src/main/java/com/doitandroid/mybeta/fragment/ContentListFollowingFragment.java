package com.doitandroid.mybeta.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.doitandroid.mybeta.ConstantIntegers;
import com.doitandroid.mybeta.ConstantStrings;
import com.doitandroid.mybeta.R;
import com.doitandroid.mybeta.adapter.ContentListFollowerAdapter;
import com.doitandroid.mybeta.adapter.ContentListFollowingAdapter;
import com.doitandroid.mybeta.itemclass.UserItem;
import com.doitandroid.mybeta.rest.APIInterface;
import com.doitandroid.mybeta.rest.LoggedInAPIClient;
import com.doitandroid.mybeta.utils.InitializationOnDemandHolderIdiom;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContentListFollowingFragment extends Fragment {

    private static final String TAG = "CLFollowingFragmentTAG";

    APIInterface apiInterface;
    CopyOnWriteArrayList<UserItem> userItemArrayList;
    RecyclerView list_rv;

    Fragment fragment;

    ContentListFollowingAdapter adapter;


    InitializationOnDemandHolderIdiom singleton = InitializationOnDemandHolderIdiom.getInstance();

    /*
    public static ContentListFollowerFragment newInstance(){
        return new ContentListFollowerFragment();
    }*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_content_list_following, container, false);

        fragment = this;

        list_rv = view.findViewById(R.id.child_fragment_content_list_following_rv);
        setAdapter();

        return view;
    }

    public void getUser(String userID){
        RequestBody requestPostID = RequestBody.create(MediaType.parse("multipart/form-data"), userID);
        Call<JsonObject> call = apiInterface.getFollowing(requestPostID);
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
        adapter = new ContentListFollowingAdapter(userItemArrayList, getParentFragment().getContext(), getParentFragment());

        // 리사이클러뷰에 연결한다.
        list_rv.setLayoutManager(layoutManager);
        list_rv.setAdapter(adapter);

        list_rv.setNestedScrollingEnabled(false);

        adapter.notifyDataSetChanged();
    }
    public ContentListFollowingFragment(CopyOnWriteArrayList userItemArrayList) {
        this.apiInterface = singleton.apiInterface;
        this.userItemArrayList = userItemArrayList;

    }

}
