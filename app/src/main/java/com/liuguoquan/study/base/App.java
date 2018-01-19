package com.liuguoquan.study.base;

import android.content.Context;
import android.support.multidex.MultiDex;
import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.bind.TypeAdapters;
import com.liuguoquan.study.BuildConfig;
import com.mdroid.lib.core.base.BaseApp;
import com.mdroid.lib.core.json.DoubleAdapter;
import com.mdroid.lib.core.json.IntegerAdapter;
import com.mdroid.lib.core.json.LongAdapter;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;
import com.tencent.bugly.crashreport.CrashReport;
import java.lang.reflect.Modifier;
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
  private Gson mAppGson;

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
    CrashReport.initCrashReport(getApplicationContext(), "3613d5bb7b", false);
  }

  public static synchronized Gson getAppGson() {
    if (mInstance.mAppGson == null) {
      mInstance.mAppGson =
          new GsonBuilder().excludeFieldsWithModifiers(Modifier.FINAL, Modifier.STATIC,
              Modifier.TRANSIENT)
              .registerTypeAdapterFactory(
                  TypeAdapters.newFactory(int.class, Integer.class, new IntegerAdapter()))
              .registerTypeAdapterFactory(
                  TypeAdapters.newFactory(double.class, Double.class, new DoubleAdapter()))
              .registerTypeAdapterFactory(
                  TypeAdapters.newFactory(long.class, Long.class, new LongAdapter()))
              .serializeNulls()
              .create();
    }
    return mInstance.mAppGson;
  }

  @Override public boolean isDebug() {
    return BuildConfig.DEBUG;
  }
}
