package com.mdroid.lib.core.base;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Description：Presenter的基类，如果有使用rxJava进行耗时操作，需调用{@link #addSubscription(Disposable)}}
 */
public abstract class BasePresenter<T> {

  protected T mView;
  private CompositeDisposable mCompositeDisposables = new CompositeDisposable();

  public void onAttach(T view) {
    this.mView = view;
  }

  public void onDetach() {
    this.mView = null;
    mCompositeDisposables.clear();
    this.onDestroy();
  }

  /**
   * 对应的view销毁时调用的清理资源方法
   */
  protected abstract void onDestroy();

  protected void addDisposable(Disposable disposable) {
    this.mCompositeDisposables.add(disposable);
  }
}
