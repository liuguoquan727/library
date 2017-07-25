package com.mdroid.lib.imagepick;

import android.os.Parcel;
import android.support.annotation.Keep;
import android.util.Log;
import java.io.Serializable;

/**
 * <p><font color=red>已实现Cloneable，增减字段需注意修改{@link #clone()}</font></p>
 */
@Keep public class Resource implements Comparable<Resource>, Serializable, Cloneable {
  private long mSize;
  private long mTime;
  private String mimeType;
  private String filename;
  private String filePath;
  private int width;
  private int height;

  /**
   * 传原图|压缩图片
   */
  private boolean mIsOriginal;
  private int mType;// 0: 图片 1: 视频
  private boolean isSelected;

  public Resource() {
  }

  public Resource(String path) {
    this.filePath = path;
  }

  public Resource(String path, String name, int width, int height, long size, long time, int type,
      String mimeType, boolean isSelected) {
    this.filePath = path;
    this.filename = name;
    this.width = width;
    this.height = height;
    this.mSize = size;
    this.mTime = time;
    this.mType = type;
    this.mimeType = mimeType;
    this.isSelected = isSelected;
  }

  /**
   * For video
   */
  public Resource(String path, String cover, String name, int width, int height, long size,
      long time, String mimeType) {
    this(path, name, width, height, size, time, 1, mimeType, false);
  }

  /**
   * For Image
   */
  public Resource(String path, String name, int width, int height, long size, long time,
      String mimeType, boolean isSelected) {
    this(path, name, width, height, size, time, 0, mimeType, isSelected);
  }

  protected Resource(Parcel in) {
    this.filePath = in.readString();
    this.filename = in.readString();
    this.width = in.readInt();
    this.height = in.readInt();
    this.mSize = in.readLong();
    this.mTime = in.readLong();
    this.mIsOriginal = in.readByte() != 0;
    this.mType = in.readInt();
    this.isSelected = in.readByte() != 0;
  }

  @Override public Resource clone() {
    try {
      return (Resource) super.clone();
    } catch (CloneNotSupportedException e) {
      Log.e("Resource", "clone error:", e);
      return new Resource();
    }
  }

  public long getmSize() {
    return mSize;
  }

  public void setmSize(long mSize) {
    this.mSize = mSize;
  }

  public long getTime() {
    return mTime;
  }

  public void setTime(long mTime) {
    this.mTime = mTime;
  }

  public String getFilename() {
    return filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }

  public String getFilePath() {
    return filePath;
  }

  public void setFilePath(String filePath) {
    this.filePath = filePath;
  }

  public int getWidth() {
    return width;
  }

  public void setWidth(int width) {
    this.width = width;
  }

  public int getHeight() {
    return height;
  }

  public void setHeight(int height) {
    this.height = height;
  }

  public boolean original() {
    return mIsOriginal;
  }

  public void setIsOriginal(boolean mIsOriginal) {
    this.mIsOriginal = mIsOriginal;
  }

  public int getmType() {
    return mType;
  }

  public void setmType(int mType) {
    this.mType = mType;
  }

  public boolean isSelected() {
    return isSelected;
  }

  public void setIsSelected(boolean isSelected) {
    this.isSelected = isSelected;
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Resource resource = (Resource) o;

    return filePath.equals(resource.filePath);
  }

  @Override public int hashCode() {
    return filePath.hashCode();
  }

  @Override public int compareTo(Resource another) {
    return (int) (mTime - another.mTime);
  }

  public boolean isVideo() {
    return mType == 1;
  }

  public String getMimeType() {
    return mimeType;
  }

  public void setMimeType(String mimeType) {
    this.mimeType = mimeType;
  }
}
