package com.mdroid.lib.imagepick.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import com.mdroid.lib.imagepick.base.BaseFragment;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by ouyangzn on 2016/10/8.<br/>
 * Description：UI操作相关工具类
 * <p>e.g. 改变状态栏颜色</p>···
 */
public class UIUtil {

  /**
   * @param isLight true: 白色背景, 深色文字
   */
  public static void requestStatusBarLight(BaseFragment fragment, boolean isLight) {
    requestStatusBarLight(fragment, isLight, isLight ? 0xffcccccc : 0xffffffff);
  }

  /**
   * @param isLight 6.0及以上系统生效
   * @param color 6.0以下系统生效
   */
  public static void requestStatusBarLight(BaseFragment fragment, boolean isLight, int color) {
    View decorView = fragment.getActivity().getWindow().getDecorView();
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      if (isLight) {
        decorView.setSystemUiVisibility(
            decorView.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
      } else {
        decorView.setSystemUiVisibility(
            decorView.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
      }
      processPrivateAPI(fragment.getActivity().getWindow(), isLight);
    } else {
      fragment.getStatusBar().setBackgroundColor(color);
    }
  }

  /**
   * 权限申请
   *
   * @param fragment f
   * @param message 提示信息
   * @param reqCode 请求码
   * @param permission 权限列表
   * @return 是否拥有权限列表中的权限
   */
  public static boolean requestPermissions(final Fragment fragment, String message,
      final int reqCode, final String... permission) {
    // 判断是否需要授权
    boolean hasPermission = true;
    for (String p : permission) {
      if (ContextCompat.checkSelfPermission(fragment.getContext(), p)
          != PackageManager.PERMISSION_GRANTED) {
        hasPermission = false;
        break;
      }
    }
    if (hasPermission) {
      return true;
    }

    // 判断是否是否需要进入设置页面进行设置
    boolean shouldShowRequestPermissionRationale = false;
    for (String p : permission) {
      if (!fragment.shouldShowRequestPermissionRationale(p)) {
        shouldShowRequestPermissionRationale = true;
        break;
      }
    }

    if (shouldShowRequestPermissionRationale) {
      //CenterDialog.create(fragment.getActivity(), "权限申请", message, "取消",
      //    new IDialog.OnClickListener() {
      //      @Override public void onClick(DialogPlus dialog, View view) {
      //        dialog.dismiss();
      //      }
      //    }, "去设置", new IDialog.OnClickListener() {
      //      @Override public void onClick(DialogPlus dialog, View view) {
      //        dialog.dismiss();
      //        Intent intent = new Intent(Settings.ACTION_APPLICATION_SETTINGS);
      //        fragment.getActivity().startActivity(intent);
      //        fragment.requestPermissions(permission, reqCode);
      //      }
      //    }).show();
      return false;
    }
    fragment.requestPermissions(permission, reqCode);
    return false;
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
}
