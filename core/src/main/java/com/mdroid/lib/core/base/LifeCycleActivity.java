package com.mdroid.lib.core.base;

import android.os.Bundle;
import android.os.Message;
import com.mdroid.PausedHandler;
import com.mdroid.lifecycle.LifecycleDispatcher;
import java.lang.ref.WeakReference;

/**
 * Description：用于自己管理activity的生命周期的activity基类
 */
public class LifeCycleActivity extends NaviAppCompatActivity {

  private PausedHandler mHandler;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    LifecycleDispatcher.get().onActivityCreated(this, savedInstanceState);
    mHandler = new LifeCycleActivity.Handler(this);
    mHandler.pause();
  }

  @Override protected void onStart() {
    super.onStart();
    LifecycleDispatcher.get().onActivityStarted(this);
  }

  @Override protected void onResume() {
    super.onResume();
    mHandler.resume();
    LifecycleDispatcher.get().onActivityResumed(this);
  }

  @Override protected void onPause() {
    mHandler.pause();
    super.onPause();
    LifecycleDispatcher.get().onActivityPaused(this);
  }

  @Override protected void onStop() {
    super.onStop();
    LifecycleDispatcher.get().onActivityStopped(this);
  }

  @Override protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    LifecycleDispatcher.get().onActivitySaveInstanceState(this, outState);
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    LifecycleDispatcher.get().onActivityDestroyed(this);
  }

  public PausedHandler getHandler() {
    return mHandler;
  }

  /**
   * @see PausedHandler#storeMessage(Message)
   */
  protected boolean storeMessage(Message message) {
    return true;
  }

  /**
   * @see PausedHandler#processMessage(Message)
   */
  protected void processMessage(Message message) {

  }

  private static class Handler extends PausedHandler {
    private WeakReference<LifeCycleActivity> mActivity;

    Handler(LifeCycleActivity activity) {
      mActivity = new WeakReference<>(activity);
    }

    @Override protected boolean storeMessage(Message message) {
      LifeCycleActivity activity = mActivity.get();
      return activity != null && activity.storeMessage(message);
    }

    @Override protected void processMessage(Message message) {
      LifeCycleActivity activity = mActivity.get();
      if (activity != null) {
        activity.processMessage(message);
      }
    }
  }
}
