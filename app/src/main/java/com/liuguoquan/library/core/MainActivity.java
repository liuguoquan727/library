package com.liuguoquan.library.core;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import com.jaeger.library.StatusBarUtil;
import com.mdroid.lib.core.base.BaseActivity;
import com.mdroid.lib.core.base.BaseExtraKeys;
import com.mdroid.lib.core.base.BasePresenter;
import com.mdroid.lib.core.base.Status;
import com.mdroid.lib.core.utils.ActivityUtil;
import com.mdroid.lib.core.view.StateFrameLayout;

public class MainActivity extends BaseActivity {

  @BindView(R.id.text) TextView mText;
  @BindView(R.id.tool_bar) Toolbar mToolBar;
  @BindView(R.id.state_layout) StateFrameLayout mStateLayout;

  @Override public Status getCurrentStatus() {
    return Status.STATUS_NORMAL;
  }

  @Override public String getPageTitle() {
    return null;
  }

  @Override public int getLayoutResId() {
    return R.layout.activity_main;
  }

  @Override public void initView(Bundle savedInstanceState) {
    StatusBarUtil.setColor(this, getResources().getColor(R.color.main_color_normal), 0);
    mStateLayout.switchStatus(getCurrentStatus());
    mToolBar.setBackgroundResource(R.color.main_color_normal);
    mText.setText("Just do it");
  }

  @Override public BasePresenter initPresenter() {
    return null;
  }

  @OnClick({ R.id.text, R.id.normal, R.id.loading, R.id.empty, R.id.error })
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.normal:
        Bundle bundle = new Bundle();
        bundle.putString("liu", "123");
        ActivityUtil.startActivity(this, LeeFragment.class, bundle, 10);
        //mStateLayout.switchStatus(Status.STATUS_NORMAL);
        break;
      case R.id.loading:
        //mStateLayout.switchStatus(Status.STATUS_LOADING);
        Intent intent = new Intent(this, WebActivity.class);
        bundle = new Bundle();
        bundle.putString(BaseExtraKeys.KEY_URL, "https://www.baidu.com/");
        bundle.putString(BaseExtraKeys.KEY_TITLE, "百度");
        intent.putExtras(bundle);
        ActivityUtil.startActivity(this, intent);
        break;
      case R.id.empty:
        //mStateLayout.switchStatus(Status.STATUS_EMPTY);
        break;
      case R.id.error:
        //mStateLayout.switchStatus(Status.STATUS_ERROR);
        break;
    }
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode == Activity.RESULT_OK && requestCode == 10) {
      Log.d("lgq", "onActivityResult: Ok");
    }
  }

  @Override public void executeCloseAnim() {
  }
}
