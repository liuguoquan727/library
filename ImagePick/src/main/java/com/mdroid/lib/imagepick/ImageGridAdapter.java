package com.mdroid.lib.imagepick;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImageGridAdapter extends BaseAdapter {

  private static final int TYPE_CAMERA = 0;
  private static final int TYPE_NORMAL = 1;
  private final Activity mActivity;
  private final LayoutInflater mInflater;
  private final int mMode;
  private List<Resource> mData = new ArrayList<>();
  private boolean showCamera = true;
  private boolean showSelectIndicator = true;

  private List<Resource> mSelectedData;

  public ImageGridAdapter(Activity context, ArrayList<Resource> data, List<Resource> selectedData,
      boolean showCamera, int mode) {
    this.mActivity = context;
    this.mInflater = mActivity.getLayoutInflater();
    ArrayList<Resource> list = new ArrayList<>();
    list.addAll(data);
    this.mData = list;
    this.mMode = mode;
    this.showCamera = showCamera;
    this.mSelectedData = selectedData;
  }

  /**
   * 显示选择指示器
   */
  public void showSelectIndicator(boolean b) {
    showSelectIndicator = b;
  }

  public boolean isShowCamera() {
    return showCamera;
  }

  public void setShowCamera(boolean b) {
    if (showCamera == b) return;

    showCamera = b;
    notifyDataSetChanged();
  }

  /**
   * 选择某个图片，改变选择状态
   */
  public void select(Resource resource) {
    if (mSelectedData.contains(resource)) {
      mSelectedData.remove(resource);
    } else {
      mSelectedData.add(resource);
    }
    notifyDataSetChanged();
  }

  /**
   * 通过图片路径设置默认选择
   */
  public void setDefaultSelected(ArrayList<String> resultList) {
    for (String path : resultList) {
      Resource image = getImageByPath(path);
      if (image != null) {
        mSelectedData.add(image);
      }
    }
    if (mSelectedData.size() > 0) {
      notifyDataSetChanged();
    }
  }

  public void setData(List<Resource> data) {
    mData.clear();
    mData.addAll(data);
    notifyDataSetChanged();
  }

  private Resource getImageByPath(String path) {
    if (mData != null && mData.size() > 0) {
      for (Resource image : mData) {
        if (image.getFilename().equalsIgnoreCase(path)) {
          return image;
        }
      }
    }
    return null;
  }

  @Override public int getViewTypeCount() {
    return 2;
  }

  @Override public int getItemViewType(int position) {
    if (showCamera) {
      return position == 0 ? TYPE_CAMERA : TYPE_NORMAL;
    }
    return TYPE_NORMAL;
  }

  @Override public int getCount() {
    return mData.size() + (showCamera ? 1 : 0);
  }

  @Override public Resource getItem(int position) {
    if (showCamera) {
      if (position == 0) {
        return null;
      }
      return mData.get(position - 1);
    } else {
      return mData.get(position);
    }
  }

  @Override public long getItemId(int position) {
    return position;
  }

  @Override public View getView(int position, View view, ViewGroup viewGroup) {

    int type = getItemViewType(position);
    if (type == TYPE_CAMERA) {
      view = mInflater.inflate(R.layout.image_pick_item_base_camera, viewGroup, false);
      view.setTag(null);
    } else if (type == TYPE_NORMAL) {
      ViewHolder holder;
      if (view == null) {
        view = mInflater.inflate(R.layout.image_pick_item_base_image, viewGroup, false);
        holder = new ViewHolder(view);
      } else {
        holder = (ViewHolder) view.getTag();
      }
      if (holder != null) {
        holder.bindData(getItem(position));
      }
    }

    return view;
  }

  class ViewHolder {
    ImageView image;
    View mask;
    ImageView indicator;

    ViewHolder(View view) {
      image = (ImageView) view.findViewById(R.id.image);
      mask = view.findViewById(R.id.mask);
      indicator = (ImageView) view.findViewById(R.id.checkmark);
      view.setTag(this);
    }

    void bindData(final Resource data) {
      if (data == null) return;
      // 处理单选和多选状态
      if (showSelectIndicator) {
        if (mSelectedData.contains(data)) {
          // 设置选中状态
          indicator.setImageResource(R.drawable.image_pick_ic_checked);
          mask.setVisibility(View.VISIBLE);
        } else {
          // 未选择
          indicator.setImageResource(R.drawable.image_pick_ic_unchecked);
          mask.setVisibility(View.GONE);
        }
      } else {
        indicator.setVisibility(View.GONE);
      }
      if (mMode == MediaSelectFragment.MODE_SINGLE || mMode == MediaSelectFragment.MODE_CROP) {
        indicator.setVisibility(View.GONE);
      }
      File imageFile = new File(data.getFilePath());
      RequestOptions options =
          new RequestOptions().placeholder(R.drawable.image_pick_ic_default_image)
              .fitCenter()
              .centerCrop();
      Glide.with(mActivity).load(imageFile).apply(options).into(image);
    }
  }
}
