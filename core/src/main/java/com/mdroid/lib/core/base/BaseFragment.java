package com.mdroid.lib.core.base;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.mdroid.lib.core.R;
import com.mdroid.lib.core.eventbus.EventBus;
import com.mdroid.lib.core.utils.Analysis;
import com.mdroid.lib.core.utils.SystemBarConfig;
import com.mdroid.lib.core.utils.Toost;
import com.mdroid.PausedHandler;
import com.mdroid.lifecycle.LifecycleDispatcher;
import com.trello.navi2.component.support.NaviFragment;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.android.FragmentEvent;
import com.trello.rxlifecycle2.navi.NaviLifecycle;
import java.lang.ref.WeakReference;

/**
 * fragment的基类，定义了一些通用操作
 * <p>如果需要处理返回键事件，需要重写{@link #onBackPressed()}</p>
 * <p>在{@link #initData(Bundle)}中初始化数据，{@link #initView(View)}中初始化控件</p>
 * <p>使用MVP模式，如果界面比较简单，可灵活处理，{@link #initPresenter()}直接返回null不使用MVP模式</p>
 * <p>可使用{@link #switchStatus(Status)}切换当前fragment的状态</p>
 *
 * @param <V> 对应View，即此fragment
 * @param <T> 对应Presenter
 */
public abstract class BaseFragment<V, T extends BasePresenter<V>> extends NaviFragment {

  protected final LifecycleProvider<FragmentEvent> mLifecycleProvider =
      NaviLifecycle.createFragmentLifecycleProvider(this);
  /** 对应MVP中的P,必须在{@link #initView(View)}或之后才能使用 */
  protected T mPresenter;
  protected LayoutInflater mInflater;
  protected ViewGroup mContentContainer;
  protected View mContentView;
  protected ViewStub mLoadingStub;
  protected ViewStub mErrorStub;
  protected ViewStub mEmptyStub;
  protected View mLoadingView;
  protected View mErrorView;
  protected View mEmptyView;
  protected ViewGroup mTitleContainer;
  private PausedHandler mHandler = new BaseFragment.Handler(this);
  private ViewGroup mRootView;
  private Status mStatus;
  private Unbinder mUnbinder;
  private RelativeLayout mToolBarContainer;
  private boolean mHasStatusBar;
  private View mStatusBar;
  private Toolbar mToolbar;
  private View mToolbarShadow;
  private SystemBarConfig mSystemBarConfig;

