package com.mdroid.utils.text;

import android.os.Build;
import android.text.Layout;
import android.text.NoCopySpan;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.MovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.TextView;

/**
 * A movement method that traverses links in the text buffer and scrolls if necessary.
 * Supports clicking on links with DPad Center or Enter.
 */
public class LinkMovementMethod extends ScrollingMovementMethod {
  private static final int CLICK = 1;
  private static final int UP = 2;
  private static final int DOWN = 3;
  private static LinkMovementMethod sInstance;
  private static Object FROM_BELOW = new NoCopySpan.Concrete();
  private boolean mHasPerformedLongPress;
  private CheckForLongPress mLongPress;
  private URLSpan mCurrentSpan;

  private static void invalidate(TextView widget) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      widget.invalidate();
    } else {
      widget.setText(widget.getText());
    }
  }

  private static int getLineForVertical(Layout layout, int vertical) {
    if (vertical < layout.getLineTop(0) || vertical > layout.getLineBottom(
        layout.getLineCount() - 1)) {
      return -1;
    }
    int high = layout.getLineCount(), low = -1, guess;

    while (high - low > 1) {
      guess = (high + low) / 2;

      if (layout.getLineTop(guess) > vertical) {
        high = guess;
      } else {
        low = guess;
      }
    }

    return low;
  }

  public static MovementMethod getInstance() {
    if (sInstance == null) sInstance = new LinkMovementMethod();

    return sInstance;
  }

  @Override public boolean canSelectArbitrarily() {
    return false;
  }

  @Override protected boolean handleMovementKey(TextView widget, Spannable buffer, int keyCode,
      int movementMetaState, KeyEvent event) {
    switch (keyCode) {
      case KeyEvent.KEYCODE_DPAD_CENTER:
      case KeyEvent.KEYCODE_ENTER:
        if (event.hasModifiers(movementMetaState)) {
          if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0 && action(
              CLICK, widget, buffer)) {
            return true;
          }
        }
        break;
    }
    return super.handleMovementKey(widget, buffer, keyCode, movementMetaState, event);
  }

  @Override protected boolean up(TextView widget, Spannable buffer) {
    if (action(UP, widget, buffer)) {
      return true;
    }

    return super.up(widget, buffer);
  }

  @Override protected boolean down(TextView widget, Spannable buffer) {
    if (action(DOWN, widget, buffer)) {
      return true;
    }

    return super.down(widget, buffer);
  }

  @Override protected boolean left(TextView widget, Spannable buffer) {
    if (action(UP, widget, buffer)) {
      return true;
    }

    return super.left(widget, buffer);
  }

  @Override protected boolean right(TextView widget, Spannable buffer) {
    if (action(DOWN, widget, buffer)) {
      return true;
    }

    return super.right(widget, buffer);
  }

  private boolean action(int what, TextView widget, Spannable buffer) {
    Layout layout = widget.getLayout();

    int padding = widget.getTotalPaddingTop() + widget.getTotalPaddingBottom();
    int areatop = widget.getScrollY();
    int areabot = areatop + widget.getHeight() - padding;

    int linetop = layout.getLineForVertical(areatop);
    int linebot = layout.getLineForVertical(areabot);

    int first = layout.getLineStart(linetop);
    int last = layout.getLineEnd(linebot);

    URLSpan[] candidates = buffer.getSpans(first, last, URLSpan.class);

    int a = Selection.getSelectionStart(buffer);
    int b = Selection.getSelectionEnd(buffer);

    int selStart = Math.min(a, b);
    int selEnd = Math.max(a, b);

    if (selStart < 0) {
      if (buffer.getSpanStart(FROM_BELOW) >= 0) {
        selStart = selEnd = buffer.length();
      }
    }

    if (selStart > last) selStart = selEnd = Integer.MAX_VALUE;
    if (selEnd < first) selStart = selEnd = -1;

    switch (what) {
      case CLICK:
        if (selStart == selEnd) {
          return false;
        }

        URLSpan[] link = buffer.getSpans(selStart, selEnd, URLSpan.class);

        if (link.length != 1) return false;

        link[0].onClick(widget);
        break;

      case UP:
        int beststart, bestend;

        beststart = -1;
        bestend = -1;

        for (int i = 0; i < candidates.length; i++) {
          int end = buffer.getSpanEnd(candidates[i]);

          if (end < selEnd || selStart == selEnd) {
            if (end > bestend) {
              beststart = buffer.getSpanStart(candidates[i]);
              bestend = end;
            }
          }
        }

        if (beststart >= 0) {
          Selection.setSelection(buffer, bestend, beststart);
          return true;
        }

        break;

      case DOWN:
        beststart = Integer.MAX_VALUE;
        bestend = Integer.MAX_VALUE;

        for (int i = 0; i < candidates.length; i++) {
          int start = buffer.getSpanStart(candidates[i]);

          if (start > selStart || selStart == selEnd) {
            if (start < beststart) {
              beststart = start;
              bestend = buffer.getSpanEnd(candidates[i]);
            }
          }
        }

        if (bestend < Integer.MAX_VALUE) {
          Selection.setSelection(buffer, beststart, bestend);
          return true;
        }

        break;
    }

    return false;
  }

  @Override public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
    int action = event.getAction();
    int x = (int) event.getX();
    int y = (int) event.getY();

    x -= widget.getTotalPaddingLeft();
    y -= widget.getTotalPaddingTop();

    x += widget.getScrollX();
    y += widget.getScrollY();

    Layout layout = widget.getLayout();
    if (action == MotionEvent.ACTION_DOWN) {
      URLSpan span = findSpan(x, y, layout, buffer);
      if (span != null) {
        mCurrentSpan = span;
        mCurrentSpan.setPressed(true);
        invalidate(widget);
        if (mCurrentSpan.isLongClickable()) {
          mLongPress = new CheckForLongPress(mCurrentSpan, widget);
          widget.postDelayed(mLongPress, ViewConfiguration.getLongPressTimeout());
        }
        return true;
      }
    } else if (action == MotionEvent.ACTION_MOVE) {
      // Be lenient about moving outside of buttons
      if (mCurrentSpan != null) {
        URLSpan span = findSpan(x, y, layout, buffer);
        // Outside button
        if (mCurrentSpan != span && mCurrentSpan.isPressed()) {
          // Remove any future long press/tap checks
          widget.removeCallbacks(mLongPress);
          mCurrentSpan.setPressed(false);
          invalidate(widget);
          mCurrentSpan = null;
        }
      }
    } else if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
      widget.removeCallbacks(mLongPress);

      if (!mHasPerformedLongPress && mCurrentSpan != null && mCurrentSpan.isPressed()) {
        mCurrentSpan.setPressed(false);
        invalidate(widget);
        if (action == MotionEvent.ACTION_UP) mCurrentSpan.onClick(widget);
      }
      mHasPerformedLongPress = false;
      mCurrentSpan = null;
      mLongPress = null;
      return true;
    }

    return super.onTouchEvent(widget, buffer, event);
  }

  @Override public void initialize(TextView widget, Spannable text) {
    Selection.removeSelection(text);
    text.removeSpan(FROM_BELOW);
  }

  @Override public void onTakeFocus(TextView view, Spannable text, int dir) {
    Selection.removeSelection(text);

    if ((dir & View.FOCUS_BACKWARD) != 0) {
      text.setSpan(FROM_BELOW, 0, 0, Spannable.SPAN_POINT_POINT);
    } else {
      text.removeSpan(FROM_BELOW);
    }
  }

  private URLSpan findSpan(int x, int y, Layout layout, Spannable buffer) {
    int line = getLineForVertical(layout, y);
    if (line >= 0) {
      int off = layout.getOffsetForHorizontal(line, x);
      URLSpan[] link = buffer.getSpans(off, off, URLSpan.class);
      if (link.length != 0) {
        return link[0];
      }
    }
    return null;
  }

  private class CheckForLongPress implements Runnable {
    URLSpan span;
    TextView widget;

    public CheckForLongPress(URLSpan span, TextView widget) {
      this.span = span;
      this.widget = widget;
    }

    public void run() {
      span.setPressed(false);
      invalidate(widget);
      span.onLongClick(widget);
      mHasPerformedLongPress = true;
    }
  }
}
