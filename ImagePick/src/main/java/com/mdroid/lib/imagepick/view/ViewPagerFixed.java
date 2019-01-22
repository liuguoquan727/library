package com.mdroid.lib.imagepick.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import androidx.viewpager.widget.ViewPager;

public class ViewPagerFixed extends ViewPager {

  public ViewPagerFixed(Context context) {
    super(context);
  }

  public ViewPagerFixed(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override public boolean onTouchEvent(MotionEvent ev) {
    try {
      return super.onTouchEvent(ev);
    } catch (IllegalArgumentException ex) {
      Log.e("ViewPagerFixed", "", ex);
    }
    return false;
  }

  @Override public boolean onInterceptTouchEvent(MotionEvent ev) {
    try {
      return super.onInterceptTouchEvent(ev);
    } catch (IllegalArgumentException ex) {
      Log.e("ViewPagerFixed", "", ex);
    }
    return false;
  }
}