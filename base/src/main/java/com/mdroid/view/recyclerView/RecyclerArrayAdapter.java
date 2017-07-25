package com.mdroid.view.recyclerView;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public abstract class RecyclerArrayAdapter<M, VH extends RecyclerView.ViewHolder>
    extends RecyclerView.Adapter<VH> {

  protected final Activity mActivity;
  protected final LayoutInflater mInflater;
  protected List<M> mItems;

  public RecyclerArrayAdapter(@NonNull Activity activity, @NonNull List<M> data) {
    this.mActivity = activity;
    this.mInflater = activity.getLayoutInflater();
    this.mItems = data;
    setHasStableIds(true);
  }

  public void add(M object) {
    mItems.add(object);
    notifyDataSetChanged();
  }

  public void add(int index, M object) {
    mItems.add(index, object);
    notifyDataSetChanged();
  }

  public void addAll(Collection<? extends M> collection) {
    if (collection != null) {
      mItems.addAll(collection);
      notifyDataSetChanged();
    }
  }

  public void addAll(M... items) {
    addAll(Arrays.asList(items));
  }

  public void set(Collection<? extends M> data) {
    mItems.clear();
    mItems.addAll(data);
    notifyDataSetChanged();
  }

  public void clear() {
    mItems.clear();
    notifyDataSetChanged();
  }

  public void remove(M object) {
    mItems.remove(object);
    notifyDataSetChanged();
  }

  public void remove(int index) {
    mItems.remove(index);
    notifyDataSetChanged();
  }

  public List<M> getData() {
    return mItems;
  }

  public M getItem(int position) {
    return mItems.get(position);
  }

  @Override public long getItemId(int position) {
    return position;
  }

  @Override public int getItemCount() {
    return mItems.size();
  }

  protected static class PlaceHolder extends RecyclerView.ViewHolder {

    public PlaceHolder(View view) {
      super(view);
    }
  }
}