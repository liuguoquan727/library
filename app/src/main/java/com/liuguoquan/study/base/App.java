package com.liuguoquan.study.base;

import android.content.Context;
import android.support.multidex.MultiDex;
import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.liuguoquan.study.BuildConfig;
import com.mdroid.lib.core.base.BaseApp;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;
import okhttp3.OkHttpClient;
import timber.log.Timber;

/**
 * Created by liuguoquan on 2017/7/26.
 */

public class App extends BaseApp {

  @Override protected void attachBaseContext(Context base) {
    super.attachBaseContext(base);
    MultiDex.install(this);
  }

  private static App mInstance;

  public static App getInstance() {
    return mInstance;
  }

  @Override public void onCreate() {
    super.onCreate();
    mInstance = this;
    FormatStrategy strategy = PrettyFormatStrategy.newBuilder().
        tag("lgq").build();
    Logger.addLogAdapter(new AndroidLogAdapter(strategy) {
      @Override public boolean isLoggable(int priority, String tag) {
        return BuildConfig.DEBUG;
      }
    });
    if (BuildConfig.DEBUG) {
      Timber.plant(new Timber.DebugTree());
    }
    Stetho.initializeWithDefaults(this);
    new OkHttpClient().newBuilder().addNetworkInterceptor(new StethoInterceptor()).build();
  }

  @Override public boolean isDebug() {
    return BuildConfig.DEBUG;
  }
}
