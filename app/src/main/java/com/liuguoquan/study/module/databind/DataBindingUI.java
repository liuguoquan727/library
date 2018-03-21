package com.liuguoquan.study.module.databind;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.jaeger.library.StatusBarUtil;
import com.liuguoquan.study.R;
import com.liuguoquan.study.base.AppBaseActivity;
import com.liuguoquan.study.bean.localbean.User;
import com.liuguoquan.study.databinding.ModuleDatabindingUiBinding;
import com.liuguoquan.study.utils.ToolBarUtils;
import com.mdroid.lib.core.base.BasePresenter;
import com.mdroid.lib.core.base.Status;
import com.mdroid.lib.core.utils.UIUtil;
import com.r0adkll.slidr.Slidr;
import java.util.Random;

public class DataBindingUI extends AppBaseActivity {

  @BindView(R.id.tool_bar) Toolbar mToolbar;
  private User mUser;
  private int mColor;

  @Override protected Status getCurrentStatus() {
    return null;
  }

  @Override protected String getPageTitle() {
    return null;
  }

  @Override protected BasePresenter initPresenter() {
    return null;
  }

  @Override protected int getContentView() {
    return R.layout.module_databinding_ui;
  }

  @Override protected void initData(Bundle savedInstanceState) {

  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    Slidr.attach(this);
    super.onCreate(savedInstanceState);
    ModuleDatabindingUiBinding binding =
        DataBindingUtil.setContentView(this, R.layout.module_databinding_ui);
    ButterKnife.bind(this);
    requestBaseInit(mToolbar, "Data Binding");
    mUser = new User();
    mUser.name = "liu";
    mUser.sex = "male";
    mUser.age = "22";
    mUser.address = "Shenzhen";
    binding.setUser(mUser);

    Random random = new Random();
    mColor = 0xff000000 | random.nextInt(0xffffff);
    mToolbar.setBackgroundColor(mColor);
    StatusBarUtil.setColorForSwipeBack(this, mColor, 38);
  }

  @OnClick({ R.id.update }) public void onClick(View v) {
    switch (v.getId()) {
      case R.id.update:
        mUser.setName("lee");
        break;
    }
  }

  protected void requestBaseInit(Toolbar toolBar, String title) {
    toolBar.setBackgroundResource(R.color.main_color_normal);
    TextView tvTitle = UIUtil.setCenterTitle(toolBar, title);
    ToolBarUtils.updateTitleText(tvTitle);
    toolBar.setNavigationIcon(R.drawable.ic_back);
    toolBar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        finish();
      }
    });
  }
}
