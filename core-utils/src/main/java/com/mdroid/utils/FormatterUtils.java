package com.mdroid.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 格式化
 */
public class FormatterUtils {

  /**
   * 转换文件大小
   *
   * @return B/KB/MB/GB
   */
  public static String formatFileSize(long size) {
    java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
    String fileSizeString;
    if (size < 1024) {
      fileSizeString = df.format((double) size) + "B";
    } else if (size < 1048576) {
      fileSizeString = df.format((double) size / 1024) + "KB";
    } else if (size < 1073741824) {
      fileSizeString = df.format((double) size / 1048576) + "MB";
    } else {
      fileSizeString = df.format((double) size / 1073741824) + "G";
    }
    return fileSizeString;
  }

  /**
   * 转换长度距离
   *
   * @return m/km
   */
  public static String formatMetre(long distance) {
    java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
    String distanceString;
    if (distance < 1000) {
      distanceString = df.format((double) distance) + "m";
    } else {
      distanceString = df.format((double) distance / 1000) + "km";
    }
    return distanceString;
  }

  /**
   * 将传入时间与当前时间进行对比, 是否今天昨天(微信聊天的时间显示)
   */
  public static String formatTimeForMessage(Date date) {
    String todySDF = "aa hh:mm";
    String yesterDaySDF = "昨天 aa hh:mm";
    String otherSDF = "M月d日 aa hh:mm";
    String otherYSDF = "yyyy年M月d日 aa hh:mm";
    SimpleDateFormat sfd = new SimpleDateFormat();
    Calendar dateCalendar = Calendar.getInstance();
    dateCalendar.setTime(date);
    Date now = new Date();
    Calendar targetCalendar = Calendar.getInstance();
    targetCalendar.setTime(now);
    targetCalendar.set(Calendar.HOUR_OF_DAY, 0);
    targetCalendar.set(Calendar.MINUTE, 0);
    if (dateCalendar.after(targetCalendar)) {
      sfd.applyPattern(todySDF);
      return sfd.format(date);
    } else {
      targetCalendar.add(Calendar.DATE, -1);
      if (dateCalendar.after(targetCalendar)) {
        sfd.applyPattern(yesterDaySDF);
        return sfd.format(date);
      }
    }
    sfd.applyPattern(dateCalendar.get(Calendar.YEAR) == targetCalendar.get(Calendar.YEAR) ? otherSDF
        : otherYSDF);
    return sfd.format(date);
  }
}
