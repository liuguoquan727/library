package com.mdroid.app;

import android.content.Context;
import android.os.Bundle;
import android.widget.TabHost;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import java.util.ArrayList;

public class TabManager {
  private static final String SAVE_MANAGER_TAB_CURRENT = "save_manager_tab_current";
  private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();
  private Context mContext;
  private FragmentManager mFragmentManager;
  private int mContainerId;
  private TabHost.OnTabChangeListener mOnTabChangeListener;
  private TabInfo mLastTab;

  public TabManager(Context context, FragmentManager fragmentManager, int containerId) {
    this.mContext = context;
    this.mFragmentManager = fragmentManager;
    this.mContainerId = containerId;
  }

  public void setOnTabChangedListener(TabHost.OnTabChangeListener l) {
    mOnTabChangeListener = l;
  }

  public TabManager addTab(String tag, Class<?> clss, Bundle args) {
    return addTab(tag, clss, 0, 0, args);
  }

  public TabManager addTab(String tag, Class<?> clss, int inAnimation, int outAnimation,
      Bundle args) {
    TabInfo info = new TabInfo(tag, clss, inAnimation, outAnimation, args);
    info.fragment = mFragmentManager.findFragmentByTag(info.tag);
    mTabs.add(info);
    return this;
  }

  public Fragment getCurrentFragment() {
    return mLastTab == null ? null : mLastTab.fragment;
  }

  public String getCurrentTag() {
    return mLastTab == null ? null : mLastTab.tag;
  }

  public void changeTab(String tabTag) {
    changeTab(tabTag, Animate.NONE);
  }

  public void changeTab(String tabTag, Animate animate) {
    FragmentTransaction ft = doTabChanged(tabTag, null, animate);
    if (ft != null) {
      ft.commit();
    }
    if (mOnTabChangeListener != null) {
      mOnTabChangeListener.onTabChanged(tabTag);
    }
  }

  private FragmentTransaction doTabChanged(String tabTag, FragmentTransaction ft, Animate animate) {
    TabInfo newTab = null;
    for (int i = 0; i < mTabs.size(); i++) {
      TabInfo tab = mTabs.get(i);
      if (tab.tag.equals(tabTag)) {
        newTab = tab;
      }
    }
    if (newTab == null) {
      throw new IllegalStateException("No tab known for tag " + tabTag);
    }
    if (mLastTab != newTab) {
      if (ft == null) {
        ft = mFragmentManager.beginTransaction();
      }
      if (mLastTab != null) {
        if (mLastTab.fragment != null) {
          if (animate == Animate.OUT) {
            ft.setCustomAnimations(mLastTab.inAnimation, mLastTab.outAnimation);
          }
          ft.detach(mLastTab.fragment);
        }
      }
      if (newTab.fragment == null) {
        newTab.fragment = Fragment.instantiate(mContext, newTab.clss.getName(), newTab.args);
        if (animate == Animate.IN) {
          ft.setCustomAnimations(newTab.inAnimation, newTab.outAnimation);
        }
        ft.add(mContainerId, newTab.fragment, newTab.tag);
      } else {
        if (animate == Animate.IN) {
          ft.setCustomAnimations(newTab.inAnimation, newTab.outAnimation);
        }
        ft.attach(newTab.fragment);
      }

      mLastTab = newTab;
    }
    return ft;
  }

  public void detach() {
    detach(Animate.NONE);
  }

  public void detach(Animate animate) {
    if (mLastTab != null) {
      if (mLastTab.fragment != null) {
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        if (animate == Animate.OUT) {
          ft.setCustomAnimations(mLastTab.inAnimation, mLastTab.outAnimation);
        }
        ft.detach(mLastTab.fragment).commit();
      }
      mLastTab = null;
      if (mOnTabChangeListener != null) {
        mOnTabChangeListener.onTabChanged(getCurrentTag());
      }
    }
  }

  public void destroy(String tabTag) {
    TabInfo newTab = null;
    for (int i = 0; i < mTabs.size(); i++) {
      TabInfo tab = mTabs.get(i);
      if (tab.tag.equals(tabTag)) {
        newTab = tab;
      }
    }
    if (newTab == null) {
      throw new IllegalStateException("No tab known for tag " + tabTag);
    }
    if (newTab.fragment != null) {
      FragmentTransaction ft = mFragmentManager.beginTransaction();
      ft.remove(newTab.fragment).commit();
      newTab.fragment = null;
    }
    if (newTab == mLastTab) {
      mLastTab = null;
      if (mOnTabChangeListener != null) {
        mOnTabChangeListener.onTabChanged(getCurrentTag());
      }
    }
  }

  public void onSaveInstanceState(Bundle outState) {
    if (mLastTab != null) {
      outState.putString(SAVE_MANAGER_TAB_CURRENT, mLastTab.tag);
    }
  }

  /**
   * Restore state when killed.
   */
  public void restoreState(Bundle savedInstanceState) {
    String tabId = savedInstanceState.getString(SAVE_MANAGER_TAB_CURRENT);
    restoreState(tabId);
  }

  public void restoreState(String tabId) {
    TabInfo newTab = null;
    for (int i = 0; i < mTabs.size(); i++) {
      TabInfo tab = mTabs.get(i);
      tab.fragment = mFragmentManager.findFragmentByTag(tab.tag);
      if (tab.tag.equals(tabId)) {
        newTab = tab;
      }
    }
    mLastTab = newTab;
    if (mOnTabChangeListener != null) {
      mOnTabChangeListener.onTabChanged(getCurrentTag());
    }
  }

  public enum Animate {
    NONE(0), IN(1), OUT(2);

    private int type;

    Animate(int type) {
      this.type = type;
    }

    public int getType() {
      return type;
    }
  }

  static final class TabInfo {
    private final String tag;
    private final Class<?> clss;
    private final Bundle args;
    private final int inAnimation;
    private final int outAnimation;
    private Fragment fragment;

    TabInfo(String _tag, Class<?> _class, int _inAnimation, int _outAnimation, Bundle _args) {
      tag = _tag;
      clss = _class;
      inAnimation = _inAnimation;
      outAnimation = _outAnimation;
      args = _args;
    }
  }
}
