package com.liuguoquan.study.base;

import android.content.Context;

import com.mdroid.PausedHandler;
import com.mdroid.lib.core.base.BaseFragment;
import com.mdroid.lib.core.base.BasePresenter;
import com.trello.rxlifecycle3.LifecycleProvider;

import androidx.lifecycle.Lifecycle;

/**
 * Description：MVP模式中，此app对应的所有P的基类
 */
public abstract class AppBaseActivityPresenter<T> extends BasePresenter<T> {

  protected Context mContext;
  protected PausedHandler mHandler;
    protected LifecycleProvider<Lifecycle.Event> mLifecycleProvider;

  /**
   * @param provider 用于rxJava的生命周期管理,具体使用:在Observable.subscribe()前调用
   * Observable.compose(mProvider.<ApiResult>bindUntilEvent(FragmentEvent.DESTROY))<br/>
   * 可通过{@link BaseFragment#mLifecycleProvider}获取
   * @param handler 用于控制fragment onPause时暂停发布消息，可通过{@link BaseFragment#getHandler()}获取
   */
  public AppBaseActivityPresenter(LifecycleProvider<Lifecycle.Event> provider,
      PausedHandler handler) {
    //mContext = App.getInstance();
    mLifecycleProvider = provider;
    mHandler = handler;
  }

  @Override protected void onDestroy() {
    mContext = null;
    mLifecycleProvider = null;
    mHandler = null;
    destroy();
  }

  protected abstract void destroy();
}
