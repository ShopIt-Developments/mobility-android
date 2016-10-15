package com.mobility.android.data.network;

import android.content.Context;

import com.trello.rxlifecycle.internal.Preconditions;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * The {@link Retrofit RestClient} which will be used to make all rest requests.
 * Uses {@link OkHttpClient} as networking client.
 *
 * @author Alex Lardschneider
 */
public final class RestClient {

    public static Retrofit ADAPTER;

    private RestClient() {
    }

    public static void init(Context context) {
        Preconditions.checkNotNull(context, "init context == null");

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .addNetworkInterceptor(new NetworkInterceptor(context))
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS);

        ADAPTER = new Retrofit.Builder()
                .baseUrl(Endpoint.API)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(builder.build())
                .build();
    }
}
