package com.mobility.android.ui.payment.client;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.zxing.ResultPoint;
import com.google.zxing.integration.android.IntentIntegrator;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.mobility.android.R;
import com.mobility.android.data.model.Payment;
import com.mobility.android.data.network.RestClient;
import com.mobility.android.data.network.api.PaymentApi;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class ScanQrCodeClientFragment extends Fragment implements BarcodeCallback {

    @BindView(R.id.payment_background_grey) FrameLayout backgroundGrey;
    @BindView(R.id.payment_background_green) FrameLayout backgroundGreen;

    @BindView(R.id.payment_circle_grey) FrameLayout circleGrey;
    @BindView(R.id.payment_circle_green) FrameLayout circleGreen;

    @BindView(R.id.payment_menu_qr_code) LinearLayout menuQrCode;
    @BindView(R.id.payment_menu_complete) LinearLayout menuComplete;

    @BindView(R.id.payment_menu_complete_card_1) FrameLayout card1;
    @BindView(R.id.payment_menu_complete_card_2) FrameLayout card2;

    @BindView(R.id.payment_menu_complete_title) TextView completeTitle;
    @BindView(R.id.payment_menu_complete_subtitle) TextView completeSubtitle;

    @BindView(R.id.zxing_barcode_scanner) DecoratedBarcodeView barcodeScannerView;

    private CaptureManager capture;

    private Payment payment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_payment_scan_code_client, container, false);
        ButterKnife.bind(this, view);

        FrameLayout scan = (FrameLayout) view.findViewById(R.id.payment_button_scan_qr_code);
        scan.setOnClickListener(v -> showCompleteMenu());

        payment = ((ClientPagerActivity) getActivity()).getPayment();

        setupScanner();

        return view;
    }

    @Override
    public void barcodeResult(BarcodeResult result) {
        Timber.e("Got barcode result: %s", result.toString());

        payment.setQrCode(result.getText());

        submitQrCodeAndPayment();
    }

    @Override
    public void possibleResultPoints(List<ResultPoint> resultPoints) {

    }

    @Override
    public void onResume() {
        super.onResume();
        capture.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        capture.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        capture.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        capture.onSaveInstanceState(outState);
    }


    private void submitQrCodeAndPayment() {
        Timber.w("Sending qr code and payment: %s", payment.toString());

        ProgressDialog dialog = new ProgressDialog(getActivity(), R.style.DialogStyle);
        dialog.setMessage("Checking code...");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();

        PaymentApi.PaymentScan scan = new PaymentApi.PaymentScan();
        scan.qrCode = payment.getQrCode();
        scan.type = payment.getType();

        PaymentApi api = RestClient.ADAPTER.create(PaymentApi.class);
        api.scan(payment.getOrderId(), scan)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Void>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Void nothing) {
                        Timber.w("Sending QR code success");
                    }
                });

        new Handler().postDelayed(() -> {
            dialog.dismiss();
            showCompleteMenu();
        }, 500);
    }

    private void setupScanner() {
        barcodeScannerView.decodeSingle(this);

        Intent scanIntent = new IntentIntegrator(getActivity())
                .setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES)
                .setBeepEnabled(true)
                .setOrientationLocked(true)
                .createScanIntent();

        capture = new CaptureManager(getActivity(), barcodeScannerView);
        capture.initializeFromIntent(scanIntent, null);
    }

    private void showCompleteMenu() {
        backgroundGreen.setVisibility(View.VISIBLE);

        ((ClientPagerActivity) getActivity()).getToolbar().setAlpha(0);

        int[] position = new int[2];
        circleGrey.getLocationOnScreen(position);

        int width = circleGrey.getWidth() / 2;
        int height = circleGrey.getHeight() / 2;

        int startX = (int) (circleGrey.getX() + width);
        int startY = (int) (circleGrey.getY() + height);

        int radiusX = backgroundGrey.getRight() - startX;
        int radiusY = backgroundGrey.getBottom() - startY;

        int radius = (int) Math.sqrt(Math.pow(radiusX, 2) + Math.pow(radiusY, 2));

        Animator reveal = ViewAnimationUtils.createCircularReveal(backgroundGreen, startX, startY,
                circleGrey.getWidth() / 2, radius);

        reveal.setInterpolator(new AccelerateInterpolator());
        reveal.setDuration(animTime(600));
        reveal.start();

        int colorFrom = ContextCompat.getColor(getActivity(), R.color.dark_gray_light);
        int colorTo = ContextCompat.getColor(getActivity(), R.color.app_green_light);

        ValueAnimator color = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        color.setDuration(animTime(300));
        color.setStartDelay(animTime(200));
        color.addUpdateListener(animator -> backgroundGreen.setBackgroundColor((int) animator.getAnimatedValue()));
        color.start();

        colorFrom = ContextCompat.getColor(getActivity(), R.color.dark_gray_dark);
        colorTo = ContextCompat.getColor(getActivity(), R.color.app_green_dark);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
            colorAnimation.setDuration(animTime(400));
            colorAnimation.setInterpolator(new AccelerateInterpolator());

            colorAnimation.addUpdateListener(animator ->
                    getActivity().getWindow().setStatusBarColor((int) animator.getAnimatedValue()));

            colorAnimation.start();
        }

        circleGrey.animate()
                .alpha(0)
                .setDuration(animTime(250))
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        circleGrey.setVisibility(View.GONE);
                    }
                })
                .start();

        circleGreen.setVisibility(View.VISIBLE);
        circleGreen.setAlpha(0);
        circleGreen.animate()
                .alpha(1)
                .setStartDelay(animTime(800))
                .setInterpolator(new FastOutSlowInInterpolator())
                .setDuration(animTime(1000))
                .start();

        menuQrCode.animate()
                .alpha(0)
                .setDuration(animTime(250))
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        menuQrCode.setVisibility(View.GONE);
                    }
                })
                .start();

        card1.setTranslationY(250);
        card2.setTranslationY(250);

        card1.setAlpha(0);
        card2.setAlpha(0);

        new Handler().postDelayed(() -> menuComplete.setVisibility(View.VISIBLE), animTime(500));

        card1.animate()
                .alpha(1)
                .translationY(0)
                .setStartDelay(animTime(600))
                .setInterpolator(new DecelerateInterpolator())
                .setDuration(animTime(600))
                .start();

        card2.animate()
                .alpha(1)
                .translationY(0)
                .setStartDelay(animTime(700))
                .setDuration(animTime(600))
                .setInterpolator(new DecelerateInterpolator())
                .start();

        completeTitle.setAlpha(0);
        completeSubtitle.setAlpha(0);

        completeTitle.setTranslationY(50);
        completeSubtitle.setTranslationY(50);

        completeTitle.animate()
                .setStartDelay(animTime(800))
                .alpha(1)
                .translationY(0)
                .setDuration(animTime(500))
                .setInterpolator(new DecelerateInterpolator())
                .start();

        completeSubtitle.animate()
                .setStartDelay(animTime(825))
                .alpha(1)
                .translationY(0)
                .setInterpolator(new DecelerateInterpolator())
                .setDuration(animTime(500))
                .start();
    }

    private int animTime(int time) {
        return (int) (time * 0.8);
    }
}
