package com.mdroid.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewCompat;
import android.util.TypedValue;
import android.view.View;
import android.widget.AbsListView;

public class ViewUtils {

  private static TypedValue sTypedValue = new TypedValue();
  private static int sActionBarHeight;

  public static Bitmap drawViewToBitmap(Bitmap dest, View view, int width, int height,
      int downSampling, Drawable drawable) {
    float scale = 1f / downSampling;
    int heightCopy = view.getHeight();
    view.layout(0, 0, width, height);
    int bmpWidth = (int) (width * scale);
    int bmpHeight = (int) (height * scale);
    if (dest == null || dest.getWidth() != bmpWidth || dest.getHeight() != bmpHeight) {
      dest = Bitmap.createBitmap(bmpWidth, bmpHeight, Bitmap.Config.ARGB_8888);
    }
    Canvas c = new Canvas(dest);
    drawable.setBounds(new Rect(0, 0, width, height));
    drawable.draw(c);
    if (downSampling > 1) {
      c.scale(scale, scale);
    }
    view.draw(c);
    view.layout(0, 0, width, heightCopy);
    return dest;
  }

  public static int getActionBarHeight(Context context) {
    if (sActionBarHeight != 0) {
      return sActionBarHeight;
    }

    context.getTheme().resolveAttribute(android.R.attr.actionBarSize, sTypedValue, true);
    sActionBarHeight = TypedValue.complexToDimensionPixelSize(sTypedValue.data,
        context.getResources().getDisplayMetrics());
    return sActionBarHeight;
  }

  /**
   * 能否向上滚动(即: 手指向下滑动)
   */
  public static boolean canScrollUp(View target) {
    if (android.os.Build.VERSION.SDK_INT < 14) {
      if (target instanceof AbsListView) {
        final AbsListView absListView = (AbsListView) target;
        return absListView.getChildCount() > 0 && (absListView.getFirstVisiblePosition() > 0
            || absListView.getChildAt(0).getTop() < absListView.getPaddingTop());
      } else {
        return ViewCompat.canScrollVertically(target, -1) || target.getScrollY() > 0;
      }
    } else {
      return ViewCompat.canScrollVertically(target, -1);
    }
  }

  /**
   * 能否向下滚动(即: 手指向上滑动)
   */
  public static boolean canScrollDown(View target) {
    if (android.os.Build.VERSION.SDK_INT < 14) {
      if (target instanceof AbsListView) {
        final AbsListView absListView = (AbsListView) target;
        View lastChild = absListView.getChildAt(absListView.getChildCount() - 1);
        if (lastChild != null) {
          return (absListView.getLastVisiblePosition() == (absListView.getCount() - 1))
              && lastChild.getBottom() > absListView.getPaddingBottom();
        } else {
          return false;
        }
      } else {
        return ViewCompat.canScrollVertically(target, 1)
            || target.getHeight() - target.getScrollY() > 0;
      }
    } else {
      return ViewCompat.canScrollVertically(target, 1);
    }
  }
}
