package com.mdroid.view.recyclerView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class EndlessScrollListener extends RecyclerView.OnScrollListener {

  private int visibleThreshold = 3;
  private RecyclerView.OnScrollListener mOnScrollListener;

  private IMore mMore;

  public EndlessScrollListener(IMore more) {
    this(3, more);
  }

  public EndlessScrollListener(int visibleThreshold, IMore more) {
    this.visibleThreshold = visibleThreshold;
    this.mMore = more;
  }

  public void setOnScrollListener(RecyclerView.OnScrollListener onScrollListener) {
    this.mOnScrollListener = onScrollListener;
  }

  @Override public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
    if (mOnScrollListener != null) {
      mOnScrollListener.onScrollStateChanged(recyclerView, newState);
    }
    if (newState == RecyclerView.SCROLL_STATE_IDLE && (mMore.hasMore()
        && !mMore.isLoading()
        && mMore.canLoad()
        && ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition()
        >= recyclerView.getAdapter().getItemCount() - visibleThreshold)) {
      mMore.loadMore();
    }
  }

  @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
    if (mOnScrollListener != null) {
      mOnScrollListener.onScrolled(recyclerView, dx, dy);
    }
  }

  public interface IMore {
    /**
     * @return 是否允许加载更多
     */
    boolean canLoad();

    /**
     * @return 是否允许显示加载更多界面
     */
    boolean canShow();

    /**
     * @return 是否有更多
     */
    boolean hasMore();

    /**
     * @return 正在加载
     */
    boolean isLoading();

    /**
     * 加载更多
     */
    void loadMore();
  }
}