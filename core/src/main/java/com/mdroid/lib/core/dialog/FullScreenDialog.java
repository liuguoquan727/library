package com.mdroid.lib.core.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.LayoutRes;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.DialogPlusBuilder;
import com.orhanobut.dialogplus.OnDismissListener;

/**
 * 全屏 Dialog
 */
public class FullScreenDialog {

  private final DialogPlus mDialog;

  private FullScreenDialog(DialogPlus dialog) {
    mDialog = dialog;
    mDialog.getWindow().getAttributes().dimAmount = 0.8f;
  }

  public DialogPlus getDialog() {
    return mDialog;
  }

  public FullScreenDialog show() {
    mDialog.show();
    return this;
  }

  public static class Builder {

    private final DialogPlusBuilder mDialogPlusBuilder;

    public Builder(Context context) {
      mDialogPlusBuilder = DialogPlus.newDialog(context)
          .setCancelable(true)
          .setGravity(Gravity.CENTER)
          .setContentWidth(ViewGroup.LayoutParams.MATCH_PARENT)
          .setContentHeight(ViewGroup.LayoutParams.MATCH_PARENT)
          .setMargin(0, 0, 0, 0);
    }

    /**
     * 设置点击返回键或对话框外部不能退出对话框
     */
    public Builder noncancelable() {
      mDialogPlusBuilder.setCancelable(false);
      return this;
    }

    public Builder onDismissListener(OnDismissListener listener) {
      mDialogPlusBuilder.setOnDismissListener(listener);
      return this;
    }

    /**
     * @param contentLayoutRes 内容布局资源
     */
    public Builder contentLayoutRes(@LayoutRes int contentLayoutRes) {
      mDialogPlusBuilder.setContentHolder(new CustomViewHolder(contentLayoutRes));
      return this;
    }

    public Builder contentView(View view) {
      mDialogPlusBuilder.setContentHolder(new CustomViewHolder(view));
      return this;
    }

    public Builder width(int width) {
      mDialogPlusBuilder.setContentWidth(width);
      return this;
    }

    public Builder height(int height) {
      mDialogPlusBuilder.setContentHeight(height);
      return this;
    }

    public Builder margin(int l, int t, int r, int b) {
      mDialogPlusBuilder.setMargin(l, t, r, b);
      return this;
    }

    public Builder gravity(int gravity) {
      mDialogPlusBuilder.setGravity(gravity);
      return this;
    }

    public Builder animationStyle(int animationStyle) {
      mDialogPlusBuilder.setAnimationStyle(animationStyle);
      return this;
    }

    public FullScreenDialog build() {
      return new FullScreenDialog(mDialogPlusBuilder.create());
    }
  }
}
