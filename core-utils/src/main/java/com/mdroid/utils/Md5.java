package com.mdroid.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5 {

  public static String MD5(String text) {
    try {
      byte[] buffer = MessageDigest.getInstance("MD5").digest(text.getBytes("UTF-8"));
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < buffer.length; i++) {
        int j = 0xFF & buffer[i];
        if (j < 16) {
          sb.append("0");
        }
        sb.append(Integer.toHexString(j));
      }
      return sb.toString();
    } catch (NoSuchAlgorithmException e) {
      throw new UnsupportedOperationException("unsupported message Digest md5");
    } catch (UnsupportedEncodingException e) {
      throw new UnsupportedOperationException("unsupported encoding UTF-8");
    }
  }
}
