Share SDK
==============
1. 在 Manifest 中设置
```xml
<activity
    android:name="cn.sharesdk.wxapi.WXEntryActivity"
    android:launchMode="singleTop" />

<activity-alias
    android:name="yourpackage.wxapi.WXEntryActivity"
    android:exported="true"
    android:targetActivity="cn.sharesdk.wxapi.WXEntryActivity" />
    
<activity
    android:name="com.mob.tools.MobUIShell"
    android:configChanges="keyboardHidden|orientation|screenSize"
    android:screenOrientation="portrait"
    android:theme="@android:style/Theme.Translucent.NoTitleBar"
    android:windowSoftInputMode="stateHidden|adjustResize">
    <intent-filter>
        <!-- 修改 QQ APP ID -->
        <data android:scheme="tencent1104651235" />
        <action android:name="android.intent.action.VIEW" />

        <category android:name="android.intent.category.BROWSABLE" />
        <category android:name="android.intent.category.DEFAULT" />
    </intent-filter>
</activity>
```
2. 重写 /assets/ShareSDK.xml, 修改里面的 app key