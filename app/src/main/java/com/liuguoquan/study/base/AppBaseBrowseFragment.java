package com.liuguoquan.study.base;

import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import com.mdroid.lib.core.base.BaseBrowseFragment;
import com.mdroid.lib.core.base.BaseChromeClient;

/**
 * Description：webView界面的基类
 */
public class AppBaseBrowseFragment extends BaseBrowseFragment {


  @Override protected void bind(View view) {
  }

  @Override protected void unbind() {
  }

  @Override protected void initView(View parent) {
    super.initView(parent);
    getToolBarShadow().setVisibility(View.GONE);
    mWebView.setWebChromeClient(new BaseChromeClient(mProgressBar) {
      @Override public void onReceivedTitle(WebView view, String title) {
        super.onReceivedTitle(view, title);
        if (TextUtils.isEmpty(mTitle)) {
          mTitleView.setText(title);
        }
      }
    });
  }
}
