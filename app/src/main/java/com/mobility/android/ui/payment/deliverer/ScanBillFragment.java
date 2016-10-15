package com.mobility.android.ui.payment.deliverer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.mobility.android.R;
import com.mobility.android.data.model.Payment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ScanBillFragment extends Fragment implements View.OnClickListener {

    @BindView(R.id.payment_fab_next) FloatingActionButton next;

    @BindView(R.id.payment_button_enter_price) FrameLayout enterPrice;
    @BindView(R.id.payment_button_enter_price_text) TextView priceText;

    private Payment payment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_payment_scan_bill, container, false);
        ButterKnife.bind(this, view);

        payment = ((DelivererPagerActivity) getActivity()).getPayment();

        next.setOnClickListener(this);
        enterPrice.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.payment_button_enter_price:
                break;
            case R.id.payment_fab_next:
                ((DelivererPagerActivity) getActivity()).goToScreen(1);
                break;
        }
    }
}
