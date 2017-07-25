package com.mdroid.lifecycle;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application.ActivityLifecycleCallbacks;
import android.os.Build;
import android.os.Bundle;

/**
 * Wraps an {@link ActivityLifecycleCallbacksCompat} into an {@link ActivityLifecycleCallbacks}.
 */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH) class ActivityLifecycleCallbacksWrapper
    implements ActivityLifecycleCallbacks {
  private ActivityLifecycleCallbacksCompat mCallback;

  public ActivityLifecycleCallbacksWrapper(ActivityLifecycleCallbacksCompat callback) {
    mCallback = callback;
  }

  @Override public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    mCallback.onActivityCreated(activity, savedInstanceState);
  }

  @Override public void onActivityStarted(Activity activity) {
    mCallback.onActivityStarted(activity);
  }

  @Override public void onActivityResumed(Activity activity) {
    mCallback.onActivityResumed(activity);
  }

  @Override public void onActivityPaused(Activity activity) {
    mCallback.onActivityPaused(activity);
  }

  @Override public void onActivityStopped(Activity activity) {
    mCallback.onActivityStopped(activity);
  }

  @Override public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    mCallback.onActivitySaveInstanceState(activity, outState);
  }

  @Override public void onActivityDestroyed(Activity activity) {
    mCallback.onActivityDestroyed(activity);
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ActivityLifecycleCallbacksWrapper)) return false;
    return ((ActivityLifecycleCallbacksWrapper) o).mCallback == mCallback;
  }

  @Override public int hashCode() {
    return mCallback.hashCode();
  }
}