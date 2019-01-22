package com.mdroid.lib.core.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import com.mdroid.lib.core.base.CommonActivity;


/**
 * activity 通用工具
 */
public class ActivityUtil {

  /***
   * fragment 启动 CommonActivity 加载Fragment
   * fragment.startActivityForResult生效必须调此类方法，否则fragment的onActivityResult将收不到回调
   * @param fragment
   * @param fragmentClass
   */
  public static void startActivity(Fragment fragment, Class<? extends Fragment> fragmentClass) {
    startActivity(fragment, CommonActivity.class, fragmentClass, null, -1, null);
  }

  public static void startActivity(Fragment fragment, Class<? extends Fragment> fragmentClass,
      int requestCode) {
    startActivity(fragment, CommonActivity.class, fragmentClass, null, requestCode, null);
  }

  public static void startActivity(Fragment fragment, Class<? extends Fragment> fragmentClass,
      Bundle bundle) {
    startActivity(fragment, CommonActivity.class, fragmentClass, bundle, -1, null);
  }

  public static void startActivity(Fragment fragment, Class<? extends Fragment> fragmentClass,
      Bundle bundle, int requestCode) {
    startActivity(fragment, CommonActivity.class, fragmentClass, bundle, requestCode, null);
  }

  public static void startActivity(Fragment fragment, Class<? extends Activity> activityClass,
      Class<? extends Fragment> fragmentClass) {
    startActivity(fragment, activityClass, fragmentClass, null, -1, null);
  }

  public static void startActivity(Fragment fragment, Class<? extends Activity> activityClass,
      Class<? extends Fragment> fragmentClass, Bundle bundle) {
    startActivity(fragment, activityClass, fragmentClass, bundle, -1, null);
  }

  public static void startActivity(Fragment fragment, Class<? extends Activity> activityClass,
      Class<? extends Fragment> fragmentClass, int requestCode) {
    startActivity(fragment, activityClass, fragmentClass, null, requestCode, null);
  }

  public static void startActivity(Fragment fragment, Class<? extends Activity> activityClass,
      Class<? extends Fragment> fragmentClass, Bundle bundle, int requestCode) {
    startActivity(fragment, activityClass, fragmentClass, bundle, requestCode, null);
  }

  public static void startActivity(Fragment fragment, Class<? extends Activity> activityClass,
      Class<? extends Fragment> fragmentClass, Bundle bundle, int requestCode, Bundle options) {
    Intent intent = new Intent(fragment.getActivity(), activityClass);
    if (bundle != null) {
      intent.putExtras(bundle);
    }
    intent.putExtra(CommonActivity.FRAGMENT_NAME, fragmentClass.getCanonicalName());

    if (requestCode == -1) {
      fragment.startActivity(intent, options);
    } else {
      fragment.startActivityForResult(intent, requestCode, options);
    }
  }

  /**
   * Activity 启动 CommonActivity 加载Fragment
   */

  public static void startActivity(Activity fragment, Class<? extends Fragment> fragmentClass) {
    startActivity(fragment, CommonActivity.class, fragmentClass, null, -1, null);
  }

  public static void startActivity(Activity fragment, Class<? extends Fragment> fragmentClass,
      int requestCode) {
    startActivity(fragment, CommonActivity.class, fragmentClass, null, requestCode, null);
  }

  public static void startActivity(Activity fragment, Class<? extends Fragment> fragmentClass,
      Bundle bundle) {
    startActivity(fragment, CommonActivity.class, fragmentClass, bundle, -1, null);
  }

  public static void startActivity(Activity fragment, Class<? extends Fragment> fragmentClass,
      Bundle bundle, int requestCode) {
    startActivity(fragment, CommonActivity.class, fragmentClass, bundle, requestCode, null);
  }

  public static void startActivity(Activity fragment, Class<? extends Activity> activityClass,
      Class<? extends Fragment> fragmentClass) {
    startActivity(fragment, activityClass, fragmentClass, null, -1, null);
  }

  public static void startActivity(Activity fragment, Class<? extends Activity> activityClass,
      Class<? extends Fragment> fragmentClass, Bundle bundle) {
    startActivity(fragment, activityClass, fragmentClass, bundle, -1, null);
  }

  public static void startActivity(Activity fragment, Class<? extends Activity> activityClass,
      Class<? extends Fragment> fragmentClass, int requestCode) {
    startActivity(fragment, activityClass, fragmentClass, null, requestCode, null);
  }

  public static void startActivity(Activity fragment, Class<? extends Activity> activityClass,
      Class<? extends Fragment> fragmentClass, Bundle bundle, int requestCode) {
    startActivity(fragment, activityClass, fragmentClass, bundle, requestCode, null);
  }

  public static void startActivity(Activity activity, Class<? extends Activity> activityClass,
      Class<? extends Fragment> fragmentClass, Bundle bundle, int requestCode, Bundle options) {
    Intent intent = new Intent(activity, activityClass);
    if (bundle != null) {
      intent.putExtras(bundle);
    }
    intent.putExtra(CommonActivity.FRAGMENT_NAME, fragmentClass.getCanonicalName());

    if (requestCode == -1) {
      ActivityCompat.startActivity(activity, intent, options);
    } else {
      ActivityCompat.startActivityForResult(activity, intent, requestCode, options);
    }
  }

  /**
   * Fragment 启动 Activity
   */
  public static void startActivity(Fragment fragment, Intent intent) {
    startActivity(fragment, intent, -1, null);
  }

  public static void startActivity(Fragment fragment, Intent intent, Bundle options) {
    startActivity(fragment, intent, -1, options);
  }

  public static void startActivity(Fragment fragment, Intent intent, int requestCode) {
    startActivity(fragment, intent, requestCode, null);
  }

  public static void startActivity(Fragment fragment, Intent intent, int requestCode,
      Bundle options) {
    if (requestCode == -1) {
      fragment.startActivity(intent, options);
    } else {
      fragment.startActivityForResult(intent, requestCode, options);
    }
  }

  /**
   * Activity 启动 Activity
   */
  public static void startActivity(Activity activity, Intent intent) {
    startActivity(activity, intent, -1, null);
  }

  public static void startActivity(Activity activity, Intent intent, Bundle options) {
    startActivity(activity, intent, -1, options);
  }

  public static void startActivity(Activity activity, Intent intent, int requestCode) {
    startActivity(activity, intent, requestCode, null);
  }

  public static void startActivity(Activity activity, Intent intent, int requestCode,
      Bundle options) {
    if (requestCode == -1) {
      ActivityCompat.startActivity(activity, intent, options);
    } else {
      ActivityCompat.startActivityForResult(activity, intent, requestCode, options);
    }
  }

  public static void finish(Activity activity) {
    ActivityCompat.finishAffinity(activity);
  }

  public static void finishAfterTransition(Activity activity) {
    ActivityCompat.finishAfterTransition(activity);
  }
}
