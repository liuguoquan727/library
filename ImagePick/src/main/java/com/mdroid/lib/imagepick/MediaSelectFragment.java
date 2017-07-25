package com.mdroid.lib.imagepick;

import android.animation.Animator;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.mdroid.lib.imagepick.animation.HeightAnimation;
import com.mdroid.lib.imagepick.base.BaseFragment;
import com.mdroid.lib.imagepick.base.ContainerActivity;
import com.mdroid.lib.imagepick.utils.PermissionUtils;
import com.mdroid.lib.imagepick.utils.SDCardUtil;
import com.mdroid.lib.imagepick.utils.TranslucentStatusCompat;
import com.mdroid.lib.imagepick.utils.UIUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * 选择页面
 */
public class MediaSelectFragment extends BaseFragment {
  /** toolbar的颜色 */
  public static final String EXTRA_TOOL_BAR_COLOR = "tool_bar_color";
  /**
   * 选择结果，返回为 {@link ArrayList}&lt;{@link Resource}&gt; 或 {@link Resource}
   */
  public static final String EXTRA_RESULT = "select_result";
  /**
   * 最大图片选择次数，int类型
   */
  public static final String EXTRA_SELECT_COUNT = "max_select_count";
  /**
   * 图片选择模式，int类型
   */
  public static final String EXTRA_SELECT_MODE = "select_count_mode";
  /**
   * 是否显示相机，boolean类型
   */
  public static final String EXTRA_SHOW_CAMERA = "show_camera";
  /**
   * 默认选择的数据集
   */
  public static final String EXTRA_DEFAULT_SELECTED_LIST = "default_result";
  /**
   * 单选 (返回 ArrayList)
   */
  public static final int MODE_SINGLE = 0;
  /**
   * 多选 (返回 ArrayList)
   */
  public static final int MODE_MULTI = 1;
  /**
   * 剪切 (返回 Resource)
   */
  public static final int MODE_CROP = 2;
  public static final int MODE_CAMERA = 3;
  public static final String EXTRA_CROP_ASPECTX = "crop_aspectx";
  public static final String EXTRA_CROP_ASPECTY = "crop_aspecty";
  public static final String EXTRA_CROP_OUTPUTX = "crop_outputx";
  public static final String EXTRA_CROP_OUTPUTY = "crop_outputy";
  static final String PREVIEW_LIST = "preview_list";
  static final String PREVIEW_COMPLETE = "preview_complete";
  // Save when recycled
  private static final String CAMERA_FILE_PATH = "camera_file_path";
  private static final String CROP_FILE_PATH = "crop_file_path";
  // 请求加载系统照相机
  private static final int REQUEST_CAMERA = 100;
  // 预览
  private static final int REQUEST_PREVIEW = 101;
  // 剪切
  private static final int REQUEST_CROP = 102;
  // 不同loader定义
  private static final int LOADER_ALL = 0;
  private static final int LOADER_CATEGORY = 1;

  private int mToolbarColor;

  private GridView mList;
  private FrameLayout mListFrame;
  private TextView mPreview;
  private ImageView mTakePhoto;
  private ImageView mTag;
  private TextView mCount;
  private RelativeLayout mCountLayout;
  private TextView mComplete;
  private FrameLayout mBottomCapture;
  private View mImageFolderBackground;
  private ListView mListDirs;
  private FrameLayout mListDirsLayout;
  private FrameLayout mImageFolderLayout;

  // 结果数据
  private ArrayList<Resource> mSelectedResources;
  // 文件夹数据
  private ArrayList<Folder> mFolders = new ArrayList<>();
  private Folder mCurrentFolder;
  private ArrayList<Resource> mResources = new ArrayList<>();

  private ImageFolderAdapter mImageFolderAdapter;

  private int mMode = MODE_SINGLE;
  private boolean mIsShowCamera = false;

  private boolean mIsFromCameraBack = false;//是否从相机返回

