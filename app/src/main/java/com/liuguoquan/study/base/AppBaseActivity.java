package com.liuguoquan.study.base;

import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import com.liuguoquan.study.R;
import com.liuguoquan.study.utils.ToolBarUtils;
import com.mdroid.lib.core.base.BaseActivity;
import com.mdroid.lib.core.base.BaseView;
import com.mdroid.lib.core.eventbus.EventBusEvent;
import com.mdroid.lib.core.utils.UIUtil;

/** Description： */
public abstract class AppBaseActivity<V extends AppBaseView, T extends AppBaseActivityPresenter<V>>
    extends BaseActivity<V, T> implements BaseView<T>, EventBusEvent.INotify {

    @Override
    protected void bind() {
    }

    @Override
    protected void unbind() {
  }

  /**
   * 数据等加载指示器，默认空实现
   *
   * @param isActive 是否正在处理
   */
  @Override
  public void setLoadingIndicator(boolean isActive) {
  }

    @Override
    public void onNotify(EventBusEvent event) {
    }

  protected void requestBaseInit(Toolbar toolBar, String title) {
    toolBar.setBackgroundResource(R.color.main_color_normal);
    TextView tvTitle = UIUtil.setCenterTitle(toolBar, title);
    ToolBarUtils.updateTitleText(tvTitle);
    toolBar.setNavigationIcon(R.drawable.ic_back);
      toolBar.setNavigationOnClickListener(
          new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  finish();
              }
          });
  }

    @Override
    public void onDestroy() {
    super.onDestroy();
  }
}
