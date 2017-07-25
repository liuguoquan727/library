/*
 * Copyright (C) 2013 Evgeny Shishkin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mdroid.app;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import com.mdroid.R;
import com.mdroid.view.ISwipeProgressBar;

/**
 * The implementation of the fragment to display content. Based on {@link
 * android.support.v4.app.ListFragment}. If you
 * are waiting for the initial data, you'll can displaying during this time an indeterminate
 * progress indicator.
 */
public abstract class ProgressFragment extends Fragment {
  protected static final int STATUS_SHOWN = 0;
  protected static final int STATUS_WELCOME = 1;
  protected static final int STATUS_PROGRESS = 2;
  protected static final int STATUS_ERROR = 3;
  protected static final int STATUS_EMPTY = 4;
  protected ViewGroup mProgressContainer;
  protected ViewGroup mNetworkErrorContainer;
  protected ViewGroup mEmptyContainer;
  protected ViewGroup mWelcomeContainer;
  protected ViewGroup mPreviewContainer;
  protected View mContentView;
  private boolean mIsViewCreated;
  private int mStatus = STATUS_SHOWN;
  private ViewStub mStubProgress;
  private ViewStub mStubNetworkError;
  private ViewStub mStubEmpty;
  private ViewStub mStubWelcome;
  private ViewStub mStubPreview;
  private ViewGroup mContentContainer;
  private View.OnClickListener mNetworkErrorListener;
  private View.OnClickListener mEmptyListener;

  private SystemBarConfig mSystemBarConfig;
  private RelativeLayout mToolBarContainer;
  private boolean mHasStatusBar;
  private View mStatusBar;
  private Toolbar mToolbar;
  private View mToolbarShadow;
  private ISwipeProgressBar mProgress;

