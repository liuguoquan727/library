package com.mdroid.lib.core.base;

import android.app.Activity;
import android.os.Bundle;
import com.mdroid.lifecycle.ActivityLifecycleCallbacksCompat;
import java.util.ArrayList;
import java.util.List;

public class ActivityLifecycle implements ActivityLifecycleCallbacksCompat {
  private static final long UPDATE_STATUS_DELAY = 300;
  private List<Activity> mActivities = new ArrayList<>();
  private Runnable mUpdateStatusTask;
  private VisibleListener mVisibleListener;

  @Override public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    mActivities.add(activity);
    updateActivateActivity();
  }

  @Override public void onActivityStarted(Activity activity) {

  }

  @Override public void onActivityResumed(Activity activity) {
    updateStatus(true);
  }

  @Override public void onActivityPaused(Activity activity) {
    updateStatus(false);
  }

  @Override public void onActivityStopped(Activity activity) {

  }

  @Override public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

  }

  @Override public void onActivityDestroyed(Activity activity) {
    mActivities.remove(activity);
    updateActivateActivity();
  }

  private void updateActivateActivity() {
    Activity activate = null;
    try {
      activate = mActivities.get(mActivities.size() - 1);
    } catch (Exception ignored) {
    }
    BaseApp.Instance().setTopActivity(activate);
  }

  private void updateStatus(final boolean visible) {
    if (mUpdateStatusTask != null) {
      BaseApp.Instance().mHandler.removeCallbacks(mUpdateStatusTask);
    }
    if (visible == BaseApp.Instance().mVisible) {
      return;
    }
    BaseApp.Instance().mHandler.postDelayed(mUpdateStatusTask = new Runnable() {
      @Override public void run() {
        BaseApp.Instance().mVisible = visible;
        if (mVisibleListener != null) {
          mVisibleListener.statusChange(visible);
        }
      }
    }, UPDATE_STATUS_DELAY);
  }

  public void setVisibleListener(VisibleListener visibleListener) {
    this.mVisibleListener = visibleListener;
  }

  public interface VisibleListener {
    void statusChange(boolean visible);
  }
}
