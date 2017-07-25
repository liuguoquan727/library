package com.orhanobut.dialogplus;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public class ContainerLayout extends FrameLayout {
  public ContainerLayout(Context context) {
    super(context);
  }

  public ContainerLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public ContainerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public ContainerLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
  }

  @Override public boolean onTouchEvent(MotionEvent event) {
    return !(isClickable() || isLongClickable() || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
        && isContextClickable())) || super.onTouchEvent(event);
  }
}
