package com.mdroid.lib.core.dialog;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.chargerlink.lib.core.R;
import com.jakewharton.rxbinding2.view.RxView;
import com.mdroid.utils.AndroidUtils;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.DialogPlusBuilder;
import com.orhanobut.dialogplus.OnDismissListener;
import com.orhanobut.dialogplus.OnShowListener;
import com.orhanobut.dialogplus.ViewHolder;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 显示在窗口中间的 Dialog
 */
public class CenterDialog {
  private static final int DEFAULT_CLICK_DURATION = 500;// milliseconds
  private final DialogPlus mDialog;

  private final ImageView mClose;

  private final TextView mContent;
  private final TextView mTitle;

  private final TextView mNegative;
  private final TextView mPositive;

  private CenterDialog(DialogPlus dialog) {
    mDialog = dialog;
    mClose = (ImageView) mDialog.findViewById(R.id.close);

    mTitle = (TextView) mDialog.findViewById(R.id.title);
    mContent = (TextView) mDialog.findViewById(R.id.content);

    mNegative = (TextView) mDialog.findViewById(R.id.negative);
    mPositive = (TextView) mDialog.findViewById(R.id.positive);

    if (mClose != null) {
      RxView.clicks(mClose)
          .throttleFirst(DEFAULT_CLICK_DURATION, TimeUnit.MILLISECONDS)
          .subscribe(new Consumer<Object>() {
            @Override public void accept(@NonNull Object o) throws Exception {
              mDialog.dismiss();
            }
          });
    }
  }

  public static CenterDialog create(Context context, String title, String content, String negative,
      IDialog.OnClickListener negativeListener, String positive,
      IDialog.OnClickListener positiveListener) {
    return create(context, true, title, content, negative, negativeListener, positive,
        positiveListener);
  }

  public static CenterDialog create(Context context, boolean cancelable, String title,
      String content, String negative, IDialog.OnClickListener negativeListener, String positive,
      IDialog.OnClickListener positiveListener) {
    CenterDialog centerDialog =
        new Builder(context).footer().cancelable(cancelable).build().setContent(content);
    if (null != title) {
      centerDialog.setTitle(title);
    }
    if (!TextUtils.isEmpty(negative)) {
      centerDialog.setNegative(negative, negativeListener);
    }
    if (!TextUtils.isEmpty(positive)) {
      centerDialog.setPositive(positive, positiveListener);
    }
    return centerDialog;
  }

  public DialogPlus getDialog() {
    return mDialog;
  }

  public CenterDialog setCloseListener(final IDialog.OnClickListener listener) {
    mClose.setVisibility(View.VISIBLE);
    RxView.clicks(mClose)
        .throttleFirst(DEFAULT_CLICK_DURATION, TimeUnit.MILLISECONDS)
        .subscribe(new Consumer<Object>() {
          @Override public void accept(@NonNull Object obj) throws Exception {
            listener.onClick(mDialog, mClose);
          }
        });
    return this;
  }

  public CenterDialog setNegative(CharSequence text, final IDialog.OnClickListener listener) {
    mPositive.setBackgroundResource(R.drawable.lib_bg_border_corners_br5_transparent_gray);
    mNegative.setVisibility(View.VISIBLE);
    mNegative.setText(text);
    RxView.clicks(mNegative)
        .throttleFirst(DEFAULT_CLICK_DURATION, TimeUnit.MILLISECONDS)
        .subscribe(new Consumer<Object>() {
          @Override public void accept(@NonNull Object o) throws Exception {
            if (listener != null) {
              listener.onClick(mDialog, mNegative);
            } else {
              mDialog.dismiss();
            }
          }
        });
    return this;
  }

  public CenterDialog setPositive(CharSequence text, final IDialog.OnClickListener listener) {
    mPositive.setText(text);
    RxView.clicks(mPositive)
        .throttleFirst(DEFAULT_CLICK_DURATION, TimeUnit.MILLISECONDS)
        .subscribe(new Consumer<Object>() {
          @Override public void accept(@NonNull Object o) throws Exception {
            if (listener != null) {
              listener.onClick(mDialog, mPositive);
            } else {
              mDialog.dismiss();
            }
          }
        });
    return this;
  }

  public CenterDialog hideNegative() {
    mNegative.setVisibility(View.GONE);
    return this;
  }

  public TextView getContent() {
    return mContent;
  }

  public CenterDialog setContent(CharSequence text) {
    mContent.setText(text);
    return this;
  }

  public TextView getTitle() {
    return mTitle;
  }

  public CenterDialog setTitle(CharSequence text) {
    mTitle.setText(text);
    return this;
  }

  public CenterDialog hideTitle() {
    mTitle.setVisibility(View.GONE);
    return this;
  }

  public TextView getNegative() {
    return mNegative;
  }

  public TextView getPositive() {
    return mPositive;
  }

  public CenterDialog show() {
    mDialog.show();
    return this;
  }

  public static class Builder {

    private final DialogPlusBuilder mDialogPlusBuilder;

    public Builder(Context context) {
      mDialogPlusBuilder = DialogPlus.newDialog(context)
          .setContentBackgroundResource(R.drawable.lib_bg_border_corners5_white)
          .setContentWidth((int) (AndroidUtils.getWidth(context) * 0.75))
          .setMargin(0, -1, 0, -1)
          .setCancelable(true)
          .setGravity(Gravity.CENTER);
    }

    /**
     * 设置底部
     */
    public Builder footer() {
      mDialogPlusBuilder.setFooter(R.layout.lib_dialog_base_center_footer);
      return this;
    }

    public Builder cancelable(boolean cancelable) {
      mDialogPlusBuilder.setCancelable(cancelable);
      return this;
    }

    /**
     * 设置点击返回键或对话框外部不能退出对话框
     */
    public Builder noncancelable() {
      mDialogPlusBuilder.setCancelable(false);
      return this;
    }

    /**
     * @param contentLayoutRes 内容布局资源
     */
    public Builder contentLayoutRes(@LayoutRes int contentLayoutRes) {
      mDialogPlusBuilder.setContentHolder(new ViewHolder(contentLayoutRes));
      return this;
    }

    public Builder contentView(View view) {
      mDialogPlusBuilder.setContentHolder(new ViewHolder(view));
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

    public Builder margin(int left, int top, int right, int bottom) {
      mDialogPlusBuilder.setMargin(left, top, right, bottom);
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

    public CenterDialog build() {
      if (mDialogPlusBuilder.getHolder() == null) {
        mDialogPlusBuilder.setContentHolder(
            new ViewHolder(R.layout.lib_dialog_base_center_content));
      }
      return new CenterDialog(mDialogPlusBuilder.create());
    }
  }
}
