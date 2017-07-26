package com.mdroid.lib.core.view;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import com.mdroid.lib.core.R;
import com.mdroid.lib.core.base.Status;

/**
 * Created by liuguoquan on 2017/7/26.
 */

public class StateFrameLayout extends FrameLayout {

  private View mLoadingView;
  private View mErrorView;
  private View mEmptyView;
  private View mContentView;
  private Status mStatus;

  public StateFrameLayout(@NonNull Context context) {
    this(context, null);
  }

  public StateFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public StateFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs,
      @AttrRes int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initView(context);
  }

  private void initView(Context context) {

  }

  @Override protected void onFinishInflate() {
    super.onFinishInflate();
    int childCount = getChildCount();
    if (childCount > 1) {
      throw new UnsupportedOperationException("layout can only host 1 element!");
    } else {

      if (mContentView == null) {
        if (childCount == 1) {
          mContentView = getChildAt(0);
        }
      }

      if (mContentView == null) {
        throw new NullPointerException("ContentView can not be null!");
      }

      if (mStatus == null) {
        switchStatus(Status.STATUS_NORMAL);
      }
    }
  }

  public void switchStatus(Status status) {
    mStatus = status;
    hideAllView();
    switch (status) {
      case STATUS_NORMAL:
        mContentView.setVisibility(VISIBLE);
        break;

      case STATUS_LOADING:
        if (mLoadingView == null) {
          mLoadingView = inflate(getContext(), R.layout.lib_content_loading, null);
          addView(mLoadingView);
        }
        mLoadingView.setVisibility(VISIBLE);
        break;

      case STATUS_EMPTY:
        if (mEmptyView == null) {
          mEmptyView = inflate(getContext(), R.layout.lib_content_empty, null);
          addView(mEmptyView);
        }
        mEmptyView.setVisibility(VISIBLE);
        break;

      case STATUS_ERROR:
        if (mErrorView == null) {
          mErrorView = inflate(getContext(), R.layout.lib_content_error, null);
          addView(mErrorView);
        }
        mErrorView.setVisibility(VISIBLE);
        break;
    }
  }

  private void hideAllView() {
    int childCount = getChildCount();
    for (int i = 0; i < childCount; i++) {
      getChildAt(i).setVisibility(View.GONE);
    }
  }

  public View getErrorView() {
    return mErrorView;
  }

  public View getEmptyView() {
    return mEmptyView;
  }
}
