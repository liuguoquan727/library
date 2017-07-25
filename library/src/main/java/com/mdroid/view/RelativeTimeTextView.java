package com.mdroid.view;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import com.mdroid.R;

/**
 * A {@code TextView} that, given a reference time, renders that time as a time period relative to
 * the current time.
 *
 * @author Kiran Rao
 * @see #setReferenceTime(long)
 */
public class RelativeTimeTextView extends TextView {

  private long mReferenceTime;
  private String mPrefix;
  private String mSuffix;
  private UpdateTimeRunnable mUpdateTimeTask;

  public RelativeTimeTextView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context, attrs);
  }

  public RelativeTimeTextView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    init(context, attrs);
  }

  private void init(Context context, AttributeSet attrs) {
    mPrefix = "";
    mSuffix = "";
    mReferenceTime = -1L;
  }

  /**
   * Returns prefix
   */
  public String getPrefix() {
    return this.mPrefix;
  }

  /**
   * String to be attached before the reference time
   *
   * @param prefix Example:
   * [prefix] in XX minutes
   */
  public void setPrefix(String prefix) {
    this.mPrefix = prefix;
    updateTextDisplay();
  }

  /**
   * Returns suffix
   */
  public String getSuffix() {
    return this.mSuffix;
  }

  /**
   * String to be attached after the reference time
   *
   * @param suffix Example:
   * in XX minutes [suffix]
   */
  public void setSuffix(String suffix) {
    this.mSuffix = suffix;
    updateTextDisplay();
  }

  /**
   * Sets the reference time for this view. At any moment, the view will render a relative time
   * period relative to the time set here.
   * <p>
   * This value can also be set with the XML attribute {@code reference_time}
   *
   * @param referenceTime The timestamp (in milliseconds since epoch) that will be the reference
   * point for this view.
   */
  public void setReferenceTime(long referenceTime) {
    this.mReferenceTime = referenceTime;
        
        /*
         * Note that this method could be called when a row in a ListView is recycled.
         * Hence, we need to first stop any currently running schedules (for example from the recycled view.
         */
    stopTaskForPeriodicallyUpdatingRelativeTime();
        
        /*
         * Instantiate a new runnable with the new reference time
         */
    mUpdateTimeTask = new UpdateTimeRunnable(mReferenceTime);
        
        /*
         * Start a new schedule.
         */
    startTaskForPeriodicallyUpdatingRelativeTime();
        
        /*
         * Finally, update the text display.
         */
    updateTextDisplay();
  }

  private void updateTextDisplay() {
    if (this.mReferenceTime == -1L) {
      setText(R.string.unknown);
      return;
    }
    setText(mPrefix + getRelativeTimeDisplayString() + mSuffix);
  }

  private CharSequence getRelativeTimeDisplayString() {
    long now = System.currentTimeMillis();
    long difference = now - mReferenceTime;
    return (difference <= DateUtils.MINUTE_IN_MILLIS) ? getResources().getString(R.string.moment)
        : DateUtils.getRelativeTimeSpanString(mReferenceTime, now, DateUtils.MINUTE_IN_MILLIS,
            DateUtils.FORMAT_ABBREV_RELATIVE);
  }

  @Override protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    startTaskForPeriodicallyUpdatingRelativeTime();
  }

  @Override protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    stopTaskForPeriodicallyUpdatingRelativeTime();
  }

  @Override protected void onVisibilityChanged(View changedView, int visibility) {
    super.onVisibilityChanged(changedView, visibility);
    if (visibility == GONE || visibility == INVISIBLE) {
      stopTaskForPeriodicallyUpdatingRelativeTime();
    } else {
      startTaskForPeriodicallyUpdatingRelativeTime();
    }
  }

  private void startTaskForPeriodicallyUpdatingRelativeTime() {
    post(mUpdateTimeTask);
  }

  private void stopTaskForPeriodicallyUpdatingRelativeTime() {
    removeCallbacks(mUpdateTimeTask);
  }

  @Override public Parcelable onSaveInstanceState() {
    Parcelable superState = super.onSaveInstanceState();
    SavedState ss = new SavedState(superState);
    ss.referenceTime = mReferenceTime;
    return ss;
  }

  @Override public void onRestoreInstanceState(Parcelable state) {
    if (!(state instanceof SavedState)) {
      super.onRestoreInstanceState(state);
      return;
    }

    SavedState ss = (SavedState) state;
    mReferenceTime = ss.referenceTime;
    super.onRestoreInstanceState(ss.getSuperState());
  }

  public static class SavedState extends BaseSavedState {

    public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
      public SavedState createFromParcel(Parcel in) {
        return new SavedState(in);
      }

      public SavedState[] newArray(int size) {
        return new SavedState[size];
      }
    };
    private long referenceTime;

    public SavedState(Parcelable superState) {
      super(superState);
    }

    private SavedState(Parcel in) {
      super(in);
      referenceTime = in.readLong();
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
      super.writeToParcel(dest, flags);
      dest.writeLong(referenceTime);
    }
  }

  private class UpdateTimeRunnable implements Runnable {

    private long mRefTime;

    UpdateTimeRunnable(long refTime) {
      this.mRefTime = refTime;
    }

    @Override public void run() {
      long difference = Math.abs(System.currentTimeMillis() - mRefTime);
      long interval = DateUtils.MINUTE_IN_MILLIS;
      if (difference > DateUtils.WEEK_IN_MILLIS) {
        interval = DateUtils.WEEK_IN_MILLIS;
      } else if (difference > DateUtils.DAY_IN_MILLIS) {
        interval = DateUtils.DAY_IN_MILLIS;
      } else if (difference > DateUtils.HOUR_IN_MILLIS) {
        interval = DateUtils.HOUR_IN_MILLIS;
      }
      updateTextDisplay();
      postDelayed(this, interval);
    }
  }
}