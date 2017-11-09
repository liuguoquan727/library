package com.liuguoquan.study.base;

import com.mdroid.lib.core.base.BaseApp;

/**
 * Created by liuguoquan on 2017/7/26.
 */

public class App extends BaseApp {

  private static App mInstance;

  public static App getInstance() {
    return mInstance;
  }

  @Override public boolean isDebug() {
    return true;
  }
}
