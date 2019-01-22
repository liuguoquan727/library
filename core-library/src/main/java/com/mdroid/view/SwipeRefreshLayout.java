/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mdroid.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.widget.AbsListView;
import androidx.core.view.MotionEventCompat;
import androidx.core.view.NestedScrollingChild;
import androidx.core.view.NestedScrollingChildHelper;
import androidx.core.view.NestedScrollingParent;
import androidx.core.view.NestedScrollingParentHelper;
import androidx.core.view.ViewCompat;
import com.mdroid.utils.ReflectUtils;

/**
 * The SwipeRefreshLayout should be used whenever the user can refresh the
 * contents of a view via a vertical swipe gesture. The activity that
 * instantiates this view should add an OnRefreshListener to be notified
 * whenever the swipe to refresh gesture is completed. The SwipeRefreshLayout
 * will notify the listener each and every time the gesture is completed again;
 * the listener is responsible for correctly determining when to actually
 * initiate a refresh of its content. If the listener determines there should
 * not be a refresh, it must call setRefreshing(false) to cancel any visual
 * indication of a refresh. If an activity wishes to show just the progress
 * animation, it should call setRefreshing(true). To disable the gesture and progress
 * animation, call setEnabled(false) on the view.
 * <p>
 * <p> This layout should be made the parent of the view that will be refreshed as a
 * result of the gesture and can only support one direct child. This view will
 * also be made the target of the gesture and will be forced to match both the
 * width and the height supplied in this layout. The SwipeRefreshLayout does not
 * provide accessibility events; instead, a menu item must be provided to allow
 * refresh of the content wherever this gesture is used.</p>
 */
