package com.liuguoquan.library.core.base;

import android.text.TextUtils;
import com.mdroid.lib.core.base.BaseExtraKeys;

/**
 * 常量配置
 */
public class Constants {

  private static final int ENVIRONMENT_F = 0;  // 正式环境
  private static final int ENVIRONMENT_T = 1;  // 测试环境

  private static final String HOST_TEST = "http://119.23.76.55/ydbus-api/";
  private static final String HOST_FACTORY = "http://www.youdianbus.cn/ydbus-api/";
  private static final String BUS_SERVICE_HOST_FACTORY =
      "http://www.youdianbus.cn/ydbus-busservice/";
  private static final String BUS_SERVICE_HOST_TEST = "http://119.23.76.55/ydbus-busservice/";

  public static final String HOST = isF() ? HOST_FACTORY : HOST_TEST;
  public static final String BUS_SERVICE_HOST =
      isF() ? BUS_SERVICE_HOST_FACTORY : BUS_SERVICE_HOST_TEST;

  public static boolean isF() {
    return true;
  }

  public static boolean isT() {
    return true;
  }

  public static String getSmallPicture(String url) {
    if (TextUtils.isEmpty(url)) return null;
    return url.concat("-small");
  }

  public static String getMediumPicture(String url) {
    if (TextUtils.isEmpty(url)) return null;
    return url.concat("-medium");
  }

  public static String getLargePicture(String url) {
    if (TextUtils.isEmpty(url)) return null;
    return url.concat("-large");
  }

  /**
   * 跳转activity时附带数据的key值
   */
  public interface ExtraKey extends BaseExtraKeys {

    /** 手机号是否绑定 **/
    String KEY_PHONE_IS_BINDED = "isbind";
    String KEY_PHONE_INPUT_TYPE = "input_type";
    String KEY_USER_NAME = "user_name";
    String KEY_MERCHANT_ID = "merchant_id";
    String KEY_SITE_ID = "siteId";
    String KEY_SITE_NAME = "siteName";
    String KEY_SITE_PHOTOS = "sitePhotos";

    String KEY_PHONE = "phone_number";
    String KEY_ORDER_ID = "orderId";
    String KEY_ORDER_START_TIME = "orderStartTime";
    String KEY_ORDER_STATUS = "orderStatus";
    String KEY_DATA = "data";
    String KEY_GUN_CODE = "gun_code";
    String KEY_STALL_NUMBER = "stall_number";
    String KEY_DEPOSIT = "deposit";
    String KEY_FROM = "from";
  }

  public interface NormalCons {
    String SEPARATOR_COMMA = ",";
    String SEPARATOR_POINT = ".";
    String SEPARATOR_LINE = "--";
    /** 分页--每页10条数据 */
    int LIMIT_10 = 10;
    /** 分页--每页20条数据 */
    int LIMIT_20 = 20;
    /** 每行3列 */
    int GRID_COLUMN_3 = 3;
    /** 每行4列 */
    int GRID_COLUMN_4 = 4;
    /** 每行5列 */
    int GRID_COLUMN_5 = 5;
  }
}
