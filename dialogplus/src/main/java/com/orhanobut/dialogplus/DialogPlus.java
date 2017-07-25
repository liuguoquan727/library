package com.orhanobut.dialogplus;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;

public class DialogPlus {

  private final Dialog dialog;
  /**
   * DialogPlus base layout root view
   */
  private final ViewGroup rootView;
  /**
   * DialogPlus content container which is a different layout rather than base layout
   */
  private final ViewGroup contentContainer;
  /**
   * Determines whether dialog should be dismissed by back button or touch in the black overlay
   */
  private final boolean isCancelable;
  /**
   * Content
   */
  private final Holder holder;
  /**
   * tag标签
   */
  private Object tag = null;
  /**
   * Listener to notify the user that dialog has been shown
   */
  private OnShowListener onShowListener;
  /**
   * Listener to notify the user that dialog has been dismissed
   */
  private OnDismissListener onDismissListener;
  /**
   * Listener to notify the user that dialog has been canceled
   */
  private OnCancelListener onCancelListener;
  /**
   * Called when the user touch on black overlay in order to dismiss the dialog
   */
  private final View.OnTouchListener onCancelableTouchListener = new View.OnTouchListener() {
    @Override public boolean onTouch(View v, MotionEvent event) {
      if (event.getAction() == MotionEvent.ACTION_DOWN) {
        if (onCancelListener != null) {
          onCancelListener.onCancel(DialogPlus.this);
        }
        dismiss();
      }
      return false;
    }
  };
  /**
   * Listener to notify back press
   */
  private OnBackPressListener onBackPressListener;

  DialogPlus(DialogPlusBuilder builder) {
    LayoutInflater layoutInflater = LayoutInflater.from(builder.getContext());

    Activity activity = (Activity) builder.getContext();

    holder = builder.getHolder();
    holder.setOnKeyListener(new View.OnKeyListener() {
      @Override public boolean onKey(View v, int keyCode, KeyEvent event) {
        switch (event.getAction()) {
          case KeyEvent.ACTION_UP:
            if (keyCode == KeyEvent.KEYCODE_BACK) {
              if (onBackPressListener != null) {
                onBackPressListener.onBackPressed(DialogPlus.this);
              }
              if (isCancelable) {
                onBackPressed(DialogPlus.this);
              }
              return true;
            }
            break;
          default:
            break;
        }
        return false;
      }
    });

    onShowListener = builder.getOnShowListener();
    onDismissListener = builder.getOnDismissListener();
    onCancelListener = builder.getOnCancelListener();
    onBackPressListener = builder.getOnBackPressListener();
    isCancelable = builder.isCancelable();

    final TypedValue outValue = new TypedValue();
    activity.getTheme().resolveAttribute(R.attr.dialogPlusStyle, outValue, true);
    int themeResId = outValue.resourceId;
    if (themeResId == 0) {
      themeResId = R.style.nothing;
    }
    dialog = new Dialog(activity, themeResId);
    TranslucentStatusCompat.requestTranslucentStatus(dialog);
    dialog.setOwnerActivity(activity);
    dialog.setContentView(R.layout.base_container);
    dialog.getWindow().setWindowAnimations(builder.getAnimationStyle());
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      dialog.getWindow().setNavigationBarColor(Color.TRANSPARENT);
    }
    dialog.setCancelable(builder.isCancelable());
    dialog.setCanceledOnTouchOutside(builder.isCancelable());

    rootView = (ViewGroup) dialog.findViewById(R.id.dialogplus_outmost_container);
    contentContainer = (ViewGroup) dialog.findViewById(R.id.dialogplus_content_container);
    contentContainer.setLayoutParams(builder.getContentParams());

    initContentView(layoutInflater, builder);
    initCancelable();
  }

  public static DialogPlusBuilder newDialog(Context context) {
    return new DialogPlusBuilder(context);
  }

  public void show() {
    if (isShowing()) {
      return;
    }
    dialog.show();
    if (onShowListener != null) onShowListener.onShow(this);

    contentContainer.requestFocus();
  }

  /**
   * It basically check if the rootView contains the dialog plus view.
   *
   * @return true if it contains
   */
  public boolean isShowing() {
    return dialog.isShowing();
  }

  /**
   * It is called when to dismiss the dialog, either by calling dismiss() method or with
   * cancellable
   */
  public void dismiss() {
    if (!dialog.isShowing()) {
      return;
    }
    if (onDismissListener != null) {
      onDismissListener.onDismiss(DialogPlus.this);
    }
    dialog.dismiss();
  }

  @SuppressWarnings("unused") public View findViewById(int resourceId) {
    return contentContainer.findViewById(resourceId);
  }

  public View getHeaderView() {
    return holder.getHeader();
  }

  public View getFooterView() {
    return holder.getFooter();
  }

  public View getHolderView() {
    return holder.getInflatedView();
  }

  public ViewGroup getContainerView() {
    return contentContainer;
  }

  /**
   * It is called in order to create content
   */
  private void initContentView(LayoutInflater inflater, DialogPlusBuilder builder) {
    View contentView = createView(inflater);
    holder.addHeader(builder.getHeaderView());
    holder.addFooter(builder.getFooterView());
    int[] margin = builder.getContentMargin();
    int[] padding = builder.getContentPadding();
    FrameLayout.LayoutParams params =
        new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT);
    params.setMargins(margin[0], margin[1], margin[2], margin[3]);
    contentView.setLayoutParams(params);
    contentView.setPadding(padding[0], padding[1], padding[2], padding[3]);
    contentContainer.addView(contentView);
  }

  /**
   * It is called to set whether the dialog is cancellable by pressing back button or
   * touching the black overlay
   */
  private void initCancelable() {
    if (!isCancelable) {
      return;
    }
    rootView.setOnTouchListener(onCancelableTouchListener);
  }

  /**
   * it is called when the content view is created
   *
   * @param inflater used to inflate the content of the dialog
   * @return any view which is passed
   */
  private View createView(LayoutInflater inflater) {
    return holder.getView(inflater, contentContainer);
  }

  /**
   * Dismiss the dialog when the user press the back button
   *
   * @param dialogPlus is the current dialog
   */
  public void onBackPressed(DialogPlus dialogPlus) {
    if (onCancelListener != null) {
      onCancelListener.onCancel(DialogPlus.this);
    }
    dismiss();
  }

  public Window getWindow() {
    return dialog.getWindow();
  }

  public Object getTag() {
    return tag;
  }

  public void setTag(Object tag) {
    this.tag = tag;
  }
}
