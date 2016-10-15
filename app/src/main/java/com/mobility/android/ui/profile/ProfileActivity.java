package com.mobility.android.ui.profile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mobility.android.Config;
import com.mobility.android.R;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import timber.log.Timber;

/**
 * Created on 15.10.2016.
 *
 * @author Martin
 */

public class ProfileActivity extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.profile_picture) CircleImageView profilePicture;
    @BindView(R.id.profile_name) TextView name;
    @BindView(R.id.profile_points) TextView points;
    @BindView(R.id.profile_refresh) SwipeRefreshLayout swipeRefreshLayout;

    private FirebaseUser mUser;

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
        Glide.with(this).load(mUser.getPhotoUrl()).into(profilePicture);
        name.setText(mUser.getDisplayName());
        points.setText(String.valueOf(new Random().nextInt(500)));

        swipeRefreshLayout.setRefreshing(false);
    }
}
