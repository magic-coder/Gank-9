# Gank

干货 - LiveData, ViewModel, Retrofit, RxJava, Dagger2 架构，采用kotlin语言开发，仅用于学习。
> 接口Api：https://gank.io/api

# 截图

主界面 | 历史 | 文章详情 |
--- | --- | ---|
![主界面][home]|![历史][history]|![文章详情][article_details]

分类 | 福利 | 福利详情 |
--- | --- | ---|
![分类][category]|![妹纸][meizhi]|![妹纸大图查看][meizhi_details]

# 依赖
- LiveData
- ViewModel
- Lifecycle
- [Retrofit][retrofit]
- [RxJava2][rxJava]
- [Dagger2][dagger]

# 功能点
- AppBarLayout、BottomNavigationView和FloatingActionButton在配合滑动列表滑动的时候隐藏/显示，[具体代码][BottomNavigationBehavior]
- Android 5.0上 webView 截取全屏方法，[具体代码-73到88行][interceptImage]
- 使用RecyclerView代替ViewPager分页查看图片，[具体代码][recyclerview_pager]
- Dagger2的使用
- ViewModel搭配LiveData的使用
- 使用Lifecycle来管理生命周期

# 协议
Copyright 2018 Zheng Wang

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

[home]:http://p8i9mda7f.bkt.clouddn.com/18-10-8/97172381.jpg "主界面"
[history]:http://p8i9mda7f.bkt.clouddn.com/18-10-8/34590332.jpg "历史"
[article_details]:http://p8i9mda7f.bkt.clouddn.com/18-10-8/32105300.jpg "文章详情"
[category]:http://p8i9mda7f.bkt.clouddn.com/18-10-8/27672822.jpg "分类"
[meizhi]:http://p8i9mda7f.bkt.clouddn.com/18-10-8/80144158.jpg "妹纸"
[meizhi_details]:http://p8i9mda7f.bkt.clouddn.com/18-10-8/24413998.jpg "妹纸"

[rxJava]:https://github.com/ReactiveX/RxJava
[dagger]:https://github.com/google/dagger
[retrofit]:https://github.com/square/retrofit
[BottomNavigationBehavior]:https://github.com/wazing/Gank/blob/master/app/src/main/java/com/wazing/gank/utils/BottomNavigationBehavior.kt
[interceptImage]:https://github.com/wazing/Gank/blob/master/app/src/main/java/com/wazing/gank/utils/Expands.kt
[recyclerview_pager]:https://github.com/wazing/Gank/blob/master/app/src/main/java/com/wazing/gank/ui/activity/ImagePageActivity.kt
