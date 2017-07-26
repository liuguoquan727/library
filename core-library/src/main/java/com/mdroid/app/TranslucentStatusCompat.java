package com.mdroid.app;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class TranslucentStatusCompat {
  static final TranslucentStatusCompatImpl IMPL;

  static {
    final int version = Build.VERSION.SDK_INT;
    if (version >= Build.VERSION_CODES.LOLLIPOP) {
      IMPL = new LollipopTranslucentStatusCompatImpl();
    } else if (version >= Build.VERSION_CODES.KITKAT) {
      IMPL = new KitKatTranslucentStatusCompatImpl();
    } else {
      IMPL = new BaseViewCompatImpl();
    }
  }

  public static void requestTranslucentStatus(Activity activity) {
    IMPL.requestTranslucentStatus(activity);
  }

  private interface TranslucentStatusCompatImpl {
    void requestTranslucentStatus(Activity activity);
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP) private static class LollipopTranslucentStatusCompatImpl
      implements TranslucentStatusCompatImpl {
    @Override public void requestTranslucentStatus(Activity activity) {
      Window window = activity.getWindow();
      window.getDecorView()
          .setSystemUiVisibility(window.getDecorView().getSystemUiVisibility()
              | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
              | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
      window.setStatusBarColor(Color.TRANSPARENT);
    }
  }

  @TargetApi(Build.VERSION_CODES.KITKAT) private static class KitKatTranslucentStatusCompatImpl
      implements TranslucentStatusCompatImpl {
    @Override public void requestTranslucentStatus(Activity activity) {
      activity.getWindow()
          .setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
              WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }
  }

  private static class BaseViewCompatImpl implements TranslucentStatusCompatImpl {
    @Override public void requestTranslucentStatus(Activity activity) {

    }
  }
}
