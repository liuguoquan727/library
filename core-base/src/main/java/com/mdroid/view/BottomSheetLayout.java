package com.mdroid.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.core.view.MotionEventCompat;
import androidx.core.view.ViewCompat;
import com.mdroid.utils.ViewUtils;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.util.Property;

/**
 * https://www.google.com/design/spec/components/bottom-sheets.html
 */
public class BottomSheetLayout extends ViewGroup {
  private static final String TAG = BottomSheetLayout.class.getSimpleName();

  private static final int DEFAULT_CHILD_GRAVITY = Gravity.TOP | Gravity.START;
  /**
   * If no fade color is given by default it will fade to 80% gray.
   */
  private static final int DEFAULT_FADE_COLOR = 0x99000000;
  private static final float DEFAULT_SLIDE_OFFSET = 0.3f;
  private static final int BASE_SETTLE_DURATION = 100; // ms
  private static final int MAX_SETTLE_DURATION = 300; // ms
  private final ViewDragHelper mDragHelper;
  private final Paint mCoveredFadePaint = new Paint();
  private int mCoveredFadeColor = DEFAULT_FADE_COLOR;
  private boolean mIsUnableToDrag;
  private float mInitialMotionX;
  private float mInitialMotionY;
  private float mPrevMotionY;
  private boolean mIsScrollableViewHandlingTouch;
  private View mScrollableView;
  private OnSheetStateChangeListener mOnSheetStateChangeListener;
  private View mSheetView;
  private boolean mIWantYou;
  /**
   * Animator for sheet view
   */
  private Property<View, Integer> VIEW_TOP = new Property<View, Integer>(Integer.class, "top") {
    @Override public Integer get(View view) {
      return view.getTop();
    }

    @Override public void set(View view, Integer value) {
      view.layout(view.getLeft(), value, view.getRight(), view.getHeight() + value);
      onSheetDragged(view.getTop());
      ViewCompat.postInvalidateOnAnimation(BottomSheetLayout.this);
    }
  };

  public BottomSheetLayout(Context context) {
    this(context, null);
  }

  public BottomSheetLayout(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public BottomSheetLayout(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    mDragHelper = ViewDragHelper.create(this, 1.0f, new DragHelperCallback());
    setWillNotDraw(false);
  }

  public static LayoutParams generateSlideLayoutParams() {
    LayoutParams params = new LayoutParams();
    params.slideable = true;
    return params;
  }

  @Override public void computeScroll() {
    if (mDragHelper.continueSettling(true)) {
      ViewCompat.postInvalidateOnAnimation(this);
    }
  }

  private LayoutParams getLayoutParams(View view) {
    if (view != null) {
      return (LayoutParams) view.getLayoutParams();
    }
    return null;
  }

  /**
   * Computes the top position of the panel based on the slide offset.
   */
  private int computeSheetTopPosition(int slideRange, float slideOffset) {
    int slidePixelOffset = (int) (slideOffset * slideRange);
    // Compute the top of the panel if its collapsed
    return getMeasuredHeight() - getPaddingBottom() - slidePixelOffset;
  }

  private View getSheetView() {
    for (int i = 0; i < getChildCount(); i++) {
      View child = getChildAt(i);
      LayoutParams lp = getLayoutParams(child);
      if (lp.slideable) return child;
    }
    return null;
  }

  @Override
  public void addView(final View child, final int index, final ViewGroup.LayoutParams params) {
    if (((LayoutParams) params).slideable) {
      final int finalIndex = 0;
      if (mSheetView != null) {
        Runnable runAfterDismissThis = new Runnable() {
          @Override public void run() {
            mSheetView = getSheetView();
            mIWantYou = true;
            requestLayout();
          }
        };
        dismissSheetViewInternal(null, runAfterDismissThis);
      } else {
        mIWantYou = true;
        mSheetView = child;
      }
      super.addView(child, finalIndex, params);
    } else {
      super.addView(child, index, params);
    }
  }

  @Override public void removeView(final View view) {
    if (view == mSheetView) {
      Runnable runAfterDismissThis = new Runnable() {
        @Override public void run() {
          BottomSheetLayout.super.removeView(view);
          mSheetView = getSheetView();
          mIWantYou = true;
        }
      };
      dismissSheetViewInternal(null, runAfterDismissThis);
    } else {
      super.removeView(view);
    }
  }

  @Override public void removeViewAt(int index) {
    removeView(getChildAt(index));
  }

  @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int count = getChildCount();

    for (int i = 0; i < count; i++) {
      final View child = getChildAt(i);
      if (child.getVisibility() != GONE) {
        measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
      }
    }
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
  }

