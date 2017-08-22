package com.liuguoquan.library.core.base;

/**
 * 常量(Key 都放在这, 防止重复)
 */
public interface DBKeys {
  /**
   * 手机所属地区(国家)列表 如: +86 +886等等
   */
  String AREA_LIST = "areas";
  /**
   * 权限对话框是否已经显示
   */
  String FIRST_USE = "is_first_use";
  /**
   * 引导页是否显示
   */
  String GUIDE_SHOW = "is_guide_show";

  /**
   * 登录账号
   */
  String USER_NAME = "username";

  //省市区
  String ADDRESS_LIST = "address_list";
  /** 站点筛选-可选地址信息 */
  String SITE_AREA_INFO = "siteAreaInfo";
  /** 站点搜索历史记录 */
  String KEY_SITE_SEARCH_HISTORY = "siteSearchHistory";
  /** 订单搜索历史记录 */
  String KEY_ORDER_SEARCH_HISTORY = "orderSearchHistory";
  String KEY_DEPOSIT = "deposit";
  String KEY_SZT_ORDER_COUNT = "szt_order_count";
  /**
   * 是否是首页请求为处理订单
   */
  String IS_HOME = "is_home";
}
