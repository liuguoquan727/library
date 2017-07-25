package com.mdroid.lib.imagepick.utils;

import android.os.Environment;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by ouyangzn on 2016/11/16.<br/>
 * Description：
 */
public class SDCardUtil {
  /**
   * 在系统 'Pictures' 文件夹中生成以 '.jpg' 结束的文件
   */
  public static File getTmpFile() {
    if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
      return getTmpFile(null);
    }
    throw new IllegalStateException("The media not mounted.");
  }

  private synchronized static File getTmpFile(String random) {
    File pic = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
    if (!pic.exists()) {
      pic.mkdirs();
    }
    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(new Date());
    StringBuilder builder = new StringBuilder();
    builder.append("media_");
    builder.append(timeStamp);
    if (random != null) builder.append("_").append(random);
    builder.append(".jpg");
    String fileName = builder.toString();
    File file = new File(pic, fileName);
    if (file.exists()) {
      file = getTmpFile(String.valueOf(System.currentTimeMillis()));
    }
    return file;
  }
}
