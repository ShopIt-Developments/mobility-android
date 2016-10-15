package com.mobility.android.ui.profile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mobility.android.Config;
import com.mobility.android.R;
import com.mobility.android.data.network.RestClient;
import com.mobility.android.data.network.api.UserApi;
import com.mobility.android.data.network.model.UserModel;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created on 15.10.2016.
 *
 * @author Martin
 */

public class ProfileActivity extends RxAppCompatActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.profile_picture) CircleImageView profilePicture;
    @BindView(R.id.profile_name) TextView name;
    @BindView(R.id.profile_points) TextView points;
    @BindView(R.id.profile_refresh) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.profile_avoided_co2) TextView avoidedCO2;
    @BindView(R.id.profile_reputation) TextView reputation;

    private FirebaseUser mUser;

    private UserApi mApi;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        mUser = FirebaseAuth.getInstance().getCurrentUser();

        if (mUser == null) {
            Timber.e("User is not logged in!");
            return;
        }

        mApi = RestClient.ADAPTER.create(UserApi.class);

        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener((v) -> finish());

        getSupportActionBar().setTitle(mUser.getDisplayName());

        swipeRefreshLayout.setOnRefreshListener(this::loadUserInfo);
        swipeRefreshLayout.setColorSchemeResources(Config.REFRESH_COLORS);

        swipeRefreshLayout.setRefreshing(true);
        loadUserInfo();
    }

    private void loadUserInfo() {
        swipeRefreshLayout.setRefreshing(true);

        mApi.getUser()
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UserModel>() {
                    @Override
                    public void onCompleted() {
                        swipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        swipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onNext(UserModel userModel) {
                        points.setText(String.valueOf(userModel.points));
                        avoidedCO2.setText(String.format(Locale.getDefault(), "%d grams", userModel.emissions));
                        reputation.setText(String.valueOf(userModel.averageRating));
                    }
                });


        Glide.with(this).load(mUser.getPhotoUrl()).into(profilePicture);
        name.setText(mUser.getDisplayName());
    }
}
