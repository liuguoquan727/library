package com.orhanobut.dialogplus;

/**
 * Interface used to allow the creator of a dialog to run some code when the
 * dialog is canceled.
 * <p>
 * This will only be called when the dialog is canceled, if the creator
 * needs to know when it is dismissed in general, use
 * {@link OnDismissListener}.
 */
public interface OnCancelListener {
  void onCancel(DialogPlus dialog);
}
