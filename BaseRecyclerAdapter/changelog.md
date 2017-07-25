# 1.0.3
修改BaseRecyclerViewAdapter中的方法,做兼容处理
```
  protected void setData(@NonNull List<T> data) {
    // 确保data不为null策略
    if (data == null) {
      data = new ArrayList<>(0);
      Log.e(TAG, "[data is null, mData in adapter is a new List]");
    }
    this.mData = data;
  }
```

# 1.0.2
修改adapter中的数据集不另外new一个集合

# 1.0.1
1、修改bug(有header时调用adapter.add()、adapter.remove()引起的)
2、增加方法,当item数据改变，需要改变界面时调用，不用考虑header的存在
```
notifyItemChangedIgnoreHeader()
notifyItemRangeChangedIgnoreHeader()
```

# 1.0.0
初始版本