package com.mobility.android.ui.payment.client;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.mobility.android.R;
import com.mobility.android.data.model.Payment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChoosePaymentTypeFragment extends Fragment implements View.OnClickListener {

    @BindView(R.id.payment_button_type_credit_card) FrameLayout buttonCreditCard;
    @BindView(R.id.payment_button_type_cash) FrameLayout buttonCash;
    @BindView(R.id.payment_button_type_paypal) FrameLayout buttonPaypal;

    private Payment payment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_payment_choose_type, container, false);
        ButterKnife.bind(this, view);

        buttonCreditCard.setOnClickListener(this);
        buttonCash.setOnClickListener(this);
        buttonPaypal.setOnClickListener(this);

        payment = ((ClientPagerActivity) getActivity()).getPayment();

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.payment_button_type_credit_card:
                payment.setType(Payment.TYPE_CREDIT_CARD);
                break;
            case R.id.payment_button_type_cash:
                payment.setType(Payment.TYPE_CASH);
                break;
            case R.id.payment_button_type_paypal:
                payment.setType(Payment.TYPE_PAYPAL);
                break;
        }

        ((ClientPagerActivity) getActivity()).goToScreen(1);
    }
}
