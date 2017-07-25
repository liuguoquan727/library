package com.mdroid.lifecycle;

import android.app.Activity;
import android.os.Bundle;

/**
 * Equivalent of {@link android.app.Application.ActivityLifecycleCallbacks} to be used with
 * {@link LifecycleDispatcher#registerActivityLifecycleCallbacks(android.app.Application, * ActivityLifecycleCallbacksCompat)} and
 * {@link LifecycleDispatcher#unregisterActivityLifecycleCallbacks(android.app.Application, * ActivityLifecycleCallbacksCompat)}.
 */
public interface ActivityLifecycleCallbacksCompat {
  void onActivityCreated(Activity activity, Bundle savedInstanceState);

  void onActivityStarted(Activity activity);

  void onActivityResumed(Activity activity);

  void onActivityPaused(Activity activity);

  void onActivityStopped(Activity activity);

  void onActivitySaveInstanceState(Activity activity, Bundle outState);

  void onActivityDestroyed(Activity activity);
}