  @Override public final void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mHandler.pause();
    mPresenter = initPresenter();
    if (mPresenter != null) {
      mPresenter.onAttach((V) this);
    }
    EventBus.bus().register(this);
    initData(savedInstanceState);
    LifecycleDispatcher.get().onFragmentCreated(this, savedInstanceState);
  }

  @Nullable @Override
  public final View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    mInflater = inflater;
    mRootView = (ViewGroup) inflater.inflate(R.layout.lib_fragment_base_content, container, false);
    mContentContainer = (ViewGroup) mRootView.findViewById(R.id.layout_container);
    mLoadingStub = (ViewStub) mContentContainer.findViewById(R.id.stub_loading);
    mErrorStub = (ViewStub) mContentContainer.findViewById(R.id.stub_error);
    mEmptyStub = (ViewStub) mContentContainer.findViewById(R.id.stub_empty);
    mContentView = inflater.inflate(getContentView(), container, false);
    if (mContentView == null) throw new UnsupportedOperationException("contentView == null");
    mContentContainer.addView(mContentView, 0);
    if (mStatus == null) {
      Status status = getCurrentStatus();
      switchStatus(status == null ? Status.STATUS_NORMAL : status);
    } else {
      switchStatus(mStatus);
    }
    mUnbinder = ButterKnife.bind(this, mContentView);
    LifecycleDispatcher.get().onFragmentCreateView(this, inflater, container, savedInstanceState);
    super.onCreateView(inflater, container, savedInstanceState);
    return mRootView;
  }

  @Override public final void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    initView(mContentView);
    LifecycleDispatcher.get().onFragmentViewCreated(this, view, savedInstanceState);
  }

  @CallSuper @Override public void onDestroyView() {
    super.onDestroyView();
    mUnbinder.unbind();
    mRootView = mTitleContainer = mContentContainer = null;
    mContentView = mErrorView = mLoadingView = mEmptyView = null;
    mInflater = null;
    mToolBarContainer = null;
    mHasStatusBar = false;
    mStatusBar = mToolbarShadow = null;
    mToolbar = null;
    mSystemBarConfig = null;
    LifecycleDispatcher.get().onFragmentDestroyView(this);
  }

  protected void setLoadingView(View loadingView) {
    loadingView.setVisibility(mLoadingView == null ? View.GONE : mLoadingView.getVisibility());
    mContentContainer.removeView(mLoadingView);
    mContentContainer.addView(loadingView);
    mLoadingView = loadingView;
  }

  protected void setErrorView(View errorView) {
    errorView.setVisibility(mErrorView == null ? View.GONE : mErrorView.getVisibility());
    mContentContainer.removeView(mErrorView);
    mContentContainer.addView(errorView);
    mErrorView = errorView;
  }

  protected void setEmptyView(View emptyView) {
    emptyView.setVisibility(mEmptyView == null ? View.GONE : mEmptyView.getVisibility());
    mContentContainer.removeView(mEmptyView);
    mContentContainer.addView(emptyView);
    mEmptyView = emptyView;
  }

  /**
   * 切换当前fragment状态
   *
   * @param status 当前状态
   * @see Status
   */
  protected void switchStatus(Status status) {
    mStatus = status;
    hideAllView();
    switch (status) {
      case STATUS_NORMAL:
        mContentView.setVisibility(View.VISIBLE);
        break;
      case STATUS_LOADING:
        if (mLoadingView == null) {
          mLoadingView = mLoadingStub.inflate();
        }
        mLoadingView.setVisibility(View.VISIBLE);
        break;
      case STATUS_ERROR:
        if (mErrorView == null) {
          mErrorView = mErrorStub.inflate();
        }
        mErrorView.setVisibility(View.VISIBLE);
        break;
      case STATUS_EMPTY:
        if (mEmptyView == null) {
          mEmptyView = mEmptyStub.inflate();
        }
        mEmptyView.setVisibility(View.VISIBLE);
        break;
    }
  }

  protected void toastMsg(String content) {
    Toost.message(content);
  }

  protected void toastMsg(int resId) {
    Toost.message(resId);
  }

  protected void toastWarning(String content) {
    Toost.warning(content);
  }

  protected void toastWarning(int resId) {
    Toost.warning(resId);
  }

  /**
   * 需处理返回键事件时重写此方法
   *
   * @return 是否消费返回键事件
   */
  protected boolean onBackPressed() {
    return false;
  }

  /**
   * 当前状态是加载中还是其他的···,会根据状态显示对应的view
   *
   * @return 状态, 如果直接显示content，状态为{@link Status#STATUS_NORMAL}
   * @see Status
   */
  protected abstract Status getCurrentStatus();

  protected abstract int getContentView();

  protected abstract T initPresenter();

  protected abstract String getPageTitle();

  /**
   * 初始化一些数据,此时view还未创建完，
   * 如果是拿到数据马上显示的操作，应放到{@link #initView(View)}中或使用{@link #getHandler()} 处理一下防止数据获取太快view还没创建
   */
  protected abstract void initData(Bundle savedInstanceState);

  protected abstract void initView(View parent);

  private void hideAllView() {
    int childCount = mContentContainer.getChildCount();
    for (int i = 0; i < childCount; i++) {
      mContentContainer.getChildAt(i).setVisibility(View.GONE);
    }
  }

  /**
   * 显示 Status bar<br>
   * {@link Build.VERSION_CODES#KITKAT} 以前的版本设置全屏, 再调用非全屏时有 StatusBar 遮挡 View 的 bug, 这时候需要调用此方法来显示
   * StatusBar<br>
   */
  public void requestHasStatusBar() {
    if (this.mTitleContainer != null) {
      throw new IllegalStateException("StatusBar status must be requested before other request");
    } else {
      this.mHasStatusBar = true;
    }
  }

  public void requestHeadBarOverlay(boolean overlay) {
    this.ensureSubDecor();
    if (overlay) {
      this.mContentContainer.setPadding(0, 0, 0, 0);
    } else {
      this.mContentContainer.setPadding(0, this.getHeadBarHeight(), 0, 0);
    }

    ViewGroup.LayoutParams params = this.mStatusBar.getLayoutParams();
    if (this.hasStatusBar()) {
      params.height = this.mSystemBarConfig.getStatusBarHeight();
    } else {
      params.height = 0;
    }

    this.mStatusBar.requestLayout();
  }

  private boolean hasStatusBar() {
    return this.isWindowTranslucentStatus() || this.mHasStatusBar;
  }

  private boolean isWindowTranslucentStatus() {
    Window window = getActivity().getWindow();
    WindowManager.LayoutParams params = window.getAttributes();
    View decorView = window.getDecorView();
    return ((params.flags & WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        == WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS) || (Build.VERSION.SDK_INT
        >= Build.VERSION_CODES.LOLLIPOP
        && (decorView.getSystemUiVisibility() & (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)) == (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN));
  }

  public int getHeadBarHeight() {
    this.ensureSubDecor();
    return this.mSystemBarConfig.getToolBarHeight() + this.getStatusBarHeight();
  }

  public int getStatusBarHeight() {
    this.ensureSubDecor();
    return this.hasStatusBar() ? this.mSystemBarConfig.getStatusBarHeight() : 0;
  }

  public int getNavigationBarHeight() {
    this.ensureSubDecor();
    return this.hasNavigationBar() ? this.mSystemBarConfig.getNavigationBarHeight() : 0;
  }

  private boolean hasNavigationBar() {
    return this.mSystemBarConfig.hasNavigtionBar();
  }

  public int getToolBarHeight() {
    this.ensureSubDecor();
    return this.mSystemBarConfig.getToolBarHeight();
  }

  public View getHeadBar() {
    this.ensureSubDecor();
    return null;
  }

  public View getStatusBar() {
    this.ensureSubDecor();
    return this.mStatusBar;
  }

  public Toolbar getToolBar() {
    this.ensureSubDecor();
    return this.mToolbar;
  }

  public View getToolBarShadow() {
    this.ensureSubDecor();
    return this.mToolbarShadow;
  }

  public View getToolBarContainer() {
    this.ensureSubDecor();
    return this.mToolBarContainer;
  }

  private void ensureSubDecor() {
    if (this.mTitleContainer == null) {
      this.mSystemBarConfig = new SystemBarConfig(this.getActivity());
      this.mTitleContainer =
          (ViewGroup) ((ViewStub) mRootView.findViewById(R.id.stub_tool_bar)).inflate();
      this.mStatusBar = this.mTitleContainer.findViewById(R.id.status_bar_background);
      this.mToolBarContainer =
          (RelativeLayout) this.mTitleContainer.findViewById(R.id.tool_bar_container);
      this.mToolbar = (Toolbar) this.mToolBarContainer.findViewById(R.id.tool_bar);
      this.mToolbarShadow = this.mToolBarContainer.findViewById(R.id.tool_bar_shadow);
      this.requestHeadBarOverlay(false);
    }
  }

  public void onAttach(Context context) {
    super.onAttach(context);
    LifecycleDispatcher.get().onFragmentAttach(this, context);
  }

  public void onStart() {
    super.onStart();
    LifecycleDispatcher.get().onFragmentStarted(this);
  }

  @CallSuper public void onResume() {
    super.onResume();
    this.mHandler.resume();
    LifecycleDispatcher.get().onFragmentResumed(this);
    Analysis.onPageStart(getActivity(), this.getClass().getName());
    Analysis.onResume(getActivity());
  }

  @CallSuper public void onPause() {
    this.mHandler.pause();
    super.onPause();
    LifecycleDispatcher.get().onFragmentPaused(this);
    Analysis.onPageEnd(getActivity(), this.getClass().getName());
    Analysis.onPause(getActivity());
  }

  public void onStop() {
    super.onStop();
    LifecycleDispatcher.get().onFragmentStopped(this);
  }

  @CallSuper public void onDestroy() {
    super.onDestroy();
    mStatus = null;
    if (mPresenter != null) mPresenter.onDetach();
    EventBus.bus().unregister(this);
    LifecycleDispatcher.get().onFragmentDestroyed(this);
  }

  public void onDetach() {
    super.onDetach();
    LifecycleDispatcher.get().onFragmentDetach(this);
  }

  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    LifecycleDispatcher.get().onFragmentSaveInstanceState(this, outState);
  }

  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    LifecycleDispatcher.get().onFragmentActivityCreated(this, savedInstanceState);
  }

  public PausedHandler getHandler() {
    return this.mHandler;
  }

  protected boolean storeMessage(Message message) {
    return true;
  }

  protected void processMessage(Message message) {
  }


  private static class Handler extends PausedHandler {
    private WeakReference<BaseFragment> mFragment;

    Handler(BaseFragment fragment) {
      this.mFragment = new WeakReference<>(fragment);
    }

    protected boolean storeMessage(Message message) {
      BaseFragment fragment = this.mFragment.get();
      return fragment != null && fragment.storeMessage(message);
    }

    protected void processMessage(Message message) {
      BaseFragment fragment = this.mFragment.get();
      if (fragment != null) {
        fragment.processMessage(message);
      }
    }
  }
}
