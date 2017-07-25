package com.mdroid;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import com.mdroid.utils.Ln;

public class Library {

  private static Library mLibrary;

  Context mContext;

  private Library(Context context) {
    mContext = context;
    boolean isDebug = isDebug();
    Ln.getConfig().setLoggingLevel(isDebug ? 0 : Log.ASSERT);
  }

  public static void init(Context context) {
    if (mLibrary != null) {
      return;
    }
    mLibrary = new Library(context.getApplicationContext());
  }

  public static Library Instance() {
    return mLibrary;
  }

  public boolean isDebug() {
    try {
      PackageInfo packageInfo =
          mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
      int flags = packageInfo.applicationInfo.flags;
      return (flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
    } catch (NameNotFoundException e) {
      return false;
    }
  }

  public Context getContext() {
    return mContext;
  }
}
