package com.mdroid.lib.core.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.LayoutRes;
import com.mdroid.lib.core.R;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.DialogPlusBuilder;
import com.orhanobut.dialogplus.Holder;
import com.orhanobut.dialogplus.OnDismissListener;
import com.orhanobut.dialogplus.OnShowListener;
import java.security.InvalidParameterException;

/**
 * 从窗口侧边弹出的 Dialog; 默认从右边弹出
 */
public class SideDialog {
  private DialogPlus mDialog;

  private SideDialog(DialogPlus dialog) {
    mDialog = dialog;
  }

  public DialogPlus getDialog() {
    return mDialog;
  }

  public SideDialog show() {
    mDialog.show();
    return this;
  }

  public static class Builder {

    private final DialogPlusBuilder mDialogPlusBuilder;

    public Builder(Context context) {
      mDialogPlusBuilder = DialogPlus.newDialog(context)
          .setContentBackgroundResource(R.color.white)
          .setContentWidth(ViewGroup.LayoutParams.WRAP_CONTENT)
          .setContentHeight(ViewGroup.LayoutParams.MATCH_PARENT)
          .setGravity(Gravity.END);
    }

    public Builder(Context context, boolean cancelable) {
      mDialogPlusBuilder = DialogPlus.newDialog(context)
          .setContentBackgroundResource(R.color.white)
          .setContentWidth(ViewGroup.LayoutParams.WRAP_CONTENT)
          .setContentHeight(ViewGroup.LayoutParams.MATCH_PARENT)
          .setGravity(Gravity.END)
          .setCancelable(cancelable);
    }

    /**
     * 设置背景颜色
     */
    public Builder contentBackgroundResource(int resourceId) {
      mDialogPlusBuilder.setContentBackgroundResource(resourceId);
      return this;
    }

    /**
     * 设置点击返回键或对话框外部不能退出对话框
     */
    public Builder noncancelable() {
      mDialogPlusBuilder.setCancelable(false);
      return this;
    }

    public Builder cancelable(boolean cancelable) {
      mDialogPlusBuilder.setCancelable(cancelable);
      return this;
    }

    public Builder gravity(int gravity) {
      if (gravity != Gravity.START && gravity != Gravity.END) {
        throw new InvalidParameterException("gravity must be Gravity.LEFT or Gravity.RIGHT");
      }
      mDialogPlusBuilder.setGravity(gravity);
      return this;
    }

    public Builder width(int width) {
      mDialogPlusBuilder.setContentWidth(width);
      return this;
    }

    public Builder height(int width) {
      mDialogPlusBuilder.setContentHeight(width);
      return this;
    }

    public Builder header(@LayoutRes int layoutRes) {
      mDialogPlusBuilder.setHeader(layoutRes);
      return this;
    }

    public Builder header(View view) {
      mDialogPlusBuilder.setHeader(view);
      return this;
    }

    public Builder content(Holder holder) {
      mDialogPlusBuilder.setContentHolder(holder);
      return this;
    }

    /**
     * @param layoutRes 内容布局资源
     */
    public Builder content(@LayoutRes int layoutRes) {
      mDialogPlusBuilder.setContentHolder(new CustomViewHolder(layoutRes));

      return this;
    }

    public Builder content(View view) {
      mDialogPlusBuilder.setContentHolder(new CustomViewHolder(view));
      return this;
    }

    public Builder footer(@LayoutRes int layoutRes) {
      mDialogPlusBuilder.setFooter(layoutRes);
      return this;
    }

    public Builder footer(View view) {
      mDialogPlusBuilder.setFooter(view);
      return this;
    }

    public Builder onShowListener(OnShowListener listener) {
      mDialogPlusBuilder.setOnShowListener(listener);
      return this;
    }

    public Builder onDismissListener(OnDismissListener listener) {
      mDialogPlusBuilder.setOnDismissListener(listener);
      return this;
    }

    public SideDialog build() {
      return new SideDialog(mDialogPlusBuilder.create());
    }
  }
}
