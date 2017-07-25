# 此库为基础库
包含一个app所必须的一些东西（如数据统计分析、eventBus、ImageLoader）和一些基类文件（BaseActivity、BaseFragment）等
**使用此库的app需继承库中的BaseApp、BaseActivity、BaseFragment，并使用ActivityUtil来进行页面跳转**

# 库中包含：
* 一些基类（activity、fragment、webView等）
* dialog基类（从 中间/两边/下面 弹出来的dialog、锁屏dialog）
* eventBus
* 网络相关的工具类
* rxJava的一些工具类
* 图片加载
* 数据统计上传
* 界面跳转工具类