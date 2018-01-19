package com.liuguoquan.study.network;

import android.support.annotation.NonNull;
import com.liuguoquan.study.base.App;
import com.liuguoquan.study.network.api.RecognizeTextApi;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Description:
 *
 * Created by liuguoquan on 2017/10/16 11:19.
 */

public class Api {

  private static Retrofit sNormalRetrofit;

  @NonNull private static Retrofit getRetrofit() {
    if (sNormalRetrofit == null) {
      sNormalRetrofit = new Retrofit.Builder().baseUrl("https://api-cn.faceplusplus.com/")
          .addConverterFactory(GsonConverterFactory.create(App.getAppGson()))
          .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
          .client(HttpClient.getDefaultHttpClient())
          .build();
    }
    return sNormalRetrofit;
  }

  public static RecognizeTextApi getRecognizeTextApi() {
    return getRetrofit().create(RecognizeTextApi.class);
  }
}
