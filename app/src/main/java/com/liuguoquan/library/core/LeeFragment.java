package com.liuguoquan.library.core;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import butterknife.OnClick;
import com.mdroid.app.TranslucentStatusCompat;
import com.mdroid.lib.core.base.BaseFragment;
import com.mdroid.lib.core.base.BasePresenter;
import com.mdroid.lib.core.base.Status;
import com.mdroid.lib.core.utils.ActivityUtil;

/**
 * Created by liuguoquan on 2017/7/26.
 */

public class LeeFragment extends BaseFragment {

  @Override protected Status getCurrentStatus() {
    return null;
  }

  @Override protected int getContentView() {
    return R.layout.activity_main;
  }

  @Override public BasePresenter initPresenter() {
    return null;
  }

  @Override protected String getPageTitle() {
    return null;
  }

  @Override protected void initData(Bundle savedInstanceState) {
    String value = getArguments().getString("liu", "000");
    Log.d("lgq", "initData: " + value);
  }

  @Override protected void initView(View parent) {
    getStatusBar().setBackgroundResource(R.color.main_color_normal);
    getToolBar().setBackgroundResource(R.color.main_color_normal);
  }

  @OnClick({ R.id.text, R.id.normal, R.id.loading, R.id.empty, R.id.error })
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.normal:
        Bundle bundle = new Bundle();
        bundle.putString("liu", "123");
        ActivityUtil.startActivity(this, LeeFragment.class, bundle, 200);
        break;
      case R.id.loading:
        break;
      case R.id.empty:
        break;
      case R.id.error:
        getActivity().setResult(Activity.RESULT_OK);
        getActivity().finish();
        break;
    }
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode == Activity.RESULT_OK && requestCode == 200) {
      Log.d("lgq", "onActivityResult: Ok" + getClass().getName());
    }
  }
}