public class SwipeRefreshLayout extends ViewGroup
    implements NestedScrollingParent, NestedScrollingChild {
  protected static final float MAX_SWIPE_DISTANCE_FACTOR = .6f;
  protected static final int REFRESH_TRIGGER_DISTANCE = 120;
  protected static final float DISTANCE_SCALE = .5f;
  private static final String LOG_TAG = SwipeRefreshLayout.class.getSimpleName();
  private static final float ACCELERATE_INTERPOLATION_FACTOR = 1.5f;
  private static final float DECELERATE_INTERPOLATION_FACTOR = 2f;
  private static final int INVALID_POINTER = -1;
  private static final int[] LAYOUT_ATTRS = new int[] {
      android.R.attr.enabled
  };
  private final NestedScrollingParentHelper mNestedScrollingParentHelper;
  private final NestedScrollingChildHelper mNestedScrollingChildHelper;
  private final int[] mParentScrollConsumed = new int[2];
  private final int[] mParentOffsetInWindow = new int[2];
  private final DecelerateInterpolator mDecelerateInterpolator;
  private final AccelerateInterpolator mAccelerateInterpolator;
  protected View mTarget; //the content that gets pulled down
  protected int mOriginalOffsetTop;
  protected boolean mIsLockDistance;
  protected float mDistanceScale = DISTANCE_SCALE;
  protected float mDistanceToTriggerSync = -1;
  // If nested scrolling is enabled, the total amount that needed to be
  // consumed by this as the nested scrolling parent is used in place of the
  // overscroll determined by MOVE events in the onTouch handler
  private float mTotalUnconsumed;
  private boolean mNestedScrollInProgress;
  private OnRefreshListener mListener;
  private int mFrom;
  private boolean mRefreshing = false;
  private int mTouchSlop;
  private int mMediumAnimationDuration;
  private float mCurrPercentage = 0;
  private final AnimationListener mShrinkAnimationListener = new BaseAnimationListener() {
    @Override public void onAnimationEnd(Animation animation) {
      mCurrPercentage = 0;
    }
  };
  private int mCurrentTargetOffsetTop;
  private final Animation mAnimateToStartPosition = new Animation() {
    @Override public void applyTransformation(float interpolatedTime, Transformation t) {
      int targetTop = 0;
      if (mFrom != mOriginalOffsetTop) {
        targetTop = (mFrom + (int) ((mOriginalOffsetTop - mFrom) * interpolatedTime));
      }
      int offset = targetTop - mTarget.getTop();
      final int currentTop = mTarget.getTop();
      if (offset + currentTop < 0) {
        offset = 0 - currentTop;
      }
      setTargetOffsetTopAndBottom(offset);
    }
  };
  private final AnimationListener mReturnToStartPositionListener = new BaseAnimationListener() {
    @Override public void onAnimationEnd(Animation animation) {
      // Once the target content has returned to its start position, reset
      // the target offset to 0
      mCurrentTargetOffsetTop = 0;
    }
  };
  private float mInitialMotionY;
  private float mLastMotionY;
  private boolean mIsBeingDragged;
  private boolean mIsRefreshable;
  private int mActivePointerId = INVALID_POINTER;
  // Target is returning to its start offset because it was cancelled or a refresh was triggered.
  private boolean mReturningToStart;
  private final Runnable mReturnToStartPosition = new Runnable() {

    @Override public void run() {
      mReturningToStart = true;
      animateOffsetToStartPosition(mCurrentTargetOffsetTop + getPaddingTop(),
          mReturnToStartPositionListener);
    }
  };
  private Animation mShrinkTrigger = new Animation() {
    @Override public void applyTransformation(float interpolatedTime, Transformation t) {
      mListener.onProgress(mCurrPercentage);
    }
  };
  // Cancel the refresh gesture and animate everything back to its original state.
  private final Runnable mCancel = new Runnable() {

    @Override public void run() {
      mReturningToStart = true;
      mShrinkTrigger.setDuration(mMediumAnimationDuration);
      mShrinkTrigger.setAnimationListener(mShrinkAnimationListener);
      mShrinkTrigger.reset();
      mShrinkTrigger.setInterpolator(mDecelerateInterpolator);
      startAnimation(mShrinkTrigger);
      animateOffsetToStartPosition(mCurrentTargetOffsetTop + getPaddingTop(),
          mReturnToStartPositionListener);
    }
  };

  /**
   * Simple constructor to use when creating a SwipeRefreshLayout from code.
   */
  public SwipeRefreshLayout(Context context) {
    this(context, null);
  }

  /**
   * Constructor that is called when inflating SwipeRefreshLayout from XML.
   */
  public SwipeRefreshLayout(Context context, AttributeSet attrs) {
    super(context, attrs);

    mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

    mMediumAnimationDuration = getResources().getInteger(android.R.integer.config_mediumAnimTime);

    setWillNotDraw(false);
    mDecelerateInterpolator = new DecelerateInterpolator(DECELERATE_INTERPOLATION_FACTOR);
    mAccelerateInterpolator = new AccelerateInterpolator(ACCELERATE_INTERPOLATION_FACTOR);

    final TypedArray a = context.obtainStyledAttributes(attrs, LAYOUT_ATTRS);
    setEnabled(a.getBoolean(0, true));
    a.recycle();

    mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);

    mNestedScrollingChildHelper = new NestedScrollingChildHelper(this);
    setNestedScrollingEnabled(true);
  }

  @Override public void onAttachedToWindow() {
    super.onAttachedToWindow();
    removeCallbacks(mCancel);
    removeCallbacks(mReturnToStartPosition);
  }

  @Override public void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    removeCallbacks(mReturnToStartPosition);
    removeCallbacks(mCancel);
  }

  private void animateOffsetToStartPosition(int from, AnimationListener listener) {
    mFrom = from;
    mAnimateToStartPosition.reset();
    mAnimateToStartPosition.setDuration(mMediumAnimationDuration);
    mAnimateToStartPosition.setAnimationListener(listener);
    mAnimateToStartPosition.setInterpolator(mDecelerateInterpolator);
    mTarget.startAnimation(mAnimateToStartPosition);
  }

  /**
   * Set the listener to be notified when a refresh is triggered via the swipe
   * gesture.
   */
  public void setOnRefreshListener(OnRefreshListener listener) {
    mListener = listener;
  }

  private void setTriggerPercentage(float percent) {
    if (percent == 0f) {
      // No-op. A null trigger means it's uninitialized, and setting it to zero-percent
      // means we're trying to reset state, so there's nothing to reset in this case.
      mCurrPercentage = 0;
      return;
    }
    mCurrPercentage = percent;
    mListener.onProgress(mCurrPercentage);
  }

  /**
   * @return Whether the SwipeRefreshWidget is actively showing refresh
   * progress.
   */
  public boolean isRefreshing() {
    return mRefreshing;
  }

  /**
   * Notify the widget that refresh state has changed. Do not call this when
   * refresh is triggered by a swipe gesture.
   *
   * @param refreshing Whether or not the view should show refresh progress.
   */
  public void setRefreshing(boolean refreshing) {
    if (mRefreshing != refreshing) {
      ensureTarget();
      mCurrPercentage = 0;
      mRefreshing = refreshing;
    }
  }

  protected void ensureTarget() {
    // Don't bother getting the parent height if the parent hasn't been laid out yet.
    if (mTarget == null) {
      if (getChildCount() > 1 && !isInEditMode()) {
        throw new IllegalStateException("SwipeRefreshLayout can host only one direct child");
      }
      mTarget = getChildAt(0);
      mOriginalOffsetTop = mTarget.getTop() + getPaddingTop();
    }
    if (mDistanceToTriggerSync == -1) {
      if (getParent() != null && ((View) getParent()).getHeight() > 0) {
        final DisplayMetrics metrics = getResources().getDisplayMetrics();
        mDistanceToTriggerSync =
            (int) Math.min(((View) getParent()).getHeight() * MAX_SWIPE_DISTANCE_FACTOR,
                REFRESH_TRIGGER_DISTANCE * metrics.density);
      }
    }
  }

  @Override protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    final int width = getMeasuredWidth();
    final int height = getMeasuredHeight();
    if (getChildCount() == 0) {
      return;
    }
    final View child = getChildAt(0);
    final int childLeft = getPaddingLeft();
    final int childTop = mCurrentTargetOffsetTop + getPaddingTop();
    final int childWidth = width - getPaddingLeft() - getPaddingRight();
    final int childHeight = height - getPaddingTop() - getPaddingBottom();
    child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
  }

  @Override public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    if (getChildCount() > 1 && !isInEditMode()) {
      throw new IllegalStateException("SwipeRefreshLayout can host only one direct child");
    }
    if (getChildCount() > 0) {
      getChildAt(0).measure(
          MeasureSpec.makeMeasureSpec(getMeasuredWidth() - getPaddingLeft() - getPaddingRight(),
              MeasureSpec.EXACTLY),
          MeasureSpec.makeMeasureSpec(getMeasuredHeight() - getPaddingTop() - getPaddingBottom(),
              MeasureSpec.EXACTLY));
    }
  }

  /**
   * @return Whether it is possible for the child view of this layout to
   * scroll up. Override this if the child view is a custom view.
   */
  public boolean canChildScrollUp() {
    if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
      if (mTarget instanceof AbsListView) {
        final AbsListView absListView = (AbsListView) mTarget;
        return absListView.getChildCount() > 0 && (absListView.getFirstVisiblePosition() > 0
            || absListView.getChildAt(0).getTop() < absListView.getPaddingTop());
      } else {
        return ViewCompat.canScrollVertically(mTarget, -1) || mTarget.getScrollY() > 0;
      }
    } else {
      return ViewCompat.canScrollVertically(mTarget, -1);
    }
  }

  @Override public boolean onInterceptTouchEvent(MotionEvent ev) {
    ensureTarget();

    final int action = MotionEventCompat.getActionMasked(ev);

    if (mReturningToStart && action == MotionEvent.ACTION_DOWN) {
      mReturningToStart = false;
    }

    if (!isEnabled()
        || mReturningToStart
        || canChildScrollUp()
        || isDisallowInterceptTouchEvent()
        || mNestedScrollInProgress) {
      // Fail fast if we're not in a state where a swipe is possible
      return false;
    }

    switch (action) {
      case MotionEvent.ACTION_DOWN:
        mLastMotionY = mInitialMotionY = ev.getY();
        mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
        mIsBeingDragged = false;
        mCurrPercentage = 0;
        break;

      case MotionEvent.ACTION_MOVE:
        if (mActivePointerId == INVALID_POINTER) {
          Log.e(LOG_TAG, "Got ACTION_MOVE event but don't have an active pointer id.");
          return false;
        }

        final int pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
        if (pointerIndex < 0) {
          Log.e(LOG_TAG, "Got ACTION_MOVE event but have an invalid active pointer id.");
          return false;
        }
        if (mRefreshing) {
          return false;
        }

        final float y = MotionEventCompat.getY(ev, pointerIndex);
        final float yDiff = y - mInitialMotionY;
        if (yDiff > mTouchSlop) {
          mLastMotionY = y;
          mIsBeingDragged = true;
        }
        break;

      case MotionEventCompat.ACTION_POINTER_UP:
        onSecondaryPointerUp(ev);
        break;

      case MotionEvent.ACTION_UP:
      case MotionEvent.ACTION_CANCEL:
        mIsBeingDragged = false;
        mCurrPercentage = 0;
        mActivePointerId = INVALID_POINTER;
        break;
    }

    return mIsBeingDragged;
  }

  private boolean isDisallowInterceptTouchEvent() {
    if (mTarget == null) {
      return false;
    }

    try {
      Integer groupFlags = ReflectUtils.getFieldValue(mTarget, ViewGroup.class, "mGroupFlags");
      Integer FLAG_DISALLOW_INTERCEPT =
          ReflectUtils.getFieldValue(mTarget, ViewGroup.class, "FLAG_DISALLOW_INTERCEPT");
      return (groupFlags & FLAG_DISALLOW_INTERCEPT) != 0;
    } catch (NoSuchFieldException ignored) {
    } catch (IllegalAccessException ignored) {
    }
    return false;
  }

  @Override public void requestDisallowInterceptTouchEvent(boolean b) {
    // if this is a List < L or another view that doesn't support nested
    // scrolling, ignore this request so that the vertical scroll event
    // isn't stolen
    if ((android.os.Build.VERSION.SDK_INT < 21 && mTarget instanceof AbsListView) || (mTarget
        != null && !ViewCompat.isNestedScrollingEnabled(mTarget))) {
      // Nope.
    } else {
      super.requestDisallowInterceptTouchEvent(b);
    }
  }

  @Override public boolean onTouchEvent(MotionEvent ev) {
    final int action = MotionEventCompat.getActionMasked(ev);

    if (mReturningToStart && action == MotionEvent.ACTION_DOWN) {
      mReturningToStart = false;
    }

    if (!isEnabled() || mReturningToStart || canChildScrollUp() || mNestedScrollInProgress) {
      // Fail fast if we're not in a state where a swipe is possible
      return false;
    }

    switch (action) {
      case MotionEvent.ACTION_DOWN:
        mLastMotionY = mInitialMotionY = ev.getY();
        mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
        mIsBeingDragged = false;
        mCurrPercentage = 0;
        break;

      case MotionEvent.ACTION_MOVE:
        final int pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
        if (pointerIndex < 0) {
          Log.e(LOG_TAG, "Got ACTION_MOVE event but have an invalid active pointer id.");
          return false;
        }

        final float y = MotionEventCompat.getY(ev, pointerIndex);
        final float yDiff = y - mInitialMotionY;

        if (!mIsBeingDragged && yDiff > mTouchSlop) {
          mIsBeingDragged = true;
        }

        if (mIsBeingDragged) {
          // User velocity passed min velocity; trigger a refresh
          if (yDiff > mDistanceToTriggerSync) {
            // User movement passed distance; trigger a refresh
            mIsRefreshable = true;
          } else {
            // Just track the user's movement
            mIsRefreshable = false;
            if (mLastMotionY > y && mTarget.getTop() == getPaddingTop()) {
              // If the user puts the view back at the top, we
              // don't need to. This shouldn't be considered
              // cancelling the gesture as the user can restart from the top.
              removeCallbacks(mCancel);
            }
          }
          float diff = Math.min(yDiff, mDistanceToTriggerSync);
          setTriggerPercentage(
              mAccelerateInterpolator.getInterpolation(diff / mDistanceToTriggerSync));
          updateContentOffsetTop((int) (yDiff));
          mLastMotionY = y;
        }
        break;

      case MotionEventCompat.ACTION_POINTER_DOWN: {
        final int index = MotionEventCompat.getActionIndex(ev);
        mLastMotionY = MotionEventCompat.getY(ev, index);
        mActivePointerId = MotionEventCompat.getPointerId(ev, index);
        break;
      }

      case MotionEventCompat.ACTION_POINTER_UP:
        onSecondaryPointerUp(ev);
        break;

      case MotionEvent.ACTION_UP:
      case MotionEvent.ACTION_CANCEL:
        if (mIsRefreshable) {
          startRefresh();
        }
        mIsRefreshable = false;
        mIsBeingDragged = false;
        mCurrPercentage = 0;
        mActivePointerId = INVALID_POINTER;
        mCancel.run();
        return false;
    }

    return true;
  }

  private void startRefresh() {
    removeCallbacks(mCancel);
    mReturnToStartPosition.run();
    setRefreshing(true);
    mListener.onRefresh();
  }

  private void updateContentOffsetTop(int targetTop) {
    targetTop *= mDistanceScale;
    final int currentTop = mTarget.getTop();
    if (targetTop > mDistanceToTriggerSync && mIsLockDistance) {
      targetTop = (int) mDistanceToTriggerSync;
    }
    if (targetTop < 0) {
      targetTop = 0;
    }
    setTargetOffsetTopAndBottom(targetTop - currentTop);
  }

  private void setTargetOffsetTopAndBottom(int offset) {
    mTarget.offsetTopAndBottom(offset);
    if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
      mTarget.invalidate();
    }
    mCurrentTargetOffsetTop = mTarget.getTop();
  }

  public boolean isLockDistance() {
    return mIsLockDistance;
  }

  /**
   * 是否锁定最大移动长度
   */
  public void setLockDistance(boolean isLockDistance) {
    this.mIsLockDistance = isLockDistance;
  }

  public float getDistanceScale() {
    return mDistanceScale;
  }

  /**
   * 移动长度缩放比例
   */
  public void setDistanceScale(float distanceScale) {
    this.mDistanceScale = distanceScale;
  }

  private void hehe(float yDiff) {
    if (yDiff > mDistanceToTriggerSync) {
      // User movement passed distance; trigger a refresh
      mIsRefreshable = true;
    } else {
      // Just track the user's movement
      mIsRefreshable = false;
    }
    float diff = Math.min(yDiff, mDistanceToTriggerSync);
    setTriggerPercentage(mAccelerateInterpolator.getInterpolation(diff / mDistanceToTriggerSync));
    updateContentOffsetTop((int) (yDiff));
  }

  private void onSecondaryPointerUp(MotionEvent ev) {
    final int pointerIndex = MotionEventCompat.getActionIndex(ev);
    final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
    if (pointerId == mActivePointerId) {
      // This was our active pointer going up. Choose a new
      // active pointer and adjust accordingly.
      final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
      mLastMotionY = MotionEventCompat.getY(ev, newPointerIndex);
      mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
    }
  }

  // NestedScrollingParent

  @Override public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
    return isEnabled()
        && !mReturningToStart
        && !mRefreshing
        && (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
  }

  @Override public void onNestedScrollAccepted(View child, View target, int axes) {
    // Reset the counter of how much leftover scroll needs to be consumed.
    mNestedScrollingParentHelper.onNestedScrollAccepted(child, target, axes);
    // Dispatch up to the nested parent
    startNestedScroll(axes & ViewCompat.SCROLL_AXIS_VERTICAL);
    mTotalUnconsumed = 0;
    mNestedScrollInProgress = true;
  }

  @Override public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
    // If we are in the middle of consuming, a scroll, then we want to move the spinner back up
    // before allowing the list to scroll
    if (dy > 0 && mTotalUnconsumed > 0) {
      if (dy > mTotalUnconsumed) {
        consumed[1] = dy - (int) mTotalUnconsumed;
        mTotalUnconsumed = 0;
      } else {
        mTotalUnconsumed -= dy;
        consumed[1] = dy;
      }
      //            moveSpinner(mTotalUnconsumed);
      hehe(mTotalUnconsumed);
    }

    //        // If a client layout is using a custom start position for the circle
    //        // view, they mean to hide it again before scrolling the child view
    //        // If we get back to mTotalUnconsumed == 0 and there is more to go, hide
    //        // the circle so it isn't exposed if its blocking content is moved
    //        if (mUsingCustomStart && dy > 0 && mTotalUnconsumed == 0
    //                && Math.abs(dy - consumed[1]) > 0) {
    //            mCircleView.setVisibility(View.GONE);
    //        }

    // Now let our nested parent consume the leftovers
    final int[] parentConsumed = mParentScrollConsumed;
    if (dispatchNestedPreScroll(dx - consumed[0], dy - consumed[1], parentConsumed, null)) {
      consumed[0] += parentConsumed[0];
      consumed[1] += parentConsumed[1];
    }
  }

  @Override public int getNestedScrollAxes() {
    return mNestedScrollingParentHelper.getNestedScrollAxes();
  }

  @Override public void onStopNestedScroll(View target) {
    mNestedScrollingParentHelper.onStopNestedScroll(target);
    mNestedScrollInProgress = false;
    // Finish the spinner for nested scrolling if we ever consumed any
    // unconsumed nested scroll
    if (mTotalUnconsumed > 0) {
      //            finishSpinner(mTotalUnconsumed);
      if (mIsRefreshable) {
        startRefresh();
      }
      mIsRefreshable = false;
      mCurrPercentage = 0;
      mCancel.run();
      mTotalUnconsumed = 0;
    }
    // Dispatch up our nested parent
    stopNestedScroll();
  }

  @Override
  public void onNestedScroll(final View target, final int dxConsumed, final int dyConsumed,
      final int dxUnconsumed, final int dyUnconsumed) {
    // Dispatch up to the nested parent first
    dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, mParentOffsetInWindow);

    // This is a bit of a hack. Nested scrolling works from the bottom up, and as we are
    // sometimes between two nested scrolling views, we need a way to be able to know when any
    // nested scrolling parent has stopped handling events. We do that by using the
    // 'offset in window 'functionality to see if we have been moved from the event.
    // This is a decent indication of whether we should take over the event stream or not.
    final int dy = dyUnconsumed + mParentOffsetInWindow[1];
    if (dy < 0) {
      mTotalUnconsumed += Math.abs(dy);
      //            moveSpinner(mTotalUnconsumed);
      hehe(mTotalUnconsumed);
    }
  }

  // NestedScrollingChild

  @Override public boolean isNestedScrollingEnabled() {
    return mNestedScrollingChildHelper.isNestedScrollingEnabled();
  }

  @Override public void setNestedScrollingEnabled(boolean enabled) {
    mNestedScrollingChildHelper.setNestedScrollingEnabled(enabled);
  }

  @Override public boolean startNestedScroll(int axes) {
    return mNestedScrollingChildHelper.startNestedScroll(axes);
  }

  @Override public void stopNestedScroll() {
    mNestedScrollingChildHelper.stopNestedScroll();
  }

  @Override public boolean hasNestedScrollingParent() {
    return mNestedScrollingChildHelper.hasNestedScrollingParent();
  }

  @Override public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed,
      int dyUnconsumed, int[] offsetInWindow) {
    return mNestedScrollingChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed,
        dyUnconsumed, offsetInWindow);
  }

  @Override
  public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
    return mNestedScrollingChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
  }

  @Override public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
    return dispatchNestedPreFling(velocityX, velocityY);
  }

  @Override
  public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
    return dispatchNestedFling(velocityX, velocityY, consumed);
  }

  @Override public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
    return mNestedScrollingChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
  }

  @Override public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
    return mNestedScrollingChildHelper.dispatchNestedPreFling(velocityX, velocityY);
  }

  /**
   * Classes that wish to be notified when the swipe gesture correctly
   * triggers a refresh should implement this interface.
   */
  public interface OnRefreshListener {
    public void onRefresh();

    public void onProgress(float progress);
  }

  public static class SimpleOnRefreshListener implements OnRefreshListener {
    @Override public void onRefresh() {

    }

    @Override public void onProgress(float progress) {

    }
  }

  /**
   * Simple AnimationListener to avoid having to implement unneeded methods in
   * AnimationListeners.
   */
  private class BaseAnimationListener implements AnimationListener {
    @Override public void onAnimationStart(Animation animation) {
    }

    @Override public void onAnimationEnd(Animation animation) {
    }

    @Override public void onAnimationRepeat(Animation animation) {
    }
  }
}
