package com.mdroid.lib.imagepick;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

/**
 * 相册列表类
 */
public class ImageFolderAdapter extends BaseAdapter {
  private Context context;
  private List<Folder> list;

  public ImageFolderAdapter(Context context, List<Folder> list) {
    super();
    this.context = context;
    this.list = list;
  }

  public void changeData(List<Folder> list) {
    this.list = list;
    notifyDataSetChanged();
  }

  @Override public int getCount() {
    return list.size();
  }

  @Override public Folder getItem(int position) {
    return list.get(position);
  }

  @Override public long getItemId(int position) {
    return position;
  }

  @Override public View getView(int position, View convertView, ViewGroup parent) {
    ViewHolder holder = null;
    if (convertView == null) {
      convertView =
          LayoutInflater.from(context).inflate(R.layout.image_pick_item_base_photo_list_dir, null);
      holder = new ViewHolder();
      holder.dirItemIcon = (ImageView) convertView.findViewById(R.id.id_dir_choose);
      holder.dirItemName = (TextView) convertView.findViewById(R.id.id_dir_item_name);
      holder.dirItemNum = (TextView) convertView.findViewById(R.id.id_dir_item_count);
      convertView.setTag(holder);
    } else {
      holder = (ViewHolder) convertView.getTag();
    }
    if (position == 0) {
      holder.dirItemName.setText("所有图片");
    } else {
      if (getItem(position).name != null) {
        holder.dirItemName.setText(getItem(position).name);
      }
    }
    holder.dirItemNum.setText("(" + getItem(position).images.size() + ")");
    holder.dirItemIcon.setVisibility(View.VISIBLE);
    return convertView;
  }

  class ViewHolder {
    TextView dirItemName;
    TextView dirItemNum;
    ImageView dirItemIcon;
  }
}
