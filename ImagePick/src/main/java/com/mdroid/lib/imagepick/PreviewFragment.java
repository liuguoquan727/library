package com.mdroid.lib.imagepick;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.mdroid.lib.imagepick.base.BaseFragment;
import com.mdroid.lib.imagepick.utils.SystemUiHider;
import com.mdroid.lib.imagepick.utils.TranslucentStatusCompat;
import com.mdroid.lib.imagepick.utils.UIUtil;
import com.mdroid.lib.imagepick.view.ViewPagerFixed;
import java.util.ArrayList;

/**
 * 预览图片
 */
public class PreviewFragment extends BaseFragment {
  TextView mImagePosition;
  CheckBox mSelected;
  private ViewPagerFixed mPager;
  private TextView mCount;
  private RelativeLayout mCountLayout;
  private TextView mComplete;
  private FrameLayout mFooter;
  private ArrayList<Resource> mResources;
  private SystemUiHider mSystemUiHider;

  private int mShortAnimTime;

  @Override protected void initData(Bundle savedInstanceState) {
    TranslucentStatusCompat.requestTranslucentStatus(getActivity());
    mResources =
        (ArrayList<Resource>) getArguments().getSerializable(MediaSelectFragment.PREVIEW_LIST);
  }

  @Override protected int getContentView() {
    return R.layout.image_pick_content_base_preview;
  }

  @Override protected void initView(View parent) {
    mPager = (ViewPagerFixed) parent.findViewById(R.id.pager);
    mCount = (TextView) parent.findViewById(R.id.count);
    mComplete = (TextView) parent.findViewById(R.id.complete);
    mFooter = (FrameLayout) parent.findViewById(R.id.footer);
    mCountLayout = (RelativeLayout) parent.findViewById(R.id.count_layout);

    requestHasStatusBar();
    requestHeadBarOverlay(true);
    UIUtil.requestStatusBarLight(this, true);
    getToolBarShadow().setVisibility(View.GONE);
    mInflater.inflate(R.layout.image_pick_content_base_preview_header, getToolBar(), true);
    getToolBar().findViewById(R.id.header_left).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        getActivity().onBackPressed();
      }
    });
    mSystemUiHider = SystemUiHider.getInstance(getActivity(), SystemUiHider.FLAG_HIDE_NAVIGATION);
    mSystemUiHider.setup();
    mSystemUiHider.setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
      @Override public void onVisibilityChange(boolean visible) {
        if (mShortAnimTime == 0) {
          mShortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
        }
        ObjectAnimator.ofFloat(mTitleContainer, "translationY", visible ? 0 : -getHeadBarHeight())
            .setDuration(mShortAnimTime)
            .start();
        ObjectAnimator.ofFloat(mFooter, "translationY", visible ? 0 : mFooter.getHeight())
            .setDuration(mShortAnimTime)
            .start();
      }
    });

    mImagePosition = (TextView) getToolBar().findViewById(R.id.imagePosition);
    mSelected = (CheckBox) getToolBar().findViewById(R.id.selected);
    if (mResources != null) {
      mImagePosition.setText(1 + "/" + mResources.size());
      mCountLayout.setVisibility(View.VISIBLE);
      mCount.setText(mResources.size() + "");
    }
  }

  @Override public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    PreviewAdapter adapter = new PreviewAdapter(getActivity(), this, mResources, mSystemUiHider);
    mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

      }

      @Override public void onPageSelected(int position) {
        if (mResources != null) {
          mImagePosition.setText(position + 1 + "/" + mResources.size());
        }
        mSelected.setChecked(mResources.get(position).isSelected());
      }

      @Override public void onPageScrollStateChanged(int state) {

      }
    });
    mPager.setAdapter(adapter);

    mSelected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        mResources.get(mPager.getCurrentItem()).setIsSelected(isChecked);
        //显示已经选择的图片数量
        showSelectedImageSize();
      }
    });

    mComplete.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        finishing(true);
        getActivity().finish();
      }
    });
  }

  /**
   * 显示已经选择的图片数量
   */
  private void showSelectedImageSize() {
    if (mResources != null) {
      int selectedSize = 0;
      for (Resource resource : mResources) {
        if (resource.isSelected()) {
          selectedSize++;
        }
      }
      if (selectedSize == 0) {
        mCountLayout.setVisibility(View.GONE);
        mComplete.setEnabled(false);
        mComplete.setTextColor(getResources().getColor(R.color.image_pick_textColorPrimaryLight));
      } else {
        mCountLayout.setVisibility(View.VISIBLE);
        mCount.setText(selectedSize + "");
        mComplete.setEnabled(true);
        mComplete.setTextColor(getResources().getColor(R.color.image_pick_main_color_normal));
      }
    }
  }

  @Override public boolean onBackPressed() {
    boolean flag = super.onBackPressed();
    if (!flag) {
      finishing(false);
    }
    return flag;
  }

  public void finishing(boolean complete) {
    ArrayList<Resource> resources = new ArrayList<>();
    for (Resource resource : mResources) {
      if (resource.isSelected()) {
        resources.add(resource);
      }
    }
    Intent intent = new Intent();
    intent.putExtra(MediaSelectFragment.PREVIEW_COMPLETE, complete);
    intent.putExtra(MediaSelectFragment.PREVIEW_LIST, resources);
    getActivity().setResult(Activity.RESULT_OK, intent);
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    mSystemUiHider = null;
    mImagePosition = null;
    mSelected = null;
  }
}
