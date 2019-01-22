package com.mdroid.lib.imagepick.base;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import com.mdroid.lib.imagepick.R;
import com.mdroid.lib.imagepick.utils.SystemBarConfig;

public abstract class BaseFragment extends Fragment {

  protected LayoutInflater mInflater;
  protected ViewGroup mContentContainer;
  protected View mContentView;
  protected ViewGroup mTitleContainer;
  private ViewGroup mRootView;
  private RelativeLayout mToolBarContainer;
  private boolean mHasStatusBar;
  private View mStatusBar;
  private Toolbar mToolbar;
  private View mToolbarShadow;
  private SystemBarConfig mSystemBarConfig;

  @Override public final void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    initData(savedInstanceState);
  }

  @Nullable @Override
  public final View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    mInflater = inflater;
    mRootView =
        (ViewGroup) inflater.inflate(R.layout.image_pick_fragment_base_content, container, false);
    mContentContainer = (ViewGroup) mRootView.findViewById(R.id.layout_container);
    mContentView = inflater.inflate(getContentView(), container, false);
    if (mContentView == null) throw new UnsupportedOperationException("contentView == null");
    mContentContainer.addView(mContentView, 0);
    return mRootView;
  }

  @Override public final void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    initView(mContentView);
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    mRootView = mTitleContainer = mContentContainer = null;
    mContentView = null;
    mInflater = null;
    mToolBarContainer = null;
    mHasStatusBar = false;
    mStatusBar = mToolbarShadow = null;
    mToolbar = null;
    mSystemBarConfig = null;
  }

  /**
   * 需处理返回键事件时重写此方法
   *
   * @return 是否消费返回键事件
   */
  public boolean onBackPressed() {
    return false;
  }

  protected abstract int getContentView();

  /**
   * 初始化一些数据,此时view还未创建完，
   * 如果是拿到数据马上显示的操作，应放到{@link #initView(View)}中，防止数据获取太快view还没创建
   */
  protected abstract void initData(Bundle savedInstanceState);

  protected abstract void initView(View parent);

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
}
