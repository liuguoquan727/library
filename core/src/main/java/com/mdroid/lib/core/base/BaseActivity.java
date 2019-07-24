package com.mdroid.lib.core.base;

import android.os.Bundle;
import android.view.View;

import com.mdroid.lib.core.R;
import com.mdroid.lib.core.eventbus.EventBus;
import com.mdroid.lib.core.utils.Toost;
import com.mdroid.utils.AndroidUtils;
import com.trello.lifecycle2.android.lifecycle.AndroidLifecycle;
import com.trello.rxlifecycle3.LifecycleProvider;

import androidx.annotation.CallSuper;
import androidx.lifecycle.Lifecycle;

/**
 * Activity基类
 */
public abstract class BaseActivity<V, T extends BasePresenter<V>> extends LifeCycleActivity {

    protected final LifecycleProvider<Lifecycle.Event> mLifecycleProvider =
        AndroidLifecycle.createLifecycleProvider(this);

  public T mPresenter;

  protected abstract Status getCurrentStatus();

  protected abstract String getPageTitle();

  protected abstract T initPresenter();

  protected abstract int getContentView();

  /**
   * onCreate方法中调用
   */
  protected abstract void initData(Bundle savedInstanceState);

  protected abstract void bind();

  protected abstract void unbind();

  @Override @CallSuper protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(getContentView());
    bind();
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
  }

  @Override @CallSuper protected void onPause() {
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
    unbind();
    if (mPresenter != null) {
      mPresenter.onDetach();
    }
  }
}
