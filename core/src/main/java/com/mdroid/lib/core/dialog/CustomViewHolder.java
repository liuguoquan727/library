package com.mdroid.lib.core.dialog;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.orhanobut.dialogplus.Holder;
import com.orhanobut.dialogplus.ViewHolder;

/**
 * {@link ViewHolder} 包裹了一层 {@link R.layout#dialog_view}<br>
 * CustomViewHolder 没有包裹任何布局
 */
class CustomViewHolder implements Holder {

  private static final int INVALID = -1;

  private View.OnKeyListener keyListener;

  private View contentView;
  private int viewResourceId = INVALID;

  public CustomViewHolder(int viewResourceId) {
    this.viewResourceId = viewResourceId;
  }

  public CustomViewHolder(View contentView) {
    this.contentView = contentView;
  }

  @Override public void addHeader(View view) {
  }

  @Override public void addFooter(View view) {
  }

  @Override public void setBackgroundResource(int colorResource) {
  }

  @Override public View getView(LayoutInflater inflater, ViewGroup parent) {
    if (viewResourceId != INVALID) {
      contentView = inflater.inflate(viewResourceId, parent, false);
    } else {
      ViewGroup parentView = (ViewGroup) contentView.getParent();
      if (parentView != null) {
        parentView.removeView(contentView);
      }
    }
    contentView.setOnKeyListener(new View.OnKeyListener() {
      @Override public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyListener == null) {
          throw new NullPointerException("keyListener should not be null");
        }
        return keyListener.onKey(v, keyCode, event);
      }
    });
    return contentView;
  }

  @Override public void setOnKeyListener(View.OnKeyListener keyListener) {
    this.keyListener = keyListener;
  }

  @Override public View getInflatedView() {
    return contentView;
  }

  @Override public View getHeader() {
    return null;
  }

  @Override public View getFooter() {
    return null;
  }

  @Override public ViewGroup getHeaderContainer() {
    return null;
  }

  @Override public ViewGroup getFooterContainer() {
    return null;
  }
}