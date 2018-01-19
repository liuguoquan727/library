package com.liuguoquan.study.network;

/**
 * Description:
 *
 * Created by liuguoquan on 2017/10/16 09:14.
 */

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.liuguoquan.study.BuildConfig;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * 匿名 SSL HttpClient
 */
public class HttpClient {

  public static OkHttpClient getDefaultHttpClient() {
    OkHttpClient.Builder builder = new OkHttpClient.Builder();
    builder.connectTimeout(60, TimeUnit.SECONDS);
    builder.writeTimeout(60, TimeUnit.SECONDS);
    builder.readTimeout(60, TimeUnit.SECONDS);
    if (BuildConfig.DEBUG) {
      HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
      loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
      builder.interceptors().add(loggingInterceptor);
      builder.addNetworkInterceptor(new StethoInterceptor());
    }
    try {
      SSLContext sc = SSLContext.getInstance("SSL");
      sc.init(null, new TrustManager[] { new TrustAnyTrustManager() },
          new java.security.SecureRandom());
      builder.sslSocketFactory(sc.getSocketFactory());
      builder.hostnameVerifier(new TrustAnyHostnameVerifier());
    } catch (KeyManagementException ignored) {
    } catch (NoSuchAlgorithmException ignored) {
    }
    return builder.build();
  }

  private static class TrustAnyTrustManager implements X509TrustManager {
    public void checkClientTrusted(X509Certificate[] chain, String authType)
        throws CertificateException {
    }

    public void checkServerTrusted(X509Certificate[] chain, String authType)
        throws CertificateException {
    }

    public X509Certificate[] getAcceptedIssuers() {
      return new X509Certificate[] {};
    }
  }

  private static class TrustAnyHostnameVerifier implements HostnameVerifier {
    public boolean verify(String hostname, SSLSession session) {
      return true;
    }
  }
}
