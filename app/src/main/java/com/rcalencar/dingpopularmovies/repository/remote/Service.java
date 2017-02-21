package com.rcalencar.dingpopularmovies.repository.remote;

import com.rcalencar.dingpopularmovies.BuildConfig;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class Service {
    public static final String API_KEY = "api_key";

    public static <S> S createService(String url, Class<S> serviceClass) {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        Retrofit.Builder builder = new Retrofit.Builder().baseUrl(url).addConverterFactory(GsonConverterFactory.create());

        httpClient.addInterceptor(chain -> {
            Request original = chain.request();
            HttpUrl originalHttpUrl = original.url();
            HttpUrl newUrl = originalHttpUrl.newBuilder()
                    .addQueryParameter(API_KEY, BuildConfig.API_KEY)
                    .build();
            Request.Builder requestBuilder = original.newBuilder()
                    .url(newUrl);
            Request request = requestBuilder.build();
            return chain.proceed(request);
        });

        OkHttpClient client = httpClient.build();
        Retrofit retrofit = builder.addCallAdapterFactory(RxJavaCallAdapterFactory.create()).client(client).build();
        return retrofit.create(serviceClass);
    }
}