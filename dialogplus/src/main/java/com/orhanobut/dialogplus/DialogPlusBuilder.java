package com.orhanobut.dialogplus;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import java.util.Arrays;

public class DialogPlusBuilder {
  private static final int INVALID = -1;

  private final int[] margin = new int[4];
  private final int[] padding = new int[4];
  private final FrameLayout.LayoutParams params =
      new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
          ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM);

  private Context context;
  private View footerView;
  private View headerView;
  private Holder holder;
  private int gravity = Gravity.BOTTOM;
  private OnShowListener onShowListener;
  private OnDismissListener onDismissListener;
  private OnCancelListener onCancelListener;
  private OnBackPressListener onBackPressListener;

  private boolean isCancelable = true;
  private int contentBackgroundResource;
  private int headerViewResourceId = INVALID;
  private int footerViewResourceId = INVALID;
  private int animationStyle = INVALID;

  private DialogPlusBuilder() {
  }

  /**
   * Initialize the builder with a valid context in order to inflate the dialog
   */
  DialogPlusBuilder(Context context) {
    if (context == null) {
      throw new NullPointerException("Context may not be null");
    }
    this.context = context;
    Arrays.fill(margin, INVALID);
  }

  /**
   * Set the footer view using the id of the layout resource
   */
  public DialogPlusBuilder setFooter(int resourceId) {
    this.footerViewResourceId = resourceId;
    return this;
  }

  /**
   * Set the footer view using a view
   */
  public DialogPlusBuilder setFooter(View view) {
    this.footerView = view;
    return this;
  }

  /**
   * Set the header view using the id of the layout resource
   */
  public DialogPlusBuilder setHeader(int resourceId) {
    this.headerViewResourceId = resourceId;
    return this;
  }

  /**
   * Set the header view using a view
   */
  public DialogPlusBuilder setHeader(View view) {
    this.headerView = view;
    return this;
  }

  /**
   * Set the content of the dialog by passing one of the provided Holders
   */
  public DialogPlusBuilder setContentHolder(Holder holder) {
    this.holder = holder;
    return this;
  }

  /**
   * Use setBackgroundResource
   */
  @Deprecated public DialogPlusBuilder setBackgroundColorResId(int resourceId) {
    return setContentBackgroundResource(resourceId);
  }

  /**
   * Set the gravity you want the dialog to have among the ones that are provided
   */
  public DialogPlusBuilder setGravity(int gravity) {
    this.gravity = gravity;
    params.gravity = gravity;
    return this;
  }

  /**
   * Add margins to your dialog. They are set to 0 except when gravity is center. In that case
   * basic
   * margins
   * are applied
   */
  public DialogPlusBuilder setMargin(int left, int top, int right, int bottom) {
    this.margin[0] = left;
    this.margin[1] = top;
    this.margin[2] = right;
    this.margin[3] = bottom;
    return this;
  }

  /**
   * Set paddings for the dialog content
   */
  public DialogPlusBuilder setPadding(int left, int top, int right, int bottom) {
    this.padding[0] = left;
    this.padding[1] = top;
    this.padding[2] = right;
    this.padding[3] = bottom;
    return this;
  }

  public DialogPlusBuilder setContentHeight(int height) {
    params.height = height;
    return this;
  }

  public DialogPlusBuilder setContentWidth(int width) {
    params.width = width;
    return this;
  }

  /**
   * Create the dialog using this builder
   */
  public DialogPlus create() {
    if (holder == null) {
      throw new IllegalStateException("Holder must be set.");
    }
    holder.setBackgroundResource(getContentBackgroundResource());
    return new DialogPlus(this);
  }

  public View getFooterView() {
    return Utils.getView(context, holder.getFooterContainer(), footerViewResourceId, footerView);
  }

  public View getHeaderView() {
    return Utils.getView(context, holder.getHeaderContainer(), headerViewResourceId, headerView);
  }

  public Holder getHolder() {
    return holder;
  }

  public Context getContext() {
    return context;
  }

  public int getAnimationStyle() {
    return (animationStyle == INVALID) ? Utils.getAnimationStyle(this.gravity) : animationStyle;
  }

  /**
   * Customize the in animation by passing an animation resource
   */
  public DialogPlusBuilder setAnimationStyle(int animationStyle) {
    this.animationStyle = animationStyle;
    return this;
  }

  public FrameLayout.LayoutParams getContentParams() {
    return params;
  }

  public boolean isCancelable() {
    return isCancelable;
  }

  /**
   * Define if the dialog is cancelable and should be closed when back pressed or click outside is
   * pressed
   */
  public DialogPlusBuilder setCancelable(boolean isCancelable) {
    this.isCancelable = isCancelable;
    return this;
  }

  public int[] getContentMargin() {
    int minimumMargin =
        context.getResources().getDimensionPixelSize(R.dimen.dialogplus_default_center_margin);
    for (int i = 0; i < margin.length; i++) {
      margin[i] = getMargin(this.gravity, margin[i], minimumMargin);
    }
    return margin;
  }

  public int[] getContentPadding() {
    return padding;
  }

  public int getContentBackgroundResource() {
    return contentBackgroundResource;
  }

  /**
   * Set background color for your dialog. If no resource is passed 'white' will be used
   */
  public DialogPlusBuilder setContentBackgroundResource(int resourceId) {
    this.contentBackgroundResource = resourceId;
    return this;
  }

  /**
   * Get margins if provided or assign default values based on gravity
   *
   * @param gravity the gravity of the dialog
   * @param margin the value defined in the builder
   * @param minimumMargin the minimum margin when gravity center is selected
   * @return the value of the margin
   */
  private int getMargin(int gravity, int margin, int minimumMargin) {
    switch (gravity) {
      case Gravity.CENTER:
        return (margin == INVALID) ? minimumMargin : margin;
      default:
        return (margin == INVALID) ? 0 : margin;
    }
  }

  public OnShowListener getOnShowListener() {
    return onShowListener;
  }

  public DialogPlusBuilder setOnShowListener(OnShowListener listener) {
    this.onShowListener = listener;
    return this;
  }

  public OnDismissListener getOnDismissListener() {
    return onDismissListener;
  }

  public DialogPlusBuilder setOnDismissListener(OnDismissListener listener) {
    this.onDismissListener = listener;
    return this;
  }

  public OnCancelListener getOnCancelListener() {
    return onCancelListener;
  }

  public DialogPlusBuilder setOnCancelListener(OnCancelListener listener) {
    this.onCancelListener = listener;
    return this;
  }

  public OnBackPressListener getOnBackPressListener() {
    return onBackPressListener;
  }

  public DialogPlusBuilder setOnBackPressListener(OnBackPressListener listener) {
    this.onBackPressListener = listener;
    return this;
  }
}
