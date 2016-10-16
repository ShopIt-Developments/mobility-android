package com.mobility.android.ui.vehicle;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mobility.android.Config;
import com.mobility.android.R;
import com.mobility.android.data.network.RestClient;
import com.mobility.android.data.network.api.VehicleApi;
import com.mobility.android.data.network.model.VehicleObject;
import com.mobility.android.ui.BaseActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MyVehiclesActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.refresh) SwipeRefreshLayout mRefresh;
    @BindView(R.id.activity_my_vehicles_recycler) RecyclerView mRecyclerView;
    @BindView(R.id.activity_my_vehicles_add) FloatingActionButton mFab;

    private VehiclesAdapter mAdapter;

    private VehicleApi mApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_vehicles);
        ButterKnife.bind(this);

        mAdapter = new VehiclesAdapter(this);

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);

        mApi = RestClient.ADAPTER.create(VehicleApi.class);

        mFab.setOnClickListener(this);

        mRefresh.setOnRefreshListener(this::loadData);
        mRefresh.setColorSchemeResources(Config.REFRESH_COLORS);

        mRefresh.setRefreshing(true);
        loadData();
    }

    @Override
    protected int getNavItem() {
        return NAVDRAWER_ITEM_MY_VEHICLES;
    }

    @Override
    public void onClick(View v) {
        startActivity(new Intent(this, AddVehicleActivity.class));
    }

    private void loadData() {
        mRefresh.setRefreshing(true);

        mApi.getMyVehicles()
                .compose(bindToActivity())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<VehicleObject>>() {
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
                    public void onNext(List<VehicleObject> vehicles) {
                        mAdapter.setItems(vehicles);
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }
}
