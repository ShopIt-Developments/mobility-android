package com.mobility.android.ui;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.mobility.android.R;
import com.mobility.android.ui.map.MapFragment;
import com.mobility.android.ui.vehicle.BookedVehiclesFragment;
import com.mobility.android.ui.widget.TabsAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {

    @BindView(R.id.viewpager) ViewPager mViewPager;
    @BindView(R.id.tabs) TabLayout mTabs;
    @BindView(R.id.toolbar) Toolbar mToolbar;

    private MapFragment mMapFragment;
    private BookedVehiclesFragment mBookedFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        getToolbar().setTitle("Map");
        getSupportActionBar().setTitle("Map");

        mMapFragment = new MapFragment();
        mBookedFragment = new BookedVehiclesFragment();

        TabsAdapter mAdapter = new TabsAdapter(getSupportFragmentManager());
        mAdapter.addFragment(mMapFragment, "Available");
        mAdapter.addFragment(mBookedFragment, "Booked");

        mViewPager.setAdapter(mAdapter);
        mTabs.setupWithViewPager(mViewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.map, menu);

        MenuItem menuItem = menu.findItem(R.id.action_refresh);

        Drawable drawable = menuItem.getIcon();
        if (drawable != null) {
            drawable.mutate();
            drawable.setColorFilter(ContextCompat.getColor(this, R.color.color_control), PorterDuff.Mode.SRC_ATOP);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                mMapFragment.parseData();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public int getNavItem() {
        return NAVDRAWER_ITEM_MAP;
    }
}
