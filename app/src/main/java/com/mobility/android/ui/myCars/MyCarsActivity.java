package com.mobility.android.ui.myCars;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;

import com.mobility.android.Config;
import com.mobility.android.R;
import com.mobility.android.ui.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created on 15.10.2016.
 *
 * @author Martin
 */

public class MyCarsActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.refresh) SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_cars);
        ButterKnife.bind(this);

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(Config.REFRESH_COLORS);
    }

    @Override
    protected int getNavItem() {
        return NAVDRAWER_ITEM_MYCARS;
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(() -> swipeRefreshLayout.setRefreshing(false), 5000);
    }
}
