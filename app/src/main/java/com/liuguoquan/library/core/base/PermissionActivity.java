package com.liuguoquan.library.core.base;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.view.View;
import com.liuguoquan.library.core.R;
import com.mdroid.lib.core.dialog.CenterDialog;
import com.mdroid.lib.core.dialog.IDialog;
import com.orhanobut.dialogplus.DialogPlus;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

/**
 * Created by liuguoquan on 2017/7/27.
 */

@RuntimePermissions
public abstract class PermissionActivity<V extends AppBaseView, T extends AppBaseActivityPresenter<V>>
    extends AppBaseActivity<V, T> {

  @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    PermissionActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode,
        grantResults);
  }

  //相机权限请求
  public void showCameraWithCheck() {
    PermissionActivityPermissionsDispatcher.showCameraWithCheck(this);
  }

  @NeedsPermission({ Manifest.permission.CAMERA }) public void showCamera() {
    // 需要重写
  }

  @OnShowRationale({ Manifest.permission.CAMERA }) void showRationaleForCamera(
      PermissionRequest request) {
    request.proceed();
  }

  @OnPermissionDenied({ Manifest.permission.CAMERA }) void showDeniedForCamera() {

  }

  @OnNeverAskAgain({ Manifest.permission.CAMERA }) void showNeverAskForCamera() {
    showSettingDialog("拍照");
  }

  //媒体文件权限请求
  public void showMediaSelectWithCheck(Bundle bundle, int requestCode) {
    PermissionActivityPermissionsDispatcher.showMediaSelectWithCheck(this, bundle, requestCode);
  }

  @NeedsPermission({ Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE })
  public void showMediaSelect(Bundle bundle, int requestCode) {
  }

  @OnShowRationale({ Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE })
  void showRationaleForMediaSelect(PermissionRequest request) {
    request.proceed();
  }

  @OnPermissionDenied({ Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE })
  void showDeniedForMediaSelect() {
  }

  @OnNeverAskAgain({ Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE })
  void showNeverAskForMediaSelect() {
    showSettingDialog("相机和存储");
  }

  //存储权限请求
  public void showWriteStorageWithCheck() {
    PermissionActivityPermissionsDispatcher.showWriteStorageWithCheck(this);
  }

  @NeedsPermission({ Manifest.permission.WRITE_EXTERNAL_STORAGE }) public void showWriteStorage() {
    // 需要重写
  }

  @OnShowRationale({ Manifest.permission.WRITE_EXTERNAL_STORAGE })
  void showRationaleForWriteStorage(PermissionRequest request) {
    request.proceed();
  }

  @OnPermissionDenied({ Manifest.permission.WRITE_EXTERNAL_STORAGE })
  void showDeniedForWriteStorage() {

  }

  @OnNeverAskAgain({ Manifest.permission.WRITE_EXTERNAL_STORAGE })
  void showNeverAskForWriteStorage() {
    showSettingDialog("存储");
  }

  //电话权限
  public void showReadPhoneWithCheck() {
    PermissionActivityPermissionsDispatcher.showReadPhoneWithCheck(this);
  }

  @NeedsPermission({ Manifest.permission.READ_PHONE_STATE }) public void showReadPhone() {
    // 需要重写
  }

  @OnShowRationale({ Manifest.permission.READ_PHONE_STATE }) void showRationaleForReadPhone(
      PermissionRequest request) {
    request.proceed();
  }

  @OnPermissionDenied({ Manifest.permission.READ_PHONE_STATE }) void showDeniedForReadPhone() {

  }

  @OnNeverAskAgain({ Manifest.permission.READ_PHONE_STATE }) void showNeverAskForReadPhone() {
    showSettingDialog("电话");
  }

  protected void showSettingDialog(String feature) {
    String name = getString(R.string.app_name);
    CenterDialog.create(this, "提示",
        String.format("在设置-应用-%s-权限中开启%s权限, 以正常使用%s功能", name, feature, name), "取消",
        new IDialog.OnClickListener() {
          @Override public void onClick(DialogPlus dialog, View view) {
            dialog.dismiss();
          }
        }, "去设置", new IDialog.OnClickListener() {
          @Override public void onClick(DialogPlus dialog, View view) {
            dialog.dismiss();
            try {
              Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
              intent.setData(Uri.parse(String.format("package:%s", getPackageName())));
              startActivity(intent);
            } catch (Exception e) {
              Intent intent = new Intent(Settings.ACTION_APPLICATION_SETTINGS);
              startActivity(intent);
            }
          }
        }).show();
  }
}
