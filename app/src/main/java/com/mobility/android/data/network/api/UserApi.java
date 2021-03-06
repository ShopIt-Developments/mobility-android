package com.mobility.android.data.network.api;

import com.mobility.android.data.network.Endpoint;
import com.mobility.android.data.network.model.User;

import java.util.List;

import retrofit2.http.GET;
import rx.Observable;

/**
 * Created on 16.10.2016.
 *
 * @author Martin
 */

public interface UserApi {

    @GET(Endpoint.USER)
    Observable<User> getUser();

    @GET(Endpoint.LEADERBOARD)
    Observable<List<User>> getAllUsers();

}
