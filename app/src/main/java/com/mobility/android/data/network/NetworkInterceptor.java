package com.mobility.android.data.network;

import android.content.Context;
import android.os.Build;

import com.mobility.android.BuildConfig;
import com.mobility.android.data.network.auth.AuthHelper;
import com.mobility.android.util.Utils;

import java.io.IOException;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import timber.log.Timber;

/**
 * Intercepts each call to the rest api and adds the user agent header to the request.
 * The user agent consists of the app name, followed by the app version name and code.
 * A token is needed to identify the user which made the request when trying to download trips.
 * The auth header is used to check if the request was made from a valid client and block
 * 3rd party users from using the api.
 *
 * @author Alex Lardschneider
 */
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
                .addHeader("User-Agent", "SasaBus Android")
                .addHeader("X-Device", Build.MODEL)
                .addHeader("X-Language", Utils.locale(mContext))
                .addHeader("X-Serial", Build.SERIAL)
                .addHeader("X-Version-Code", String.valueOf(BuildConfig.VERSION_CODE))
                .addHeader("X-Version-Name", BuildConfig.VERSION_NAME);

        if (requiresAuthHeader(originalRequest.url().encodedPathSegments())) {
            String token = AuthHelper.getTokenIfValid();

            if (token == null) {
                Timber.w("Token is invalid, skipping auth header");
            }
        }

        String uid = AuthHelper.getUserId(mContext);

        newRequest.addHeader("Authorization", uid != null ? uid : "");

        Request request = newRequest.build();

        Timber.w("%s: %s", request.method(), originalRequest.url());

        return chain.proceed(request);
    }

    private boolean requiresAuthHeader(List<String> segments) {
        String url = segments.get(1);

        //noinspection RedundantIfStatement
        if (url.equals("eco") || url.equals("sync")) {
            return true;
        }

        if (url.equals("auth")) {
            if (segments.get(2).equals("password")) return true;
            if (segments.get(2).equals("logout")) return true;
            if (segments.get(2).equals("delete")) return true;
        }

        return false;
    }
}