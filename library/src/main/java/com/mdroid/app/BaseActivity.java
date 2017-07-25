package com.mdroid.app;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.AndroidRuntimeException;
import com.mdroid.R;
import com.mdroid.view.SwipeOverlayFrameLayout;

public abstract class BaseActivity extends AppCompatActivity {

  private boolean mIsSwipeDisable;
  private SwipeOverlayFrameLayout mSwipeLayout;

  public static void overridePendingTransitionInRight(final Activity activity) {
    int enterAnim = R.anim.slide_in_right_activity;
    int exitAnim = R.anim.slide_out_left_activity;
    activity.overridePendingTransition(enterAnim, exitAnim);
  }

  public static void overridePendingTransitionInLeft(final Activity activity) {
    int enterAnim = R.anim.slide_in_left_activity;
    int exitAnim = R.anim.slide_out_right_activity;
    activity.overridePendingTransition(enterAnim, exitAnim);
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_base);
    if (!mIsSwipeDisable) {
      mSwipeLayout = (SwipeOverlayFrameLayout) findViewById(R.id.content);
      mSwipeLayout.setOnSwipeListener(new SwipeOverlayFrameLayout.Listener() {
        @Override public boolean swipeToLeft() {
          onBackPressed();
          overridePendingTransitionInLeft(BaseActivity.this);
          return true;
        }

        @Override public boolean swipeToRight() {
          return false;
        }
      });
    }
    ProgressFragment fragment =
        (ProgressFragment) getSupportFragmentManager().findFragmentByTag(getFragmentTag());
    if (savedInstanceState == null) {
      if (fragment == null) {
        fragment = newFragment();
      }
      if (fragment != null) {
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.content, fragment, getFragmentTag())
            .commit();
      }
    }
  }

  public void requestDisableSwipe() {
    if (mSwipeLayout != null) {
      throw new AndroidRuntimeException(
          "request disable swipe must be requested before adding super.onCreate(...)");
    }
    mIsSwipeDisable = true;
  }

  public void setSwipeEnabled(boolean enabled) {
    mSwipeLayout.setSwipeEnabled(enabled);
  }

  @Override public void onBackPressed() {
    ProgressFragment fragment = getFragment();
    if (fragment == null || !fragment.onBackPressed()) {
      super.onBackPressed();
    }
  }

  /**
   * @return Find fragment by tag with {@link #getFragmentTag()}
   */
  protected final ProgressFragment getFragment() {
    return (ProgressFragment) getSupportFragmentManager().findFragmentByTag(getFragmentTag());
  }

  /**
   * @return The fragment tag
   */
  protected abstract String getFragmentTag();

  protected abstract ProgressFragment newFragment();
}
