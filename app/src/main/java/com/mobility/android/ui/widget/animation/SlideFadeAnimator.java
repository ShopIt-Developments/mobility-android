package com.mobility.android.ui.widget.animation;

import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;

import jp.wasabeef.recyclerview.animators.BaseItemAnimator;

public class SlideFadeAnimator extends BaseItemAnimator {

    public SlideFadeAnimator() {
    }

    public SlideFadeAnimator(int duration) {
        setAddDuration(duration);
    }

    @Override
    protected void animateRemoveImpl(RecyclerView.ViewHolder holder) {
        ViewCompat.animate(holder.itemView)
                .alpha(0)
                .setDuration(getRemoveDuration())
                .setInterpolator(mInterpolator)
                .setListener(new DefaultRemoveVpaListener(holder))
                .setStartDelay(getRemoveDelay(holder))
                .start();
    }

    @Override
    protected void preAnimateAddImpl(RecyclerView.ViewHolder holder) {
        ViewCompat.setTranslationY(holder.itemView, holder.itemView.getHeight());
        ViewCompat.setAlpha(holder.itemView, 0);
    }

    @Override
    protected void animateAddImpl(RecyclerView.ViewHolder holder) {
        ViewCompat.animate(holder.itemView)
                .translationY(0)
                .alpha(1)
                .setDuration(getAddDuration())
                .setInterpolator(mInterpolator)
                .setListener(new DefaultAddVpaListener(holder))
                .setStartDelay(getAddDelay(holder))
                .start();
    }

    @Override
    public boolean animateChange(RecyclerView.ViewHolder oldHolder, RecyclerView.ViewHolder newHolder,
                                 int fromX, int fromY, int toX, int toY) {
        return true;
    }
}