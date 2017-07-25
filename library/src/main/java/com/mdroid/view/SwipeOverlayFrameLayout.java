package com.mdroid.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import com.mdroid.utils.AndroidUtils;
import com.mdroid.utils.Ln;

public class SwipeOverlayFrameLayout extends FrameLayout {
  private GestureDetector mGestureDetector;
  private Listener mListener;
  private float mMaxY;
  private float mMinX;
  private boolean mSwipeEnabled;
  private boolean mDisallowInterceptEnabled;
  private boolean mDisallowIntercept;

  public SwipeOverlayFrameLayout(final Context context) {
    super(context);
    init(context);
  }

  public SwipeOverlayFrameLayout(final Context context, final AttributeSet set) {
    super(context, set);
    init(context);
  }

  public SwipeOverlayFrameLayout(final Context context, final AttributeSet set, final int n) {
    super(context, set, n);
    init(context);
  }

  void init(final Context context) {
    mSwipeEnabled = true;
    mDisallowInterceptEnabled = true;
    mDisallowIntercept = false;

    final DefaultGestureDetectorListener detectorListener = new DefaultGestureDetectorListener();
    mMaxY = AndroidUtils.dp2px(context, 45f);
    mMinX = AndroidUtils.dp2px(context, 65f);
    mGestureDetector = new GestureDetector(context.getApplicationContext(), detectorListener);
    mGestureDetector.setIsLongpressEnabled(false);
  }

  boolean swipe(final MotionEvent e1, final MotionEvent e2, final float velocityX,
      float velocityY) {
    if (mListener == null) {
      return false;
    }
    final float adVelocityX = Math.abs(velocityX);
    final float adVelocityY = Math.abs(velocityY);
    final float adX = Math.abs(e2.getX() - e1.getX());
    final float adY = Math.abs(e2.getY() - e1.getY());
    if (adY <= mMaxY && adX > mMinX && adVelocityY < adVelocityX && adY < adX) {
      if (velocityX > 0.0f) {
        return mListener.swipeToLeft();
      } else if (velocityX < 0.0f) {
        return mListener.swipeToRight();
      }
    }
    return false;
  }

  public boolean dispatchTouchEvent(final MotionEvent ev) {
    if (ev.getAction() == MotionEvent.ACTION_DOWN) {
      mDisallowIntercept = false;
    }
    if (mSwipeEnabled
        && !(mDisallowIntercept && mDisallowInterceptEnabled)
        && mGestureDetector.onTouchEvent(ev)) {
      ev.setAction(MotionEvent.ACTION_CANCEL);
    }
    try {
      super.dispatchTouchEvent(ev);
    } catch (Throwable t) {
      Ln.w(t);
    }
    return true;
  }

  @Override public void requestDisallowInterceptTouchEvent(final boolean disallowIntercept) {
    super.requestDisallowInterceptTouchEvent(disallowIntercept);
    mDisallowIntercept = disallowIntercept;
  }

  public void setDisallowInterceptEnabled(final boolean enabled) {
    mDisallowInterceptEnabled = enabled;
  }

  public void setOnSwipeListener(final Listener listener) {
    mListener = listener;
  }

  public void setSwipeEnabled(final boolean enabled) {
    mSwipeEnabled = enabled;
  }

  public interface Listener {
    boolean swipeToLeft();

    boolean swipeToRight();
  }

  private class DefaultGestureDetectorListener extends GestureDetector.SimpleOnGestureListener {
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
      return swipe(e1, e2, velocityX, velocityY);
    }
  }
}
