package android.support.v4.app;

import java.util.List;

public class FragmentHelper {
  public static void noteStateNotSaved(FragmentActivity activity) {
    activity.mFragments.noteStateNotSaved();
  }

  public static List<Fragment> getActive(FragmentActivity activity) {
    return activity.mFragments.getActiveFragments(null);
  }

  public static int getIndex(Fragment fragment) {
    return fragment.mIndex;
  }

  public static List<Fragment> getChildActive(Fragment fragment) {
    return fragment.getChildFragmentManager().getFragments();
  }
}