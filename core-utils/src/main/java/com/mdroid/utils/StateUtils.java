package com.mdroid.utils;

import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.core.view.MotionEventCompat;

public class StateUtils {

  private static final float[] STATE_PRESSED = new float[] {
      1, 0, 0, 0, -20, 0, 1, 0, 0, -20, 0, 0, 1, 0, -20, 0, 0, 0, 1, 0
  };
  private static final float[] STATE_NORMAL = new float[] {
      1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0
  };
  private static final float[] STATE_FOCUSED = new float[] {
      1.20742f, -0.18282f, -0.0246f, 0, 40, -0.09258f, 1.11718f, -0.0246f, 0, 40, -0.09258f,
      -0.18282f, 1.2754f, 0, 40, 0, 0, 0, 1, 0
  };
  private static final float[] STATE_ENABLED = new float[] {
      0.37774f, 0.54846f, 0.0738f, 0, 0, 0.27774f, 0.64846f, 0.0738f, 0, 0, 0.27774f, 0.54846f,
      0.1738f, 0, 0, 0, 0, 0, 1, 0
  };
  private static final String TAG = StateUtils.class.getSimpleName();

  public static void setBackground(View view, Drawable drawable) {
    drawable = getSelector(drawable);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
      view.setBackground(drawable);
    } else {
      view.setBackgroundDrawable(drawable);
    }
  }

  public static void updateForeground(FrameLayout view) {
    view.setForeground(getSelector(view.getForeground()));
  }

  public static void updateBackground(View view) {
    setBackground(view, view.getBackground());
  }

  public static void setImageDrawable(ImageView view, Drawable drawable) {
    drawable = getSelector(drawable);
    view.setImageDrawable(drawable);
  }

  public static void updateImageDrawable(ImageView view) {
    Drawable drawable = getSelector(view.getDrawable());
    view.setImageDrawable(drawable);
  }

  public static Drawable getSelector(Drawable drawable) {
    Drawable normal;
    if (drawable == null) {
      normal = new ColorDrawable(Color.TRANSPARENT);
      drawable = new ColorDrawable(0x0f333333);
    } else {
      normal = drawable;
      if (drawable instanceof ColorDrawable
          && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
        Log.w(TAG, "ColorDrawable not support, Instead of ShapeDrawable");
      }
    }
    FilterableStateListDrawable selector = new FilterableStateListDrawable();
    //"-"号表示该状态值为 false, 否则为 true
    //注意该处的顺序，匹配状态时，是一种优先包含的关系.
    selector.addState(new int[] { android.R.attr.state_enabled, android.R.attr.state_focused },
        drawable, new ColorMatrixColorFilter(STATE_FOCUSED));
    selector.addState(new int[] { android.R.attr.state_enabled, android.R.attr.state_pressed },
        drawable, new ColorMatrixColorFilter(STATE_PRESSED));
    selector.addState(new int[] { android.R.attr.state_selected }, drawable,
        new ColorMatrixColorFilter(STATE_PRESSED));
    selector.addState(new int[] { -android.R.attr.state_enabled }, drawable,
        new ColorMatrixColorFilter(STATE_ENABLED));
    selector.addState(new int[] {}, normal);
    return selector;
  }

  /**
   * 设置控件点击变暗
   */
  public static void setDarkWhenPressed(View view) {
    setDarkWhenPressed(view, null);
  }

  /**
   * 设置控件点击变暗
   */
  public static void setDarkWhenPressed(View view, View.OnTouchListener onTouchListener) {
    view.setClickable(true);
    view.setOnTouchListener(new DarkerTouchListener(onTouchListener));
  }

  /**
   * 改变控件点击状态, 控件必须为 clickable = true 才会生效
   */
  private static class DarkerTouchListener implements View.OnTouchListener {
    private View.OnTouchListener mExtraOnTouchListener;

    public DarkerTouchListener(View.OnTouchListener onTouchListener) {
      this.mExtraOnTouchListener = onTouchListener;
    }

    public void setExtraOnTouchListener(View.OnTouchListener onTouchListener) {
      this.mExtraOnTouchListener = onTouchListener;
    }

    @Override public boolean onTouch(View v, MotionEvent event) {
      boolean consumed = mExtraOnTouchListener != null && mExtraOnTouchListener.onTouch(v, event);
      if (!v.isClickable()) {
        return consumed;
      }

      final int action = MotionEventCompat.getActionMasked(event);
      switch (action) {
        case MotionEvent.ACTION_DOWN: {
          if (v instanceof ImageView) {
            ImageView iv = (ImageView) v;
            iv.setColorFilter(new ColorMatrixColorFilter(STATE_PRESSED));
          } else {
            Drawable background = v.getBackground();
            if (background != null) {
              background.setColorFilter(new ColorMatrixColorFilter(STATE_PRESSED));
              background.invalidateSelf();
            }
          }
          break;
        }
        case MotionEvent.ACTION_CANCEL:
        case MotionEvent.ACTION_OUTSIDE:
        case MotionEvent.ACTION_UP: {
          if (v instanceof ImageView) {
            ImageView iv = (ImageView) v;
            iv.setColorFilter(new ColorMatrixColorFilter(STATE_NORMAL));
          } else {
            Drawable background = v.getBackground();
            if (background != null) {
              background.setColorFilter(new ColorMatrixColorFilter(STATE_NORMAL));
              background.invalidateSelf();
            }
          }
          break;
        }
      }
      return consumed;
    }
  }

  /**
   * This is an extension to {@link StateListDrawable} that workaround a bug not allowing
   * to set a {@link ColorFilter} to the drawable in one of the states., it add a method
   * {@link #addState(int[], Drawable, ColorFilter)} for that purpose.
   */
  public static class FilterableStateListDrawable extends StateListDrawable {

    private int currIdx = -1;
    private int childrenCount = 0;
    private SparseArray<ColorFilter> filterMap;

    public FilterableStateListDrawable() {
      super();
      filterMap = new SparseArray<ColorFilter>();
    }

    @Override public void addState(int[] stateSet, Drawable drawable) {
      super.addState(stateSet, drawable);
      childrenCount++;
    }

    /**
     * Same as {@link #addState(int[], Drawable)}, but allow to set a colorFilter associated to
     * this
     * Drawable.
     *
     * @param stateSet - An array of resource Ids to associate with the image.
     * Switch to this image by calling setState().
     * @param drawable -The image to show.
     * @param colorFilter - The {@link ColorFilter} to apply to this state
     */
    public void addState(int[] stateSet, Drawable drawable, ColorFilter colorFilter) {
      int currChild = childrenCount;
      addState(stateSet, drawable);
      filterMap.put(currChild, colorFilter);
    }

    @Override public boolean selectDrawable(int idx) {
      if (currIdx != idx) {
        setColorFilter(getColorFilterForIdx(idx));
      }
      boolean result = super.selectDrawable(idx);
      if (getCurrent() != null) {
        currIdx = result ? idx : currIdx;
        if (!result) {
          setColorFilter(getColorFilterForIdx(currIdx));
        }
      } else {
        currIdx = -1;
        setColorFilter(null);
      }
      return result;
    }

    private ColorFilter getColorFilterForIdx(int idx) {
      return filterMap != null ? filterMap.get(idx) : null;
    }
  }
}
