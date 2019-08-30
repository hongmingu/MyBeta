package com.doitandroid.mybeta.rest;



import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClient {

    private static Retrofit retrofit;
    private static String test_url = ConstantREST.URL_HOME;

    public static Retrofit getClient() {

        Interceptor token_interceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                String token = "Token 513ac4bc0a81418ff70794452e55ae63ef0e6b06";

                Request request;
                request = chain.request().newBuilder().addHeader("Authorization", token).build();

                return chain.proceed(request);
            }
        };

        HttpLoggingInterceptor log_interceptor = new HttpLoggingInterceptor();
        log_interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient
                .Builder()
                .addInterceptor(log_interceptor)
                .addInterceptor(token_interceptor)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(test_url)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();



        return retrofit;
    }
}