package com.liuguoquan.library.core;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import com.jaeger.library.StatusBarUtil;
import com.mdroid.lib.core.base.BaseActivity;
import com.mdroid.lib.core.base.BaseExtraKeys;
import com.mdroid.lib.core.base.BasePresenter;
import com.mdroid.lib.core.base.Status;
import com.mdroid.lib.core.utils.ActivityUtil;
import com.mdroid.lib.core.utils.Toost;
import com.mdroid.lib.core.view.StateViewLayout;

public class MainActivity extends BaseActivity {

  @BindView(R.id.text) TextView mText;
  @BindView(R.id.tool_bar) Toolbar mToolBar;
  @BindView(R.id.state_layout) StateViewLayout mStateLayout;
  @BindView(R.id.input) EditText mInput;
  private String mUrl;

  @Override public Status getCurrentStatus() {
    return Status.STATUS_NORMAL;
  }

  @Override public String getPageTitle() {
    return null;
  }

  @Override protected int getContentView() {
    return R.layout.activity_main;
  }

  @Override public void initData(Bundle savedInstanceState) {
    StatusBarUtil.setColor(this, getResources().getColor(R.color.main_color_normal), 0);
    mStateLayout.switchStatus(getCurrentStatus());
    mToolBar.setBackgroundResource(R.color.main_color_normal);
    mText.setText("");

    mInput.setText("http://m.szdjx.com/test/testpay/demo/wxH5MicroAuthPaymentRequest.aspx");
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
        //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://m.szdjx.com/test/testpay/demo/wxH5MicroAuthPaymentRequest.aspx"));
        //startActivity(intent);
        mUrl = mInput.getText().toString().trim();
        if (TextUtils.isEmpty(mUrl)) {
          Toost.message("输入为空");
          return;
        }
        Intent intent = new Intent(this, WebActivity.class);
        bundle = new Bundle();
        bundle.putString(BaseExtraKeys.KEY_URL, mUrl);
        bundle.putString(BaseExtraKeys.KEY_TITLE, "道嘉鲜科技");
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

  @Override public void onCreate(@Nullable Bundle savedInstanceState,
      @Nullable PersistableBundle persistentState) {
    super.onCreate(savedInstanceState, persistentState);
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
