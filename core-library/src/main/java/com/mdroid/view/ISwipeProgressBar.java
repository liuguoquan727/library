package com.mdroid.view;

public interface ISwipeProgressBar {
  void setTriggerPercentage(float percent);

  void setPercentage(float percent);

  void setColorSchemeResources(int colorRes1, int colorRes2, int colorRes3, int colorRes4);

  void setColorSchemeColors(int color1, int color2, int color3, int color4);

  void start();

  void stop();
}
