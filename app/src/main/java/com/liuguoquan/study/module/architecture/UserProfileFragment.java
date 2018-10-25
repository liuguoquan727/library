package com.liuguoquan.study.module.architecture;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.view.View;
import com.liuguoquan.study.R;
import com.liuguoquan.study.base.AppBaseFragment;
import com.mdroid.lib.core.base.BasePresenter;
import com.mdroid.lib.core.base.Status;

/**
 * Description:
 *
 * Created by liuguoquan on 2018/1/18 10:51.
 */

public class UserProfileFragment extends AppBaseFragment {

  private UserProfileViewModel mViewModel;

  @Override protected Status getCurrentStatus() {
    return null;
  }

  @Override protected int getContentView() {
    return R.layout.module_architecture_ui;
  }

  @Override protected BasePresenter initPresenter() {
    return null;
  }

  @Override protected String getPageTitle() {
    return "ViewModel";
  }

  @Override protected void initData(Bundle savedInstanceState) {
    requestBaseInit(getPageTitle());
  }

  @Override protected void initView(View parent) {
    mViewModel = ViewModelProviders.of(this).get(UserProfileViewModel.class);
  }
}
