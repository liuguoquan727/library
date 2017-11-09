package com.liuguoquan.study;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import butterknife.BindView;
import butterknife.OnClick;
import com.jaeger.library.StatusBarUtil;
import com.liuguoquan.study.base.AppBaseActivity;
import com.mdroid.lib.core.base.BasePresenter;
import com.mdroid.lib.core.base.Status;

public class MainActivity extends AppBaseActivity {

  @BindView(R.id.tool_bar) Toolbar mToolBar;

  @Override public Status getCurrentStatus() {
    return Status.STATUS_NORMAL;
  }

  @Override public String getPageTitle() {
    return "首页";
  }

  @Override protected int getContentView() {
    return R.layout.activity_main;
  }

  @Override public void initData(Bundle savedInstanceState) {
    StatusBarUtil.setColor(this, getResources().getColor(R.color.main_color_normal), 0);
    requestBaseInit(mToolBar, getPageTitle());
  }

  @Override public BasePresenter initPresenter() {
    return null;
  }

  @OnClick({}) public void onClick(View v) {
    switch (v.getId()) {
    }
  }

  @Override public void finish() {
    super.finish();
    overridePendingTransition(R.anim.fade_in_center, R.anim.fade_out_center);
  }
}
