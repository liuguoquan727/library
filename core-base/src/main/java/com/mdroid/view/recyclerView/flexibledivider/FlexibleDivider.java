package com.mdroid.view.recyclerView.flexibledivider;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public abstract class FlexibleDivider extends RecyclerView.ItemDecoration {
  protected MarginProvider mMarginProvider;
  protected VisibilityProvider mVisibilityProvider;

  public FlexibleDivider() {
    this(new SimpleMarginProvider(), new SimpleVisibilityProvider());
  }

  public FlexibleDivider(MarginProvider marginProvider, VisibilityProvider visibilityProvider) {
    this.mMarginProvider = marginProvider;
    this.mVisibilityProvider = visibilityProvider;
  }

  protected static int getOrientation(RecyclerView parent) {
    LinearLayoutManager layoutManager;
    try {
      layoutManager = (LinearLayoutManager) parent.getLayoutManager();
    } catch (ClassCastException e) {
      throw new IllegalStateException(
          "FlexibleDivider can only be used with a LinearLayoutManager.", e);
    }
    return layoutManager.getOrientation();
  }

  @Override public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
    int childCount = parent.getChildCount();
    for (int i = 0; i < childCount; i++) {
      View child = parent.getChildAt(i);
      int position = parent.getChildAdapterPosition(child);
      preDraw(c, parent, position, child, false);
      if (i == childCount - 1) {
        preDraw(c, parent, position + 1, child, true);
      }
    }
  }

  @Override
  public void getItemOffsets(Rect outRect, View v, RecyclerView parent, RecyclerView.State state) {
    int position = parent.getChildLayoutPosition(v);
    int size = getDividerSize(position, parent);
    int nextSize = getDividerSize(position + 1, parent);
    if (getOrientation(parent) == LinearLayoutManager.HORIZONTAL) {
      if (position == 0) {// first item
        outRect.set(size, 0, nextSize / 2, 0);
      } else {
        if (position == parent.getLayoutManager().getItemCount() - 1) {// last item
          outRect.set((int) Math.ceil(size / 2f), 0, nextSize, 0);
        } else {
          outRect.set((int) Math.ceil(size / 2f), 0, (int) Math.floor(nextSize / 2f), 0);
        }
      }
    } else {
      if (position == 0) {// first item
        outRect.set(0, size, 0, nextSize / 2);
      } else {
        if (position == parent.getLayoutManager().getItemCount() - 1) {// last item
          outRect.set(0, (int) Math.ceil(size / 2f), 0, nextSize);
        } else {
          outRect.set(0, (int) Math.ceil(size / 2f), 0, (int) Math.floor(nextSize / 2f));
        }
      }
    }
  }

  void preDraw(Canvas c, RecyclerView parent, int position, View child, boolean forFix) {
    if (mVisibilityProvider.shouldHideDivider(position, parent)) {
      return;
    }

    draw(c, parent, position, child, forFix);
  }

  abstract void draw(Canvas c, RecyclerView parent, int position, View child, boolean forFix);

  protected abstract int getDividerSize(int position, RecyclerView parent);

  /**
   * Interface for controlling paint instance for divider drawing
   */
  public interface PaintLineProvider {

    /**
     * Returns {@link Paint} for divider
     *
     * @param position Divider position
     * @param parent RecyclerView
     * @return Paint instance
     */
    public Paint dividerPaint(int position, RecyclerView parent);
  }

  /**
   * Interface for controlling divider visibility
   */
  public interface VisibilityProvider {

    /**
     * Returns true if divider should be hidden.
     *
     * @param position Divider position
     * @param parent RecyclerView
     * @return True if the divider at position should be hidden
     */
    public boolean shouldHideDivider(int position, RecyclerView parent);
  }

  /**
   * Interface for controlling divider margin
   */
  public interface MarginProvider {

    /**
     * Returns left margin of divider.
     *
     * @param position Divider position
     * @param parent RecyclerView
     * @return Start margin
     */
    public int dividerStartMargin(int position, RecyclerView parent);

    /**
     * Returns right margin of divider.
     *
     * @param position Divider position
     * @param parent RecyclerView
     * @return End margin
     */
    public int dividerEndMargin(int position, RecyclerView parent);
  }

  public static class SimpleVisibilityProvider implements VisibilityProvider {
    private VisibilityProvider mVisibilityProvider;

    public SimpleVisibilityProvider() {
    }

    public SimpleVisibilityProvider(VisibilityProvider visibilityProvider) {
      this.mVisibilityProvider = visibilityProvider;
    }

    @Override public boolean shouldHideDivider(int position, RecyclerView parent) {
      if (mVisibilityProvider != null) {
        return mVisibilityProvider.shouldHideDivider(position, parent);
      }
      return false;
    }
  }

  public static class SimpleMarginProvider implements MarginProvider {

    private MarginProvider mMarginProvider;

    public SimpleMarginProvider() {
    }

    public SimpleMarginProvider(MarginProvider marginProvider) {
      this.mMarginProvider = marginProvider;
    }

    @Override public int dividerStartMargin(int position, RecyclerView parent) {
      if (mMarginProvider != null) {
        return mMarginProvider.dividerStartMargin(position, parent);
      }
      return 0;
    }

    @Override public int dividerEndMargin(int position, RecyclerView parent) {
      if (mMarginProvider != null) {
        return mMarginProvider.dividerEndMargin(position, parent);
      }
      return 0;
    }
  }
}