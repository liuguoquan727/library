package com.mdroid.lib.core.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.StringRes;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.Toast;
import com.chargerlink.lib.core.R;
import com.mdroid.utils.AndroidUtils;

public class Toost {

  @SuppressLint("StaticFieldLeak") private static Context sContext;
  private static Toast sMessage;
  private static Toast sWarning;

  public static void init(Context context) {
    sContext = context.getApplicationContext();
  }

  public static void message(CharSequence s) {
    ensureMessage(false);
    sMessage.setText(s);
    sMessage.show();
  }

  public static void message(@StringRes int resid) {
    message(sContext.getString(resid));
  }

  public static void warning(CharSequence s) {
    ensureMessage(true);
    sWarning.setText(s);
    sWarning.show();
  }

  public static void warning(@StringRes int resid) {
    warning(sContext.getString(resid));
  }

  public static void networkWarning() {
    warning(R.string.network_error_tips);
  }

  private static void ensureMessage(boolean isWarning) {
    if (isWarning) {
      if (sWarning == null) {
        sWarning = new Toast(sContext);
        sWarning.setDuration(Toast.LENGTH_LONG);
        LayoutInflater inflater = LayoutInflater.from(sContext);
        sWarning.setView(inflater.inflate(R.layout.lib_toast_warning_layout, null));
        sWarning.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0,
            AndroidUtils.dp2px(sContext, 60));
      }
    } else {
      if (sMessage == null) {
        sMessage = new Toast(sContext);
        sMessage.setDuration(Toast.LENGTH_SHORT);
        LayoutInflater inflater = LayoutInflater.from(sContext);
        sMessage.setView(inflater.inflate(R.layout.lib_toast_message_layout, null));
        sMessage.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0,
            AndroidUtils.dp2px(sContext, 60));
      }
    }
  }
}
