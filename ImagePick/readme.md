# 图片选择器
* 可以从所有图片中选择
* 可以根据目录筛选
* 可以指定最多选择多少张图片（也可指定单选多选）
* 可以指定哪些图片已选
* 可以选择拍照
* 可以对图片进行剪裁
* 可以对选择的图片进行预览

# 使用
启动ContainerActivity,默认会加载选择图片的fragment
```java
    Bundle bundle = new Bundle();
    // 指定头部的颜色
    bundle.putInt(MediaSelectFragment.EXTRA_TOOL_BAR_COLOR, getResources().getColor(R.color.main_color_normal));
    // 单选/多选
    bundle.putInt(MediaSelectFragment.EXTRA_SELECT_MODE, MediaSelectFragment.MODE_MULTI);
    // 最大选择数量
    bundle.putInt(MediaSelectFragment.EXTRA_SELECT_COUNT, 9);
    // MODE_CROP：剪裁、 MODE_CAMERA：拍照······
    Intent intent = new Intent(getContext(), ContainerActivity.class);
    // 也可不添加此参数
    intent.putExtra(ContainerActivity.FRAGMENT_NAME, MediaSelectFragment.class.getName());
    intent.putExtras(bundle);
    startActivityForResult(intent, REQUEST_CODE_IMAGE_GALLERY);
```
