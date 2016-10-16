package com.mobility.android.ui.map;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.transition.ArcMotion;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Switch;

import com.mobility.android.R;
import com.mobility.android.data.model.Filter;
import com.mobility.android.ui.widget.animation.MorphDialogToFab;
import com.mobility.android.ui.widget.animation.MorphFabToDialog;
import com.mobility.android.ui.widget.animation.SlideFadeAnimator;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FilterActivity extends AppCompatActivity {

    public static final String EXTRA_SHOW_BUSES = "com.mobility.android.EXTRA_SHOW_BUSES";
    public static final String EXTRA_SHOW_CARS = "com.mobility.android.EXTRA_SHOW_CARS";
    public static final String EXTRA_SHOW_BIKES = "com.mobility.android.EXTRA_SHOW_BIKES";
    public static final String EXTRA_FILTER_ENABLED = "com.mobility.android.EXTRA_FILTER_ENABLED";

    @BindView(R.id.filter_toolbar) FrameLayout mFilterToolbar;
    @BindView(R.id.filter_toggle) Switch mToggle;
    @BindView(R.id.recycler) RecyclerView mRecyclerView;

    private FilterAdapter mAdapter;

    private List<Filter> mItems = new ArrayList<>();

    private boolean showBuses;
    private boolean showCars;
    private boolean showBikes;

    private boolean mFilterEnabled;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_filter);
        ButterKnife.bind(this);

        Intent intent = getIntent();

        showBuses = intent.getBooleanExtra(EXTRA_SHOW_BUSES, true);
        showCars = intent.getBooleanExtra(EXTRA_SHOW_CARS, true);
        showBikes = intent.getBooleanExtra(EXTRA_SHOW_BIKES, true);

        mFilterEnabled = intent.getBooleanExtra(EXTRA_FILTER_ENABLED, true);

        mToggle.setChecked(mFilterEnabled);
        mToggle.setOnCheckedChangeListener((buttonView, isChecked) -> mFilterEnabled = isChecked);

        mAdapter = new FilterAdapter(this, mItems);

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new SlideFadeAnimator());

        ((SimpleItemAnimator) mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP &&
                !Build.MANUFACTURER.equalsIgnoreCase("batmobile")) {

            CardView frameLayout = (CardView) findViewById(R.id.filter_window);
            setupTransitions(frameLayout, getResources().getDimensionPixelSize(R.dimen.dialog_corners));
        }

        new Handler().postDelayed(() -> {
            mItems.add(new Filter("Show buses", showBuses));
            mItems.add(new Filter("Show cars", showCars));
            mItems.add(new Filter("Show bikes", showBikes));

            mAdapter.notifyItemRangeInserted(0, mItems.size());
        }, 500);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setupTransitions(View target, int dialogCornerRadius) {
        ArcMotion arcMotion = new ArcMotion();
        arcMotion.setMinimumHorizontalAngle(50f);
        arcMotion.setMinimumVerticalAngle(50f);

        int color = ContextCompat.getColor(this, R.color.accent);

        MorphFabToDialog sharedEnter = new MorphFabToDialog(color, dialogCornerRadius);
        sharedEnter.setPathMotion(arcMotion);
        sharedEnter.setInterpolator(new FastOutSlowInInterpolator());

        MorphDialogToFab sharedReturn = new MorphDialogToFab(color);
        sharedReturn.setPathMotion(arcMotion);
        sharedReturn.setInterpolator(new FastOutSlowInInterpolator());

        sharedEnter.addTarget(target);
        sharedReturn.addTarget(target);

        getWindow().setSharedElementEnterTransition(sharedEnter);
        getWindow().setSharedElementReturnTransition(sharedReturn);
    }

    @OnClick({R.id.filter_exit, R.id.filter_main})
    public void dismiss(View view) {
        Intent data = new Intent();
        data.putExtra(EXTRA_FILTER_ENABLED, mFilterEnabled);
        data.putExtra(EXTRA_SHOW_BUSES, showBuses);
        data.putExtra(EXTRA_SHOW_CARS, showCars);
        data.putExtra(EXTRA_SHOW_BIKES, showBikes);

        setResult(Activity.RESULT_OK, data);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            for (int i = 0; i < mFilterToolbar.getChildCount(); i++) {
                mFilterToolbar.getChildAt(i).animate()
                        .alpha(0)
                        .setStartDelay(0)
                        .setDuration(100)
                        .start();
            }

            int colorFrom = ContextCompat.getColor(this, R.color.card_background);
            int colorTo = ContextCompat.getColor(this, R.color.accent);
            ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
            colorAnimation.setDuration(250);
            colorAnimation.addUpdateListener(animator -> mRecyclerView.setBackgroundColor((int) animator.getAnimatedValue()));
            colorAnimation.start();

            new Handler().postDelayed(this::finishAfterTransition, 100);
        } else {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        dismiss(null);
    }
}
