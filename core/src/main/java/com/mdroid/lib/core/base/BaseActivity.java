package com.mdroid.lib.core.base;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentHelper;
import android.text.TextUtils;
import android.util.AndroidRuntimeException;
import android.view.View;
import com.chargerlink.lib.core.R;
import com.mdroid.lib.core.eventbus.EventBus;
import com.mdroid.lib.core.utils.Analysis;
import com.mdroid.utils.AndroidUtils;
import com.mdroid.utils.Ln;
import com.mdroid.view.SwipeOverlayFrameLayout;
import java.util.ArrayList;
import java.util.List;

/**
 * activity基类,如果此activity下的fragment需要使用startActivityForResult打开另一个activity，
 * 需要调用{@link #setFragmentRecord(Fragment)}以防止可能出现onActivityResult不回调的问题
 */
public class BaseActivity extends LifeCycleActivity {

  // 用于记录此activity下嵌套的fragment并做相关处理时使用
  public static final String FRAGMENT_NAME = "fragment_name";
  private static final String FRAGMENT_RECORD = "fragment_record";
  private boolean mIsSwipeDisable;
  private SwipeOverlayFrameLayout mSwipeLayout;
  private String mFragmentName;
  private ArrayList<Integer> mFragmentRecord = new ArrayList<>();

  protected String getFragmentName() {
    return mFragmentName;
  }

  @Override @CallSuper protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.lib_activity_base);
    EventBus.bus().register(this);
    initSwipeLayout();
    initFragment(savedInstanceState);
  }

  private void initFragment(Bundle savedInstanceState) {
    Intent intent = getIntent();
    mFragmentName = intent.getStringExtra(FRAGMENT_NAME);
    if (mFragmentName == null) {
      try {
        ActivityInfo info =
            getPackageManager().getActivityInfo(getComponentName(), PackageManager.GET_META_DATA);
        Bundle metaData = info.metaData;
        if (metaData != null) mFragmentName = metaData.getString("fragment");
      } catch (PackageManager.NameNotFoundException ignored) {
      }
    }
    if (!TextUtils.isEmpty(mFragmentName)) {
      BaseFragment fragment =
          (BaseFragment) getSupportFragmentManager().findFragmentByTag(mFragmentName);
      if (savedInstanceState == null) {
        if (fragment == null) {
          fragment = newFragment();
        }
        if (fragment != null) {
          getSupportFragmentManager().beginTransaction()
              .replace(R.id.content, fragment, mFragmentName)
              .commit();
        }
      }
    }
  }

  private void initSwipeLayout() {
    if (!mIsSwipeDisable) {
      mSwipeLayout = (SwipeOverlayFrameLayout) findViewById(R.id.content);
      mSwipeLayout.setOnSwipeListener(new SwipeOverlayFrameLayout.Listener() {
        @Override public boolean swipeToLeft() {
          onBackPressed();
          overridePendingTransition(R.anim.slide_in_left_activity, R.anim.slide_out_right_activity);
          return true;
        }

        @Override public boolean swipeToRight() {
          return false;
        }
      });
    }
  }

  /**
   * 禁用侧滑返回，必须在onCreate之前调用
   */
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

  @Override @CallSuper public void onBackPressed() {
    View view = this.getWindow().peekDecorView();
    if (view != null) {
      AndroidUtils.hideInputMethod(this, view.getWindowToken());
    }
    BaseFragment fragment = getFragment();
    if (fragment == null || !fragment.onBackPressed()) {
      super.onBackPressed();
    }
  }

  @Override @CallSuper protected void onResume() {
    super.onResume();
    Analysis.onResume(this);
  }

  @Override @CallSuper protected void onPause() {
    Analysis.onPause(this);
    super.onPause();
  }

  @Override @CallSuper public void finish() {
    super.finish();
    // 解决activity的windowBackground设置为透明时，动画失效的问题
    executeCloseAnim();
  }

  protected void executeCloseAnim() {
    overridePendingTransition(R.anim.slide_in_left_activity, R.anim.slide_out_right_activity);
  }

  @Override @CallSuper protected void onDestroy() {
    super.onDestroy();
    EventBus.bus().unregister(this);
  }

  @CallSuper @Override protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putIntegerArrayList(FRAGMENT_RECORD, mFragmentRecord);
  }

  @CallSuper @Override protected void onRestoreInstanceState(Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);
    mFragmentRecord = savedInstanceState.getIntegerArrayList(FRAGMENT_RECORD);
  }

  /**
   * @return 此activity嵌套的主fragment(而不是fragment中嵌套的fragment)
   */
  protected final BaseFragment getFragment() {
    return (BaseFragment) getSupportFragmentManager().findFragmentByTag(mFragmentName);
  }

  protected BaseFragment newFragment() {
    if (TextUtils.isEmpty(mFragmentName)) {
      return null;
    }
    return (BaseFragment) Fragment.instantiate(this, mFragmentName, getIntent().getExtras());
  }

  /**
   * 解决fragment中嵌套fragment时，内层的fragment使用startActivityForResult收不到onActivityResult的问题
   *
   * @param fragment 使用startActivityForResult的fragment
   */
  public void setFragmentRecord(Fragment fragment) {
    Fragment node = fragment;
    while (node != null) {
      int index = FragmentHelper.getIndex(node);
      if (index < 0) {
        throw new IllegalStateException("Fragment is out of FragmentManager: " + node);
      }
      mFragmentRecord.add(0, index);
      node = node.getParentFragment();
    }
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    FragmentHelper.noteStateNotSaved(this);
    List<Fragment> active = FragmentHelper.getActive(this);
    Fragment fragment = null;
    for (Integer index : mFragmentRecord) {
      if (active != null && index >= 0 && index < active.size()) {
        fragment = active.get(index);
        if (fragment == null) {
          Ln.w("Activity result no fragment exists for index: 0x" + Integer.toHexString(index));
        } else {
          active = FragmentHelper.getChildActive(fragment);
        }
      }
    }
    mFragmentRecord.clear();
    if (fragment == null) {
      super.onActivityResult(requestCode, resultCode, data);
    } else {
      fragment.onActivityResult(requestCode, resultCode, data);
    }
  }
}
