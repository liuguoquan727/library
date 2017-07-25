package com.mdroid.lib.imagepick.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import com.mdroid.lib.imagepick.MediaSelectFragment;
import com.mdroid.lib.imagepick.R;

/**
 * Created by ouyangzn on 2016/11/16.<br/>
 * Description：
 */
public class ContainerActivity extends AppCompatActivity {

  public static final String FRAGMENT_NAME = "fragment_name";

  private String mFragmentName;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.image_pick_activity_container);
    initData(savedInstanceState);
    initView(savedInstanceState);
  }

  private void initView(Bundle savedInstanceState) {
    if (mFragmentName == null) {
      mFragmentName = MediaSelectFragment.class.getName();
    }
    if (!TextUtils.isEmpty(mFragmentName)) {
      BaseFragment fragment =
          (BaseFragment) getSupportFragmentManager().findFragmentByTag(mFragmentName);
      if (savedInstanceState == null) {
        if (fragment == null) {
          fragment = newFragment();
        }
        if (fragment != null) {
          getSupportFragmentManager().beginTransaction()
              .replace(R.id.layout_container, fragment, mFragmentName)
              .commit();
        }
      }
    }
  }

  private void initData(Bundle savedInstanceState) {
    mFragmentName = getIntent().getStringExtra(FRAGMENT_NAME);
  }

  protected BaseFragment newFragment() {
    if (TextUtils.isEmpty(mFragmentName)) {
      return null;
    }
    return (BaseFragment) Fragment.instantiate(this, mFragmentName, getIntent().getExtras());
  }
}
