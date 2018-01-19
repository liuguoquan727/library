package com.mdroid.lib.core.utils;

/**
 * Description:
 *
 * Created by liuguoquan on 2018/1/19 10:49.
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.DrawableRes;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import java.io.File;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Description：app统一的加载图片的工具类，方便必要时切换图片加载库
 */
public class ImageLoader {
  @SuppressLint("StaticFieldLeak") private static Context sContext;

  private ImageLoader() {
  }

  public static void init(Context context) {
    sContext = context.getApplicationContext();
  }

  /**
   * 加载为圆角图片
   *
   * @param target imageView
   * @param defImage 默认的图片
   * @param radius 圆角半径
   * @param urlOrPath 图片url或本地路径
   */
  public static void loadAsRadius(ImageView target, int defImage, int radius, String urlOrPath) {
    RequestOptions options =
        new RequestOptions().bitmapTransform(new RoundedCornersTransformation(sContext, radius, 0)).
            placeholder(defImage);
    Glide.with(sContext).load(urlOrPath).apply(options).into(target);
  }

  /**
   * 加载为圆角图片
   *
   * @param target imageView
   * @param defImage 默认的图片
   * @param radius 圆角半径
   * @param file 图片文件
   */
  public static void loadAsRadius(ImageView target, int defImage, int radius, File file) {
    RequestOptions options =
        new RequestOptions().bitmapTransform(new RoundedCornersTransformation(sContext, radius, 0))
            .placeholder(defImage);
    Glide.with(sContext).load(file).apply(options).into(target);
  }

  /**
   * 加载为圆角图片
   *
   * @param target imageView
   * @param defImage 默认的图片
   * @param radius 圆角半径
   * @param resId 图片资源id
   */
  public static void loadAsRadius(ImageView target, int defImage, int radius,
      @DrawableRes int resId) {
    RequestOptions options =
        new RequestOptions().bitmapTransform(new RoundedCornersTransformation(sContext, radius, 0))
            .placeholder(defImage);
    Glide.with(sContext).load(resId).apply(options).into(target);
  }

  /**
   * 加载为圆形图片
   *
   * @param target imageView
   * @param defImage 默认的图片
   * @param urlOrPath 图片url或本地路径
   */
  public static void loadAsCircle(ImageView target, int defImage, String urlOrPath) {
    RequestOptions options =
        new RequestOptions().bitmapTransform(new CropCircleTransformation(sContext))
            .placeholder(defImage);
    Glide.with(sContext).load(urlOrPath).apply(options).into(target);
  }

  /**
   * 加载为圆形图片
   *
   * @param target imageView
   * @param defImage 默认的图片
   * @param file 图片文件
   */
  public static void loadAsCircle(ImageView target, int defImage, File file) {
    RequestOptions options =
        new RequestOptions().bitmapTransform(new CropCircleTransformation(sContext))
            .placeholder(defImage);
    Glide.with(sContext).load(file).apply(options).into(target);
  }

  /**
   * 加载为圆形图片
   *
   * @param target imageView
   * @param defImage 默认的图片
   * @param resId 图片资源id
   */
  public static void loadAsCircle(ImageView target, int defImage, @DrawableRes int resId) {
    RequestOptions options =
        new RequestOptions().bitmapTransform(new CropCircleTransformation(sContext))
            .placeholder(defImage);
    Glide.with(sContext).load(resId).apply(options).into(target);
  }

  /**
   * 正常加载
   *
   * @param target imageView
   * @param defImage 默认的图片
   * @param urlOrPath 图片url或本地路径
   */
  public static void load(ImageView target, int defImage, String urlOrPath) {
    RequestOptions options = new RequestOptions().placeholder(defImage).fitCenter().centerCrop();
    Glide.with(sContext).asBitmap().load(urlOrPath).apply(options).into(target);
  }

  /**
   * 正常加载
   *
   * @param target imageView
   * @param defImage 默认的图片
   * @param file 图片文件
   */
  public static void load(ImageView target, int defImage, File file) {
    RequestOptions options = new RequestOptions().placeholder(defImage).fitCenter().centerCrop();
    Glide.with(sContext).asBitmap().load(file).apply(options).into(target);
  }

  /**
   * 正常加载
   *
   * @param target imageView
   * @param defImage 默认的图片
   * @param resId 图片资源id
   */
  public static void load(ImageView target, int defImage, @DrawableRes int resId) {
    RequestOptions options = new RequestOptions().placeholder(defImage).fitCenter().centerCrop();
    Glide.with(sContext).asBitmap().load(resId).apply(options).into(target);
  }
}