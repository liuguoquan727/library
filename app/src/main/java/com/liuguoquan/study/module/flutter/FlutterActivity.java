package com.liuguoquan.study.module.flutter;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.liuguoquan.study.R;
import com.liuguoquan.study.base.AppBaseActivity;
import com.mdroid.lib.core.base.BasePresenter;
import com.mdroid.lib.core.base.Status;

import butterknife.BindView;
import io.flutter.facade.Flutter;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.view.FlutterView;

/**
 * Description:
 *
 * <p>Created by liuguoquan on 2019/3/6 11:12.
 */
public class FlutterActivity extends AppBaseActivity {

    @BindView(R.id.main_layout)
    LinearLayout mMainLayout;

    @BindView(R.id.tv_flutter)
    TextView mFlutterTextView;

    private final String CHANEL = "samples.flutter.io/toAndroid";
    private final String CHANEL_Flutter = "samples.flutter.io/toFlutter";
    /** Android 接收 flutter 参数 */
    private MethodChannel methodChannel;
    /** flutter 接收 Android 餐宿 */
    private EventChannel mEventChannel;

    private EventChannel.EventSink mSink;
    private FlutterView flutterView;

    @Override
    protected Status getCurrentStatus() {
        return null;
    }

    @Override
    protected String getPageTitle() {
        return null;
    }

    @Override
    protected BasePresenter initPresenter() {
        return null;
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_flutter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        flutterView = Flutter.createView(this, getLifecycle(), "route1");
        FrameLayout.LayoutParams layout =
            new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mMainLayout.addView(flutterView, layout);

        mFlutterTextView.setVisibility(View.GONE);

        methodChannel = new MethodChannel(flutterView, CHANEL);
        methodChannel.setMethodCallHandler(
            new MethodChannel.MethodCallHandler() {
                @Override
                public void onMethodCall(MethodCall call, MethodChannel.Result result) {
                    if (call.method.equals("getBatteryLevel")) {
                        int batteryLevel = getBatteryLevel();
                        if (batteryLevel != -1) {
                            result.success(batteryLevel);
                        } else {
                            result.error("UNAVAILABLE", "Battery level not available.", null);
                        }
                    } else {
                        result.notImplemented();
                    }
                }
            });

        mEventChannel = new EventChannel(flutterView, CHANEL_Flutter);
        mEventChannel.setStreamHandler(
            new EventChannel.StreamHandler() {
                @Override
                public void onListen(Object o, EventChannel.EventSink eventSink) {
                    Log.d("lgq", "onListen:");
                    mSink = eventSink;
                }

                @Override
                public void onCancel(Object o) {
                    Log.d("lgq", "onCancel: ");
                }
            });

        mFlutterTextView.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 向flutter传事件
                    if (mSink != null) {
                        mSink.success("I am from Android");
                    }

                    // 调用flutter方法
                    // methodChannel.invokeMethod("aaa", "cccc");
                }
            });
    }

    private int getBatteryLevel() {
        int batteryLevel = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            BatteryManager batteryManager =
                (BatteryManager) getSystemService(Context.BATTERY_SERVICE);
            batteryLevel = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        } else {
            Intent intent =
                new ContextWrapper(this)
                    .registerReceiver(
                        null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            batteryLevel =
                intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                    * 100
                    / intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        }

        return batteryLevel;
    }

    @Override
    public void onBackPressed() {
        if (mFlutterTextView != null) {
            flutterView.popRoute();
        } else {
            finish();
        }
    }
}
