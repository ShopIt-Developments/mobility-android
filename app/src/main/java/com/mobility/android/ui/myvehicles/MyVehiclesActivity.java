package com.mobility.android.ui.myvehicles;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mobility.android.Config;
import com.mobility.android.R;
import com.mobility.android.data.network.RestClient;
import com.mobility.android.data.network.api.VehiclesApi;
import com.mobility.android.data.network.model.VehicleObject;
import com.mobility.android.ui.BaseActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created on 15.10.2016.
 *
 * @author Martin
 */

public class MyVehiclesActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.refresh) SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.activity_my_cars_recycler) RecyclerView mRecyclerView;
    @BindView(R.id.activity_my_cars_add) FloatingActionButton mFab;
    private MyVehiclesAdapter mAdapter;
    VehiclesApi mApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_vehicles);
        ButterKnife.bind(this);

        mAdapter = new MyVehiclesAdapter();

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);

        mApi = RestClient.ADAPTER.create(VehiclesApi.class);

        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(Config.REFRESH_COLORS);

        mSwipeRefreshLayout.setRefreshing(true);
        loadData();
    }

    @Override
    protected int getNavItem() {
        return NAVDRAWER_ITEM_MYCARS;
    }

    @Override
    public void onRefresh() {
        loadData();
    }

    private void loadData() {
        mApi.getMyVehicles()
                .compose(bindToActivity())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<VehicleObject>>() {
                    @Override
                    public void onCompleted() {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onNext(List<VehicleObject> vehicles) {
                        mAdapter.setItems(vehicles);
                    }
                });
    }
}
