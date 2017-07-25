package com.mdroid.lib.core.base;

/**
 * Description：
 */
public interface BaseView<T> {

  /**
   * 数据等加载指示器
   *
   * @param isActive 是否正在处理
   */
  void setLoadingIndicator(boolean isActive);
}
