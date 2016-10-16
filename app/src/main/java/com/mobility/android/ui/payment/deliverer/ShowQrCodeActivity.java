package com.mobility.android.ui.payment.deliverer;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.zxing.EncodeHintType;
import com.mobility.android.R;
import com.mobility.android.ui.MainActivity;
import com.mobility.android.ui.profile.ProfileActivity;
import com.mobility.android.ui.vehicle.MyVehiclesActivity;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import net.glxn.qrgen.android.QRCode;

import java.text.NumberFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class ShowQrCodeActivity extends RxAppCompatActivity implements View.OnClickListener {

    public static final String EXTRA_QR_CODE = "com.mobility.android.EXTRA_QR_CODE";

    public static final String ACTION_PAYMENT_COMPLETE = "com.mobility.android.ACTION_PAYMENT_COMPLETE";
    public static final String EXTRA_PRICE = "com.mobility.android.EXTRA_PRICE";

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

    @BindView(R.id.payment_button_view_map) FrameLayout viewMap;
    @BindView(R.id.payment_button_view_vehicles) FrameLayout viewVehicles;
    @BindView(R.id.payment_button_view_profile) FrameLayout viewProfile;

    @BindView(R.id.toolbar) Toolbar toolbar;

    private boolean isCompleted;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Timber.e("Got %s broadcast", ACTION_PAYMENT_COMPLETE);

            float price = intent.getFloatExtra(EXTRA_PRICE, -1);
            if (price == -1) {
                Timber.e("Missing intent extra %s", EXTRA_PRICE);
            }

            showCompleteMenu(price);
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_payment_scan_code_deliverer);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        Intent intent = getIntent();
        String qrCodeString = intent.getStringExtra(EXTRA_QR_CODE);

        if (qrCodeString == null) {
            Timber.e("Missing intent extra %s", EXTRA_QR_CODE);
            finish();
            return;
        }

        ImageView qrCode = (ImageView) findViewById(R.id.payment_qr_code);
        qrCode.setOnClickListener((v) -> showCompleteMenu(4));

        int px = (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 156, getResources()
                .getDisplayMetrics()) + 0.5);

        Bitmap bitmap = QRCode.from(qrCodeString)
                .withSize(px, px)
                .withHint(EncodeHintType.MARGIN, 3)
                .withCharset("UTF-8")
                .bitmap();

        qrCode.setImageBitmap(bitmap);

        IntentFilter filter = new IntentFilter(ACTION_PAYMENT_COMPLETE);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);

        viewMap.setOnClickListener(this);
        viewVehicles.setOnClickListener(this);
        viewProfile.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_dark_close, menu);

        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.payment_button_view_map:
                startActivity(new Intent(this, MainActivity.class));
                finish();
                break;
            case R.id.payment_button_view_vehicles:
                startActivity(new Intent(this, MyVehiclesActivity.class));
                finish();
                break;
            case R.id.payment_button_view_profile:
                startActivity(new Intent(this, ProfileActivity.class));
                finish();
                break;
        }
    }


    private void showCompleteMenu(float price) {
        if (isCompleted) return;
        isCompleted = true;

        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.ITALY);
        completeSubtitle.setText("You were rewarded " + format.format(price));

        backgroundGreen.setVisibility(View.VISIBLE);

        toolbar.animate()
                .alpha(0)
                .setDuration(250)
                .start();

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

        int colorFrom = ContextCompat.getColor(this, R.color.dark_gray_light);
        int colorTo = ContextCompat.getColor(this, R.color.app_green_light);

        ValueAnimator color = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        color.setDuration(animTime(300));
        color.setStartDelay(animTime(200));
        color.addUpdateListener(animator -> backgroundGreen.setBackgroundColor((int) animator.getAnimatedValue()));
        color.start();

        colorFrom = ContextCompat.getColor(this, R.color.dark_gray_dark);
        colorTo = ContextCompat.getColor(this, R.color.app_green_dark);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
            colorAnimation.setDuration(animTime(400));
            colorAnimation.setInterpolator(new AccelerateInterpolator());

            colorAnimation.addUpdateListener(animator ->
                    getWindow().setStatusBarColor((int) animator.getAnimatedValue()));

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
                .setStartDelay(animTime(850))
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
