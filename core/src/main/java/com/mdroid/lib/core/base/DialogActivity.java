package com.mdroid.lib.core.base;

import android.os.Bundle;
import android.view.WindowManager;

/**
 * Dialog 样式的 Activity
 */
public class DialogActivity extends CommonActivity {
  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
        WindowManager.LayoutParams.MATCH_PARENT);
  }
}
