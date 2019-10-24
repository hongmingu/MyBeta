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

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.widget.TextViewCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.doitandroid.mybeta.ConstantIntegers;
import com.doitandroid.mybeta.ConstantStrings;
import com.doitandroid.mybeta.R;
import com.doitandroid.mybeta.rest.APIInterface;
import com.doitandroid.mybeta.rest.ConstantREST;
import com.doitandroid.mybeta.rest.LoggedInAPIClient;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

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
    LinearLayoutCompat fragment_search_find_ll;
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

        fragment_search_find_ll = rootView.findViewById(R.id.fragment_search_find_ll);
        apiInterface = getApiInterface();
        return rootView;
    }

    public void search(String searchWord){
        fragment_search_find_ll.removeAllViews();
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

                        JsonArray jsonArray = jsonObject.get("content").getAsJsonArray();
                        Log.d(TAG, jsonArray.toString());

                        for (final JsonElement item: jsonArray){
                            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            View view = inflater.inflate(R.layout.search_user_item, fragment_search_find_ll, false);

                            CircleImageView circleImageView = view.findViewById(R.id.search_user_item_user_photo_civ);
                            Glide.with(getContext())
                                    .load((ConstantREST.URL_HOME).substring(0, ConstantREST.URL_HOME.length()-1) + item.getAsJsonObject().get("user_photo").getAsString())
                                    .into(circleImageView);

                            TextView fullName = view.findViewById(R.id.search_user_item_full_name_tv);
                            fullName.setText(item.getAsJsonObject().get("full_name").getAsString());
                            TextView username = view.findViewById(R.id.search_user_item_username_tv);
                            username.setText(item.getAsJsonObject().get("username").getAsString());
                            fragment_search_find_ll.addView(view);

                            final ImageView follow = view.findViewById(R.id.search_user_item_follow_iv);
                            if (item.getAsJsonObject().get("is_followed").getAsBoolean()){
                                //following
                                follow.setBackground(getResources().getDrawable(R.drawable.bg_skyblue));
                            } else {
                                follow.setBackground(getResources().getDrawable(R.drawable.bg_darkblue_border_radius4dp));

                            }
                            follow.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    follow_user(item.getAsJsonObject().get("user_id").getAsString(), follow);
                                }
                            });



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

    private APIInterface getApiInterface(){
        SharedPreferences sp = getActivity().getSharedPreferences(ConstantStrings.INIT_APP, MODE_PRIVATE);
        String auth_token = sp.getString(ConstantStrings.TOKEN, ConstantStrings.REMOVE_TOKEN);
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
