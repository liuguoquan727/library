package com.liuguoquan.study.utils;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.liuguoquan.study.R;
import com.mdroid.lib.recyclerview.BaseRecyclerViewAdapter;
import com.mdroid.utils.AndroidUtils;
import com.mdroid.utils.Ln;
import com.mdroid.utils.ReflectUtils;

/**
 * Description：放置一些公用的方法
 */
public class ToolBarUtils {

  /**
   * 给RecyclerView设置加载更多和加载更多失败时的属性
   *
   * @param mAdapter BaseRecyclerViewAdapter
   * @param recyclerView RecyclerView
   */
  public static void setRecyclerViewLoadMore(final BaseRecyclerViewAdapter mAdapter,
      RecyclerView recyclerView) {
    LayoutInflater inflater = LayoutInflater.from(recyclerView.getContext());
    mAdapter.setLoadMoreView(inflater.inflate(R.layout.item_load_more, recyclerView, false));
    View loadMoreFail = inflater.inflate(R.layout.item_load_more_failure, recyclerView, false);
    loadMoreFail.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        mAdapter.reloadMore();
      }
    });
    mAdapter.setLoadMoreFailureView(loadMoreFail);
  }

  public static View getEmptyView(LayoutInflater inflater, ViewGroup parent, String desc,
      @DrawableRes int resId) {
    View emptyView = inflater.inflate(R.layout.view_empty, parent, false);
    TextView text = (TextView) emptyView.findViewById(R.id.desc);
    text.setText(desc);
    ImageView image = (ImageView) emptyView.findViewById(R.id.empty);
    image.setImageResource(resId);
    return emptyView;
  }

  public static View getEmptyView(LayoutInflater inflater, ViewGroup parent, String desc) {
    View emptyView = inflater.inflate(R.layout.view_empty, parent, false);
    TextView text = (TextView) emptyView.findViewById(R.id.desc);
    text.setText(desc);
    return emptyView;
  }

  public static void addDivider2TabLayout(Context context, TabLayout tabLayout) {
    try {
      LinearLayout tabStrip = ReflectUtils.getFieldValue(tabLayout, "mTabStrip");
      tabStrip.setDividerPadding(AndroidUtils.dp2px(context, 13));
      tabStrip.setDividerDrawable(context.getResources().getDrawable(R.drawable.divider));
      tabStrip.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
    } catch (NoSuchFieldException e) {
      Ln.e(e);
    } catch (IllegalAccessException e) {
      Ln.e(e);
    }
  }

  /**
   * 把标题字体加粗、颜色白色
   *
   * @param titleView TextView
   */
  public static void updateTitleText(TextView titleView) {
    titleView.setTextColor(0xffffffff);
    titleView.getPaint().setFakeBoldText(true);
    titleView.setTextSize(20.0f);
    titleView.setMaxEms(10);
  }

  /**
   * 给toolbar右边添加一个按钮
   *
   * @param toolbar toolbar
   * @param resId 图片资源id
   * @return 被添加的ImageView
   */
  public static ImageView addBtn2ToolbarRight(Toolbar toolbar, int resId) {
    Context context = toolbar.getContext();
    ImageView img = new ImageView(context, null,
        android.support.v7.appcompat.R.attr.toolbarNavigationButtonStyle);
    img.setImageResource(resId);
    img.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
    Toolbar.LayoutParams params = new Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT,
        Toolbar.LayoutParams.MATCH_PARENT);
    params.gravity = Gravity.END | Gravity.CENTER;
    img.setLayoutParams(params);
    toolbar.addView(img);
    return img;
  }

  /***
   * 给toolbar右边增加一个按钮
   *
   * @param toolbar Toolbar
   * @param inflater LayoutInflater
   * @param btnText 按钮上的字
   * @return 按钮TextView
   */
  public static TextView addBtn2ToolbarRight(Toolbar toolbar, LayoutInflater inflater,
      String btnText) {
    TextView btn = (TextView) inflater.inflate(R.layout.view_toolbar_right_text, toolbar, false);
    ((Toolbar.LayoutParams) btn.getLayoutParams()).gravity = Gravity.END;
    btn.setText(btnText);
    toolbar.addView(btn);
    return btn;
  }

  /***
   * 给toolbar右边增加一个按钮
   *
   * @param toolbar Toolbar
   * @param inflater LayoutInflater
   * @param btnText 按钮上的字
   * @return 按钮TextView
   */
  public static TextView addBtn2ToolbarRight(Toolbar toolbar, LayoutInflater inflater,
      @StringRes int btnText) {
    TextView btn = (TextView) inflater.inflate(R.layout.view_toolbar_right_text, toolbar, false);
    ((Toolbar.LayoutParams) btn.getLayoutParams()).gravity = Gravity.END;
    btn.setText(btnText);
    toolbar.addView(btn);
    return btn;
  }

  /**
   * 停止刷新
   *
   * @param refreshLayout SwipeRefreshLayout
   */
  public static void stopRefresh(SwipeRefreshLayout refreshLayout) {
    if (refreshLayout != null && refreshLayout.isRefreshing()) {
      refreshLayout.setRefreshing(false);
    }
  }
}
