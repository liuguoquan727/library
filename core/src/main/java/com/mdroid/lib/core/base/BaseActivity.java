package com.mdroid.lib.core.base;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.view.View;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.mdroid.lib.core.R;
import com.mdroid.lib.core.eventbus.EventBus;
import com.mdroid.lib.core.utils.Analysis;
import com.mdroid.lib.core.utils.Toost;
import com.mdroid.utils.AndroidUtils;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.navi.NaviLifecycle;

/**
 * Activity基类
 */
public abstract class BaseActivity<V, T extends BasePresenter<V>> extends LifeCycleActivity {

  protected final LifecycleProvider<ActivityEvent> mLifecycleProvider =
      NaviLifecycle.createActivityLifecycleProvider(this);

  public T mPresenter;
  private Unbinder mUnbinder;

  protected abstract Status getCurrentStatus();

  protected abstract String getPageTitle();

  protected abstract T initPresenter();

  protected abstract int getContentView();

  /**
   * onCreate方法中调用
   */
  protected abstract void initData(Bundle savedInstanceState);

  @Override @CallSuper protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(getContentView());
    mUnbinder = ButterKnife.bind(this);
    EventBus.bus().register(this);
    mPresenter = initPresenter();
    if (mPresenter != null) {
      mPresenter.onAttach((V) this);
    }
    initData(savedInstanceState);
  }

  @Override @CallSuper public void onBackPressed() {
    View view = this.getWindow().peekDecorView();
    if (view != null) {
      AndroidUtils.hideInputMethod(this, view.getWindowToken());
    }
    super.onBackPressed();
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

  public void executeCloseAnim() {
    overridePendingTransition(R.anim.slide_in_left_activity, R.anim.slide_out_right_activity);
  }

  protected void toastMsg(String content) {
    Toost.message(content);
  }

  protected void toastMsg(int resId) {
    Toost.message(resId);
  }


  @Override @CallSuper protected void onDestroy() {
    super.onDestroy();
    EventBus.bus().unregister(this);
    if (mUnbinder != null) {
      mUnbinder.unbind();
    }
    if (mPresenter != null) {
      mPresenter.onDetach();
    }
  }

}
