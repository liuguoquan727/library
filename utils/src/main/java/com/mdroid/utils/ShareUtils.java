package com.mdroid.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import java.io.File;

/**
 * 分享
 */
public class ShareUtils {

  /**
   * 分享功能
   *
   * @param activity 上下文
   * @param activityTitle Activity的名字
   * @param msgTitle 消息标题
   * @param msgText 消息内容
   * @param imgPath 图片路径，不分享图片则传null
   */
  public static void share(Activity activity, String activityTitle, String msgTitle, String msgText,
      String imgPath) {
    Intent intent = new Intent(Intent.ACTION_SEND);
    if (imgPath == null || imgPath.equals("")) {
      intent.setType("text/plain"); // 纯文本
    } else {
      File f = new File(imgPath);
      if (f.exists() && f.isFile()) {
        intent.setType("image/jpeg");
        Uri u = Uri.fromFile(f);
        intent.putExtra(Intent.EXTRA_STREAM, u);
      }
    }
    intent.putExtra(Intent.EXTRA_SUBJECT, msgTitle);
    intent.putExtra(Intent.EXTRA_TEXT, msgText);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    activity.startActivity(Intent.createChooser(intent, activityTitle));
  }
}
