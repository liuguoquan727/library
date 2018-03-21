package com.liuguoquan.study.base;

import android.os.Bundle;
import me.yokeyword.fragmentation.SwipeBackLayout;
import me.yokeyword.fragmentation_swipeback.core.ISwipeBackActivity;
import me.yokeyword.fragmentation_swipeback.core.SwipeBackActivityDelegate;

/**
 * You can also refer to {@link me.yokeyword.fragmentation_swipeback.SwipeBackActivity} to implement
 * YourSwipeBackActivity
 * (extends Activity and impl {@link me.yokeyword.fragmentation.ISupportActivity})
 * <p>
 * Created by YoKey on 16/4/19.
 */
public abstract class BaseSwipeBackActivity<V extends AppBaseView, T extends AppBaseActivityPresenter<V>>
    extends SupportActivity<V, T> implements ISwipeBackActivity {
  final SwipeBackActivityDelegate mDelegate = new SwipeBackActivityDelegate(this);

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mDelegate.onCreate(savedInstanceState);
    getSwipeBackLayout().setEdgeOrientation(SwipeBackLayout.EDGE_ALL);
  }

  @Override protected void onPostCreate(Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);
    mDelegate.onPostCreate(savedInstanceState);
  }

  @Override public SwipeBackLayout getSwipeBackLayout() {
    return mDelegate.getSwipeBackLayout();
  }

  /**
   * 是否可滑动
   */
  @Override public void setSwipeBackEnable(boolean enable) {
    mDelegate.setSwipeBackEnable(enable);
  }

  @Override public void setEdgeLevel(SwipeBackLayout.EdgeLevel edgeLevel) {
    mDelegate.setEdgeLevel(edgeLevel);
  }

  @Override public void setEdgeLevel(int widthPixel) {
    mDelegate.setEdgeLevel(widthPixel);
  }

  /**
   * 限制SwipeBack的条件,默认栈内Fragment数 <= 1时 , 优先滑动退出Activity , 而不是Fragment
   *
   * @return true: Activity优先滑动退出;  false: Fragment优先滑动退出
   */
  @Override public boolean swipeBackPriority() {
    return true;
  }
}