  private ImageGridAdapter mMediaSelectAdapter;
  private LoaderManager.LoaderCallbacks<Cursor> mLoaderCallback =
      new LoaderManager.LoaderCallbacks<Cursor>() {

        private final String[] IMAGE_PROJECTION = {
            MediaStore.Images.Media.DATA, MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED, MediaStore.Images.Media.MIME_TYPE,
            MediaStore.Images.Media._ID
        };

        @Override public Loader<Cursor> onCreateLoader(int id, Bundle args) {
          if (id == LOADER_ALL) {
            CursorLoader cursorLoader =
                new CursorLoader(getActivity(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    IMAGE_PROJECTION, null, null, IMAGE_PROJECTION[2] + " DESC");
            return cursorLoader;
          } else if (id == LOADER_CATEGORY) {
            CursorLoader cursorLoader =
                new CursorLoader(getActivity(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    IMAGE_PROJECTION,
                    IMAGE_PROJECTION[0] + " like '%" + args.getString("path") + "%'", null,
                    IMAGE_PROJECTION[2] + " DESC");
            return cursorLoader;
          }

          return null;
        }

        @Override public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
          if (data != null) {
            ArrayList<Folder> folders = new ArrayList<>();
            int count = data.getCount();
            if (count > 0) {
              data.moveToFirst();
              do {
                String path = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]));
                String name = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]));
                long dateTime = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[2]));
                String mimeType = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[3]));

                long size = new File(path).length();
                if (size <= 0) continue;

                Resource resource = new Resource(path, name, 0, 0, size, dateTime, mimeType, false);
                // 获取文件夹名称
                File imageFile = new File(resource.getFilePath());
                File folderFile = imageFile.getParentFile();
                if (folderFile != null && !folderFile.getAbsolutePath()
                    .contains("/chargerlink/")) {//过滤掉/DIMC/chargerlink目录的图片（临时图片）
                  Folder folder = new Folder();
                  folder.name = folderFile.getName();
                  folder.path = folderFile.getAbsolutePath();
                  if (!folders.contains(Folder.FOLDER_ALL)) {
                    Folder folderAll = new Folder();
                    List<Resource> resourceList = new ArrayList<>();
                    resourceList.add(resource);
                    folderAll.images = resourceList;
                    folders.add(folderAll);
                  } else {
                    Folder f = folders.get(folders.indexOf(Folder.FOLDER_ALL));
                    f.images.add(resource);
                  }
                  if (!folders.contains(folder)) {
                    List<Resource> resourceList = new ArrayList<>();
                    resourceList.add(resource);
                    folder.images = resourceList;
                    folders.add(folder);
                  } else {
                    // 更新
                    Folder f = folders.get(folders.indexOf(folder));
                    f.images.add(resource);
                  }
                }
              } while (data.moveToNext());

              mFolders.clear();
              mFolders.addAll(folders);
              if (mCurrentFolder != null) {
                mCurrentFolder = mFolders.get(mFolders.indexOf(mCurrentFolder));
              } else {
                mCurrentFolder = mFolders.get(mFolders.indexOf(Folder.FOLDER_ALL));
              }
              mResources.clear();
              mResources.addAll(mCurrentFolder.images);
              mMediaSelectAdapter.setData(mCurrentFolder.images);
              if (isResumed()) {
                mImageFolderAdapter.notifyDataSetChanged();
              }
            }
          }
        }

        @Override public void onLoaderReset(Loader<Cursor> loader) {

        }
      };
  private File mCameraTmpFile;
  private File mCropTmpFile;
  private int mDesireImageCount;
  private TextView mHeaderTitle;
  private ImageView mHeaderIcon;

  public static Intent getImageFromCrop(Uri data, Uri out, int aspectX, int aspectY, int outputX,
      int outputY) {
    Intent intent = new Intent("com.android.camera.action.CROP");
    intent.setDataAndType(data, "image/*");
    intent.putExtra("output", out);
    intent.putExtra("crop", "true");
    intent.putExtra("aspectX", aspectX);// 裁剪框比例
    intent.putExtra("aspectY", aspectY);
    intent.putExtra("outputX", outputX);// 输出图片大小
    intent.putExtra("outputY", outputY);
    intent.putExtra("scale", true);// 去黑边
    intent.putExtra("scaleUpIfNeeded", true);// 去黑边
    intent.putExtra("return-data", false);// 不返回图片数据. 如果为 true 的话, 返回图片太大就会 crash
    return intent;
  }

  @Override protected void initData(Bundle savedInstanceState) {
    if (!PermissionUtils.hasSelfPermissions(getContext(), CAMERA, WRITE_EXTERNAL_STORAGE)) {
      toast(getString(R.string.image_pick_request_permission_desc, getApplicationName()));
      getActivity().finish();
      return;
    }
    TranslucentStatusCompat.requestTranslucentStatus(getActivity());
    if (savedInstanceState != null) {
      String path = savedInstanceState.getString(CAMERA_FILE_PATH);
      if (path != null) {
        mCameraTmpFile = new File(path);
      }
      path = savedInstanceState.getString(CROP_FILE_PATH);
      if (path != null) {
        mCropTmpFile = new File(path);
      }
    }
    // 图片选择模式
    Bundle bundle = getArguments();
    mToolbarColor = bundle.getInt(EXTRA_TOOL_BAR_COLOR, Color.WHITE);

    mMode = bundle.getInt(EXTRA_SELECT_MODE, MODE_SINGLE);

    // 选择图片数量
    mDesireImageCount = bundle.getInt(EXTRA_SELECT_COUNT);

    if (mMode == MODE_MULTI) {
      mSelectedResources =
          (ArrayList<Resource>) bundle.getSerializable(EXTRA_DEFAULT_SELECTED_LIST);
    } else if (mMode == MODE_CAMERA) {
      showCameraAction();
    }
    if (mSelectedResources == null) {
      mSelectedResources = new ArrayList<>();
    }

    // 是否显示照相机
    mIsShowCamera = bundle.getBoolean(EXTRA_SHOW_CAMERA, false);

    getLoaderManager().initLoader(LOADER_ALL, null, mLoaderCallback);
  }

  @Override protected int getContentView() {
    return R.layout.image_pick_content_base_media_select;
  }

  @Override protected void initView(View parent) {
    findView(parent);
    getStatusBar().setBackgroundColor(mToolbarColor);
    Toolbar toolbar = getToolBar();
    toolbar.setBackgroundColor(mToolbarColor);
    mInflater.inflate(R.layout.image_pick_header_base_photo_selecter, toolbar, true);
    mHeaderTitle = (TextView) toolbar.findViewById(R.id.header_title);
    mHeaderTitle.setText("所有图片");
    mHeaderIcon = (ImageView) toolbar.findViewById(R.id.header_icon);
    View headerCenter = toolbar.findViewById(R.id.header_center);
    headerCenter.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        initListDirPopupWindow();
      }
    });
    View headerLeft = toolbar.findViewById(R.id.header_left);
    headerLeft.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        getActivity().onBackPressed();
      }
    });

    updateUI();
    mImageFolderAdapter = new ImageFolderAdapter(getActivity(), mFolders);
    mMediaSelectAdapter =
        new ImageGridAdapter(getActivity(), mResources, mSelectedResources, mIsShowCamera, mMode);
    mList.setAdapter(mMediaSelectAdapter);
    mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (mMediaSelectAdapter.isShowCamera()) {
          // 如果显示照相机，则第一个Grid显示为照相机，处理特殊逻辑
          if (i == 0) {
            showCameraAction();
          } else {
            // 正常操作
            Resource image = (Resource) adapterView.getAdapter().getItem(i);
            image.setIsSelected(true);
            selectResource(image);
          }
        } else {
          // 正常操作
          Resource image = (Resource) adapterView.getAdapter().getItem(i);
          image.setIsSelected(true);
          selectResource(image);
        }
      }
    });

    mTakePhoto.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        String name = getApplicationName();
        boolean hasPermission = UIUtil.requestPermissions(MediaSelectFragment.this,
            getString(R.string.image_pick_request_permission_desc, name), 11, CAMERA);
        if (hasPermission) {
          showCameraAction();
        }
      }
    });

    if (mMode == MODE_SINGLE || mMode == MODE_CROP) {
      mPreview.setVisibility(View.GONE);
      mComplete.setVisibility(View.GONE);
    }
  }

  public String getApplicationName() {
    PackageManager packageManager = null;
    ApplicationInfo applicationInfo = null;
    try {
      packageManager = getActivity().getApplicationContext().getPackageManager();
      applicationInfo = packageManager.getApplicationInfo(getActivity().getPackageName(), 0);
    } catch (PackageManager.NameNotFoundException e) {
      applicationInfo = null;
    }
    return (String) packageManager.getApplicationLabel(applicationInfo);
  }

  private void findView(View parent) {
    mList = (GridView) parent.findViewById(R.id.list);
    mListFrame = (FrameLayout) parent.findViewById(R.id.list_frame);
    mPreview = (TextView) parent.findViewById(R.id.preview);
    mTakePhoto = (ImageView) parent.findViewById(R.id.take_photo);
    mTag = (ImageView) parent.findViewById(R.id.tag);
    mCount = (TextView) parent.findViewById(R.id.count);
    mCountLayout = (RelativeLayout) parent.findViewById(R.id.count_layout);
    mComplete = (TextView) parent.findViewById(R.id.complete);
    mBottomCapture = (FrameLayout) parent.findViewById(R.id.bottom_capture);
    mImageFolderBackground = parent.findViewById(R.id.image_folder_background);
    mListDirs = (ListView) parent.findViewById(R.id.list_dirs);
    mListDirsLayout = (FrameLayout) parent.findViewById(R.id.list_dirs_layout);
    mImageFolderLayout = (FrameLayout) parent.findViewById(R.id.image_folder_layout);
  }

  @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    switch (requestCode) {
      case 11:
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          // Permission Granted
          showCameraAction();
        }
        break;
      default:
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
  }

  @Override public void onDestroy() {
    getLoaderManager().destroyLoader(LOADER_ALL);
    super.onDestroy();
  }

  private void toast(int resId) {
    Toast.makeText(getContext(), getString(resId), Toast.LENGTH_SHORT).show();
  }

  private void toast(String text) {
    Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
  }

  /**
   * 选择相机
   */
  private void showCameraAction() {
    // 判断选择数量问题
    if (mMode == MODE_MULTI && mDesireImageCount == mSelectedResources.size()) {
      toast(getString(R.string.image_pick_msg_amount_limit, mDesireImageCount));
      return;
    }
    // 跳转到系统照相机
    try {
      // 设置系统相机拍照后的输出路径
      // 创建临时文件
      mCameraTmpFile = SDCardUtil.getTmpFile();
      Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
      intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mCameraTmpFile));
      startActivityForResult(intent, REQUEST_CAMERA);
    } catch (ActivityNotFoundException e) {
      toast(R.string.image_pick_msg_no_camera);
    } catch (Exception e) {
      toast(R.string.image_pick_msg_no_sdcard);
    }
  }

  /**
   * 选择图片操作
   */
  private void selectResource(Resource resource) {
    if (resource != null) {
      // 多选模式
      if (mMode == MODE_MULTI) {
        if (mSelectedResources.contains(resource)) {
          mSelectedResources.remove(resource);
        } else {
          // 判断选择数量问题
          if (mDesireImageCount == mSelectedResources.size()) {
            toast(getString(R.string.image_pick_msg_amount_limit, mDesireImageCount));
            return;
          }
          resource.setIsSelected(true);
          mSelectedResources.add(resource);
        }
        updateUI();
        mMediaSelectAdapter.notifyDataSetChanged();
      } else if (mMode == MODE_SINGLE) {
        mSelectedResources.clear();
        mSelectedResources.add(resource);
        Intent data = new Intent();
        data.putExtra(EXTRA_RESULT, mSelectedResources);
        getActivity().setResult(Activity.RESULT_OK, data);
        getActivity().onBackPressed();
      } else if (mMode == MODE_CROP) {
        try {
          Uri data = Uri.fromFile(new File(resource.getFilePath()));
          mCropTmpFile = SDCardUtil.getTmpFile();
          Uri out = Uri.fromFile(mCropTmpFile);
          int aspectX = getArguments().getInt(EXTRA_CROP_ASPECTX);
          int aspectY = getArguments().getInt(EXTRA_CROP_ASPECTY);
          int outputX = getArguments().getInt(EXTRA_CROP_OUTPUTX);
          int outputY = getArguments().getInt(EXTRA_CROP_OUTPUTY);
          Intent intent = getImageFromCrop(data, out, aspectX, aspectY, outputX, outputY);
          startActivityForResult(intent, REQUEST_CROP);
        } catch (ActivityNotFoundException e) {
          toast(R.string.image_pick_msg_no_crop);
        } catch (Exception e) {
          toast(R.string.image_pick_msg_no_sdcard);
        }
      }
    }
  }

  /**
   * 初始化展示文件夹的popupWindow
   */
  private void initListDirPopupWindow() {
    if (dismissMenu()) {
      return;
    }
    mImageFolderBackground.setAlpha(0);
    mImageFolderBackground.animate().setDuration(300).alpha(0.6f).setListener(null).start();
    mImageFolderLayout.setVisibility(View.VISIBLE);
    mHeaderIcon.setSelected(true);
    mListDirs.setAdapter(mImageFolderAdapter);
    mListDirs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        try {
          mHeaderIcon.setImageResource(R.drawable.image_pick_ic_select_photo_drop_down);
          if (position == 0) {
            mHeaderTitle.setText("所有图片");
          } else {
            mHeaderTitle.setText(mFolders.get(position).name);
          }
          List<File> imgs = null;
          if (position != 0) {
            File imgDir = new File(mFolders.get(position).path);
            imgs = Arrays.asList(imgDir.listFiles());
          }
          mImageFolderAdapter.changeData(mFolders);
          List<Resource> typeResources = new ArrayList<>();
          if (position != 0) {
            for (Resource resource : mResources) {
              for (File file : imgs) {
                if (resource.getFilePath().equals(file.getAbsolutePath())) {
                  typeResources.add(resource);
                }
              }
            }
            mMediaSelectAdapter.setData(typeResources);
          } else {
            mMediaSelectAdapter.setData(mResources);
          }
          dismissMenu();
        } catch (Exception e) {
          Log.e("MediaSelectFragment", "", e);
        }
      }
    });
    HeightAnimation animation =
        new HeightAnimation(mListDirsLayout, 0, UIUtil.dp2px(getContext(), 400));
    animation.setDuration(300);
    mListDirsLayout.startAnimation(animation);
    mImageFolderBackground.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        dismissMenu();
      }
    });
  }

  private boolean dismissMenu() {
    if (mImageFolderLayout.getVisibility() == View.VISIBLE) {
      HeightAnimation animation =
          new HeightAnimation(mListDirsLayout, UIUtil.dp2px(getContext(), 400), 0);
      animation.setDuration(300);
      mListDirsLayout.startAnimation(animation);
      mImageFolderBackground.animate()
          .setDuration(300)
          .alpha(0)
          .setListener(new Animator.AnimatorListener() {
            @Override public void onAnimationStart(Animator animation) {

            }

            @Override public void onAnimationEnd(Animator animation) {
              mImageFolderLayout.setVisibility(View.GONE);
              mHeaderIcon.setSelected(false);
            }

            @Override public void onAnimationCancel(Animator animation) {

            }

            @Override public void onAnimationRepeat(Animator animation) {

            }
          })
          .start();
      return true;
    }
    return false;
  }

  public boolean onBackPressed() {
    if (dismissMenu()) {
      return true;
    }
    return false;
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    if (mCameraTmpFile != null) {
      outState.putString(CAMERA_FILE_PATH, mCameraTmpFile.getAbsolutePath());
    }
    if (mCropTmpFile != null) {
      outState.putString(CROP_FILE_PATH, mCropTmpFile.getAbsolutePath());
    }
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (mMode == MODE_CAMERA && resultCode != Activity.RESULT_OK) {
      getActivity().onBackPressed();//如果选择拍照，取消拍照，则直接返回
      return;
    }
    if (resultCode == Activity.RESULT_OK) {
      if (requestCode == REQUEST_CAMERA) {
        Resource resource = new Resource(mCameraTmpFile.getAbsolutePath());
        if (mMode == MODE_CROP) {
          selectResource(resource);
        } else {
          // 扫描刚拍照完成后的照片并设置为true,用于拍照完后预览
          resource.setIsSelected(true);
          getActivity().sendBroadcast(
              new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(mCameraTmpFile)));

          mIsFromCameraBack = true;
          //拍完照片直接返回
          Intent intent = new Intent();
          Bundle bundle = new Bundle();
          mSelectedResources.add(resource);
          bundle.putSerializable(EXTRA_RESULT, mSelectedResources);
          intent.putExtras(bundle);
          getActivity().setResult(Activity.RESULT_OK, intent);
          getActivity().onBackPressed();
        }
        return;
      } else if (requestCode == REQUEST_CROP) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_RESULT, new Resource(mCropTmpFile.getAbsolutePath()));
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().onBackPressed();
        return;
      } else if (requestCode == REQUEST_PREVIEW) {
        ArrayList<Resource> resources =
            (ArrayList<Resource>) data.getSerializableExtra(PREVIEW_LIST);
        boolean complete = data.getBooleanExtra(PREVIEW_COMPLETE, false);
        if (complete) {
          Intent intent = new Intent();
          intent.putExtra(EXTRA_RESULT, resources);
          getActivity().setResult(Activity.RESULT_OK, intent);
          getActivity().onBackPressed();
        } else {
          mSelectedResources.clear();
          mSelectedResources.addAll(resources);
          updateUI();
          mMediaSelectAdapter.notifyDataSetChanged();
        }
      }
    }
    super.onActivityResult(requestCode, resultCode, data);
  }

  private void updateUI() {
    if (mMode != MODE_MULTI) {
      return;
    }
    int selectedCount = mSelectedResources.size();
    String previewText = getString(R.string.image_pick_preview);
    mPreview.setTextColor(
        selectedCount != 0 ? getResources().getColor(R.color.image_pick_main_color_normal)
            : getResources().getColor(R.color.image_pick_textColorPrimaryLight));
    mComplete.setTextColor(
        selectedCount != 0 ? getResources().getColor(R.color.image_pick_main_color_normal)
            : getResources().getColor(R.color.image_pick_textColorPrimaryLight));
    mPreview.setEnabled(selectedCount != 0);
    mPreview.setText(previewText);
    mPreview.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(PREVIEW_LIST, mSelectedResources);
        Intent intent = new Intent(MediaSelectFragment.this.getContext(), ContainerActivity.class);
        intent.putExtra(ContainerActivity.FRAGMENT_NAME, PreviewFragment.class.getName());
        intent.putExtras(bundle);
        startActivity(intent);
      }
    });

    if (selectedCount > 0) {
      mCountLayout.setVisibility(View.VISIBLE);
      mCount.setText(String.valueOf(selectedCount));
      if (mIsFromCameraBack) {
        mHeaderTitle.setText("所有图片");
      }
    } else {
      mCountLayout.setVisibility(View.GONE);
    }
    mComplete.setEnabled(selectedCount != 0);
    mComplete.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        Intent data = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_RESULT, mSelectedResources);
        data.putExtras(bundle);
        FragmentActivity activity = MediaSelectFragment.this.getActivity();
        activity.setResult(Activity.RESULT_OK, data);
        activity.finish();
      }
    });
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    mHeaderTitle = null;
    mHeaderIcon = null;
  }
}
