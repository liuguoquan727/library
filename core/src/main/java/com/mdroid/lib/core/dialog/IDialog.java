package com.mdroid.lib.core.dialog;

import android.view.View;
import com.orhanobut.dialogplus.DialogPlus;

public interface IDialog {
  /**
   * Interface used to allow the creator of a dialog to run some code when an
   * item on the dialog is clicked..
   */
  public interface OnClickListener {
    void onClick(DialogPlus dialog, View view);
  }

  public interface OnChooseListener {
    void onChoose(DialogPlus dialog, View view, Object o);
  }
}
