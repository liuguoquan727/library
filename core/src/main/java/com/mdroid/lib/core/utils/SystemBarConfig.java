package com.mdroid.lib.core.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.util.TypedValue;
import java.lang.reflect.Method;

public class SystemBarConfig {
  private static final String STATUS_BAR_HEIGHT_RES_NAME = "status_bar_height";
  private static final String NAV_BAR_HEIGHT_RES_NAME = "navigation_bar_height";
  private static final String NAV_BAR_HEIGHT_LANDSCAPE_RES_NAME = "navigation_bar_height_landscape";
  private static final String NAV_BAR_WIDTH_RES_NAME = "navigation_bar_width";
  private static final String SHOW_NAV_BAR_RES_NAME = "config_showNavigationBar";
  private static String sNavBarOverride;

  static {
    // Android allows a system property to override the presence of the navigation bar.
    // Used by the emulator.
    // See https://github.com/android/platform_frameworks_base/blob/master/policy/src/com/android/internal/policy/impl/PhoneWindowManager.java#L1076
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      try {
        Class c = Class.forName("android.os.SystemProperties");
        Method m = c.getDeclaredMethod("get", String.class);
        m.setAccessible(true);
        sNavBarOverride = (String) m.invoke(null, "qemu.hw.mainkeys");
      } catch (Throwable e) {
        sNavBarOverride = null;
      }
    }
  }

  private final int mStatusBarHeight;
  private final int mToolBarHeight;
  private final boolean mHasNavigationBar;
  private final int mNavigationBarHeight;
  private final int mNavigationBarWidth;
  private final boolean mInPortrait;

  public SystemBarConfig(Context context) {
    Resources res = context.getResources();
    mInPortrait = (res.getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT);
    mStatusBarHeight = getInternalDimensionSize(res, STATUS_BAR_HEIGHT_RES_NAME);
    mToolBarHeight = getActionBarHeight(context);
    mNavigationBarHeight = getNavigationBarHeight(context);
    mNavigationBarWidth = getNavigationBarWidth(context);
    mHasNavigationBar = (mNavigationBarHeight > 0);
  }

  private int getActionBarHeight(Context context) {
    TypedValue tv = new TypedValue();
    context.getTheme().resolveAttribute(com.mdroid.R.attr.toolBarSize, tv, true);
    return TypedValue.complexToDimensionPixelSize(tv.data,
        context.getResources().getDisplayMetrics());
  }

  private int getNavigationBarHeight(Context context) {
    Resources res = context.getResources();
    int result = 0;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
      if (hasNavBar(context)) {
        String key;
        if (mInPortrait) {
          key = NAV_BAR_HEIGHT_RES_NAME;
        } else {
          key = NAV_BAR_HEIGHT_LANDSCAPE_RES_NAME;
        }
        return getInternalDimensionSize(res, key);
      }
    }
    return result;
  }

  private int getNavigationBarWidth(Context context) {
    Resources res = context.getResources();
    int result = 0;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
      if (hasNavBar(context)) {
        return getInternalDimensionSize(res, NAV_BAR_WIDTH_RES_NAME);
      }
    }
    return result;
  }

  private boolean hasNavBar(Context context) {
    Resources res = context.getResources();
    int resourceId = res.getIdentifier(SHOW_NAV_BAR_RES_NAME, "bool", "android");
    if (resourceId != 0) {
      boolean hasNav = res.getBoolean(resourceId);
      // check override flag (see static block)
      if ("1".equals(sNavBarOverride)) {
        hasNav = false;
      } else if ("0".equals(sNavBarOverride)) {
        hasNav = true;
      }
      return hasNav;
    } else { // fallback
      return false;
    }
  }

  private int getInternalDimensionSize(Resources res, String key) {
    int result = 0;
    int resourceId = res.getIdentifier(key, "dimen", "android");
    if (resourceId > 0) {
      result = res.getDimensionPixelSize(resourceId);
    }
    return result;
  }

  /**
   * Get the height of the system status bar.
   *
   * @return The height of the status bar (in pixels).
   */
  public int getStatusBarHeight() {
    return mStatusBarHeight;
  }

  /**
   * Get the height of the tool bar.
   *
   * @return The height of the tool bar (in pixels).
   */
  public int getToolBarHeight() {
    return mToolBarHeight;
  }

  /**
   * Does this device have a system navigation bar?
   *
   * @return True if this device uses soft key navigation, False otherwise.
   */
  public boolean hasNavigtionBar() {
    return mHasNavigationBar;
  }

  /**
   * Get the height of the system navigation bar.
   *
   * @return The height of the navigation bar (in pixels). If the device does not have
   * soft navigation keys, this will always return 0.
   */
  public int getNavigationBarHeight() {
    return mNavigationBarHeight;
  }

  /**
   * Get the width of the system navigation bar when it is placed vertically on the screen.
   *
   * @return The width of the navigation bar (in pixels). If the device does not have
   * soft navigation keys, this will always return 0.
   */
  public int getNavigationBarWidth() {
    return mNavigationBarWidth;
  }
}
