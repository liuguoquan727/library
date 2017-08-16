package com.mdroid.lib.core.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import com.mdroid.lib.core.base.BaseApp;
import com.umeng.analytics.MobclickAgent;
import java.util.HashMap;

/**
 * 数据统计
 */
public class Analysis {
  private static final boolean UM = true;
  private static boolean initialed = false;

  private Analysis() {
  }

  public static void init(Context context) {
    if (BaseApp.Instance().isDebug()) return;
    if (UM) {
      MobclickAgent.openActivityDurationTrack(false);
    }
    initialed = true;
  }

  public static void onResume(Activity activity) {
    if (BaseApp.Instance().isDebug()) return;
    if (!initialed) init(activity);
    if (UM) {
      MobclickAgent.onResume(activity);
    }
  }

  public static void onPause(Activity activity) {
    if (BaseApp.Instance().isDebug()) return;
    if (!initialed) init(activity);
    if (UM) {
      MobclickAgent.onPause(activity);
    }
  }

  public static void onPageStart(Context context, String name) {
    if (BaseApp.Instance().isDebug()) return;
    if (!initialed) init(context);
    if (UM) {
      MobclickAgent.onPageStart(name);
    }
  }

  public static void onPageEnd(Context context, String name) {
    if (BaseApp.Instance().isDebug()) return;
    if (!initialed) init(context);
    if (UM) {
      MobclickAgent.onPageEnd(name);
    }
  }

  public static void onEvent(Context context, String eventId) {
    if (BaseApp.Instance().isDebug()) return;
    if (!initialed) init(context);
    if (UM) {
      MobclickAgent.onEvent(context, eventId);
    }
  }

  public static void onEvent(Context context, String eventId, String label) {
    if (BaseApp.Instance().isDebug()) return;
    if (!initialed) init(context);
    if (UM) {
      MobclickAgent.onEvent(context, eventId, label);
    }
  }

  public static void onEvent(Context context, String eventId, HashMap<String, String> map) {
    if (BaseApp.Instance().isDebug()) return;
    if (!initialed) init(context);
    if (UM) {
      MobclickAgent.onEvent(context, eventId, map);
    }
  }

  private static boolean isDebug(Context context) {
    try {
      PackageInfo packageInfo =
          context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
      int flags = packageInfo.applicationInfo.flags;
      return (flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
    } catch (PackageManager.NameNotFoundException e) {
      return false;
    }
  }
}
