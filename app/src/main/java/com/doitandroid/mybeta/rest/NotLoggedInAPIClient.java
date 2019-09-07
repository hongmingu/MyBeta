package com.doitandroid.mybeta.rest;



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