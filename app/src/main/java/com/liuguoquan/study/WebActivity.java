package com.liuguoquan.study;

import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.jaeger.library.StatusBarUtil;
import com.mdroid.lib.core.base.BaseBrowseActivity;
import com.mdroid.lib.core.base.BaseChromeClient;
import com.mdroid.lib.core.base.BaseExtraKeys;
import com.mdroid.lib.core.base.BaseWebView;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by liuguoquan on 2017/7/27.
 */

public class WebActivity extends BaseBrowseActivity {

  private Unbinder unbinder;

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
    mToolbar.setNavigationIcon(R.drawable.lib_ic_toolbar_close_black);
    mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        finish();
      }
    });
    if (mWebView != null) {
      mWebView.setWebViewClient(new MyWebViewClient());
      mWebView.setWebChromeClient(new BaseChromeClient(mProgressBar));
      mWebView.loadUrl(mUrl);
    }
  }

  @Override protected void bind() {
    unbinder = ButterKnife.bind(this);
  }

  @Override protected void unbind() {
    if (unbinder != null) {
      unbinder.unbind();
    }
  }

  @Override public void onBackPressed() {
    if (mWebView.canGoBack()) {
      mWebView.goBack();
      return;
    }
    super.onBackPressed();
  }

  public class MyWebViewClient extends WebViewClient {

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
      handler.proceed();
    }

    @Override public boolean shouldOverrideUrlLoading(WebView view, String url) {
      if (TextUtils.isEmpty(url)) {
        return false;
      }

      if (url.startsWith("weixin://wap/pay?")) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
        return true;
      }

      Log.d("lgq", "shouldOverrideUrlLoading: " + url);

      // 处理其他链接
      Map<String, String> extraHeaders = new HashMap<String, String>();
      extraHeaders.put("Referer", "http://m.szdjx.com");
      view.loadUrl(url, extraHeaders);
      return true;
    }
  }
}
