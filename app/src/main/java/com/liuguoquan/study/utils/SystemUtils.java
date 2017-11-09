package com.liuguoquan.study.utils;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.liuguoquan.study.R;
import com.mdroid.lib.core.base.BaseBrowseFragment;
import com.mdroid.lib.core.base.BaseExtraKeys;
import com.mdroid.lib.core.utils.ActivityUtil;
import com.mdroid.utils.text.SpanUtils;
import com.mdroid.utils.text.URLSpan;
import com.orhanobut.dialogplus.DialogPlus;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Description:
 *
 * Created by liuguoquan on 2017/9/26 12:44.
 */

public class SystemUtils {

  /**
   * 给toolbar添加一张图片
   *
   * @param toolbar toolbar
   * @param resId 图片资源id
   * @param gravity 添加的位置，对应{@link Gravity#LEFT}、{@link Gravity#RIGHT}
   * @param margin 图片的四周边距{@link Toolbar.LayoutParams#setMargins(int, int, int, int)}
   * @return 被添加的ImageView
   */
  public static ImageView addImage2Toolbar(Toolbar toolbar, int resId, int gravity, int[] margin) {
    Context context = toolbar.getContext();
    ImageView img = new ImageView(context);
    img.setImageResource(resId);
    img.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
    Toolbar.LayoutParams params = new Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT,
        Toolbar.LayoutParams.WRAP_CONTENT);
    params.gravity = gravity | Gravity.CENTER;
    try {
      params.setMargins(margin[0], margin[1], margin[2], margin[3]);
    } catch (Exception e) {
      int margin_15 = dp2px(context, 15);
      params.setMargins(margin_15, 0, margin_15, 0);
    }
    img.setLayoutParams(params);
    toolbar.addView(img);
    return img;
  }

  /**
   * 给toolbar添加文字
   *
   * @param toolbar Toolbar
   * @param text 文字
   * @param gravity 添加的位置，对应{@link Gravity#LEFT}、{@link Gravity#RIGHT}
   * @param margin 文字的四周边距{@link Toolbar.LayoutParams#setMargins(int, int, int, int)}
   * @return TextView
   */
  public static TextView addText2Toolbar(Toolbar toolbar, String text, int gravity, int[] margin) {
    Context context = toolbar.getContext();
    TextView textView = new TextView(context);
    textView.setText(text);
    Toolbar.LayoutParams params = new Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT,
        Toolbar.LayoutParams.WRAP_CONTENT);
    params.gravity = gravity | Gravity.CENTER;
    try {
      params.setMargins(margin[0], margin[1], margin[2], margin[3]);
    } catch (Exception e) {
      int margin_15 = dp2px(context, 15);
      params.setMargins(margin_15, 0, margin_15, 0);
    }
    textView.setLayoutParams(params);
    toolbar.addView(textView);
    return textView;
  }

  public static TextView setCenterTitle(Toolbar toolbar, @StringRes int titleRes) {
    return setCenterTitle(toolbar, toolbar.getContext().getString(titleRes));
  }

  public static TextView setCenterTitle(Toolbar toolbar, String title) {
    TextView titleView = new TextView(toolbar.getContext());
    titleView.setGravity(Gravity.CENTER);
    titleView.setTextAppearance(toolbar.getContext(), R.style.Toolbar_titleTextAppearance);
    titleView.setSingleLine();
    titleView.setMaxEms(10);
    titleView.setEllipsize(TextUtils.TruncateAt.END);
    titleView.setText(title);
    ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT);
    titleView.setLayoutParams(params);
    Toolbar.LayoutParams lp = new Toolbar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.MATCH_PARENT);
    lp.gravity = Gravity.CENTER;
    toolbar.addView(titleView, lp);
    return titleView;
  }

  /**
   * @param isLight true: 白色背景, 深色文字
   */
  public static void requestStatusBarLight(Activity activity, boolean isLight) {
    requestStatusBarLight(activity, isLight, isLight ? 0xffcccccc : 0xffffffff);
  }

  /**
   * @param isLight 6.0及以上系统生效
   * @param color 6.0以下系统生效
   */
  public static void requestStatusBarLight(Activity activity, boolean isLight, int color) {
    View decorView = activity.getWindow().getDecorView();
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      if (isLight) {
        decorView.setSystemUiVisibility(
            decorView.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
      } else {
        decorView.setSystemUiVisibility(
            decorView.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
      }
      processPrivateAPI(activity.getWindow(), isLight);
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      activity.getWindow().setStatusBarColor(color);
    }
  }

  /**
   * @param isLight true: 白色背景, 深色文字
   */
  public static void requestStatusBarLightForDialog(DialogPlus dialog, View statusBar,
      boolean isLight) {
    Window window = dialog.getWindow();
    View decorView = window.getDecorView();
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      if (isLight) {
        decorView.setSystemUiVisibility(
            decorView.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
      } else {
        decorView.setSystemUiVisibility(
            decorView.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
      }
      processPrivateAPI(window, isLight);
      if (isLight) {
        statusBar.setBackgroundColor(0xffffffff);
      } else {
        statusBar.setBackgroundColor(0xffcccccc);
      }
    } else {
      if (isLight) {
        statusBar.setBackgroundColor(0xffcccccc);
      } else {
        statusBar.setBackgroundColor(0xffffffff);
      }
    }
  }

  public static void addUrlStyle(final Activity activity, Spannable s) {
    SpanUtils.addStyle(s, new URLSpan.SpanCreator() {
      @Override public URLSpan create(final String url) {
        return new URLSpan(url) {
          @Override public void onClick(View widget) {
            if (getURL() == null) return;
            Uri uri = Uri.parse(getURL());
            if (uri.getScheme() != null && (uri.getScheme().equalsIgnoreCase("http")
                || uri.getScheme().equalsIgnoreCase("https"))) {
              Bundle bundle = new Bundle();
              bundle.putString(BaseExtraKeys.KEY_URL, url);
              ActivityUtil.startActivity(activity, BaseBrowseFragment.class, bundle);
              return;
            }
            super.onClick(widget);
          }
        };
      }
    });
  }

  private static void processPrivateAPI(Window window, boolean lightStatusBar) {
    try {
      processFlyMe(window, lightStatusBar);
    } catch (Exception e) {
      try {
        processMIUI(window, lightStatusBar);
      } catch (Exception e2) {
        //
      }
    }
  }

  /**
   * 改变小米的状态栏字体颜色为黑色, 要求MIUI6以上
   * Tested on: MIUIV7 5.0 Redmi-Note3
   *
   * @throws Exception
   */
  private static void processMIUI(Window window, boolean lightStatusBar) throws Exception {
    Class<? extends Window> clazz = window.getClass();
    int darkModeFlag;
    Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
    Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
    darkModeFlag = field.getInt(layoutParams);
    Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
    extraFlagField.invoke(window, lightStatusBar ? darkModeFlag : 0, darkModeFlag);
  }

  /**
   * 改变魅族的状态栏字体为黑色，要求FlyMe4以上
   *
   * @throws Exception
   */
  private static void processFlyMe(Window window, boolean isLightStatusBar) throws Exception {
    WindowManager.LayoutParams lp = window.getAttributes();
    Class<?> instance = Class.forName("android.view.WindowManager$LayoutParams");
    int value = instance.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON").getInt(lp);
    Field field = instance.getDeclaredField("meizuFlags");
    field.setAccessible(true);
    int origin = field.getInt(lp);
    if (isLightStatusBar) {
      field.set(lp, origin | value);
    } else {
      field.set(lp, (~value) & origin);
    }
  }

  public static int dp2px(Context context, float dp) {
    return Math.round(dp * context.getResources().getDisplayMetrics().density);
  }

  /**
   * 处理编辑框跟清空文字按钮
   *
   * @param edit EditText
   * @param del 删除按钮
   */
  public static void renderEditText(final EditText edit, final View del) {
    del.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        edit.setText("");
      }
    });
    edit.addTextChangedListener(new TextWatcher() {
      @Override public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
      }

      @Override public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
      }

      @Override public void afterTextChanged(Editable editable) {
        del.setVisibility(editable.length() > 0 ? View.VISIBLE : View.INVISIBLE);
      }
    });
    edit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
      @Override public void onFocusChange(View v, boolean hasFocus) {
        del.setVisibility(hasFocus && edit.getText().length() > 0 ? View.VISIBLE : View.INVISIBLE);
      }
    });
  }
}

