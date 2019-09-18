package com.doitandroid.mybeta.rest;



import android.content.SharedPreferences;

import com.doitandroid.mybeta.ConstantIntegers;
import com.doitandroid.mybeta.ConstantStrings;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoggedInAPIClient {

    private static Retrofit retrofit;
    private static String base_url = ConstantREST.URL_HOME;

    public static Retrofit getClient(final String token) {

        Interceptor token_interceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                String auth_token = "Token "+ token ;

                Request request;
                request = chain.request().newBuilder().addHeader("Authorization", auth_token).build();

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
                .baseUrl(base_url)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();



        return retrofit;
    }
}