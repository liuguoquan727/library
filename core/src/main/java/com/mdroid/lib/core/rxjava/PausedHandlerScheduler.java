package com.mdroid.lib.core.rxjava;

import android.os.Handler;
import android.os.Message;
import com.mdroid.PausedHandler;
import io.reactivex.Scheduler;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.concurrent.TimeUnit;

/**
 * A {@link Scheduler} backed by a {@link PausedHandler}.
 */
public final class PausedHandlerScheduler extends Scheduler {
  private final PausedHandler handler;

  PausedHandlerScheduler(PausedHandler handler) {
    this.handler = handler;
  }

  /**
   * Create a {@link Scheduler} which uses {@code PausedHandler} to execute actions.
   */
  public static PausedHandlerScheduler from(PausedHandler handler) {
    if (handler == null) throw new NullPointerException("handler == null");
    return new PausedHandlerScheduler(handler);
  }

  @Override
  public Disposable scheduleDirect(@NonNull Runnable run, long delay, @NonNull TimeUnit unit) {
    if (run == null) throw new NullPointerException("run == null");
    if (unit == null) throw new NullPointerException("unit == null");

    run = RxJavaPlugins.onSchedule(run);
    ScheduledRunnable scheduled = new ScheduledRunnable(handler, run);
    handler.postDelayed(scheduled, Math.max(0L, unit.toMillis(delay)));
    return scheduled;
  }

  @Override public Worker createWorker() {
    return new HandlerWorker(handler);
  }

  private static final class HandlerWorker extends Worker {

    private final Handler handler;
    private volatile boolean disposed;

    HandlerWorker(Handler handler) {
      this.handler = handler;
    }

    @Override
    public Disposable schedule(@NonNull Runnable run, long delay, @NonNull TimeUnit unit) {
      if (run == null) throw new NullPointerException("run == null");
      if (unit == null) throw new NullPointerException("unit == null");

      if (disposed) {
        return Disposables.disposed();
      }

      run = RxJavaPlugins.onSchedule(run);
      ScheduledRunnable scheduled = new ScheduledRunnable(handler, run);

      Message message = Message.obtain(handler, PausedHandler.RX_MESSAGE_ID);
      message.obj = scheduled;
      handler.sendMessageDelayed(message, Math.max(0L, unit.toMillis(delay)));

      if (disposed) {
        handler.removeMessages(PausedHandler.RX_MESSAGE_ID, scheduled);
        return Disposables.disposed();
      }

      return scheduled;
    }

    @Override public void dispose() {
      disposed = true;
      handler.removeCallbacksAndMessages(this);
    }

    @Override public boolean isDisposed() {
      return disposed;
    }
  }

  private static final class ScheduledRunnable implements Runnable, Disposable {
    private final Handler handler;
    private final Runnable delegate;

    private volatile boolean disposed;

    ScheduledRunnable(Handler handler, Runnable delegate) {
      this.handler = handler;
      this.delegate = delegate;
    }

    @Override public void run() {
      try {
        delegate.run();
      } catch (Throwable t) {
        IllegalStateException ie =
            new IllegalStateException("Fatal Exception thrown on Scheduler.", t);
        RxJavaPlugins.onError(ie);
        Thread thread = Thread.currentThread();
        thread.getUncaughtExceptionHandler().uncaughtException(thread, ie);
      }
    }

    @Override public void dispose() {
      disposed = true;
      handler.removeCallbacks(this);
    }

    @Override public boolean isDisposed() {
      return disposed;
    }
  }
}
