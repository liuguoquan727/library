package com.mdroid.lib.core.base;

import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ProgressBar;
import com.mdroid.lib.core.dialog.CenterDialog;
import com.mdroid.lib.core.dialog.IDialog;
import com.mdroid.utils.Ln;
import com.orhanobut.dialogplus.DialogPlus;

/**
 * Description：
 */
public class BaseChromeClient extends WebChromeClient {

  private ProgressBar mProgressBar;

  public BaseChromeClient() {
  }

  public BaseChromeClient(ProgressBar progressBar) {
    this.mProgressBar = progressBar;
  }

  @Override public void onProgressChanged(WebView view, int newProgress) {
    if (mProgressBar == null) {
      super.onProgressChanged(view, newProgress);
      return;
    }

    if (newProgress < 100) {
      if (mProgressBar.getVisibility() == View.GONE) {
        mProgressBar.setVisibility(View.VISIBLE);
      }
      mProgressBar.setProgress(newProgress);
    } else {
      mProgressBar.setVisibility(View.GONE);
    }
  }

  @Override
  public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
    CenterDialog.create(view.getContext(), null, message, "取消", new IDialog.OnClickListener() {
      @Override public void onClick(DialogPlus dialog, View view) {
        dialog.dismiss();
        result.cancel();
      }
    }, "确定", new IDialog.OnClickListener() {
      @Override public void onClick(DialogPlus dialog, View view) {
        dialog.dismiss();
        result.confirm();
      }
    }).show();
    return true;
  }

  @Override
  public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
    CenterDialog.create(view.getContext(), null, message, null, null, "确定",
        new IDialog.OnClickListener() {
          @Override public void onClick(DialogPlus dialog, View view) {
            dialog.dismiss();
            result.confirm();
          }
        }).show();
    return true;
  }

  @Override public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
    Ln.d(consoleMessage.message());
    return true;
  }
}
