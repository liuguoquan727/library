package com.mdroid.view.recyclerView.flexibledivider;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class DrawableDivider extends FlexibleDivider {
  private static final int[] ATTRS = new int[] {
      android.R.attr.listDivider
  };

  protected DrawableProvider mDrawableProvider;

  public DrawableDivider(Context context) {
    mDrawableProvider = new SimpleDrawableProvider(context);
  }

  public DrawableDivider(DrawableProvider provider) {
    this.mDrawableProvider = provider;
  }

  public DrawableDivider(DrawableProvider drawableProvider, MarginProvider marginProvider,
      VisibilityProvider visibilityProvider) {
    super(marginProvider, visibilityProvider);
    mDrawableProvider = drawableProvider;
  }

  public DrawableDivider(final Drawable drawable) {
    mDrawableProvider = new DrawableProvider() {
      @Override public Drawable dividerDrawable(int position, RecyclerView parent) {
        return drawable;
      }

      @Override public int dividerSize(int position, RecyclerView parent) {
        return -1;
      }
    };
  }

  @Override void draw(Canvas c, RecyclerView parent, int position, View child, boolean forFix) {
    Rect bounds = getDividerBound(position, parent, child, forFix);
    Drawable drawable = mDrawableProvider.dividerDrawable(position, parent);
    if (drawable != null) {
      drawable.setBounds(bounds);
      drawable.draw(c);
    }
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
        bounds.right = bounds.left + dividerSize;
      } else {
        bounds.right = child.getLeft() - params.leftMargin;
        bounds.left = bounds.right - dividerSize;
      }
    } else {
      bounds.left = parent.getPaddingLeft() + mMarginProvider.dividerStartMargin(position, parent);
      bounds.right =
          parent.getWidth() - parent.getPaddingRight() - mMarginProvider.dividerEndMargin(position,
              parent);
      if (forFix) {// last divider
        bounds.top = child.getBottom() + params.bottomMargin;
        bounds.bottom = bounds.top + dividerSize;
      } else {
        bounds.bottom = child.getTop() - params.topMargin;
        bounds.top = bounds.bottom - dividerSize;
      }
    }
    return bounds;
  }

  @Override protected int getDividerSize(int position, RecyclerView parent) {
    int size = mDrawableProvider.dividerSize(position, parent);
    if (size < 0) {
      Drawable drawable = mDrawableProvider.dividerDrawable(position, parent);
      size = getOrientation(parent) == LinearLayoutManager.HORIZONTAL ? drawable.getIntrinsicWidth()
          : drawable.getIntrinsicHeight();
    }
    return size;
  }

  /**
   * Interface for controlling drawable object for divider drawing
   */
  public interface DrawableProvider {

    /**
     * Returns drawable instance for divider
     *
     * @param position Divider position
     * @param parent RecyclerView
     * @return Drawable instance
     */
    public Drawable dividerDrawable(int position, RecyclerView parent);

    /**
     * Returns size value of divider.
     * Height for horizontal divider, width for vertical divider
     *
     * @param position Divider position
     * @param parent RecyclerView
     * @return Size of divider. If a negative number is returned, will use value of the drawable
     */
    public int dividerSize(int position, RecyclerView parent);
  }

  public static class SimpleDrawableProvider implements DrawableProvider {
    private Drawable mDrawable;

    public SimpleDrawableProvider(Context context) {
      TypedArray a = context.obtainStyledAttributes(ATTRS);
      mDrawable = a.getDrawable(0);
      a.recycle();
    }

    @Override public Drawable dividerDrawable(int position, RecyclerView parent) {
      return mDrawable;
    }

    @Override public int dividerSize(int position, RecyclerView parent) {
      return -1;
    }
  }
}
