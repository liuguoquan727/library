package com.liuguoquan.study.module.faceplusplus;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import butterknife.BindView;
import butterknife.OnClick;
import com.liuguoquan.study.R;
import com.liuguoquan.study.base.AppBaseActivity;
import com.liuguoquan.study.bean.localbean.ApiResult;
import com.liuguoquan.study.network.Api;
import com.mdroid.lib.core.base.BasePresenter;
import com.mdroid.lib.core.base.Status;
import com.mdroid.lib.core.rxjava.PausedHandlerScheduler;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Description:
 *
 * Created by liuguoquan on 2017/12/25 14:43.
 */

public class OcrUI extends AppBaseActivity {

  @BindView(R.id.toolbar) Toolbar mToolbar;
  private OrcCondition mCondition = new OrcCondition();

  @Override protected Status getCurrentStatus() {
    return null;
  }

  @Override protected String getPageTitle() {
    return "图像识别";
  }

  @Override protected BasePresenter initPresenter() {
    return null;
  }

  @Override protected int getContentView() {
    return R.layout.module_face_ocr_ui;
  }

  @Override protected void initData(Bundle savedInstanceState) {
    requestBaseInit(mToolbar, getPageTitle());
  }

  @OnClick({ R.id.start }) public void onClick(View v) {
    switch (v.getId()) {
      case R.id.start:
        doOcr();
        break;
    }
  }

  private void doOcr() {
    Api.getRecognizeTextApi()
        .recognizetext(mCondition.faceApiKey, mCondition.faceApiSecret, mCondition.compare)
        .subscribeOn(Schedulers.io())
        .observeOn(PausedHandlerScheduler.from(getHandler()))
        .subscribe(new Consumer<ApiResult>() {
          @Override public void accept(ApiResult result) throws Exception {

          }
        }, new Consumer<Throwable>() {
          @Override public void accept(Throwable throwable) throws Exception {

          }
        });
  }
}
