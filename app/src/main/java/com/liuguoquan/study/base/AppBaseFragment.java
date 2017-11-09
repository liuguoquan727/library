package com.liuguoquan.study.base;

import com.mdroid.lib.core.base.BaseFragment;
import com.mdroid.lib.core.base.BaseView;
import com.mdroid.lib.core.eventbus.EventBusEvent;
import com.squareup.otto.Subscribe;

/**
 * Description：
 */
public abstract class AppBaseFragment<V extends AppBaseView, T extends AppBaseFragmentPresenter<V>>
    extends BaseFragment<V, T> implements BaseView<T>, EventBusEvent.INotify {

  @Override public void onDestroy() {
    super.onDestroy();
    //mProcessDialog = null;
  }

  protected void showProcessDialog() {
  }

  protected void dismissProcessDialog() {
  }

  @Subscribe @Override public void onNotify(EventBusEvent event) {
    //if (event.getType() == EventType.TYPE_TOKEN_ERROR) {
    //  getHandler().sendAction(new Runnable() {
    //    @Override public void run() {
    //      toastMsg("登录信息已过期,请重新登录");
    //      Intent intent = new Intent(getActivity(), LoginActivity.class);
    //      intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
    //      startActivity(intent);
    //    }
    //  });
    //}
  }

  /**
   * 初始化toolbar等通用基础view
   *
   * @param title 页面标题
   */
  protected void requestBaseInit(String title) {
    //TranslucentStatusCompat.requestTranslucentStatus(getActivity());
    //getToolBarShadow().setVisibility(View.GONE);
    //Toolbar toolBar = getToolBar();
    //getStatusBar().setBackgroundResource(R.color.main_color_normal);
    //toolBar.setBackgroundResource(R.color.main_color_normal);
    //TextView tvTitle = UIUtil.setCenterTitle(toolBar, title);
    //ToolBarUtils.updateTitleText(tvTitle);
    //toolBar.setNavigationIcon(R.drawable.ic_back_indicator);
    //toolBar.setNavigationOnClickListener(new View.OnClickListener() {
    //  @Override public void onClick(View view) {
    //    getActivity().onBackPressed();
    //  }
    //});
  }
}
