package com.mdroid.lib.core.base;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.chargerlink.lib.core.R;
import com.mdroid.lib.core.utils.UIUtil;

/**
 * Created by ouyangzn on 2016/8/1.<br/>
 * Description：web页面的基类
 */
public class BaseBrowseFragment extends BaseFragment {

  protected ProgressBar mProgressBar;
  protected BaseWebView mWebView;

  protected String mUrl;
  protected String mTitle;
  protected TextView mTitleView;

  @Override protected Status getCurrentStatus() {
    return Status.STATUS_NORMAL;
  }

  @Override protected int getContentView() {
    return R.layout.lib_content_base_browse;
  }

  @Override protected void initData(Bundle savedInstanceState) {
    Bundle bundle = getArguments();
    if (bundle != null) {
      mUrl = bundle.getString(BaseExtraKeys.KEY_URL);
      mTitle = bundle.getString(BaseExtraKeys.KEY_TITLE);
    }
  }

  @Override protected void initView(View parent) {
    mProgressBar = (ProgressBar) parent.findViewById(R.id.progressBar);
    mWebView = (BaseWebView) parent.findViewById(R.id.webview);

    getActivity().getWindow()
        .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
            | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    parent.setFitsSystemWindows(true);
    requestHeadBarOverlay(false);

    initToolBar();

    if (mWebView != null) {
      mWebView.loadUrl(mUrl);
    }
  }

  /**
   * 如果要修改头部toolBar，重写此方法
   */
  protected void initToolBar() {
    Toolbar toolbar = getToolBar();
    toolbar.setNavigationIcon(R.drawable.lib_ic_toolbar_close_black);
    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        getActivity().finish();
      }
    });

    mTitleView = UIUtil.setCenterTitle(toolbar, getPageTitle());
    mTitleView.setMaxEms(10);
  }

  @Override public boolean onBackPressed() {
    if (mWebView != null && mWebView.canGoBack()) {
      mWebView.goBack();
      return true;
    }
    return false;
  }

  @Override public BasePresenter initPresenter() {
    return null;
  }

  @Override protected String getPageTitle() {
    return mTitle;
  }

  @Override public void onPause() {
    super.onPause();
    if (mWebView != null) {
      mWebView.onPause();
    }
  }

  @Override public void onResume() {
    if (mWebView != null) {
      mWebView.onResume();
    }
    super.onResume();
  }

  @Override public void onDestroyView() {
    if (mWebView != null) {
      mWebView.destroy();
      mWebView = null;
    }
    mTitleView = null;
    mProgressBar = null;
    super.onDestroyView();
  }
}
