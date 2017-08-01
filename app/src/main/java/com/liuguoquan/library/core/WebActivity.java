package com.liuguoquan.library.core;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import com.jaeger.library.StatusBarUtil;
import com.mdroid.lib.core.base.BaseBrowseActivity;
import com.mdroid.lib.core.base.BaseChromeClient;
import com.mdroid.lib.core.base.BaseExtraKeys;
import com.mdroid.lib.core.base.BaseWebView;

/**
 * Created by liuguoquan on 2017/7/27.
 */

public class WebActivity extends BaseBrowseActivity {

  @Override public void initData(Bundle savedInstanceState) {
    StatusBarUtil.setColor(this, getResources().getColor(R.color.main_color_normal), 0);
    Bundle bundle = getIntent().getExtras();
    if (bundle != null) {
      mUrl = bundle.getString(BaseExtraKeys.KEY_URL);
      mTitle = bundle.getString(BaseExtraKeys.KEY_TITLE);
    }
    mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
    mWebView = (BaseWebView) findViewById(R.id.webview);
    mToolbar = (Toolbar) findViewById(R.id.tool_bar);
    if (mWebView != null) {
      mWebView.setWebViewClient(new WebViewClient());
      mWebView.setWebChromeClient(new BaseChromeClient(mProgressBar));
      mWebView.loadUrl(mUrl);
    }
  }
}
