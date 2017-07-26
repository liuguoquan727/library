package com.mdroid.lib.core.base;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import com.mdroid.lib.core.R;

/**
 * 全屏 Activity
 */
public class FullScreenActivity extends CommonActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
    supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
    super.onCreate(savedInstanceState);
  }

  @Override public void finish() {
    overridePendingTransition(R.anim.fade_in_center, R.anim.fade_out_center);
    super.finish();
  }
}
