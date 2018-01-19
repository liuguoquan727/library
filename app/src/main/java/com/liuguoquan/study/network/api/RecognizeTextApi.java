package com.liuguoquan.study.network.api;

import com.liuguoquan.study.bean.localbean.ApiResult;
import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Description:
 *
 * Created by liuguoquan on 2017/12/25 14:33.
 */

public interface RecognizeTextApi {

  @FormUrlEncoded @POST("imagepp/v1/recognizetext") Observable<ApiResult> recognizetext(
      @Field("api_key") String api_key, @Field("api_secret") String api_secret,
      @Field("image_url") String imageUrl);
}
