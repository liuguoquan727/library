package com.mdroid.utils;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.media.ExifInterface;
import android.net.Uri;
import de.greenrobot.common.io.IoUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static android.media.ExifInterface.ORIENTATION_FLIP_HORIZONTAL;
import static android.media.ExifInterface.ORIENTATION_FLIP_VERTICAL;
import static android.media.ExifInterface.ORIENTATION_NORMAL;
import static android.media.ExifInterface.ORIENTATION_ROTATE_180;
import static android.media.ExifInterface.ORIENTATION_ROTATE_270;
import static android.media.ExifInterface.ORIENTATION_ROTATE_90;
import static android.media.ExifInterface.ORIENTATION_TRANSPOSE;
import static android.media.ExifInterface.ORIENTATION_TRANSVERSE;
import static android.media.ExifInterface.TAG_ORIENTATION;

public class BitmapUtils {

  /**
   * @param scale > 0 && < 1
   * @param radius > 0 && <= 25
   */
  public static Bitmap blur(Bitmap source, float scale, float radius) {
    Bitmap scaleSource = scale(source, scale);
    Bitmap result = FastBlur.doBlur(scaleSource, (int) radius, true);
    if (scaleSource != source && scaleSource != result) scaleSource.recycle();
    return result;
  }

  /**
   * 创建圆角图片
   */
  public static Bitmap roundCorner(Bitmap source, int pixels) {
    Bitmap result =
        Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(result);
    final int color = 0xff424242;
    final Paint paint = new Paint();
    final Rect rect = new Rect(0, 0, source.getWidth(), source.getHeight());
    final RectF rectF = new RectF(rect);
    final float round = pixels;
    paint.setAntiAlias(true);
    canvas.drawARGB(0, 0, 0, 0);
    paint.setColor(color);
    canvas.drawRoundRect(rectF, round, round, paint);

    paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
    canvas.drawBitmap(source, rect, rect, paint);
    return result;
  }

  /**
   * 顶部是圆角, 底部是直角
   */
  public static Bitmap topCorner(Bitmap source, int pixels) {
    Bitmap result =
        Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(result);
    final Paint paint = new Paint();
    final Rect rect = new Rect(0, 0, source.getWidth(), source.getHeight());
    paint.setAntiAlias(true);
    canvas.drawARGB(0, 0, 0, 0);

    int width = source.getWidth();
    int height = source.getHeight();
    Path path = new Path();
    RectF rectF1 = new RectF(0, 0, pixels * 2, pixels * 2);
    path.arcTo(rectF1, 180, 90);
    path.lineTo(width - pixels, 0);
    RectF rectF2 = new RectF(width - pixels * 2, 0, width, pixels * 2);
    path.arcTo(rectF2, 270, 90);
    path.lineTo(width, height);
    path.lineTo(0, height);
    path.close();
    canvas.drawPath(path, paint);

    paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
    canvas.drawBitmap(source, rect, rect, paint);
    return result;
  }

