package com.mdroid.lib.core.eventbus;

public class EventBusEvent {
  private int mType;
  private Object mExtra;

  public EventBusEvent(int type) {
    this(type, null);
  }

  public EventBusEvent(int type, Object extra) {
    this.mType = type;
    this.mExtra = extra;
  }

  public int getType() {
    return mType;
  }

  public Object getExtra() {
    return mExtra;
  }

  public interface INotify {
    void onNotify(EventBusEvent event);
  }
}
