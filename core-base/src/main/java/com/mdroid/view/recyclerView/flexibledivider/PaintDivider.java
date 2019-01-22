package com.mdroid.view.recyclerView.flexibledivider;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class PaintDivider extends FlexibleDivider {
  private static final int DEFAULT_SIZE = 2;
  protected PaintProvider mPaintProvider;

  public PaintDivider() {
    mPaintProvider = new SimplePaintProvider();
  }

  public PaintDivider(PaintProvider provider) {
    mPaintProvider = provider;
  }

  public PaintDivider(PaintProvider paintProvider, MarginProvider marginProvider,
      VisibilityProvider visibilityProvider) {
    super(marginProvider, visibilityProvider);
    mPaintProvider = paintProvider;
  }

  public PaintDivider(final int color) {
    mPaintProvider = new PaintProvider() {
      @Override public Paint dividerPaint(int position, RecyclerView parent) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
        return paint;
      }

      @Override public int dividerSize(int position, RecyclerView parent) {
        return -1;
      }
    };
  }

  @Override void draw(Canvas c, RecyclerView parent, int position, View child, boolean forFix) {
    Rect bounds = getDividerBound(position, parent, child, forFix);
    Paint paint = mPaintProvider.dividerPaint(position, parent);
    c.drawLine(bounds.left, bounds.top, bounds.right, bounds.bottom, paint);
  }

  protected Rect getDividerBound(int position, RecyclerView parent, View child, boolean forFix) {
    Rect bounds = new Rect(0, 0, 0, 0);
    RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
    int dividerSize = getDividerSize(position, parent);
    if (getOrientation(parent) == LinearLayoutManager.HORIZONTAL) {
      bounds.top = parent.getPaddingTop() + mMarginProvider.dividerStartMargin(position, parent);
      bounds.bottom =
          parent.getHeight() - parent.getPaddingBottom() - mMarginProvider.dividerEndMargin(
              position, parent);
      if (forFix) {// last divider
        bounds.left = child.getRight() + params.rightMargin;
        bounds.right = bounds.left + dividerSize / 2;
      } else {
        bounds.right = child.getLeft() - params.leftMargin;
        bounds.left = bounds.right - dividerSize / 2;
      }
    } else {
      bounds.left = parent.getPaddingLeft() + mMarginProvider.dividerStartMargin(position, parent);
      bounds.right =
          parent.getWidth() - parent.getPaddingRight() - mMarginProvider.dividerEndMargin(position,
              parent);
      if (forFix) {// last divider
        bounds.top = child.getBottom() + params.bottomMargin + dividerSize / 2;
        bounds.bottom = bounds.top;
      } else {
        bounds.bottom = child.getTop() - params.topMargin - dividerSize / 2;
        bounds.top = bounds.bottom;
      }
    }
    return bounds;
  }

  @Override protected int getDividerSize(int position, RecyclerView parent) {
    int size = mPaintProvider.dividerSize(position, parent);
    if (size < 0) {
      size = (int) mPaintProvider.dividerPaint(position, parent).getStrokeWidth();
      if (size == 0) {
        size = DEFAULT_SIZE;
      }
    }
    return size;
  }

  /**
   * Interface for controlling paint instance for divider drawing
   */
  public interface PaintProvider {

    /**
     * Returns {@link Paint} for divider
     *
     * @param position Divider position
     * @param parent RecyclerView
     * @return Paint instance
     */
    public Paint dividerPaint(int position, RecyclerView parent);

    /**
     * Returns size value of divider.
     * Height for horizontal divider, width for vertical divider
     *
     * @param position Divider position
     * @param parent RecyclerView
     * @return Size of divider
     */
    public int dividerSize(int position, RecyclerView parent);
  }

  public static class SimplePaintProvider implements PaintProvider {
    @Override public Paint dividerPaint(int position, RecyclerView parent) {
      return new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override public int dividerSize(int position, RecyclerView parent) {
      return -1;
    }
  }
}
