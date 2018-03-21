package com.mdroid.lib.core.base;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentHelper;
import android.text.TextUtils;
import android.view.View;
import com.mdroid.lib.core.R;
import com.mdroid.lib.core.eventbus.EventBus;
import com.mdroid.utils.AndroidUtils;
import com.mdroid.utils.Ln;
import java.util.ArrayList;
import java.util.List;

/**
 * activity基类,如果此activity下的fragment需要使用startActivityForResult打开另一个activity，
 * 需要调用{@link #setFragmentRecord(Fragment)}以防止可能出现onActivityResult不回调的问题
 */
public class CommonActivity extends LifeCycleActivity {

  // 用于记录此activity下嵌套的fragment并做相关处理时使用
  public static final String FRAGMENT_NAME = "fragment_name";
  private String mFragmentName;
  private ArrayList<Integer> mFragmentRecord = new ArrayList<>();

  protected String getFragmentName() {
    return mFragmentName;
  }

  @Override @CallSuper protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.lib_activity_common);
    EventBus.bus().register(this);
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
  }

  @Override @CallSuper protected void onPause() {
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
