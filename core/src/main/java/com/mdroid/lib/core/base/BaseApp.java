package com.mdroid.lib.core.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Message;
import android.support.multidex.MultiDexApplication;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.bind.TypeAdapters;
import com.mdroid.Library;
import com.mdroid.PausedHandler;
import com.mdroid.lib.core.json.DoubleAdapter;
import com.mdroid.lib.core.json.IntegerAdapter;
import com.mdroid.lib.core.json.LongAdapter;
import com.mdroid.lib.core.utils.Analysis;
import com.mdroid.lib.core.utils.Toost;
import com.mdroid.lifecycle.LifecycleDispatcher;

public abstract class BaseApp extends MultiDexApplication {
  @SuppressLint("StaticFieldLeak") protected static BaseApp mInstance;
  protected boolean mVisible = false;
  protected PausedHandler mHandler;
  protected Gson mGson;
  // 当前处于最顶层的activity
  private Activity mTopActivity;

  public Activity getTopActivity() {
    return mTopActivity;
  }

  public void setTopActivity(Activity topActivity) {
    mTopActivity = topActivity;
  }

  public static BaseApp Instance() {
    return mInstance;
  }

  public static PausedHandler getMainHandler() {
    return mInstance.mHandler;
  }

  public static boolean isVisible() {
    return mInstance.mVisible;
  }

  public static synchronized Gson getGson() {
    if (mInstance.mGson == null) {
      mInstance.mGson = new GsonBuilder().registerTypeAdapterFactory(
          TypeAdapters.newFactory(int.class, Integer.class, new IntegerAdapter()))
          .registerTypeAdapterFactory(
              TypeAdapters.newFactory(double.class, Double.class, new DoubleAdapter()))
          .registerTypeAdapterFactory(
              TypeAdapters.newFactory(long.class, Long.class, new LongAdapter()))
          .create();
    }
    return mInstance.mGson;
  }

  @Override public void onCreate() {
    super.onCreate();
    mInstance = this;
    init();
  }

  private void init() {
    Library.init(this);
    Analysis.init(mInstance);
    Toost.init(mInstance);
    mHandler = new Handler();

    ActivityLifecycle lifecycle = new ActivityLifecycle();
    lifecycle.setVisibleListener(new ActivityLifecycle.VisibleListener() {
      @Override public void statusChange(boolean visible) {
        if (visible) {
          mHandler.resume();
        } else {
          mHandler.pause();
        }
      }
    });
    LifecycleDispatcher.registerActivityLifecycleCallbacks(this, lifecycle);
  }

  public abstract boolean isDebug();

  private static class Handler extends PausedHandler {

    Handler() {
    }

    @Override protected void processMessage(Message message) {
    }
  }
}
