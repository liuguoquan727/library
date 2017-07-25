package com.mdroid.view.viewpager;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class AutoScrollViewPager extends LoopViewPager {

  private long mDelay = 3000;
  private boolean mIsAutoScroll = true;
  private boolean mIsStopByTouch = false;

  private Runnable mScrollRunnable = new Runnable() {
    @Override public void run() {
      smoothToNext();
      if (mIsAutoScroll && !mIsStopByTouch) {
        postDelayed(this, mDelay);
      }
    }
  };

  public AutoScrollViewPager(Context context) {
    super(context);
  }

  public AutoScrollViewPager(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  /**
   * start auto scroll, first scroll delay time is
   */
  public void startAutoScroll() {
    mIsAutoScroll = true;
    removeCallbacks(mScrollRunnable);
    postDelayed(mScrollRunnable, mDelay);
  }

  /**
   * start auto scroll
   *
   * @param delayTimeInMills first scroll delay time
   */
  public void startAutoScroll(int delayTimeInMills) {
    mIsAutoScroll = true;
    removeCallbacks(mScrollRunnable);
    postDelayed(mScrollRunnable, delayTimeInMills);
  }

  /**
   * stop auto scroll
   */
  public void stopAutoScroll() {
    mIsAutoScroll = false;
    removeCallbacks(mScrollRunnable);
  }

  /**
   * scroll only once
   */
  public void smoothToNext() {
    int currentItem = getCurrentItem();
    setCurrentItem(currentItem + 1);
  }

  @Override protected void onDetachedFromWindow() {
    stopAutoScroll();
    super.onDetachedFromWindow();
  }

  /**
   * <ul>
   * if stopScrollWhenTouch is true
   * <li>if event is down, stop auto scroll.</li>
   * <li>if event is up, start auto scroll again.</li>
   * </ul>
   */
  @Override public boolean onTouchEvent(MotionEvent ev) {
    int action = MotionEventCompat.getActionMasked(ev);
    if ((action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL)
        && mIsStopByTouch) {
      mIsStopByTouch = false;
      startAutoScroll();
    } else if (mIsAutoScroll) {
      mIsStopByTouch = true;
      stopAutoScroll();
    }

    getParent().requestDisallowInterceptTouchEvent(true);
    return super.onTouchEvent(ev);
  }
}
