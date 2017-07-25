package com.mdroid.view;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.mdroid.R;

/**
 * Dialog for block user operation.
 */
@Deprecated public class BlockDialog extends Dialog {
  public static final int SUCCESS = 0;
  public static final int WARNING = 1;
  public static final int DANGER = 2;
  private View mProgressLayout;
  private ProgressBar mProgressBar;
  private TextView mContent;
  private TextView mResult;
  private final Runnable mCancel = new Runnable() {
    @Override public void run() {
      dismiss();
    }
  };

  private BlockDialog(Context context) {
    this(context, R.style.dialogTheme);
  }

  private BlockDialog(Context context, int theme) {
    super(context, theme);
    init();
  }

  public static BlockDialog create(Context context) {
    return new BlockDialog(context);
  }

  private void init() {
    super.setContentView(R.layout.dialog_block);
    mProgressLayout = findViewById(R.id.progress);
    mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
    mContent = (TextView) findViewById(R.id.content);
    mResult = (TextView) findViewById(R.id.result);

    setCancelable(false);
    setCanceledOnTouchOutside(false);
  }

  public BlockDialog setContent(int resId) {
    mContent.setText(resId);
    return this;
  }

  public BlockDialog setContent(int resId, Object... objects) {
    mContent.setText(getContext().getResources().getString(resId, objects));
    return this;
  }

  public BlockDialog setContent(String content) {
    mContent.setText(content);
    return this;
  }

  public BlockDialog pop() {
    show();
    return this;
  }

  public void setResult(String result) {
    setResult(result, SUCCESS);
  }

  public void setResult(String result, int status) {
    mResult.setText(result);
    mResult.setTextColor(getContext().getResources()
        .getColor(status == DANGER ? R.color.danger
            : status == WARNING ? R.color.warning : R.color.white));
    Animation fadeIn = AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in);
    Animation fadeOut = AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_out);
    mResult.startAnimation(fadeIn);
    mResult.setVisibility(View.VISIBLE);
    mProgressLayout.startAnimation(fadeOut);
    mProgressLayout.setVisibility(View.GONE);
    mResult.postDelayed(mCancel, status == DANGER ? 4000 : status == WARNING ? 3000 : 1500);
  }

  @Override public void dismiss() {
    mResult.removeCallbacks(mCancel);
    super.dismiss();
  }

  @Override public void setContentView(View view) {
    throw new IllegalStateException("Can not call setContentView method");
  }

  @Override public void setContentView(int layoutResID) {
    throw new IllegalStateException("Can not call setContentView method");
  }

  @Override public void setContentView(View view, ViewGroup.LayoutParams params) {
    throw new IllegalStateException("Can not call setContentView method");
  }
}
