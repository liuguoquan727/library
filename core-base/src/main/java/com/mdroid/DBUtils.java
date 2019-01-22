package com.mdroid;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.mdroid.utils.Ln;
import io.paperdb.Book;
import io.paperdb.Paper;
import java.util.List;

public class DBUtils {
  private static final String DEFAULT_DB_NAME = "mDroidDB";
  private static Book BOOK;

  static {
    Paper.init(Library.Instance().getContext());
    create(DEFAULT_DB_NAME);
  }

  public static <T> Book write(@NonNull String key, @NonNull T value) {
    try {
      return BOOK.write(key, value);
    } catch (Throwable e) {
      Ln.e(e);
      return BOOK;
    }
  }

  public static <T> T read(@NonNull String key) {
    return read(key, null);
  }

  public static <T> T read(@NonNull String key, @Nullable T defaultValue) {
    try {
      return BOOK.read(key, defaultValue);
    } catch (Throwable e) {
      Ln.e(e);
      return defaultValue;
    }
  }

  public static void delete(@NonNull String key) {
    try {
      BOOK.delete(key);
    } catch (Throwable e) {
      Ln.e(e);
    }
  }

  public static boolean exist(@NonNull String key) {
    try {
      return BOOK.exist(key);
    } catch (Throwable e) {
      Ln.e(e);
      return false;
    }
  }

  public static List<String> getAllKeys() {
    try {
      return BOOK.getAllKeys();
    } catch (Throwable e) {
      Ln.e(e);
      return null;
    }
  }

  public static void destroy() {
    try {
      BOOK.destroy();
    } catch (Throwable e) {
      Ln.e(e);
    }
  }

  public static void create(@NonNull String name) {
    BOOK = Paper.book(name);
  }
}
