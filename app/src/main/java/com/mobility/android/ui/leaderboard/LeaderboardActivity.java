package com.mobility.android.ui.leaderboard;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mobility.android.Config;
import com.mobility.android.R;
import com.mobility.android.data.network.RestClient;
import com.mobility.android.data.network.api.UserApi;
import com.mobility.android.data.network.model.User;
import com.mobility.android.ui.BaseActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created on 16.10.2016.
 *
 * @author Martin
 */

public class LeaderboardActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.activity_leaderboard_recycler) RecyclerView mRecycler;
    @BindView(R.id.refresh) SwipeRefreshLayout mRefresh;
    @BindView(R.id.activity_leaderboard_fab_sort) FloatingActionButton fabSort;

    private LeaderboardAdapter mAdapter;
    private UserApi mApi;

    private boolean sortByStars;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_leaderboard);
        ButterKnife.bind(this);

        mAdapter = new LeaderboardAdapter();

        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecycler.setHasFixedSize(true);
        mRecycler.setAdapter(mAdapter);

        mRefresh.setColorSchemeResources(Config.REFRESH_COLORS);
        mRefresh.setOnRefreshListener(this::loadLeaderBoard);

        mApi = RestClient.ADAPTER.create(UserApi.class);

        fabSort.setOnClickListener(this);

        loadLeaderBoard();
    }

    private void loadLeaderBoard() {
        mRefresh.setRefreshing(true);

        mApi.getAllUsers()
                .compose(bindToActivity())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<User>>() {
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
                    public void onNext(List<User> users) {
                        mAdapter.setItems(users);
                    }
                });
    }

    @Override
    protected int getNavItem() {
        return NAVDRAWER_ITEM_LEADERBOARD;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_leaderboard_fab_sort:
                fabSort.setImageResource(sortByStars ? R.drawable.ic_star_white_24dp : R.drawable.ic_unfold_more_white_24dp);
                mAdapter.sort();
                sortByStars = !sortByStars;
                break;
        }
    }
}
