package com.mobility.android.ui.profile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mobility.android.Config;
import com.mobility.android.R;
import com.mobility.android.data.network.RestClient;
import com.mobility.android.data.network.api.UserApi;
import com.mobility.android.data.network.model.User;
import com.mobility.android.ui.BaseActivity;
import com.mobility.android.util.TimeUtils;

import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created on 15.10.2016.
 *
 * @author Martin
 */

public class ProfileActivity extends BaseActivity {

    @BindView(R.id.profile_picture) CircleImageView profilePicture;
    @BindView(R.id.profile_name) TextView name;
    @BindView(R.id.profile_points) TextView points;
    @BindView(R.id.profile_avoided_co2) TextView avoidedCO2;
    @BindView(R.id.profile_reputation_bar) RatingBar ratingBar;
    @BindView(R.id.profile_num_ratings) TextView numRatings;
    @BindView(R.id.profile_driven_time) TextView drivenTime;
    @BindView(R.id.profile_borrowed_cars) TextView usedVehicles;
    @BindView(R.id.profile_rented_cars) TextView offeredVehicles;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.refresh) SwipeRefreshLayout mRefresh;

    private UserApi mApi;

    private FirebaseUser mUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        mApi = RestClient.ADAPTER.create(UserApi.class);
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener((v) -> finish());

        getSupportActionBar().setTitle(mUser.getDisplayName());

        mRefresh.setOnRefreshListener(this::loadUserInfo);
        mRefresh.setColorSchemeResources(Config.REFRESH_COLORS);

        loadUserInfo();
    }

    @Override
    protected int getNavItem() {
        return NAVDRAWER_ITEM_PROFILE;
    }

    private void loadUserInfo() {
        mRefresh.setRefreshing(true);

        mApi.getUser()
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<User>() {
                    @Override
                    public void onCompleted() {
                        mRefresh.setRefreshing(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mRefresh.setRefreshing(false);
                    }

                    @Override
                    public void onNext(User userModel) {
                        points.setText(String.valueOf(userModel.points));
                        avoidedCO2.setText(String.format(Locale.getDefault(), "%d grams", userModel.emissions));
                        ratingBar.setRating(userModel.averageRating);
                        numRatings.setText(String.valueOf(userModel.ratingsCount));

                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(TimeUtils.isoToDate(userModel.drivenTime));

                        usedVehicles.setText(String.valueOf(userModel.usedVehicles));
                        offeredVehicles.setText(String.valueOf(userModel.offeredVehicles));
                    }
                });


        Glide.with(this).load(mUser.getPhotoUrl()).into(profilePicture);
        name.setText(mUser.getDisplayName());
    }
}
