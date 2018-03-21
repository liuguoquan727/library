package com.mdroid.lib.core.base;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.GeolocationPermissions;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.mdroid.lib.core.dialog.CenterDialog;
import com.mdroid.lib.core.dialog.IDialog;
import com.mdroid.utils.AndroidUtils;
import com.orhanobut.dialogplus.DialogPlus;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BaseWebView extends WebView {

  protected boolean mIsDestroy;

  public BaseWebView(Context context) {
    this(context, null);
  }

  public BaseWebView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public BaseWebView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  private void init() {
    setWebViewClient(new MyWebViewClient());
    setWebChromeClient(new MyWebChromeClient());
    WebSettings webSettings = getSettings();
    webSettings.setUserAgentString(webSettings.getUserAgentString() + "/ydbus");
    webSettings.setLoadWithOverviewMode(true);
    webSettings.setUseWideViewPort(true);
    webSettings.setJavaScriptEnabled(true);
    webSettings.setDomStorageEnabled(true);
    webSettings.setGeolocationEnabled(true);
    webSettings.setAllowFileAccess(true);
    webSettings.setAppCacheEnabled(true);
    setBackgroundColor(0xFFFFFFFF);
  }

  @Override protected void onDraw(Canvas canvas) {
    try {
      super.onDraw(canvas);
    } catch (Exception ignored) {
    }
  }

  @Override public void loadUrl(String url) {
    Map<String, String> header = getHeader();
    header.put("pageDataType", "web");
    super.loadUrl(url, header);
  }

  @Override public void destroy() {
    mIsDestroy = true;
    ViewParent view = getParent();
    if (view instanceof ViewGroup) {
      ((ViewGroup) view).removeView(this);
    }
    removeAllViews();
    super.destroy();
  }

  private Map<String, String> getHeader() {
    Map<String, String> map = new HashMap<String, String>();
    map.put("device", getAppInfo());
    return map;
  }

  private String getAppInfo() {
    Map<String, Object> map = new HashMap<>();
    map.put("client", "android");
    map.put("os_version", Build.VERSION.SDK_INT + "");
    map.put("ver", AndroidUtils.getVersionCode(getContext()) + "");
    map.put("network", AndroidUtils.getNetworkType(getContext()));
    return format(map);
  }

  private String format(Map<String, Object> params) {
    try {
      final StringBuilder result = new StringBuilder();
      Set<String> keys = params.keySet();
      for (final String key : keys) {
        final Object value = params.get(key);
        if (value == null) continue;
        if (result.length() > 0) result.append("&");
        result.append(URLEncoder.encode(key, "UTF-8"));
        result.append("=");
        result.append(URLEncoder.encode(value.toString(), "UTF-8"));
      }
      return result.toString();
    } catch (UnsupportedEncodingException e) {
      // Impossible!
      throw new IllegalArgumentException(e);
    }
  }

  public class MyWebChromeClient extends BaseChromeClient {

    @Override public void onGeolocationPermissionsShowPrompt(final String origin,
        final GeolocationPermissions.Callback callback) {
      if (mIsDestroy) return;
      CenterDialog.create(getContext(), null, origin + "需要获取您的地理位置", "拒绝",
          new IDialog.OnClickListener() {
            @Override public void onClick(DialogPlus dialog, View view) {
              dialog.dismiss();
              callback.invoke(origin, false, false);
            }
          }, "允许", new IDialog.OnClickListener() {
            @Override public void onClick(DialogPlus dialog, View view) {
              dialog.dismiss();
              callback.invoke(origin, true, true);
            }
          }).show();
    }
  }

  /**
   * 自定义WebViewClient，否则会自动跳转到系统的浏览器的
   */
  public class MyWebViewClient extends WebViewClient {

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
      handler.proceed();
    }

    @Override public boolean shouldOverrideUrlLoading(WebView view, String url) {
      if (TextUtils.isEmpty(url)) {
        return false;
      }
      // 电话短信拦截
      if (url.startsWith("sms:") || url.startsWith("tel:")) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        getContext().startActivity(intent);
        return true;
      }

      return true;
    }
  }
}