  @Override protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    View sheetView = null;
    final int count = getChildCount();

    final int parentLeft = getPaddingLeft();
    final int parentRight = right - left - getPaddingRight();

    final int parentTop = getPaddingTop();
    final int parentBottom = bottom - top - getPaddingBottom();

    for (int i = 0; i < count; i++) {
      final View child = getChildAt(i);
      if (child.getVisibility() != GONE) {
        final LayoutParams lp = (LayoutParams) child.getLayoutParams();

        final int width = child.getMeasuredWidth();
        final int height = child.getMeasuredHeight();

        int childLeft;
        int childTop;

        int gravity = lp.gravity;
        if (gravity == -1) {
          gravity = DEFAULT_CHILD_GRAVITY;
        }

        final int layoutDirection = ViewCompat.getLayoutDirection(this);
        final int absoluteGravity = GravityCompat.getAbsoluteGravity(gravity, layoutDirection);
        final int verticalGravity = gravity & Gravity.VERTICAL_GRAVITY_MASK;

        switch (absoluteGravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
          case Gravity.CENTER_HORIZONTAL:
            childLeft = parentLeft + (parentRight - parentLeft - width) / 2 + lp.leftMargin
                - lp.rightMargin;
            break;
          case Gravity.RIGHT:
            childLeft = parentRight - width - lp.rightMargin;
            break;
          case Gravity.LEFT:
          default:
            childLeft = parentLeft + lp.leftMargin;
        }
        if (!lp.slideable) {
          switch (verticalGravity) {
            case Gravity.TOP:
              childTop = parentTop + lp.topMargin;
              break;
            case Gravity.CENTER_VERTICAL:
              childTop = parentTop + (parentBottom - parentTop - height) / 2 + lp.topMargin
                  - lp.bottomMargin;
              break;
            case Gravity.BOTTOM:
              childTop = parentBottom - height - lp.bottomMargin;
              break;
            default:
              childTop = parentTop + lp.topMargin;
          }
        } else {
          if (sheetView == null) {
            sheetView = child;
          }
          lp.coveredFadeRect.set(left, top + lp.topMargin + lp.bottomMargin, right, bottom);
          lp.marginTopRect.set(left, top, right, top + lp.topMargin + lp.bottomMargin);
          lp.slideRange = Math.min(child.getMeasuredHeight(),
              bottom - top - lp.topMargin - lp.bottomMargin - getPaddingTop() - getPaddingBottom());
          if (lp.anchorId != 0) {
            View anchorView = child.findViewById(lp.anchorId);
            lp.minAnchorHeight = anchorView.getMeasuredHeight();
          }
          if (lp.state == State.ANCHORED) {
            lp.slideOffset = lp.computeAnchorPoint();
          }
          childTop = computeSheetTopPosition(lp.slideRange, lp.slideOffset);
        }

        child.layout(childLeft, childTop, childLeft + width, childTop + height);
      }
    }

