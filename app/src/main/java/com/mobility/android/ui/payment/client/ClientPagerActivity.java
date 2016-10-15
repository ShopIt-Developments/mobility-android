package com.mobility.android.ui.payment.client;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import com.mobility.android.R;
import com.mobility.android.data.model.Payment;
import com.mobility.android.ui.widget.LockViewPager;
import com.mobility.android.ui.widget.TabsAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ClientPagerActivity extends AppCompatActivity {

    @BindView(R.id.viewpager) LockViewPager mViewPager;
    @BindView(R.id.toolbar) Toolbar toolbar;

    private ChoosePaymentTypeFragment choosePaymentTypeFragment;
    private ScanQrCodeClientFragment scanQrCodeFragment;

    private Payment payment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pager);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        payment = new Payment();

        choosePaymentTypeFragment = new ChoosePaymentTypeFragment();
        scanQrCodeFragment = new ScanQrCodeClientFragment();

        TabsAdapter mAdapter = new TabsAdapter(getSupportFragmentManager());
        mAdapter.addFragment(choosePaymentTypeFragment, null);
        mAdapter.addFragment(scanQrCodeFragment, null);

        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_dark_close, menu);

        return true;
    }

    public void goToScreen(int position) {
        mViewPager.setCurrentItem(position, true);
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public Payment getPayment() {
        return payment;
    }
}
