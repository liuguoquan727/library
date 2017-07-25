package com.mdroid.lib.core.rxjava;

import com.mdroid.utils.Ln;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import java.net.SocketTimeoutException;

public class RxJavaUtil {

  public static BiFunction<Integer, Throwable, Boolean> timeoutRetry() {
    return new BiFunction<Integer, Throwable, Boolean>() {
      @Override public Boolean apply(Integer integer, Throwable throwable) {
        Ln.w(throwable);
        if (throwable instanceof Throwable) throwable = throwable.getCause();
        return throwable instanceof SocketTimeoutException && integer < 3;
      }
    };
  }

  public static <T> Consumer<T> discardResult() {
    return new Consumer<T>() {
      @Override public void accept(T t) {
        Ln.w("discardResult, result = " + (t == null ? "null" : t.toString()));
      }
    };
  }

  public static Consumer<Throwable> discardError() {
    return new Consumer<Throwable>() {
      @Override public void accept(Throwable t) {
        Ln.w("discardError, error = " + (t == null ? "null" : t.getMessage()));
      }
    };
  }

  public static <T> Predicate<T> filterNon() {
    return new Predicate<T>() {
      @Override public boolean test(T t) {
        return t != null;
      }
    };
  }
}
