package com.mdroid.utils.text;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Browser;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import com.mdroid.utils.Ln;

public class URLSpan extends ClickableSpan {

  private final String mURL;
  private String content;
  private int mColor = 0xFF576b95;
  private int mBgColor = 0xFF454545;
  private int mSize;
  private boolean mIsUnderline;
  private boolean mIsLongClickable;
  private View.OnLongClickListener mOnLongClickListener;

  private boolean mPressed;

  public URLSpan() {
    mURL = null;
  }

  public URLSpan(String url) {
    mURL = url;
  }

  public URLSpan(String url, int color, int bgColor, int size, boolean isUnderline) {
    this.mURL = url;
    this.mColor = color;
    this.mBgColor = bgColor;
    this.mSize = size;
    this.mIsUnderline = isUnderline;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public boolean isUnderline() {
    return mIsUnderline;
  }

  public void setUnderline(boolean isUnderline) {
    this.mIsUnderline = isUnderline;
  }

  public int getBgColor() {
    return mBgColor;
  }

  public void setBgColor(int bgColor) {
    this.mBgColor = bgColor;
  }

  public int getSize() {
    return mSize;
  }

  public void setSize(int size) {
    this.mSize = size;
  }

  public boolean isLongClickable() {
    return mIsLongClickable;
  }

  public void setLongClickable(boolean isLongClickable) {
    this.mIsLongClickable = isLongClickable;
  }

  public String getURL() {
    return mURL;
  }

  public int getColor() {
    return mColor;
  }

  @Override public void updateDrawState(TextPaint ds) {
    ds.setColor(mColor);
    ds.setUnderlineText(false);
    ds.setUnderlineText(mIsUnderline);
    if (mSize > 0) {
      ds.setTextSize(mSize);
    }
    if (mPressed) {
      ds.bgColor = mBgColor;
    }
  }

  @Override public void onClick(View widget) {
    if (getURL() == null) return;

    Uri uri = Uri.parse(getURL());
    Context context = widget.getContext();
    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
    intent.putExtra(Browser.EXTRA_APPLICATION_ID, context.getPackageName());
    try {
      context.startActivity(intent);
    } catch (ActivityNotFoundException e) {
      Ln.w("Actvity was not found for intent, " + intent.toString());
    }
  }

  public void setOnLongClickListener(@Nullable View.OnLongClickListener l) {
    if (!isLongClickable()) {
      setLongClickable(true);
    }
    mOnLongClickListener = l;
  }

  public void onLongClick(View widget) {
    mOnLongClickListener.onLongClick(widget);
  }

  public boolean isPressed() {
    return mPressed;
  }

  public void setPressed(boolean pressed) {
    this.mPressed = pressed;
  }

  public interface SpanCreator {
    URLSpan create(String url);
  }
}
