package com.liuguoquan.study.base;

import android.os.Bundle;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import androidx.appcompat.widget.Toolbar;
import com.liuguoquan.study.R;
import com.mdroid.lib.core.base.BaseBrowseActivity;
import com.mdroid.lib.core.base.BaseChromeClient;
import com.mdroid.lib.core.base.BaseExtraKeys;
import com.mdroid.lib.core.base.BaseWebView;

/**
 * Description:
 *
 * <p>Created by liuguoquan on 2017/8/17 16:26.
 */
public class AppBrowseActivity extends BaseBrowseActivity {

  @Override
  public void initData(Bundle savedInstanceState) {
    Bundle bundle = getIntent().getExtras();
    if (bundle != null) {
      mUrl = bundle.getString(BaseExtraKeys.KEY_URL);
      mTitle = bundle.getString(BaseExtraKeys.KEY_TITLE);
    }
    mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
    mWebView = (BaseWebView) findViewById(R.id.webview);
    mToolbar = (Toolbar) findViewById(R.id.tool_bar);
    requestBaseInit(mToolbar, mTitle);
    if (mWebView != null) {
      mWebView.setWebViewClient(new WebViewClient());
      mWebView.setWebChromeClient(new BaseChromeClient(mProgressBar));
      mWebView.loadUrl(mUrl);
    }
  }

  @Override
  protected void bind() {
  }

  @Override
  protected void unbind() {
  }

  protected void requestBaseInit(Toolbar toolBar, String title) {
    // toolBar.setBackgroundResource(R.color.main_color_normal);
    // TextView tvTitle = UIUtil.setCenterTitle(toolBar, title);
    // ToolBarUtils.updateTitleText(tvTitle);
    // toolBar.setNavigationIcon(R.drawable.ic_back_indicator);
    // toolBar.setNavigationOnClickListener(new View.OnClickListener() {
    //  @Override public void onClick(View view) {
    //    finish();
    //  }
    // });
  }
}
