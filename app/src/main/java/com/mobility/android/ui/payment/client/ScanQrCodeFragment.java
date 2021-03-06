package com.mobility.android.ui.payment.client;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.text.Html;
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
import com.mobility.android.data.network.response.ScanResponse;
import com.mobility.android.ui.profile.ProfileActivity;
import com.mobility.android.ui.vehicle.MyVehiclesActivity;
import com.mobility.android.util.UIUtils;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class ScanQrCodeFragment extends Fragment implements BarcodeCallback, View.OnClickListener {

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

    @BindView(R.id.payment_button_leave_feedback) FrameLayout leaveFeedback;
    @BindView(R.id.payment_button_view_vehicles) FrameLayout viewVehicles;
    @BindView(R.id.payment_button_view_profile) FrameLayout viewProfile;

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

        leaveFeedback.setOnClickListener(this);
        viewVehicles.setOnClickListener(this);
        viewProfile.setOnClickListener(this);

        setupScanner();

        return view;
    }

    @Override
    public void barcodeResult(BarcodeResult result) {
        Timber.e("Got barcode result: %s", result.toString());

        Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(500);

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.payment_button_leave_feedback:
                showFeedbackDialog();
                break;
            case R.id.payment_button_view_vehicles:
                startActivity(new Intent(getActivity(), MyVehiclesActivity.class));
                getActivity().finish();
                break;
            case R.id.payment_button_view_profile:
                startActivity(new Intent(getActivity(), ProfileActivity.class));
                getActivity().finish();
                break;
        }
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
        api.scan(payment.getId(), scan)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ScanResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        dialog.dismiss();

                        UIUtils.okDialog(getActivity(), "Error", "Couldn't verify QR code.");
                    }

                    @Override
                    public void onNext(ScanResponse response) {
                        Timber.w("Sending QR code success");

                        dialog.dismiss();

                        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.ITALY);

                        String message = String.format("Confirm payment of <b>%s</b> for this booked vehicle?",
                                format.format(4));

                        UIUtils.okDialog(getActivity(), "Confirm payment", Html.fromHtml(message), (dialog1, which) -> {
                            approvePayment();
                        });
                    }
                });
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

    private void approvePayment() {
        Timber.w("Approving payment");

        ProgressDialog dialog = new ProgressDialog(getActivity(), R.style.DialogStyle);
        dialog.setMessage("Completing payment...");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();

        PaymentApi api = RestClient.ADAPTER.create(PaymentApi.class);
        api.accept(payment.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Void>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        dialog.dismiss();

                        //UIUtils.okDialog(getActivity(), "Error", "Couldn't accept payment.");
                    }

                    @Override
                    public void onNext(Void nothing) {
                        Timber.w("Approving payment successful");

                        dialog.dismiss();

                        showCompleteMenu();
                    }
                });
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

    private void showFeedbackDialog() {

    }

    private int animTime(int time) {
        return (int) (time * 0.8);
    }
}
