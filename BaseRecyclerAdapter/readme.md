# RecyclerView的BaseAdapter
可以极大的简写adapter

## Features：
- 1、支持item和item中某个控件的click事件：
```java
    mAdapter.setOnRecyclerViewItemClickListener(this);
    mAdapter.setOnRecyclerViewItemLongClickListener(this);
    mAdapter.setOnRecyclerViewItemChildClickListener(this);
```
- 2、支持自由添加header、footer、emptyView：
```java
    mAdapter.addHeaderView(mInflater.inflate(R.layout.item_recycler_header, mRecyclerView, false));
    mAdapter.addFooterView(mInflater.inflate(R.layout.item_recycler_footer, mRecyclerView, false));
    mAdapter.setEmptyView(mInflater.inflate(R.layout.item_empty, mRecyclerView, false));
    mAdapter.setShowHeaderFooterWhenEmpty(true);
```
- 3、支持多种类型item的adapter，多类型item使用接口定义各自的itemType：
```java
    public class TestData implements MultipleEntity
```
- 4、支持loadMore：
```java
    mAdapter.setOnLoadingMoreListener(this);
    mAdapter.setHasMore(true);
```