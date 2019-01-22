package com.mdroid.lib.imagepick;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.core.app.ActivityCompat;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.mdroid.lib.imagepick.utils.SystemUiHider;
import com.mdroid.lib.imagepick.view.RecyclingPagerAdapter;
import java.io.File;
import java.util.List;
import uk.co.senab.photoview.PhotoViewAttacher;

public class PreviewAdapter extends RecyclingPagerAdapter {

  private Activity mActivity;
  private PreviewFragment mFragment;
  private List<Resource> mResources;
  private SystemUiHider mSystemUiHider;

  public PreviewAdapter(Activity activity, PreviewFragment previewFragment,
      List<Resource> resources, SystemUiHider systemUiHider) {
    this.mActivity = activity;
    this.mResources = resources;
    this.mFragment = previewFragment;
    this.mSystemUiHider = systemUiHider;
  }

  @Override public View getView(int position, View convertView, ViewGroup container) {
    final ViewHolder holder;
    if (convertView == null) {
      View view = mActivity.getLayoutInflater()
          .inflate(R.layout.image_pick_item_base_preview_media, container, false);
      holder = new ViewHolder(view);
      convertView = view;
      convertView.setTag(holder);
      holder.mAttacher.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
        @Override public void onViewTap(View view, float v, float v1) {
          mSystemUiHider.toggle();
        }
      });
      holder.mImage.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          mSystemUiHider.toggle();
        }
      });
    } else {
      holder = (ViewHolder) convertView.getTag();
    }
    final Resource resource = mResources.get(position);
    RequestOptions options = new RequestOptions().fitCenter();
    Glide.with(mActivity)
        .asBitmap().load(new File(resource.getFilePath())).apply(options)
        .into(new BitmapImageViewTarget(holder.mImage) {
          @Override protected void setResource(Bitmap resource) {
            super.setResource(resource);
            holder.mAttacher.update();
          }
        });
    holder.mPlay.setVisibility(resource.isVideo() ? View.VISIBLE : View.GONE);
    holder.mPlay.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(resource.getFilePath())),
            resource.getMimeType());
        ActivityCompat.startActivity(mActivity, intent, null);
      }
    });
    return convertView;
  }

  @Override public int getCount() {
    return mResources.size();
  }

  static class ViewHolder {
    PhotoViewAttacher mAttacher;
    ImageView mImage;
    ImageView mPlay;

    ViewHolder(View view) {
      mImage = (ImageView) view.findViewById(R.id.image);
      mPlay = (ImageView) view.findViewById(R.id.play);
      mAttacher = new PhotoViewAttacher(mImage);
    }
  }
}