    if (mIWantYou) {
      if (mSheetView != null) {
        anchorSheetViewInternal();
      }
    } else {
      if (mSheetView != null) {
        LayoutParams lp = getLayoutParams(mSheetView);
        if (lp.dismissAnimator != null) {
          Runnable runAfterCancel =
              ((CancelDetectionAnimationListener) lp.dismissAnimator.getListeners()
                  .get(0)).runAfterCancel;
          Runnable runAfterEnd =
              ((CancelDetectionAnimationListener) lp.dismissAnimator.getListeners()
                  .get(0)).runAfterEnd;
          lp.cancelDismissAnimation();
          dismissSheetViewInternal(runAfterCancel, runAfterEnd);
        } else if (lp.showAnimator != null) {
          lp.cancelShowAnimation();
          anchorSheetViewInternal();
        }
      }
    }
    mIWantYou = false;
  }

  @Override public boolean dispatchTouchEvent(@NonNull MotionEvent ev) {
    final int action = MotionEventCompat.getActionMasked(ev);

    if (!isTouchEnabled() || (mIsUnableToDrag && action != MotionEvent.ACTION_DOWN)) {
      mDragHelper.cancel();
      return super.dispatchTouchEvent(ev);
    }
    final int actionIndex = MotionEventCompat.getActionIndex(ev);
    final float x = MotionEventCompat.getX(ev, actionIndex);
    final float y = MotionEventCompat.getY(ev, actionIndex);
    LayoutParams lp = getLayoutParams(mSheetView);

    if (action == MotionEvent.ACTION_DOWN) {
      mIsScrollableViewHandlingTouch = false;
      mPrevMotionY = y;
      mScrollableView = findScrollableView(mSheetView, (int) x, (int) y);
    } else if (action == MotionEvent.ACTION_MOVE) {
      float dy = y - mPrevMotionY;
      mPrevMotionY = y;

      // Which direction (up or down) is the drag moving?
      if (dy > 0) { // Collapsing
        if (mScrollableView != null && ViewUtils.canScrollUp(mScrollableView)) {
          if (!mIsScrollableViewHandlingTouch
              && mDragHelper.getViewDragState() == ViewDragHelper.STATE_DRAGGING) {
            mDragHelper.cancel();
            ev.setAction(MotionEvent.ACTION_DOWN);
          }
          mIsScrollableViewHandlingTouch = true;
          return super.dispatchTouchEvent(ev);
        }

        // Was the child handling the touch previously?
        // Then we need to rejigger things so that the
        // drag panel gets a proper down event.
        if (mIsScrollableViewHandlingTouch) {
          // Send an 'UP' event to the child.
          MotionEvent up = MotionEvent.obtain(ev);
          up.setAction(MotionEvent.ACTION_CANCEL);
          super.dispatchTouchEvent(up);
          up.recycle();

          // Send a 'DOWN' event to the panel. (We'll cheat
          // and hijack this one)
          ev.setAction(MotionEvent.ACTION_DOWN);

          mScrollableView = findScrollableView(mSheetView, (int) x, (int) y);
        }

        mIsScrollableViewHandlingTouch = false;
        return super.dispatchTouchEvent(ev);
      } else if (dy < 0) { // Expanding
        if (mScrollableView != null) {
          // Is the panel less than fully expanded?
          // Then we'll handle the drag here.
          if (lp.slideOffset < 1.0f) {
            mIsScrollableViewHandlingTouch = false;
            return super.dispatchTouchEvent(ev);
          }

          // Was the panel handling the touch previously?
          // Then we need to rejigger things so that the
          // child gets a proper down event.
          if (!mIsScrollableViewHandlingTouch
              && mDragHelper.getViewDragState() == ViewDragHelper.STATE_DRAGGING) {
            mDragHelper.cancel();
            ev.setAction(MotionEvent.ACTION_DOWN);
          }
          mIsScrollableViewHandlingTouch = true;
        }
        return super.dispatchTouchEvent(ev);
      }
    } else if (action == MotionEvent.ACTION_UP && mIsScrollableViewHandlingTouch) {
      // If the scrollable view was handling the touch and we receive an up
      // we want to clear any previous dragging state so we don't intercept a touch stream accidentally
      mDragHelper.abort();
    } else if ((action == MotionEvent.ACTION_POINTER_DOWN
        || action == MotionEvent.ACTION_POINTER_UP) && mIsScrollableViewHandlingTouch) {
      return onInterceptTouchEvent(ev);
    }

    // In all other cases, just let the default behavior take over.
    return super.dispatchTouchEvent(ev);
  }

  @Override public boolean onInterceptTouchEvent(@NonNull MotionEvent ev) {
    // If the scrollable view is handling touch, never intercept
    if (mIsScrollableViewHandlingTouch) {
      mDragHelper.cancel();
      return false;
    }

    final int action = MotionEventCompat.getActionMasked(ev);
    final int actionIndex = MotionEventCompat.getActionIndex(ev);
    final float x = MotionEventCompat.getX(ev, actionIndex);
    final float y = MotionEventCompat.getY(ev, actionIndex);
    if (!isTouchEnabled() || (mIsUnableToDrag && action != MotionEvent.ACTION_DOWN)) {
      mDragHelper.cancel();
      return super.onInterceptTouchEvent(ev);
    }

    if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
      mDragHelper.cancel();
      return false;
    }

    LayoutParams lp = getLayoutParams(mSheetView);
    boolean interceptTap = false;

    switch (action) {
      case MotionEvent.ACTION_DOWN: {
        mIsUnableToDrag = false;
        mInitialMotionX = x;
        mInitialMotionY = y;

        if ((isSheetShowing() && isUnder(lp.coveredFadeRect, (int) x, (int) y)/*是否在灰色区域内*/)
            && (!isViewUnder(mSheetView, (int) x, (int) y) || isUnder(lp.marginTopRect, (int) x,
            (int) y))) {
          interceptTap = true;
        }
        break;
      }

      case MotionEvent.ACTION_MOVE: {
        final float adx = Math.abs(x - mInitialMotionX);
        final float ady = Math.abs(y - mInitialMotionY);
        final int slop = mDragHelper.getTouchSlop();
        if (adx > slop && ady > adx) {
          mDragHelper.cancel();
          mIsUnableToDrag = true;
          return false;
        }
        break;
      }

      case MotionEvent.ACTION_POINTER_DOWN: {
        if ((isSheetShowing() && isUnder(lp.coveredFadeRect, (int) x, (int) y)/*是否在灰色区域内*/)
            && (!isViewUnder(mSheetView, (int) x, (int) y) || isUnder(lp.marginTopRect, (int) x,
            (int) y))) {
          interceptTap = true;
        }
        break;
      }
    }

    final boolean interceptForDrag = mDragHelper.shouldInterceptTouchEvent(ev);

    return interceptForDrag || interceptTap;
  }

  @Override public boolean onTouchEvent(MotionEvent ev) {
    if (!isTouchEnabled()) {
      return super.onTouchEvent(ev);
    }

    try {
      mDragHelper.processTouchEvent(ev);
    } catch (Throwable ignored) {
    }

    final int action = MotionEventCompat.getActionMasked(ev);
    LayoutParams lp = getLayoutParams(mSheetView);
    boolean wantTouchEvents = true;

    switch (action) {
      case MotionEvent.ACTION_DOWN: {
        final float x = ev.getX();
        final float y = ev.getY();
        mInitialMotionX = x;
        mInitialMotionY = y;
        break;
      }

      case MotionEvent.ACTION_UP: {
        final float x = ev.getX();
        final float y = ev.getY();
        final float dx = x - mInitialMotionX;
        final float dy = y - mInitialMotionY;
        final int slop = mDragHelper.getTouchSlop();
        if (dx * dx + dy * dy < slop * slop && (isUnder(lp.coveredFadeRect, (int) x, (int) y)
            && !isViewUnder(mSheetView, (int) x, (int) y))) {
          // Taps close a dimmed open pane.
          if (lp.closeOnTouchOutside) {
            dismissSheetViewInternal(null, null);
          }
          break;
        }
        break;
      }
    }

    return wantTouchEvents;
  }

  private boolean isViewUnder(View view, int x, int y) {
    if (view == null) return false;
    int[] viewLocation = new int[2];
    view.getLocationOnScreen(viewLocation);
    int[] parentLocation = new int[2];
    this.getLocationOnScreen(parentLocation);
    int screenX = parentLocation[0] + x;
    int screenY = parentLocation[1] + y;
    return screenX >= viewLocation[0]
        && screenX < viewLocation[0] + view.getWidth()
        && screenY >= viewLocation[1]
        && screenY < viewLocation[1] + view.getHeight();
  }

  private boolean isUnder(Rect rect, int x, int y) {
    int[] parentLocation = new int[2];
    this.getLocationOnScreen(parentLocation);
    int screenX = parentLocation[0] + x;
    int screenY = parentLocation[1] + y;
    return rect.contains(screenX, screenY);
  }

  private View findScrollableView(View view, float x, float y) {
    if (view == null) return null;
    View scrollableView = null;
    if (ViewUtils.canScrollUp(view) || ViewUtils.canScrollDown(view)) {
      scrollableView = view;
    } else if (view instanceof ViewGroup) {
      ViewGroup vg = (ViewGroup) view;
      for (int i = 0; i < vg.getChildCount(); i++) {
        View child = vg.getChildAt(i);
        if (isViewUnder(child, (int) x, (int) y)) {
          scrollableView = findScrollableView(child, x, y);
        }
      }
    }
    return scrollableView;
  }

  private void anchorSheetViewInternal() {
    final LayoutParams lp = getLayoutParams(mSheetView);
    if (lp.showAnimator != null) {
      return;
    }
    mDragHelper.abort();
    lp.cancelDismissAnimation();
    int dist = computeSheetTopPosition(lp.slideRange, lp.computeAnchorPoint());
    Animator animator = ObjectAnimator.ofInt(mSheetView, VIEW_TOP, dist);
    animator.setDuration(computeAxisDuration(mSheetView.getTop() - dist, lp.slideRange));
    animator.setInterpolator(new DecelerateInterpolator());
    animator.addListener(new CancelDetectionAnimationListener() {
      @Override public void onAnimationEnd(Animator animation) {
        lp.showAnimator = null;
        if (!canceled) {
          lp.state = State.ANCHORED;
          if (mOnSheetStateChangeListener != null) {
            mOnSheetStateChangeListener.onStateChange(mSheetView, lp.state, lp.slideOffset);
          }
        }
      }
    });
    animator.start();
    lp.showAnimator = animator;
  }

  private void dismissSheetViewInternal(Runnable runAfterCanceled, Runnable runAfterDismiss) {
    final LayoutParams lp = getLayoutParams(mSheetView);
    if (lp.dismissAnimator != null) {
      return;
    }
    mDragHelper.abort();
    lp.cancelShowAnimation();
    int dist = computeSheetTopPosition(lp.slideRange, 0);
    Animator animator = ObjectAnimator.ofInt(mSheetView, VIEW_TOP, dist);
    animator.setDuration(computeAxisDuration(mSheetView.getTop() - dist, lp.slideRange));
    animator.setInterpolator(new DecelerateInterpolator());
    animator.addListener(new CancelDetectionAnimationListener(runAfterCanceled, runAfterDismiss) {
      @Override public void onAnimationEnd(Animator animation) {
        lp.dismissAnimator = null;
        if (canceled) {
          if (runAfterCancel != null) {
            runAfterCancel.run();
          }
        } else {
          lp.state = State.HIDDEN;
          if (mOnSheetStateChangeListener != null) {
            mOnSheetStateChangeListener.onStateChange(mSheetView, lp.state, lp.slideOffset);
          }
          if (runAfterEnd != null) {
            runAfterEnd.run();
          }
        }
      }
    });
    animator.start();
    lp.dismissAnimator = animator;
  }

  private int computeAxisDuration(int delta, int motionRange) {
    if (delta == 0) {
      return 0;
    }

    final float range = (float) Math.abs(delta) / motionRange;
    int duration = (int) ((range + 1) * BASE_SETTLE_DURATION);
    return Math.min(duration, MAX_SETTLE_DURATION);
  }

  public void anchorSheetView() {
    if (mSheetView == null) {
      return;
    }
    mIWantYou = true;
    requestLayout();
  }

  public void dismissSheetView() {
    if (mSheetView == null) {
      return;
    }
    dismissSheetViewInternal(null, null);
  }

  public boolean isTouchEnabled() {
    return isEnabled() && isSheetShowing();
  }

  /**
   * @return Whether or not a sheet is currently presented.
   */
  public final boolean isSheetShowing() {
    LayoutParams lp = getLayoutParams(mSheetView);
    return lp != null && lp.isShowing();
  }

  public final boolean hasSheetView() {
    return mSheetView != null;
  }

  public final void setCoveredFadeColor(int color) {
    mCoveredFadeColor = color;
    invalidate();
  }

  private boolean isSheetAnimating() {
    LayoutParams lp = getLayoutParams(mSheetView);
    return lp != null && lp.isAnimating();
  }

  private void onSheetDragged(int newTop) {
    LayoutParams lp = getLayoutParams(mSheetView);
    // Recompute the slide offset based on the new top position
    lp.state = State.DRAGGING;
    lp.slideOffset = computeSlideOffset(lp.slideRange, newTop);
    if (mOnSheetStateChangeListener != null) {
      mOnSheetStateChangeListener.onStateChange(mSheetView, lp.state, lp.slideOffset);
    }
  }

  /**
   * Computes the slide offset based on the top position of the panel
   */
  private float computeSlideOffset(int slideRange, int topPosition) {
    // Compute the panel top position if the panel is collapsed (offset 0)
    final int topBoundCollapsed = computeSheetTopPosition(slideRange, 0);

    // Determine the new slide offset based on the collapsed top position and the new required
    // top position
    return (float) (topBoundCollapsed - topPosition) / slideRange;
  }

  @Override public void draw(Canvas canvas) {
    LayoutParams lp = getLayoutParams(mSheetView);
    if (mCoveredFadeColor != 0 && lp != null && lp.slideOffset > 0) {
      final int baseAlpha = (mCoveredFadeColor & 0xff000000) >>> 24;
      final int imag = (int) (baseAlpha * lp.slideOffset);
      final int color = imag << 24 | (mCoveredFadeColor & 0xffffff);
      mCoveredFadePaint.setColor(color);
      canvas.drawRect(lp.coveredFadeRect, mCoveredFadePaint);
    }
    super.draw(canvas);
  }

  public void setOnSheetStateChangeListener(OnSheetStateChangeListener listener) {
    mOnSheetStateChangeListener = listener;
  }

  @Override protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
    return new LayoutParams();
  }

  @Override protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
    return p instanceof MarginLayoutParams ? new LayoutParams((MarginLayoutParams) p)
        : new LayoutParams(p);
  }

  @Override protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
    return p instanceof LayoutParams && super.checkLayoutParams(p);
  }

  @Override public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
    return new LayoutParams(getContext(), attrs);
  }

  /**
   * Current state of the slideable view.
   */
  public enum State {
    EXPANDED, ANCHORED, HIDDEN, DRAGGING
  }

  public interface OnSheetStateChangeListener {
    void onStateChange(View sheetView, State state, float offset);
  }

  public static class LayoutParams extends MarginLayoutParams {
    private static final int[] ATTRS = new int[] {
        android.R.attr.gravity
    };

    /**
     * The gravity to apply with the View to which these layout parameters
     * are associated.
     *
     * @see Gravity
     */
    public int gravity = -1;
    public int minAnchorHeight;
    public boolean slideable;
    public boolean canSlideToHide = true;
    public boolean closeOnTouchOutside = true;
    public float anchorPoint = DEFAULT_SLIDE_OFFSET;
    public int anchorId;
    private float slideOffset = 0.0f;
    private int slideRange;
    private State state = State.HIDDEN;
    private Rect coveredFadeRect = new Rect();
    private Rect marginTopRect = new Rect();
    private Animator showAnimator;
    private Animator dismissAnimator;

    public LayoutParams() {
      super(MATCH_PARENT, MATCH_PARENT);
    }

    public LayoutParams(int width, int height) {
      super(width, height);
    }

    public LayoutParams(ViewGroup.LayoutParams source) {
      super(source);
    }

    public LayoutParams(MarginLayoutParams source) {
      super(source);
    }

    public LayoutParams(LayoutParams source) {
      super(source);
      this.gravity = source.gravity;
    }

    public LayoutParams(Context c, AttributeSet attrs) {
      super(c, attrs);

      final TypedArray a = c.obtainStyledAttributes(attrs, ATTRS);
      this.gravity = a.getInt(0, -1);
      a.recycle();
    }

    public float computeAnchorPoint() {
      if (minAnchorHeight > 0 && slideRange > 0) {
        return Math.min(minAnchorHeight / (slideRange * 1f), 1.0f);
      } else {
        return anchorPoint;
      }
    }

    public boolean isShowing() {
      return state != State.HIDDEN;
    }

    private void cancelShowAnimation() {
      if (showAnimator != null) {
        showAnimator.cancel();
        showAnimator = null;
      }
    }

    private void cancelDismissAnimation() {
      if (dismissAnimator != null) {
        dismissAnimator.cancel();
        dismissAnimator = null;
      }
    }

    private boolean isAnimating() {
      return showAnimator != null || dismissAnimator != null;
    }
  }

  /**
   * Utility class which registers if the animation has been canceled so that subclasses may
   * respond
   * differently in onAnimationEnd
   */
  private class CancelDetectionAnimationListener extends AnimatorListenerAdapter {

    protected boolean canceled;
    protected Runnable runAfterCancel;
    protected Runnable runAfterEnd;

    public CancelDetectionAnimationListener() {
    }

    public CancelDetectionAnimationListener(Runnable runAfterCancel, Runnable runAfterEnd) {
      this.runAfterCancel = runAfterCancel;
      this.runAfterEnd = runAfterEnd;
    }

    @Override public void onAnimationCancel(Animator animation) {
      canceled = true;
    }
  }

  private class DragHelperCallback extends ViewDragHelper.Callback {

    @Override public boolean tryCaptureView(View child, int pointerId) {
      if (mIsUnableToDrag || isSheetAnimating()) {
        return false;
      }
      if (mSheetView == child) {
        return true;
      } else if (isSheetShowing()) {
        mDragHelper.captureChildView(mSheetView, pointerId);
      }
      return false;
    }

    @Override public void onViewDragStateChanged(int state) {
      LayoutParams lp = getLayoutParams(mSheetView);
      if (mDragHelper.getViewDragState() == ViewDragHelper.STATE_IDLE && lp != null) {
        lp.slideOffset = computeSlideOffset(lp.slideRange, mSheetView.getTop());

        if (lp.slideOffset == 1) {
          lp.state = State.EXPANDED;
        } else if (lp.slideOffset == 0) {
          lp.state = State.HIDDEN;
        } else {
          lp.state = State.ANCHORED;
        }
        if (mOnSheetStateChangeListener != null) {
          mOnSheetStateChangeListener.onStateChange(mSheetView, lp.state, lp.slideOffset);
        }
      }
    }

    @Override
    public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
      onSheetDragged(top);
      invalidate();
    }

    @Override public void onViewCaptured(View capturedChild, int activePointerId) {
      super.onViewCaptured(capturedChild, activePointerId);
    }

    @Override public void onViewReleased(View releasedChild, float xvel, float yvel) {
      int target = 0;

      // direction is always positive if we are sliding in the expanded direction
      float direction = -yvel;

      LayoutParams lp = getLayoutParams(mSheetView);
      if (lp == null) return;
      float slideOffset = lp.slideOffset;
      float anchorPoint = lp.computeAnchorPoint();
      if (direction > 0 && slideOffset <= anchorPoint) {
        // swipe up -> expand and stop at anchor point
        target = computeSheetTopPosition(lp.slideRange, anchorPoint);
      } else if (direction > 0 && slideOffset > anchorPoint) {
        // swipe up past anchor -> expand
        target = computeSheetTopPosition(lp.slideRange, 1.0f);
      } else if (direction < 0 && slideOffset >= anchorPoint) {
        // swipe down -> collapse and stop at anchor point
        target = computeSheetTopPosition(lp.slideRange, anchorPoint);
      } else if (direction < 0 && slideOffset < anchorPoint) {
        // swipe down past anchor -> collapse
        target = computeSheetTopPosition(lp.slideRange,
            !lp.canSlideToHide ? lp.computeAnchorPoint() : 0.f);
      } else if (slideOffset >= (1.f + anchorPoint) / 2) {
        // zero velocity, and far enough from anchor point => expand to the top
        target = computeSheetTopPosition(lp.slideRange, 1.0f);
      } else if (slideOffset >= anchorPoint / 2) {
        // zero velocity, and close enough to anchor point => go to anchor
        target = computeSheetTopPosition(lp.slideRange, anchorPoint);
      } else {
        // settle at the bottom
        target = computeSheetTopPosition(lp.slideRange,
            !lp.canSlideToHide ? lp.computeAnchorPoint() : 0.f);
      }

      mDragHelper.settleCapturedViewAt(releasedChild.getLeft(), target);
      invalidate();
    }

    @Override public int getViewVerticalDragRange(View child) {
      return getLayoutParams(child).slideRange;
    }

    @Override public int clampViewPositionHorizontal(View child, int left, int dx) {
      return child.getLeft();
    }

    @Override public int clampViewPositionVertical(View child, int top, int dy) {
      LayoutParams lp = getLayoutParams(child);
      final int collapsedTop = computeSheetTopPosition(lp.slideRange,
          !lp.canSlideToHide ? lp.computeAnchorPoint() : 0.f);
      final int expandedTop = computeSheetTopPosition(lp.slideRange, 1.0f);
      return Math.min(Math.max(top, expandedTop), collapsedTop);
    }

    @Override public int getOrderedChildIndex(int index) {
      int sheetViewIndex = indexOfChild(mSheetView);
      if (sheetViewIndex != -1) {
        if (index == getChildCount() - 1) {
          index = sheetViewIndex;
        } else if (index >= sheetViewIndex) {
          index = index + 1;
        }
      }
      return index;
    }

    @Override public View findTopChildUnder(int x, int y) {
      return isUnder(getLayoutParams(mSheetView).coveredFadeRect, x, y) ? mSheetView : null;
    }
  }
}
