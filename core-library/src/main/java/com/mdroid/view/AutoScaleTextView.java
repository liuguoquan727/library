package com.mdroid.view;

import android.content.Context;
import android.content.res.Resources;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.method.TransformationMethod;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.TextView;

/**
 * A custom Text View that lowers the text size when the text is to big for the TextView.
 */
public class AutoScaleTextView extends TextView {
  // Minimum size of the text in pixels
  private static final int DEFAULT_MIN_TEXT_SIZE = 8; //sp
  // How precise we want to be when reaching the target textWidth size
  private static final float DEFAULT_PRECISION = 0.5f;

  private float mMinTextSize = DEFAULT_MIN_TEXT_SIZE;
  private float mPrecision = DEFAULT_PRECISION;

  private int mMaxLines;

  public AutoScaleTextView(Context context) {
    this(context, null);
  }

  public AutoScaleTextView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public AutoScaleTextView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  /**
   * Re-sizes the textSize of the TextView so that the text fits within the bounds of the View.
   */
  private static void autofit(TextView view, TextPaint paint, float minTextSize, float maxTextSize,
      int maxLines, float precision) {
    if (maxLines <= 0 || maxLines == Integer.MAX_VALUE) {
      // Don't auto-size since there's no limit on lines.
      return;
    }

    int targetWidth = view.getWidth() - view.getPaddingLeft() - view.getPaddingRight();
    if (targetWidth <= 0) {
      return;
    }

    CharSequence text = view.getText();
    TransformationMethod method = view.getTransformationMethod();
    if (method != null) {
      text = method.getTransformation(text, view);
    }

    Context context = view.getContext();
    Resources r = Resources.getSystem();
    DisplayMetrics displayMetrics;

    float size = maxTextSize;
    float high = size;
    float low = 0;

    if (context != null) {
      r = context.getResources();
    }
    displayMetrics = r.getDisplayMetrics();

    paint.set(view.getPaint());
    paint.setTextSize(size);

    if ((maxLines == 1 && paint.measureText(text, 0, text.length()) > targetWidth)
        || getLineCount(text, paint, size, targetWidth, displayMetrics) > maxLines) {
      size = getAutofitTextSize(text, paint, targetWidth, maxLines, low, high, precision,
          displayMetrics);
    }

    if (size < minTextSize) {
      size = minTextSize;
    }

    view.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
  }

  /**
   * Recursive binary search to find the best size for the text.
   */
  private static float getAutofitTextSize(CharSequence text, TextPaint paint, float targetWidth,
      int maxLines, float low, float high, float precision, DisplayMetrics displayMetrics) {
    float mid = (low + high) / 2.0f;
    int lineCount = 1;
    StaticLayout layout = null;

    paint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, mid, displayMetrics));

    if (maxLines != 1) {
      layout = new StaticLayout(text, paint, (int) targetWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f,
          0.0f, true);
      lineCount = layout.getLineCount();
    }

    if (lineCount > maxLines) {
      return getAutofitTextSize(text, paint, targetWidth, maxLines, low, mid, precision,
          displayMetrics);
    } else if (lineCount < maxLines) {
      return getAutofitTextSize(text, paint, targetWidth, maxLines, mid, high, precision,
          displayMetrics);
    } else {
      float maxLineWidth = 0;
      if (maxLines == 1) {
        maxLineWidth = paint.measureText(text, 0, text.length());
      } else {
        for (int i = 0; i < lineCount; i++) {
          if (layout.getLineWidth(i) > maxLineWidth) {
            maxLineWidth = layout.getLineWidth(i);
          }
        }
      }

      if ((high - low) < precision) {
        return low;
      } else if (maxLineWidth > targetWidth) {
        return getAutofitTextSize(text, paint, targetWidth, maxLines, low, mid, precision,
            displayMetrics);
      } else if (maxLineWidth < targetWidth) {
        return getAutofitTextSize(text, paint, targetWidth, maxLines, mid, high, precision,
            displayMetrics);
      } else {
        return mid;
      }
    }
  }

  private static int getLineCount(CharSequence text, TextPaint paint, float size, float width,
      DisplayMetrics displayMetrics) {
    paint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, size, displayMetrics));
    StaticLayout layout =
        new StaticLayout(text, paint, (int) width, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true);
    return layout.getLineCount();
  }

  private void autofit() {
    autofit(this, getPaint(), mMinTextSize, getTextSize(), mMaxLines, mPrecision);
  }

  @Override public void setMaxLines(int maxlines) {
    super.setMaxLines(maxlines);
    mMaxLines = maxlines;
  }

  @Override public void setLines(int lines) {
    super.setLines(lines);
    mMaxLines = lines;
  }

  @Override protected void onTextChanged(final CharSequence text, final int start, final int before,
      final int after) {
    autofit();
  }

  @Override protected void onSizeChanged(int width, int height, int oldwidth, int oldheight) {
    if (width != oldwidth) autofit();
  }
}