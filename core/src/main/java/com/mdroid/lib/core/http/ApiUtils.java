package com.mdroid.lib.core.http;

import android.support.annotation.Nullable;
import com.mdroid.utils.Size;
import java.io.File;
import java.net.FileNameMap;
import java.net.URLConnection;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * 网络请求常用方法
 */
public class ApiUtils {

  public static String guessMimeType(String fileName) {
    FileNameMap fileNameMap = URLConnection.getFileNameMap();
    String contentTypeFor = fileNameMap.getContentTypeFor(fileName);
    if (contentTypeFor == null) {
      contentTypeFor = "application/octet-stream";
    }
    return contentTypeFor;
  }

  public static RequestBody body(@Nullable String content) {
    if (content == null) return null;
    return RequestBody.create(MediaType.parse("text/plain"), content);
  }

  public static RequestBody body(Object content) {
    if (content == null) return null;
    return body(String.valueOf(content));
  }

  public static MultipartBody.Part part(String name, File file) {
    return part(name, file, null);
  }

  public static MultipartBody.Part part(String name, File file, Size size) {
    return MultipartBody.Part.createFormData(name, file.getName(),
        new ImageBody(MediaType.parse("image/jpeg"), file, size));
  }
}
