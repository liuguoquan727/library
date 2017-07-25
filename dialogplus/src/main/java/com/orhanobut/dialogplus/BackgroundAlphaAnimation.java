package com.orhanobut.dialogplus;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

class BackgroundAlphaAnimation extends Animation {

  protected final int originalAlpha;
  protected final View view;
  protected float perValue;

  public BackgroundAlphaAnimation(View view, int fromAlpha, int toAlpha) {
    this.view = view;
    this.originalAlpha = fromAlpha;
    this.perValue = (toAlpha - fromAlpha);
  }

  @Override protected void applyTransformation(float interpolatedTime, Transformation t) {
    view.getBackground().setAlpha((int) (originalAlpha + perValue * interpolatedTime));
    view.requestLayout();
  }

  @Override public boolean willChangeBounds() {
    return false;
  }
}