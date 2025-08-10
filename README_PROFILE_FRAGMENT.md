# ProfileFragment 使用说明

## 概述
ProfileFragment 是一个Android相亲交友类APP的用户个人主页界面，采用MVVM架构模式实现，风格简洁清新、少女感。

## 功能特性

### 1. 顶部区域
- 居中显示圆形头像（带淡紫色光影装饰）
- 头像下方显示昵称（示例：Ella）
- 两个功能按钮：
  - "资料待完善"（灰白底，左侧勾选图标，右上角红点提示）
  - "我的理想型"（紫色渐变底，白色文字）

### 2. 统计数据栏
- 四等分横向排列
- 喜欢我、访客数、人气值击败、动态互动
- 数字为黑色大号字体，标签为灰色小号字体

### 3. VIP广告区
- 左侧"VIP特权"卡片（金色标题，文字"无限滑卡"）
- 右侧横幅广告图（深紫色背景 + 白色文字"红娘帮你找男友" + 小视频播放按钮）

### 4. 功能菜单列表
- 我的相册（图片图标）
- 我的认证（带粉色"去认证"标签）
- 我的钱包（钱包图标）
- 把牵手推荐给朋友（副标题"免费领100牵手币"）
- 搜索用户（放大镜图标）
- 设置（齿轮图标）

### 5. 底部横幅提示
- 橙色背景，文字："上传3张『我的生活』，收获喜欢的几率会增加2倍哦！"
- 右侧"立即上传"按钮（粉色底，白色文字）

## 技术实现

### 架构模式
- 采用MVVM架构模式
- 继承LYBaseFragment基类
- 使用ViewBinding进行视图绑定

### 文件结构
```
app/src/main/java/com/xly/business/user/
├── view/
│   └── ProfileFragment.kt          # 主要Fragment类
├── viewmodel/
│   ├── ProfileViewModel.kt         # ViewModel类
│   └── ProfileRepository.kt        # Repository类
└── model/
    └── UserInfo.kt                 # 用户信息数据模型
```

### 布局文件
- `fragment_profile.xml` - 主要布局文件
- 使用ConstraintLayout作为根布局
- 支持滚动，适配不同屏幕尺寸

### 资源文件
- 渐变背景：左上粉色渐变到右下浅蓝色
- 各种图标：使用Vector Drawable
- 颜色资源：在colors.xml中定义

## 使用方法

### 1. 在Activity中使用
```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // 添加ProfileFragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, ProfileFragment())
            .commit()
    }
}
```

### 2. 自定义用户资料
```kotlin
// 在ProfileViewModel中修改loadUserProfile方法
fun loadUserProfile(userId: String) {
    viewModelScope.launch {
        // 调用真实的API或本地数据库
        val result = repository.getUserProfile(userId)
        userProfileLiveData.value = result
    }
}
```

### 3. 自定义统计数据
```kotlin
// 在ProfileViewModel中修改loadProfileStats方法
fun loadProfileStats(userId: String) {
    viewModelScope.launch {
        // 从服务器获取真实数据
        val stats = repository.getProfileStats(userId)
        profileStatsLiveData.value = stats
    }
}
```

## 自定义配置

### 1. 修改颜色主题
在`colors.xml`中修改相关颜色值：
```xml
<color name="profile_pink">#FFE1F5FE</color>
<color name="profile_light_pink">#FFF8BBD9</color>
<color name="profile_light_blue">#FFE3F2FD</color>
```

### 2. 修改渐变背景
在`profile_gradient_bg.xml`中调整渐变角度和颜色：
```xml
<gradient
    android:startColor="#FFE1F5FE"
    android:centerColor="#FFF8BBD9"
    android:endColor="#FFE3F2FD"
    android:angle="135"
    android:type="linear" />
```

### 3. 添加新功能菜单
在布局文件中添加新的菜单项，并在Fragment中添加相应的点击事件处理。

## 注意事项

1. **权限要求**：确保应用有必要的网络和存储权限
2. **图片加载**：建议使用Glide或Picasso等图片加载库来加载头像
3. **网络请求**：实际项目中需要处理网络请求失败的情况
4. **数据缓存**：建议实现本地数据缓存，提升用户体验
5. **主题适配**：支持深色模式切换

## 扩展功能

### 1. 添加下拉刷新
```kotlin
// 使用SwipeRefreshLayout包装ScrollView
// 在ViewModel中添加刷新方法
```

### 2. 添加头像编辑功能
```kotlin
// 实现头像点击事件，跳转到头像编辑页面
// 支持拍照和从相册选择
```

### 3. 添加数据统计图表
```kotlin
// 使用MPAndroidChart等库显示数据趋势
// 在统计数据区域添加图表展示
```

## 版本历史

- v1.0.0 - 初始版本，包含基本的个人主页功能
- 支持MVVM架构
- 完整的UI界面设计
- 基础的点击事件处理
