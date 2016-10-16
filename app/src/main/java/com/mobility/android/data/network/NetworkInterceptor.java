package com.mobility.android.data.network;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import com.mobility.android.BuildConfig;
import com.mobility.android.data.network.auth.AuthHelper;
import com.mobility.android.util.Utils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import timber.log.Timber;

class NetworkInterceptor implements Interceptor {

    private final Context mContext;

    NetworkInterceptor(Context context) {
        mContext = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();

        Request.Builder newRequest = originalRequest
                .newBuilder()
                .addHeader("User-Agent", "Mobility South Tyrol Android")
                .addHeader("X-Device", Build.MODEL)
                .addHeader("X-Language", Utils.locale(mContext))
                .addHeader("X-Serial", Build.SERIAL)
                .addHeader("X-Version-Code", String.valueOf(BuildConfig.VERSION_CODE))
                .addHeader("X-Version-Name", BuildConfig.VERSION_NAME);

        String uid = AuthHelper.getUserId();

        if (!TextUtils.isEmpty(uid)) {
            Timber.d("Authorization header: %s", uid);
            newRequest.addHeader("Authorization", uid);
        } else {
            Timber.e("Invalid auth header, skipping...");
        }

        Request request = newRequest.build();

        Timber.w("%s: %s", request.method(), originalRequest.url());

        return chain.proceed(request);
    }
}