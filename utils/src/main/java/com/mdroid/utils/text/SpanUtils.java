package com.mdroid.utils.text;

import android.content.res.ColorStateList;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.CharacterStyle;
import android.text.style.TextAppearanceSpan;
import java.util.regex.Pattern;

public class SpanUtils {
  public static final Pattern URL = Pattern.compile(
      "((http[s]{0,1}|ftp)://[a-zA-Z0-9\\.\\-]+\\.([a-zA-Z]{2,4})(:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>]*)?)|(www.[a-zA-Z0-9\\.\\-]+\\.([a-zA-Z]{2,4})(:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>]*)?)");
  public static final Pattern TEL = Pattern.compile(
      "(?<=^|\\D)(\\d{3}-\\d{8}|\\d{3}-\\d{7}|\\d{4}-\\d{8}|\\d{4}-\\d{7}|1[3-9]\\d{9}|\\d{8}|\\d{7})(?=$|\\D)");
  public static final Pattern EMAIL_ADDRESS = Pattern.compile("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}"
      + "\\@"
      + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}"
      + "("
      + "\\."
      + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}"
      + ")+");

  public static void addStyle(Spannable s, URLSpan.SpanCreator creator) {
    addStyle(s, Linkify.ALL, creator);
  }

  public static void addStyle(Spannable s, int mask, URLSpan.SpanCreator creator) {
    Linkify.addLinks(s, mask, creator);
  }

  public static void addStyle(Spannable s, Pattern pattern, String scheme) {
    Linkify.addLinks(s, pattern, scheme, null);
  }

  public static void addStyle(Spannable s, Pattern pattern, String scheme,
      URLSpan.SpanCreator spanCreator) {
    Linkify.addLinks(s, pattern, scheme, spanCreator);
  }

  public static void addStyle(Spannable s, int start, int end, CharacterStyle span) {
    s.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
  }

  public static void addStyle(Spannable s, int start, int end, int size, int color) {
    setStyle(s, start, end, null, 0, size, ColorStateList.valueOf(color), null);
  }

  public static void addStyle(Spannable s, int start, int end, int size, ColorStateList color) {
    setStyle(s, start, end, null, 0, size, color, null);
  }

  public static void setStyle(Spannable s, int start, int end, String family, int style, int size,
      ColorStateList color, ColorStateList linkColor) {
    addStyle(s, start, end, new TextAppearanceSpan(family, style, size, color, linkColor));
  }
}
