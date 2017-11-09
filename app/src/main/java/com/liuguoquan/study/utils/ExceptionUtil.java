package com.liuguoquan.study.utils;

import com.liuguoquan.study.BuildConfig;

/**
 * Description:
 *
 * Created by liuguoquan on 2017/10/21 11:28.
 */

public class ExceptionUtil {
  public static String getMessage(Throwable throwable) {
    if (BuildConfig.DEBUG) {
      return throwable.getMessage();
    } else {
      return "网络异常";
    }
  }
}