  /**
   * Provide default implementation to return a simple view. Subclasses can override to replace
   * with
   * their own layout.
   * If doing so, the returned view hierarchy <em>must</em> have a progress container whose id is
   * {@link R.id#progress_container R.id.progress_container}, content container whose id
   * is {@link R.id#content_container R.id.content_container} and can optionally have a
   * sibling view id {@link android.R.id#empty android.R.id.empty} that is to be shown when the
   * content is empty.
   * <p/>
   * <p/>
   * If you are overriding this method with your own custom content, consider including the
   * standard
   * layout
   * {@link R.layout#fragment_progress} in your layout file, so that you continue to
   * retain all of the standard behavior of ProgressFragment. In particular, this is currently the
   * only way to have
   * the built-in indeterminant progress state be shown.
   */
  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    return onViewCreating(inflater, container, savedInstanceState);
  }

  protected View onViewCreating(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_progress, container, false);
  }

  /**
   * Attach to view once the view hierarchy has been created.
   */
  @Override public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    ensureContent();
    View contentView = onCreateContentView(getLayoutInflater(savedInstanceState), mContentContainer,
        savedInstanceState);
    setContentView(contentView);
    mIsViewCreated = true;
    confirmStatus();

    final View treeView = mContentContainer;
    treeView.getViewTreeObserver()
        .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
          @Override public void onGlobalLayout() {
            onGlobalLayoutFinish();
            removeOnGlobalLayoutListener(treeView, this);
          }
        });
  }

  /**
   * 控件完成布局
   */
  public void onGlobalLayoutFinish() {
  }

  private void removeOnGlobalLayoutListener(View view,
      ViewTreeObserver.OnGlobalLayoutListener listener) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
      view.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
    } else {
      view.getViewTreeObserver().removeGlobalOnLayoutListener(listener);
    }
  }

  protected abstract View onCreateContentView(LayoutInflater inflater, ViewGroup parent,
      Bundle savedInstanceState);

  /**
   * Detach from view.
   */
  @Override public void onDestroyView() {
    mIsViewCreated = false;
    mStubProgress = mStubNetworkError = mStubEmpty = mStubWelcome = mStubPreview = null;
    mContentView = mEmptyContainer = mWelcomeContainer =
        mNetworkErrorContainer = mProgressContainer = mContentContainer = mPreviewContainer = null;
    mSystemBarConfig = null;
    mToolBarContainer = null;
    mStatusBar = null;
    mToolbar = null;
    mToolbarShadow = null;
    mProgress = null;
    super.onDestroyView();
  }

  private void confirmStatus() {
    switch (mStatus) {
      case STATUS_EMPTY: {
        switchView(mContentView, getEmptyContainer(), false);
        break;
      }
      case STATUS_ERROR: {
        switchView(mContentView, getNetworkErrorContainer(), false);
        break;
      }
      case STATUS_WELCOME: {
        switchView(mContentView, getWelcomeContainer(), false);
        break;
      }
      case STATUS_PROGRESS: {
        switchView(mContentView, getProgressContainer(), false);
        break;
      }
    }
  }

  /**
   * Return content view or null if the content view has not been initialized.
   *
   * @return content view or null
   * @see #setContentView(View)
   */
  public View getContentView() {
    return mContentView;
  }

  /**
   * Set the content view to an explicit view. If the content view was installed earlier, the
   * content will be replaced
   * with a new view.
   *
   * @param view The desired content to display. Value can't be null.
   * @see #getContentView()
   */
  private void setContentView(View view) {
    if (view == null) {
      return;
    }

    mContentContainer.addView(view);
    mContentView = view;
  }

  public void showEmpty() {
    showEmpty(true);
  }

  public void showEmpty(boolean animate) {
    if (mStatus == STATUS_EMPTY) {
      return;
    }
    View shownView = getShownView();
    mStatus = STATUS_EMPTY;
    View hiddenView = getEmptyContainer();
    switchView(shownView, hiddenView, animate);
  }

  public void showWelcome() {
    showWelcome(true);
  }

  public void showWelcome(boolean animate) {
    if (mStatus == STATUS_WELCOME) {
      return;
    }
    View shownView = getShownView();
    mStatus = STATUS_WELCOME;
    View hiddenView = getWelcomeContainer();
    switchView(shownView, hiddenView, animate);
  }

  public void showProgress() {
    showProgress(true);
  }

  public void showProgress(boolean animate) {
    if (mStatus == STATUS_PROGRESS) {
      return;
    }
    View shownView = getShownView();
    mStatus = STATUS_PROGRESS;
    View hiddenView = getProgressContainer();
    switchView(shownView, hiddenView, animate);
  }

  public void showError() {
    showError(true);
  }

  public void showError(boolean animate) {
    if (mStatus == STATUS_ERROR) {
      return;
    }
    View shownView = getShownView();
    mStatus = STATUS_ERROR;
    View hiddenView = getNetworkErrorContainer();
    switchView(shownView, hiddenView, animate);
  }

  public void showContent() {
    showContent(true);
  }

  /**
   * Control whether the content is being displayed. You can make it not displayed if you are
   * waiting for the initial
   * data to show in it. During this time an indeterminant progress indicator will be shown
   * instead.
   */
  public void showContent(boolean animate) {
    if (mStatus == STATUS_SHOWN) {
      return;
    }
    View shownView = getShownView();
    mStatus = STATUS_SHOWN;
    View hiddenView = mContentView;
    switchView(shownView, hiddenView, animate);
  }

  private void switchView(View shownView, View hiddenView, boolean animate) {
    if (animate) {
      shownView.startAnimation(
          AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out));
      hiddenView.startAnimation(
          AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in));
    } else {
      shownView.clearAnimation();
      hiddenView.clearAnimation();
    }
    shownView.setVisibility(View.GONE);
    hiddenView.setVisibility(View.VISIBLE);
  }

  private View getShownView() {
    switch (mStatus) {
      case STATUS_SHOWN: {
        return mContentView;
      }
      case STATUS_EMPTY: {
        return mEmptyContainer;
      }
      case STATUS_PROGRESS: {
        return mProgressContainer;
      }
      case STATUS_ERROR: {
        return mNetworkErrorContainer;
      }
      default: {
        return mWelcomeContainer;
      }
    }
  }

  public boolean onBackPressed() {
    return false;
  }

  public int getStatus() {
    return mStatus;
  }

  private ViewGroup getProgressContainer() {
    if (mProgressContainer == null) {
      mProgressContainer = (ViewGroup) mStubProgress.inflate();
    }
    return mProgressContainer;
  }

  private ViewGroup getNetworkErrorContainer() {
    if (mNetworkErrorContainer == null) {
      mNetworkErrorContainer = (ViewGroup) mStubNetworkError.inflate();
      mNetworkErrorContainer.setOnClickListener(mNetworkErrorListener);
    }
    return mNetworkErrorContainer;
  }

  private ViewGroup getEmptyContainer() {
    if (mEmptyContainer == null) {
      mEmptyContainer = (ViewGroup) mStubEmpty.inflate();
      mEmptyContainer.setOnClickListener(mEmptyListener);
    }
    return mEmptyContainer;
  }

  private ViewGroup getWelcomeContainer() {
    if (mWelcomeContainer == null) {
      mWelcomeContainer = (ViewGroup) mStubWelcome.inflate();
    }
    return mWelcomeContainer;
  }

  public ViewGroup getContentContainer() {
    return mContentContainer;
  }

  public ViewGroup getPreviewContainer() {
    ensureSubDecor();
    return mPreviewContainer;
  }

  /**
   * 显示 Status bar<br>
   * {@link Build.VERSION_CODES#KITKAT} 以前的版本设置全屏, 再调用非全屏时有 StatusBar 遮挡 View 的 bug, 这时候需要调用此方法来显示
   * StatusBar<br>
   */
  public void requestHasStatusBar() {
    if (mPreviewContainer != null) {
      throw new IllegalStateException("StatusBar status must be requested before other request");
    }
    mHasStatusBar = true;
  }

  /**
   * HeadBar overlay: 默认非Overlay
   */
  public void requestHeadBarOverlay(boolean overlay) {
    ensureSubDecor();
    if (overlay) {
      mContentContainer.setPadding(0, 0, 0, 0);
    } else {
      mContentContainer.setPadding(0, getHeadBarHeight(), 0, 0);
    }

    ViewGroup.LayoutParams params = mStatusBar.getLayoutParams();
    if (hasStatusBar()) {
      params.height = mSystemBarConfig.getStatusBarHeight();
    } else {
      params.height = 0;
    }
    mStatusBar.requestLayout();
  }

  /**
   * Contain status bar & tool bar
   */
  public View getHeadBar() {
    ensureSubDecor();
    return null;
  }

  public View getStatusBar() {
    ensureSubDecor();
    return mStatusBar;
  }

  public Toolbar getToolBar() {
    ensureSubDecor();
    return mToolbar;
  }

  public View getToolBarShadow() {
    ensureSubDecor();
    return mToolbarShadow;
  }

  public View getToolBarContainer() {
    ensureSubDecor();
    return mToolBarContainer;
  }

  public ISwipeProgressBar getProgressBar() {
    ensureSubDecor();
    return mProgress;
  }

  private void ensureSubDecor() {
    if (mPreviewContainer != null) {
      return;
    }
    mSystemBarConfig = new SystemBarConfig(getActivity());
    mPreviewContainer = (ViewGroup) mStubPreview.inflate();
    mStatusBar = mPreviewContainer.findViewById(R.id.status_bar_background);
    mToolBarContainer = (RelativeLayout) mPreviewContainer.findViewById(R.id.tool_bar_container);
    mToolbar = (Toolbar) mToolBarContainer.findViewById(R.id.tool_bar);
    mToolbarShadow = mToolBarContainer.findViewById(R.id.tool_bar_shadow);
    mProgress = (ISwipeProgressBar) mToolBarContainer.findViewById(R.id.tool_bar_progress);

    requestHeadBarOverlay(false);
  }

  private boolean hasStatusBar() {
    return isWindowTranslucentStatus() || mHasStatusBar;
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

  /**
   * Tool bar + Status bar
   */
  public int getHeadBarHeight() {
    ensureSubDecor();
    return mSystemBarConfig.getToolBarHeight() + getStatusBarHeight();
  }

  /**
   * Status bar
   */
  public int getStatusBarHeight() {
    ensureSubDecor();
    return hasStatusBar() ? mSystemBarConfig.getStatusBarHeight() : 0;
  }

  /**
   * navigationBar 高
   */
  public int getNavigationBarHeight() {
    ensureSubDecor();
    return hasNavigationBar() ? mSystemBarConfig.getNavigationBarHeight() : 0;
  }

  private boolean hasNavigationBar() {
    return mSystemBarConfig.hasNavigtionBar();
  }

  /**
   * Tool bar
   */
  public int getToolBarHeight() {
    ensureSubDecor();
    return mSystemBarConfig.getToolBarHeight();
  }

  public void setNetworkErrorClickListener(View.OnClickListener networkErrorListener) {
    this.mNetworkErrorListener = networkErrorListener;
    if (mNetworkErrorContainer != null) {
      mNetworkErrorContainer.setOnClickListener(networkErrorListener);
    }
  }

  public void setEmptyClickListener(View.OnClickListener emptyListener) {
    this.mEmptyListener = emptyListener;
    if (mEmptyContainer != null) mEmptyContainer.setOnClickListener(emptyListener);
  }

  public boolean isViewCreated() {
    return mIsViewCreated;
  }

  /**
   * Initialization views.
   */
  private void ensureContent() {
    if (mContentContainer != null) {
      return;
    }
    View root = getView();
    if (root == null) {
      throw new IllegalStateException("Content view not yet created");
    }
    mStubProgress = (ViewStub) root.findViewById(R.id.fragment_progress_stub_progress);
    if (mStubProgress == null) {
      throw new RuntimeException(
          "Your content must have a ViewStub whose id attribute is 'R.id.fragment_progress_stub_progress'");
    }
    mStubNetworkError = (ViewStub) root.findViewById(R.id.fragment_progress_stub_network_error);
    if (mStubNetworkError == null) {
      throw new RuntimeException(
          "Your content must have a ViewStub whose id attribute is 'R.id.fragment_progress_stub_network_error'");
    }
    mStubEmpty = (ViewStub) root.findViewById(R.id.fragment_progress_stub_empty);
    if (mStubEmpty == null) {
      throw new RuntimeException(
          "Your content must have a ViewStub whose id attribute is 'R.id.fragment_progress_stub_empty'");
    }
    mStubWelcome = (ViewStub) root.findViewById(R.id.fragment_progress_stub_welcome);
    if (mStubWelcome == null) {
      throw new RuntimeException(
          "Your content must have a ViewStub whose id attribute is 'R.id.fragment_progress_stub_welcome'");
    }
    mStubPreview = (ViewStub) root.findViewById(R.id.fragment_progress_stub_preview);
    if (mStubPreview == null) {
      throw new RuntimeException(
          "Your content must have a ViewStub whose id attribute is 'R.id.fragment_progress_stub_preview'");
    }
    mContentContainer = (ViewGroup) root.findViewById(R.id.content_container);
    if (mContentContainer == null) {
      throw new RuntimeException(
          "Your content must have a ViewGroup whose id attribute is 'R.id.content_container'");
    }
  }
}
