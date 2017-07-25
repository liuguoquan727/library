package com.mdroid.lib.core.http;

import com.mdroid.lib.core.base.BaseApp;
import com.mdroid.utils.AndroidUtils;
import com.mdroid.utils.BitmapUtils;
import com.mdroid.utils.Size;
import java.io.File;
import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.internal.Util;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

/**
 * 对上传的图片进行压缩
 */
public class ImageBody extends RequestBody {
  private MediaType mContentType;
  private File mFile;
  private Size mSize;
  private File mTempFile;

  public ImageBody(final MediaType contentType, final File file, Size size) {
    this.mContentType = contentType;
    this.mFile = file;
    this.mSize = size;
  }

  @Override public MediaType contentType() {
    return mContentType;
  }

  @Override public long contentLength() {
    ensureTempFile();
    File file = mTempFile == null ? mFile : mTempFile;
    return file.length();
  }

  @Override public void writeTo(BufferedSink sink) throws IOException {
    ensureTempFile();
    File file = mTempFile == null ? mFile : mTempFile;
    Source source = null;
    try {
      source = Okio.source(file);
      sink.writeAll(source);
    } finally {
      Util.closeQuietly(source);
    }
    if (mTempFile != null) mTempFile.delete();
    mTempFile = null;
  }

  private void ensureTempFile() {
    if (mTempFile != null || mSize == null) return;
    mTempFile = AndroidUtils.getTmpFile(BaseApp.Instance().getCacheDir(), null);
    BitmapUtils.compress(mFile.getAbsolutePath(), mTempFile, mSize);
  }
}
