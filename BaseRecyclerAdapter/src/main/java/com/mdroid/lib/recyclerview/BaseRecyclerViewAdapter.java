package com.mdroid.lib.recyclerview;

import android.content.Context;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import java.util.ArrayList;
import java.util.List;

/**
 * Description：可以添加:
 * <p>header {@link #addHeaderView(View)}</p>
 * <p>footer{@link #addFooterView(View)}</p>
 * <p>emptyView{@link #setEmptyView(View)} </p>
 * <p>loadMoreView{@link #setLoadMoreView(View)} </p>
 * 当loadMoreView出现时，会回调{@link OnLoadingMoreListener#requestMoreData()},<br/>
 * 数据请求完成时，需调用{@link #loadMoreFinish(boolean, List)}刷新界面<br/>
 * <p><b>多类型item使用时：</b>
 * <li>item的数据需要实现{@link MultipleEntity}</li>
 * <li><font color=blue>使用{@link #BaseRecyclerViewAdapter(SparseIntArray, * List)}构造方法添加item类型</font></li></p>
 */
public abstract class BaseRecyclerViewAdapter<T>
    extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  protected static final int VIEW_TYPE_DATA = 0x000001000;
  protected static final int VIEW_TYPE_HEADER = 0x000001001;
  protected static final int VIEW_TYPE_FOOTER = 0x000001002;
  protected static final int VIEW_TYPE_EMPTY = 0x000001003;
  protected static final int VIEW_TYPE_LOAD_MORE = 0x000001004;
  protected static final int VIEW_TYPE_LOAD_MORE_FAILURE = 0x000001005;
  private static final String TAG = BaseRecyclerViewAdapter.class.getSimpleName();
  protected Context mContext;
  protected LayoutInflater mLayoutInflater;
  protected List<T> mData;
  /**
   * layout资源id缓存
   */
  private SparseIntArray mLayoutsMap;
  private OnRecyclerViewItemClickListener mOnRecyclerViewItemClickListener;
  private OnRecyclerViewItemLongClickListener mOnRecyclerViewItemLongClickListener;
  private OnRecyclerViewItemChildClickListener mChildClickListener;
  private OnLoadingMoreListener mLoadingMoreListener;
  private View mHeaderView;
  private View mFooterView;
  private View mEmptyView;
  private View mLoadMoreView;
  private View mLoadMoreFailureView;
  private boolean mShowHeaderFooterWhenEmpty = false;
  private boolean mHasMore = false;
  private boolean mLoadMoreFailure = false;
  private boolean mIsLoading = false;

  /**
   * @param layoutResId item文件的资源id
   * @param data 数据
   */
  public BaseRecyclerViewAdapter(int layoutResId, @NonNull List<T> data) {
    setData(data);
    if (layoutResId != 0) {
      if (mLayoutsMap == null) mLayoutsMap = new SparseIntArray(1);
      mLayoutsMap.put(VIEW_TYPE_DATA, layoutResId);
    }
  }

  /**
   * @param layoutResId item文件的资源id
   * @param data 数据
   * @param hasMore 是否有更多数据
   */
  public BaseRecyclerViewAdapter(int layoutResId, @NonNull List<T> data, boolean hasMore) {
    setData(data);
    if (layoutResId != 0) {
      if (mLayoutsMap == null) mLayoutsMap = new SparseIntArray(1);
      mLayoutsMap.put(VIEW_TYPE_DATA, layoutResId);
    }
    this.setHasMore(hasMore);
  }

  /**
   * 如果item为多种类型，使用此构造方法
   *
   * @param layoutMap item文件的map集合，key为itemType，value为layout资源文件id
   * @param data 数据
   */
  public BaseRecyclerViewAdapter(SparseIntArray layoutMap, @NonNull List<T> data) {
    setData(data);
    this.mLayoutsMap = layoutMap;
  }

  /**
   * 如果item为多种类型，使用此构造方法
   *
   * @param layoutMap item文件的map集合，key为itemType，value为layout资源文件id
   * @param data 数据
   * @param hasMore 是否有更多数据
   */
  public BaseRecyclerViewAdapter(SparseIntArray layoutMap, @NonNull List<T> data, boolean hasMore) {
    setData(data);
    this.mLayoutsMap = layoutMap;
    this.setHasMore(hasMore);
  }

  public void addHeaderView(View header) {
    this.mHeaderView = header;
    this.notifyDataSetChanged();
  }

  public void addFooterView(View footer) {
    this.mFooterView = footer;
    this.notifyDataSetChanged();
  }

  public void setEmptyView(View emptyView) {
    this.mEmptyView = emptyView;
  }

  public void setLoadMoreView(View loadMoreView) {
    this.mLoadMoreView = loadMoreView;
  }

  public void setLoadMoreFailureView(View loadMoreFailureView) {
    mLoadMoreFailureView = loadMoreFailureView;
  }

  public void setShowHeaderFooterWhenEmpty(boolean showHeaderFooterWhenEmpty) {
    this.mShowHeaderFooterWhenEmpty = showHeaderFooterWhenEmpty;
  }

  public void setHasMore(boolean hasMore) {
    this.mHasMore = hasMore;
  }

  public boolean isLoadingMore() {
    return mIsLoading;
  }

  /**
   * 完成loadMore
   *
   * @param hasMore 是否还有更多数据
   * @param data 数据，可为null
   */
  public void loadMoreFinish(boolean hasMore, List<T> data) {
    this.mHasMore = hasMore;
    this.mIsLoading = false;
    if (data != null) {
      mData.addAll(data);
    }
    notifyDataSetChanged();
  }

  /**
   * loadMore失败时,后续可通过{@link #reloadMore()}重新加载
   */
  public void loadMoreFailure() {
    this.mHasMore = true;
    this.mIsLoading = false;
    this.mLoadMoreFailure = true;
    notifyDataSetChanged();
  }

  /**
   * 重新loadMore，当recyclerView滚动到最下面时，重新loadMore的流程
   */
  public void reloadMore() {
    this.mLoadMoreFailure = false;
    notifyDataSetChanged();
  }

  public boolean isLoadMoreFailure() {
    return mLoadMoreFailure;
  }

  public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener listener) {
    this.mOnRecyclerViewItemClickListener = listener;
  }

  public void setOnRecyclerViewItemLongClickListener(OnRecyclerViewItemLongClickListener listener) {
    this.mOnRecyclerViewItemLongClickListener = listener;
  }

  public void setOnRecyclerViewItemChildClickListener(
      OnRecyclerViewItemChildClickListener listener) {
    this.mChildClickListener = listener;
  }

  public void setOnLoadingMoreListener(OnLoadingMoreListener listener) {
    this.mLoadingMoreListener = listener;
  }

  public List<T> getData() {
    return mData;
  }

  protected void setData(@NonNull List<T> data) {
    // 确保data不为null策略
    if (data == null) {
      data = new ArrayList<>(0);
      Log.e(TAG, "[data is null, mData in adapter is a new List]");
    }
    this.mData = data;
  }

  public T getItem(int position) {
    if (mData.size() == 0) return null;
    return mData.get(position);
  }

  public void addData(T t) {
    int oldSize = mData.size();
    mData.add(t);
    // 原来没数据但是有emptyView
    if (oldSize == 0 && getEmptyViewsCount() != 0) {
      // 实际上emptyView只能有1个
      notifyItemChangedIgnoreHeader(0);
    } else {
      notifyItemInserted(oldSize + getHeaderViewsCount());
    }
  }

  public void addData(T t, int position) {
    if (mData.size() == 0) {
      addData(t);
    } else {
      mData.add(position, t);
      notifyItemInserted(position + getHeaderViewsCount());
    }
  }

  public void addData(@NonNull List<T> data) {
    int oldSize = mData.size();
    mData.addAll(data);
    // 原来没数据，显示的EmptyView
    if (oldSize == 0 && getEmptyViewsCount() != 0) {
      notifyDataSetChanged();
    } else {
      notifyItemRangeInserted(oldSize + getHeaderViewsCount(), data.size());
    }
  }

  public void addData(@NonNull List<T> data, int position) {
    if (mData.size() == 0) {
      addData(data);
    } else {
      mData.addAll(position, data);
      notifyItemRangeInserted(position + getHeaderViewsCount(), data.size());
    }
  }

  public void resetData(@NonNull List<T> data) {
    mData.clear();
    if (data != null) {
      mData.addAll(data);
    }
    notifyDataSetChanged();
  }

  public void remove(int position) {
    mData.remove(position);
    if (mData.size() == 0) {
      notifyDataSetChanged();
    } else {
      notifyItemRemoved(position + getHeaderViewsCount());
    }
  }

  public void remove(T t) {
    int size = mData.size();
    for (int i = 0; i < size; i++) {
      if (t.equals(mData.get(i))) {
        remove(i);
        break;
      }
    }
  }

  /**
   * 修改了某个数据，不用考虑header的存在
   *
   * @param dataPosition 数据在数据集的位置
   */
  public void notifyItemChangedIgnoreHeader(int dataPosition) {
    notifyItemChanged(dataPosition + getHeaderViewsCount());
  }

  /**
   * 修改了某些数据，不用考虑header的存在
   *
   * @param positionStart 第一个数据在集合中的位置
   * @param itemCount 数据的数量
   */
  public void notifyItemRangeChangedIgnoreHeader(int positionStart, int itemCount) {
    notifyItemRangeChanged(positionStart + getHeaderViewsCount(), itemCount);
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    BaseViewHolder baseViewHolder;
    if (mContext == null) this.mContext = parent.getContext();
    if (mLayoutInflater == null) this.mLayoutInflater = LayoutInflater.from(mContext);
    switch (viewType) {
      case VIEW_TYPE_HEADER:
        baseViewHolder = new BaseViewHolder(mHeaderView);
        break;
      case VIEW_TYPE_FOOTER:
        baseViewHolder = new BaseViewHolder(mFooterView);
        break;
      case VIEW_TYPE_EMPTY:
        baseViewHolder = new BaseViewHolder(mEmptyView);
        break;
      case VIEW_TYPE_LOAD_MORE:
        baseViewHolder = new BaseViewHolder(mLoadMoreView);
        break;
      case VIEW_TYPE_LOAD_MORE_FAILURE:
        baseViewHolder = new BaseViewHolder(mLoadMoreFailureView);
        break;
      case VIEW_TYPE_DATA:
      default:
        baseViewHolder = createContentViewHolder(parent, viewType);
        initItemClickListener(baseViewHolder);
        break;
    }
    return baseViewHolder;
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    int viewType = holder.getItemViewType();
    switch (viewType) {
      case VIEW_TYPE_LOAD_MORE:
        if (mLoadingMoreListener == null) {
          Log.w(TAG, "mLoadingMoreListener == null, loadMore will not be executed");
        }
        if (!mIsLoading && mLoadingMoreListener != null) {
          mIsLoading = true;
          mLoadingMoreListener.requestMoreData();
        }
        break;
      case VIEW_TYPE_HEADER:
      case VIEW_TYPE_FOOTER:
      case VIEW_TYPE_EMPTY:
      case VIEW_TYPE_LOAD_MORE_FAILURE:
        break;
      case VIEW_TYPE_DATA:
      default:
        convert((BaseViewHolder) holder,
            mData.get(holder.getLayoutPosition() - getHeaderViewsCount()));
        break;
    }
  }

  /**
   * 使用holder中的方法设置item的各个数据、view的点击事件等<br/>
   * eg: {@link BaseViewHolder#setText(int, CharSequence)}、<br/>
   * {@link BaseViewHolder#setOnClickListener(int, View.OnClickListener)}、<br/>
   * {@link BaseViewHolder#setOnClickListener(int, OnItemChildClickListener)}...<br/>
   * 使用{@link BaseViewHolder#setOnClickListener(int, OnItemChildClickListener)}可以通过
   * {@link #setOnRecyclerViewItemChildClickListener(OnRecyclerViewItemChildClickListener)}接收点击事件
   *
   * @param holder 当前项的holder
   * @param item 当前项的数据
   */
  protected abstract void convert(BaseViewHolder holder, T item);

  @Override public int getItemViewType(int position) {
    // 没有数据并且不显示头、尾部，则为emptyView
    if (mData.size() == 0 && mEmptyView != null && !mShowHeaderFooterWhenEmpty) {
      return VIEW_TYPE_EMPTY;
    }
    // 没有数据，有头部时position == 1 或者 没有头部position == 0，为emptyView
    if (mData.size() == 0 && ((mEmptyView != null && mHeaderView != null && position == 1)
        || mEmptyView != null && mHeaderView == null && position == 0)) {
      return VIEW_TYPE_EMPTY;
    }
    // 加载更多失败时的view
    if (mLoadMoreFailure
        && position == mData.size() + getHeaderViewsCount() + getFooterViewsCount()) {
      return VIEW_TYPE_LOAD_MORE_FAILURE;
    }
    // 有更多时，最后一行为加载更多
    if (mHasMore && position == mData.size() + getHeaderViewsCount() + getFooterViewsCount()) {
      return VIEW_TYPE_LOAD_MORE;
    }
    // 显示头部的情况
    if (mHeaderView != null && position == 0) return VIEW_TYPE_HEADER;
    // 显示尾部的情况
    if (mFooterView != null && ((mData.size() == 0
        && position == getHeaderViewsCount() + getEmptyViewsCount()) || (position == (mData.size()
        + getHeaderViewsCount())))) {
      return VIEW_TYPE_FOOTER;
    }
    return getContentItemViewType(position - getHeaderViewsCount());
  }

  /**
   * 获取item的类型，如果使用多类型的item布局，重写此方法返回对应的类型
   *
   * @param position 当前item的position
   * @return item类型
   */
  protected int getContentItemViewType(int position) {
    if (mLayoutsMap.size() == 1) {
      return VIEW_TYPE_DATA;
    } else {
      return ((MultipleEntity) mData.get(position)).getItemType();
    }
  }

  @Override public int getItemCount() {
    int count = mData.size();
    if (count == 0 && mEmptyView != null) {
      count += 1;
    }
    if (mData.size() > 0 || mShowHeaderFooterWhenEmpty) {
      count += getHeaderViewsCount();
      count += getFooterViewsCount();
      if (mHasMore && mLoadMoreView != null) {
        count += 1;
      }
    }
    return count;
  }

  /**
   * 解决header等view的宽度问题
   */
  @Override public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
    super.onViewAttachedToWindow(holder);
    int type = holder.getItemViewType();
    if (type == VIEW_TYPE_HEADER
        || type == VIEW_TYPE_FOOTER
        || type == VIEW_TYPE_EMPTY
        || type == VIEW_TYPE_LOAD_MORE
        || type == VIEW_TYPE_LOAD_MORE_FAILURE) {
      setFullSpan(holder);
    }
  }

  protected void setFullSpan(RecyclerView.ViewHolder holder) {
    if (holder.itemView.getLayoutParams() instanceof StaggeredGridLayoutManager.LayoutParams) {
      StaggeredGridLayoutManager.LayoutParams params =
          (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
      params.setFullSpan(true);
    }
  }

  /**
   * 解决header等view的宽度问题
   */
  @Override public void onAttachedToRecyclerView(RecyclerView recyclerView) {
    super.onAttachedToRecyclerView(recyclerView);
    RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
    if (manager instanceof GridLayoutManager) {
      final GridLayoutManager gridManager = ((GridLayoutManager) manager);
      gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
        @Override public int getSpanSize(int position) {
          int type = getItemViewType(position);
          return (type == VIEW_TYPE_HEADER
              || type == VIEW_TYPE_FOOTER
              || type == VIEW_TYPE_EMPTY
              || type == VIEW_TYPE_LOAD_MORE
              || type == VIEW_TYPE_LOAD_MORE_FAILURE) ? gridManager.getSpanCount() : 1;
        }
      });
    }
  }

  public int getHeaderViewsCount() {
    return mHeaderView == null ? 0 : 1;
  }

  public int getFooterViewsCount() {
    return mFooterView == null ? 0 : 1;
  }

  public int getEmptyViewsCount() {
    return mEmptyView == null ? 0 : 1;
  }

  private void initItemClickListener(final BaseViewHolder baseViewHolder) {
    if (mOnRecyclerViewItemClickListener != null) {
      baseViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          // 返回去除header之后的position
          mOnRecyclerViewItemClickListener.onItemClick(v,
              baseViewHolder.getLayoutPosition() - getHeaderViewsCount());
        }
      });
    }
    if (mOnRecyclerViewItemLongClickListener != null) {
      baseViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
        @Override public boolean onLongClick(View v) {
          // 返回去除header之后的position
          return mOnRecyclerViewItemLongClickListener.onItemLongClick(v,
              baseViewHolder.getLayoutPosition() - getHeaderViewsCount());
        }
      });
    }
  }

  /**
   * 创建recyclerView的viewHolder，如果要支持多类型的item，需要重写此方法
   *
   * @param parent item的父view
   * @param viewType 当前item的viewType
   */
  protected BaseViewHolder createContentViewHolder(ViewGroup parent, int viewType) {
    return createDefContentViewHolder(parent, mLayoutsMap.get(viewType));
  }

  protected BaseViewHolder createDefContentViewHolder(ViewGroup parent, int layoutResId) {
    return new BaseViewHolder(getItemView(layoutResId, parent));
  }

  protected View getItemView(int layoutResId, ViewGroup parent) {
    return mLayoutInflater.inflate(layoutResId, parent, false);
  }

  public interface OnRecyclerViewItemClickListener {
    /**
     * @param view 点击的那项item view
     * @param position item的position(已排除掉header的影响)
     */
    void onItemClick(View view, int position);
  }

  public interface OnRecyclerViewItemLongClickListener {
    /**
     * @param view 长按的那项item view
     * @param position item的position(已排除掉header的影响)
     * @return 消费了长按事件则返回true
     */
    boolean onItemLongClick(View view, int position);
  }

  public interface OnRecyclerViewItemChildClickListener {
    /**
     * RecyclerView的item中的view的点击事件
     *
     * @param adapter BaseRecyclerViewAdapter
     * @param view 点击的那个view
     * @param position 点击的view所在的position(已排除掉header的影响)
     */
    void onItemChildClick(BaseRecyclerViewAdapter adapter, View view, int position);
  }

  public interface OnLoadingMoreListener {
    /**
     * 请求更多数据，当出现loadMoreView时,如果不是正在加载，则会调用此方法用于加载更多数据<br/>
     * <p>加载完更多数据后需要调用{@link #loadMoreFinish(boolean, List)}</p>
     */
    void requestMoreData();
  }

  public class OnItemChildClickListener implements View.OnClickListener {
    private RecyclerView.ViewHolder mViewHolder;

    public void setViewHolder(RecyclerView.ViewHolder viewHolder) {
      mViewHolder = viewHolder;
    }

    @Override public void onClick(View v) {
      if (mChildClickListener != null) {
        // 返回去除header之后的position
        mChildClickListener.onItemChildClick(BaseRecyclerViewAdapter.this, v,
            mViewHolder.getLayoutPosition() - getHeaderViewsCount());
      }
    }
  }
}
