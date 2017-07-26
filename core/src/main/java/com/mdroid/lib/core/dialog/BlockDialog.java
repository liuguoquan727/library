package com.mdroid.lib.core.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import com.mdroid.lib.core.R;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

/**
 * Dialog for block user operation.
 */
public class BlockDialog {
  private final DialogPlus mDialog;

  public BlockDialog(DialogPlus dialog) {
    mDialog = dialog;
  }

  public static BlockDialog create(Context context) {
    return create(context, false);
  }

  public static BlockDialog create(Context context, boolean cancelable) {
    return create(context, cancelable, null);
  }

  public static BlockDialog create(Context context, View view) {
    return create(context, false, view);
  }

  public static BlockDialog create(Context context, boolean cancelable, View view) {
    return new BlockDialog(DialogPlus.newDialog(context)
        .setContentHolder(view == null ? new ViewHolder(R.layout.lib_dialog_base_content_block)
            : new ViewHolder(view))
        .setCancelable(cancelable)
        .setGravity(Gravity.CENTER)
        .setContentWidth(ViewGroup.LayoutParams.WRAP_CONTENT)
        .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
        .create());
  }

  public BlockDialog show() {
    mDialog.show();
    return this;
  }

  public void dismiss() {
    mDialog.dismiss();
  }

  public DialogPlus dialog() {
    return mDialog;
  }
}
