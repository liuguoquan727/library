package com.mdroid.lib.core.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import com.mdroid.lib.core.base.BaseActivity;
import com.mdroid.utils.Ln;

/**
 * activity 通用工具
 */
public class ActivityUtil {

  public static void startActivity(Fragment fragment, Class<? extends Fragment> fragmentClass) {
    startActivity(fragment, fragmentClass, null, -1, null);
  }

  public static void startActivity(Fragment fragment, Class<? extends Fragment> fragmentClass,
      int requestCode) {
    startActivity(fragment, fragmentClass, null, requestCode, null);
  }

  public static void startActivity(Fragment fragment, Class<? extends Fragment> fragmentClass,
      Bundle bundle) {
    startActivity(fragment, fragmentClass, bundle, -1, null);
  }

  public static void startActivity(Fragment fragment, Class<? extends Fragment> fragmentClass,
      Bundle bundle, int requestCode) {
    startActivity(fragment, fragmentClass, bundle, requestCode, null);
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

  public static void startActivity(Activity activity, Class<? extends Fragment> fragmentClass) {
    startActivity(activity, fragmentClass, null, -1, null);
  }

  public static void startActivity(Activity activity, Class<? extends Fragment> fragmentClass,
      int requestCode) {
    startActivity(activity, fragmentClass, null, requestCode, null);
  }

  public static void startActivity(Activity activity, Class<? extends Fragment> fragmentClass,
      Bundle bundle) {
    startActivity(activity, fragmentClass, bundle, -1, null);
  }

  public static void startActivity(Activity activity, Class<? extends Fragment> fragmentClass,
      Bundle bundle, int requestCode) {
    startActivity(activity, fragmentClass, bundle, requestCode, null);
  }

  public static void startActivity(Activity activity, Class<? extends Activity> activityClass,
      Class<? extends Fragment> fragmentClass) {
    startActivity(activity, activityClass, fragmentClass, null, -1, null);
  }

  public static void startActivity(Activity activity, Class<? extends Activity> activityClass,
      Class<? extends Fragment> fragmentClass, int requestCode) {
    startActivity(activity, activityClass, fragmentClass, null, requestCode, null);
  }

  public static void startActivity(Activity activity, Class<? extends Activity> activityClass,
      Class<? extends Fragment> fragmentClass, Bundle bundle) {
    startActivity(activity, activityClass, fragmentClass, bundle, -1, null);
  }

  public static void startActivity(Activity activity, Class<? extends Activity> activityClass,
      Class<? extends Fragment> fragmentClass, Bundle bundle, int requestCode) {
    startActivity(activity, activityClass, fragmentClass, bundle, requestCode, null);
  }

  public static void startActivity(Fragment fragment, Class<? extends Fragment> fragmentClass,
      Bundle bundle, int requestCode, Bundle options) {
    startActivity(fragment, BaseActivity.class, fragmentClass, bundle, requestCode, options);
  }

  /**
   * @param bundle bundle for {@link Fragment#setArguments(Bundle)}
   * @param requestCode request code for activity result
   * @param options Additional options for how the Activity should be started.
   * May be null if there are no options.  See {@link android.support.v4.app.ActivityOptionsCompat}
   * for how to build the Bundle supplied here; there are no supported definitions
   * for building it manually.
   */
  public static void startActivity(Fragment fragment, Class<? extends Activity> activityClass,
      Class<? extends Fragment> fragmentClass, Bundle bundle, int requestCode, Bundle options) {
    Activity activity = fragment.getActivity();
    if (activity == null) {
      Ln.e(fragment.getClass().getCanonicalName() + " not added.");
      return;
    }
    if (activity instanceof BaseActivity && requestCode != -1) {
      ((BaseActivity) activity).setFragmentRecord(fragment);
    }
    startActivity(activity, activityClass, fragmentClass, bundle, requestCode, options);
  }

  public static void startActivity(Activity activity, Class<? extends Fragment> fragmentClass,
      Bundle bundle, int requestCode, Bundle options) {
    startActivity(activity, BaseActivity.class, fragmentClass, bundle, requestCode, options);
  }

  public static void startActivity(Activity activity, Class<? extends Activity> activityClass,
      Class<? extends Fragment> fragmentClass, Bundle bundle, int requestCode, Bundle options) {
    Intent intent = new Intent(activity, activityClass);
    if (bundle != null) intent.putExtras(bundle);
    intent.putExtra(BaseActivity.FRAGMENT_NAME, fragmentClass.getCanonicalName());

    startActivity(activity, intent, requestCode, options);
  }

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
    Activity activity = fragment.getActivity();
    if (activity instanceof BaseActivity && requestCode != -1) {
      ((BaseActivity) activity).setFragmentRecord(fragment);
    }
    startActivity(activity, intent, requestCode, options);
  }

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
