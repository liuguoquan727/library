package com.liuguoquan.study;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import com.jaeger.library.StatusBarUtil;
import com.liuguoquan.study.base.AppBaseActivity;
import com.liuguoquan.study.module.databind.DataBindingUI;
import com.liuguoquan.study.module.faceplusplus.OcrUI;
import com.liuguoquan.study.module.room.RoomUI;
import com.mdroid.lib.core.base.BasePresenter;
import com.mdroid.lib.core.base.Status;
import com.mdroid.lib.core.utils.ActivityUtil;

public class MainActivity extends AppBaseActivity {

    Toolbar mToolBar;

    @Override
    public Status getCurrentStatus() {
    return Status.STATUS_NORMAL;
  }

    @Override
    public String getPageTitle() {
    return "首页";
  }

    @Override
    protected int getContentView() {
    return R.layout.activity_main;
  }

    @Override
    public void initData(Bundle savedInstanceState) {
    StatusBarUtil.setColor(this, getResources().getColor(R.color.main_color_normal), 0);
        mToolBar = findViewById(R.id.tool_bar);
    requestBaseInit(mToolBar, getPageTitle());
  }

    @Override
    public BasePresenter initPresenter() {
    return null;
  }

    public void onClick(View v) {
    switch (v.getId()) {
      case R.id.room:
        ActivityUtil.startActivity(this, RoomUI.class);
        break;
      case R.id.data_binding:
        Intent intent = new Intent(this, DataBindingUI.class);
        ActivityUtil.startActivity(this, intent);
        break;
      case R.id.image_ocr:
        intent = new Intent(this, OcrUI.class);
        ActivityUtil.startActivity(this, intent);
        break;
    }
  }

    @Override
    public void finish() {
    super.finish();
    overridePendingTransition(R.anim.fade_in_center, R.anim.fade_out_center);
  }
}
