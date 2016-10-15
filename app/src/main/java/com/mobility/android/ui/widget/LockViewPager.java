package com.mobility.android.ui.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

import java.lang.reflect.Field;

public class LockViewPager extends ViewPager {

    private boolean pagingEnabled;

    public LockViewPager(Context context) {
        super(context);

        setCustomScroller();
    }

    public LockViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);

        setCustomScroller();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return pagingEnabled && super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return pagingEnabled && super.onTouchEvent(event);
    }

    public void setPagingEnabled(boolean enabled) {
        pagingEnabled = enabled;
    }

    private void setCustomScroller() {
        try {
            Class<?> viewpager = ViewPager.class;
            Field scroller = viewpager.getDeclaredField("mScroller");
            scroller.setAccessible(true);
            scroller.set(this, new MyScroller(getContext()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class MyScroller extends Scroller {

        MyScroller(Context context) {
            super(context, new DecelerateInterpolator());
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            super.startScroll(startX, startY, dx, dy, 500);
        }
    }
}