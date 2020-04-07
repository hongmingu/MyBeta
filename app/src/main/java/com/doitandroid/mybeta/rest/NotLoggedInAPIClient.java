package com.doitandroid.mybeta.rest;



import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NotLoggedInAPIClient {

    private static Retrofit retrofit;
    private static String base_url = ConstantREST.URL_HOME;

    public static Retrofit getClient() {

        HttpLoggingInterceptor log_interceptor = new HttpLoggingInterceptor();
        log_interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient
                .Builder()
                .connectTimeout(5, TimeUnit.MINUTES)
                .writeTimeout(5, TimeUnit.MINUTES) // write timeout
                .readTimeout(5, TimeUnit.MINUTES)
                .addInterceptor(log_interceptor)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(base_url)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        return retrofit;
    }
}

/*
* public void getComment(String postID){
        RequestBody requestPostID = RequestBody.create(MediaType.parse("multipart/form-data"), postID);
        Call<JsonObject> call = apiInterface.getComment(requestPostID);
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
                            CommentItem commentItem = new CommentItem(item);
                            commentItemArrayList.add(commentItem);

                        }

                        Log.d(TAG, "size: "+commentItemArrayList.size() + "");


                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                        // 어댑터를 연결시킨다.
                        commentAdapter = new CommentAdapter(commentItemArrayList, context);

                        // 리사이클러뷰에 연결한다.
                        comment_content_rv.setLayoutManager(layoutManager);
                        comment_content_rv.setAdapter(commentAdapter);

                        comment_content_rv.setNestedScrollingEnabled(false);

                        commentAdapter.notifyDataSetChanged();


                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                call.cancel();

            }
        });

    }
* */