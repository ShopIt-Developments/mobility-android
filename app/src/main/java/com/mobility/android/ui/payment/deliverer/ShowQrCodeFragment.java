package com.mobility.android.ui.payment.deliverer;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.zxing.EncodeHintType;
import com.mobility.android.R;
import com.mobility.android.data.model.Payment;

import net.glxn.qrgen.android.QRCode;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShowQrCodeFragment extends Fragment {

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

    private Payment payment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_payment_scan_code_deliverer, container, false);
        ButterKnife.bind(this, view);

        ImageView qrCode = (ImageView) view.findViewById(R.id.payment_qr_code);
        qrCode.setOnClickListener(v -> showCompleteMenu());

        payment = ((DelivererPagerActivity) getActivity()).getPayment();

        int px = (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 156, getResources()
                .getDisplayMetrics()) + 0.5);

        int margin = (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources()
                .getDisplayMetrics()) + 0.5);

        Bitmap bitmap = QRCode.from("123412341234123412341234123412341234123412341234123412341234")
                .withSize(px, px)
                .withHint(EncodeHintType.MARGIN, margin)
                .withCharset("UTF-8")
                .bitmap();

        qrCode.setImageBitmap(bitmap);

        return view;
    }

    private void showCompleteMenu() {
        backgroundGreen.setVisibility(View.VISIBLE);

        ((DelivererPagerActivity) getActivity()).getToolbar().setAlpha(0);

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
                .setStartDelay(animTime(850))
                .alpha(1)
                .translationY(0)
                .setInterpolator(new DecelerateInterpolator())
                .setDuration(animTime(500))
                .start();
    }

    private int animTime(int time) {
        return time;
    }
}
