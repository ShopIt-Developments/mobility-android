package com.mobility.android.ui.vehicle;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobility.android.Config;
import com.mobility.android.R;
import com.mobility.android.data.network.RestClient;
import com.mobility.android.data.network.api.VehicleApi;
import com.mobility.android.data.network.model.VehicleObject;
import com.trello.rxlifecycle.components.support.RxFragment;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class BookedVehiclesFragment extends RxFragment {

    @BindView(R.id.refresh) SwipeRefreshLayout mRefresh;
    @BindView(R.id.activity_my_vehicles_recycler) RecyclerView mRecyclerView;

    private BookedVehiclesAdapter mAdapter;

    VehicleApi mApi;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_booked_vehicles, container, false);
        ButterKnife.bind(this, view);

        mAdapter = new BookedVehiclesAdapter(getActivity());

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setHasFixedSize(true);

        mRefresh.setOnRefreshListener(this::loadData);
        mRefresh.setColorSchemeResources(Config.REFRESH_COLORS);

        mApi = RestClient.ADAPTER.create(VehicleApi.class);

        loadData();

        return view;
    }

    private void loadData() {
        mRefresh.setRefreshing(true);

        mApi.getBookedVehicles()
                .compose(bindToLifecycle())
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
}
