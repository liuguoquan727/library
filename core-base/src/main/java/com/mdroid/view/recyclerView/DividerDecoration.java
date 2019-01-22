package com.mdroid.view.recyclerView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.mdroid.view.recyclerView.flexibledivider.FlexibleDivider;

/**
 * @see FlexibleDivider
 * @deprecated
 */
public class DividerDecoration extends RecyclerView.ItemDecoration {

  public static final int HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL;
  public static final int VERTICAL_LIST = LinearLayoutManager.VERTICAL;
  private static final int[] ATTRS = new int[] {
      android.R.attr.listDivider
  };
  private Drawable mDivider;

  public DividerDecoration(Context context) {
    final TypedArray a = context.obtainStyledAttributes(ATTRS);
    mDivider = a.getDrawable(0);
    a.recycle();
  }

  public DividerDecoration(Drawable divider) {
    mDivider = divider;
  }

  public DividerDecoration(Context context, int resid) {
    mDivider = context.getResources().getDrawable(resid);
  }

  private int getOrientation(RecyclerView parent) {
    LinearLayoutManager layoutManager;
    try {
      layoutManager = (LinearLayoutManager) parent.getLayoutManager();
    } catch (ClassCastException e) {
      throw new IllegalStateException(
          "DividerDecoration can only be used with a " + "LinearLayoutManager.", e);
    }
    return layoutManager.getOrientation();
  }

  @Override public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
    super.onDraw(c, parent, state);
    if (getOrientation(parent) == VERTICAL_LIST) {
      drawVertical(c, parent);
    } else {
      drawHorizontal(c, parent);
    }
  }

  public void drawVertical(Canvas c, RecyclerView parent) {
    final int left = parent.getPaddingLeft();
    final int right = parent.getWidth() - parent.getPaddingRight();

    final int childCount = parent.getChildCount();
    for (int i = 0; i < childCount; i++) {
      final View child = parent.getChildAt(i);
      final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
      final int top = child.getBottom() + params.bottomMargin;
      final int bottom = top + mDivider.getIntrinsicHeight();
      mDivider.setBounds(left, top, right, bottom);
      mDivider.draw(c);
    }
  }

  public void drawHorizontal(Canvas c, RecyclerView parent) {
    final int top = parent.getPaddingTop();
    final int bottom = parent.getHeight() - parent.getPaddingBottom();

    final int childCount = parent.getChildCount();
    for (int i = 0; i < childCount; i++) {
      final View child = parent.getChildAt(i);
      final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
      final int left = child.getRight() + params.rightMargin;
      final int right = left + mDivider.getIntrinsicHeight();
      mDivider.setBounds(left, top, right, bottom);
      mDivider.draw(c);
    }
  }

  @Override public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
      RecyclerView.State state) {
    super.getItemOffsets(outRect, view, parent, state);
    if (getOrientation(parent) == VERTICAL_LIST) {
      outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
    } else {
      outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0);
    }
  }
}