  /**
   * 创建倒影图片
   */
  public static Bitmap reflection(Bitmap source) {
    final int reflectionGap = 4;
    int width = source.getWidth();
    int height = source.getHeight();

    // 实现图片的反转
    Matrix matrix = new Matrix();
    matrix.preScale(1, -1);
    matrix.postRotate(360f);
    // 创建反转后的图片Bitmap对象，图片高是原图的一半。
    Bitmap reflectionImage =
        Bitmap.createBitmap(source, 0, height / 2, width, height / 2, matrix, false);
    // 创建标准的Bitmap对象，宽和原图一致，高是原图的1.5倍。
    Bitmap result = Bitmap.createBitmap(width, (height + height / 2), Bitmap.Config.ARGB_8888);
    // 创建画布对象，将原图画于画布，起点是原点位置。
    Canvas canvas = new Canvas(result);
    canvas.drawBitmap(source, 0, 0, null);
    //将反转后的图片画到画布中。
    Paint defaultPaint = new Paint();
    canvas.drawRect(0, height, width, height + reflectionGap, defaultPaint);
    canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);
    Paint paint = new Paint();
    //创建线性渐变LinearGradient 对象。
    LinearGradient shader =
        new LinearGradient(0, source.getHeight(), 0, result.getHeight() + reflectionGap, 0X70ffffff,
            0X00ffffff, Shader.TileMode.MIRROR);
    paint.setShader(shader);
    paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
    canvas.drawRect(0, height, width, result.getHeight() + reflectionGap, paint);
    return result;
  }

  /**
   * 圆形图片
   */
  public static Bitmap circle(Bitmap source) {
    Bitmap result =
        Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(result);

    final Paint paint = new Paint();
    final Rect rect = new Rect(0, 0, source.getWidth(), source.getHeight());
    final RectF rectF = new RectF(rect);
    final float roundPx = source.getWidth() / 2;

    paint.setAntiAlias(true);
    canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

    paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
    canvas.drawBitmap(source, rect, rect, paint);
    return result;
  }

  public static Bitmap decodeContentStream(int width, int height, ContentResolver contentResolver,
      Uri data) throws FileNotFoundException {
    BitmapFactory.Options options = new BitmapFactory.Options();
    options.inJustDecodeBounds = true;
    InputStream is = null;
    try {
      is = contentResolver.openInputStream(data);
      BitmapFactory.decodeStream(is, null, options);
    } finally {
      IoUtils.safeClose(is);
    }

    calculateInSampleSize(width, height, options);
    try {
      is = contentResolver.openInputStream(data);
      return BitmapFactory.decodeStream(is, null, options);
    } finally {
      IoUtils.safeClose(is);
    }
  }

  public static Bitmap decodeContentStream(int width, int height, String file) {
    BitmapFactory.Options options = new BitmapFactory.Options();
    options.inJustDecodeBounds = true;
    BitmapFactory.decodeFile(file, options);

    calculateInSampleSize(width, height, options);

    return BitmapFactory.decodeFile(file, options);
  }

  static void calculateInSampleSize(int reqWidth, int reqHeight, BitmapFactory.Options options) {
    final int height = options.outHeight;
    final int width = options.outWidth;
    int sampleSize = 1;
    if (height > reqHeight || width > reqWidth) {
      final int heightRatio = (int) Math.floor((float) height / (float) reqHeight);
      final int widthRatio = (int) Math.floor((float) width / (float) reqWidth);
      sampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
    }

    options.inSampleSize = sampleSize;
    options.inJustDecodeBounds = false;
  }

  /**
   * @param scale > 0 && < 1
   */
  public static Bitmap scale(Bitmap source, float scale) {
    Matrix matrix = new Matrix();
    matrix.postScale(scale, scale); //长和宽放大缩小的比例
    Bitmap result =
        Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    return result;
  }

  public static boolean compress(String src, File dest, Size size) {
    OutputStream fos = null;
    try {
      Bitmap srcBitmap = decodeContentStream(size.getWidth(), size.getHeight(), src);
      Bitmap resultBitmap =
          transformResult(srcBitmap, size.getWidth(), size.getHeight(), getFileExifRotation(src),
              true, false, true);
      if (srcBitmap != resultBitmap) {
        srcBitmap.recycle();
      }
      fos = new FileOutputStream(dest);
      return resultBitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
    } catch (Throwable e) {
      return false;
    } finally {
      IoUtils.safeClose(fos);
    }
  }

  static Bitmap transformResult(Bitmap result, int targetWidth, int targetHeight,
      int exifOrientation, boolean onlyScaleDown, boolean centerCrop, boolean centerInside) {
    int inWidth = result.getWidth();
    int inHeight = result.getHeight();

    int drawX = 0;
    int drawY = 0;
    int drawWidth = inWidth;
    int drawHeight = inHeight;

    Matrix matrix = new Matrix();

    // EXIf interpretation should be done before cropping in case the dimensions need to
    // be recalculated
    if (exifOrientation != 0) {
      int exifRotation = getExifRotation(exifOrientation);
      int exifTranslation = getExifTranslation(exifOrientation);
      if (exifRotation != 0) {
        matrix.preRotate(exifRotation);
        if (exifRotation == 90 || exifRotation == 270) {
          // Recalculate dimensions after exif rotation
          int tmpHeight = targetHeight;
          targetHeight = targetWidth;
          targetWidth = tmpHeight;
        }
      }
      if (exifTranslation != 1) {
        matrix.postScale(exifTranslation, 1);
      }
    }

    if (centerCrop) {
      // Keep aspect ratio if one dimension is set to 0
      float widthRatio =
          targetWidth != 0 ? targetWidth / (float) inWidth : targetHeight / (float) inHeight;
      float heightRatio =
          targetHeight != 0 ? targetHeight / (float) inHeight : targetWidth / (float) inWidth;
      float scaleX, scaleY;
      if (widthRatio > heightRatio) {
        int newSize = (int) Math.ceil(inHeight * (heightRatio / widthRatio));
        drawY = (inHeight - newSize) / 2;
        drawHeight = newSize;
        scaleX = widthRatio;
        scaleY = targetHeight / (float) drawHeight;
      } else if (widthRatio < heightRatio) {
        int newSize = (int) Math.ceil(inWidth * (widthRatio / heightRatio));
        drawX = (inWidth - newSize) / 2;
        drawWidth = newSize;
        scaleX = targetWidth / (float) drawWidth;
        scaleY = heightRatio;
      } else {
        drawX = 0;
        drawWidth = inWidth;
        scaleX = scaleY = heightRatio;
      }
      if (shouldResize(onlyScaleDown, inWidth, inHeight, targetWidth, targetHeight)) {
        matrix.preScale(scaleX, scaleY);
      }
    } else if (centerInside) {
      // Keep aspect ratio if one dimension is set to 0
      float widthRatio =
          targetWidth != 0 ? targetWidth / (float) inWidth : targetHeight / (float) inHeight;
      float heightRatio =
          targetHeight != 0 ? targetHeight / (float) inHeight : targetWidth / (float) inWidth;
      float scale = widthRatio < heightRatio ? widthRatio : heightRatio;
      if (shouldResize(onlyScaleDown, inWidth, inHeight, targetWidth, targetHeight)) {
        matrix.preScale(scale, scale);
      }
    } else if ((targetWidth != 0 || targetHeight != 0) //
        && (targetWidth != inWidth || targetHeight != inHeight)) {
      // If an explicit target size has been specified and they do not match the results bounds,
      // pre-scale the existing matrix appropriately.
      // Keep aspect ratio if one dimension is set to 0.
      float sx = targetWidth != 0 ? targetWidth / (float) inWidth : targetHeight / (float) inHeight;
      float sy =
          targetHeight != 0 ? targetHeight / (float) inHeight : targetWidth / (float) inWidth;
      if (shouldResize(onlyScaleDown, inWidth, inHeight, targetWidth, targetHeight)) {
        matrix.preScale(sx, sy);
      }
    }

    return Bitmap.createBitmap(result, drawX, drawY, drawWidth, drawHeight, matrix, true);
  }

  private static boolean shouldResize(boolean onlyScaleDown, int inWidth, int inHeight,
      int targetWidth, int targetHeight) {
    return !onlyScaleDown || inWidth > targetWidth || inHeight > targetHeight;
  }

  static int getExifRotation(int orientation) {
    int rotation;
    switch (orientation) {
      case ORIENTATION_ROTATE_90:
      case ORIENTATION_TRANSPOSE:
        rotation = 90;
        break;
      case ORIENTATION_ROTATE_180:
      case ORIENTATION_FLIP_VERTICAL:
        rotation = 180;
        break;
      case ORIENTATION_ROTATE_270:
      case ORIENTATION_TRANSVERSE:
        rotation = 270;
        break;
      default:
        rotation = 0;
    }
    return rotation;
  }

  static int getExifTranslation(int orientation) {
    int translation;
    switch (orientation) {
      case ORIENTATION_FLIP_HORIZONTAL:
      case ORIENTATION_FLIP_VERTICAL:
      case ORIENTATION_TRANSPOSE:
      case ORIENTATION_TRANSVERSE:
        translation = -1;
        break;
      default:
        translation = 1;
    }
    return translation;
  }

  static int getFileExifRotation(String src) throws IOException {
    ExifInterface exifInterface = new ExifInterface(src);
    return exifInterface.getAttributeInt(TAG_ORIENTATION, ORIENTATION_NORMAL);
  }
}
