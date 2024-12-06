package com.example.foodMateFrontend;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final String BASE_URL = "http://10.0.2.2:8080/";
    private static Retrofit retrofit;

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            Gson gson = new GsonBuilder()
                    .setLenient() // 设置宽松模式
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(createCustomOkHttpClient()) // 添加自定义 OkHttpClient
                    .build();
        }
        return retrofit;
    }
    // 保留逻辑的同时引入自定义的 OkHttpClient
    private static OkHttpClient createCustomOkHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)  // 设置连接超时时间为 60 秒
                .readTimeout(120, TimeUnit.SECONDS)    // 设置读取超时时间为 120 秒
                .writeTimeout(120, TimeUnit.SECONDS)   // 设置写入超时时间为 120 秒
                .build();
    }


}