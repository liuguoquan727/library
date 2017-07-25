package com.mdroid.lib.core.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import com.mdroid.lib.core.base.BaseApp;
import com.tendcloud.tenddata.TCAgent;
import com.umeng.analytics.MobclickAgent;
import java.util.HashMap;

/**
 * 数据统计
 */
public class Analysis {
  private static final boolean TC = true;
  private static final boolean UM = true;
  private static boolean initialed = false;

  private Analysis() {
  }

  public static void init(Context context) {
    if (BaseApp.Instance().isDebug()) return;
    if (TC) {
      TCAgent.init(context.getApplicationContext());
      TCAgent.LOG_ON = isDebug(context);
    }
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
    if (TC) {
      TCAgent.onResume(activity);
    }
  }

  public static void onPause(Activity activity) {
    if (BaseApp.Instance().isDebug()) return;
    if (!initialed) init(activity);
    if (UM) {
      MobclickAgent.onPause(activity);
    }
    if (TC) {
      TCAgent.onPause(activity);
    }
  }

  public static void onPageStart(Context context, String name) {
    if (BaseApp.Instance().isDebug()) return;
    if (!initialed) init(context);
    if (UM) {
      MobclickAgent.onPageStart(name);
    }
    if (TC) {
      TCAgent.onPageStart(context, name);
    }
  }

  public static void onPageEnd(Context context, String name) {
    if (BaseApp.Instance().isDebug()) return;
    if (!initialed) init(context);
    if (UM) {
      MobclickAgent.onPageEnd(name);
    }
    if (TC) {
      TCAgent.onPageEnd(context, name);
    }
  }

  public static void onEvent(Context context, String eventId) {
    if (BaseApp.Instance().isDebug()) return;
    if (!initialed) init(context);
    if (UM) {
      MobclickAgent.onEvent(context, eventId);
    }
    if (TC) {
      TCAgent.onEvent(context, eventId);
    }
  }

  public static void onEvent(Context context, String eventId, String label) {
    if (BaseApp.Instance().isDebug()) return;
    if (!initialed) init(context);
    if (UM) {
      MobclickAgent.onEvent(context, eventId, label);
    }
    if (TC) {
      TCAgent.onEvent(context, eventId, label);
    }
  }

  public static void onEvent(Context context, String eventId, HashMap<String, String> map) {
    if (BaseApp.Instance().isDebug()) return;
    if (!initialed) init(context);
    if (UM) {
      MobclickAgent.onEvent(context, eventId, map);
    }
    if (TC) {
      TCAgent.onEvent(context, eventId, null, map);
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
