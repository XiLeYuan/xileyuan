# 友盟SDK集成说明

## 一、集成完成情况

✅ 已完成友盟统计SDK的基础集成，包括：
- 添加友盟SDK依赖
- 配置AndroidManifest.xml
- 创建友盟工具类 `UmengHelper`
- 在Application中初始化SDK
- 在BaseActivity和BaseFragment中添加页面统计

## 二、配置步骤

### 1. 获取友盟AppKey

1. 访问 [友盟官网](https://www.umeng.com/)
2. 注册/登录账号
3. 创建应用（选择Android平台）
4. 在应用信息中获取 `AppKey`

### 2. 配置AppKey

#### 方式一：在AndroidManifest.xml中配置（推荐）

打开 `app/src/main/AndroidManifest.xml`，找到以下配置：

```xml
<meta-data
    android:name="UMENG_APPKEY"
    android:value="YOUR_UMENG_APPKEY" />
```

将 `YOUR_UMENG_APPKEY` 替换为实际的AppKey。

#### 方式二：在代码中配置

打开 `app/src/main/java/com/xly/LYApplication.kt`，找到以下代码：

```kotlin
UmengHelper.init(
    context = this,
    appKey = "YOUR_UMENG_APPKEY", // 请替换为实际的AppKey
    channel = getChannelName(),
    isDebug = com.xly.BuildConfig.DEBUG
)
```

将 `YOUR_UMENG_APPKEY` 替换为实际的AppKey。

### 3. 渠道配置

渠道名称已自动从 `build.gradle` 中的 `productFlavors` 获取，无需额外配置。

当前已配置的渠道：
- Alpha
- Oppo
- Xiaomi
- Huawei
- Tradplus
- Honor
- Ali
- Kuaishou
- Douyin
- Vivo
- KSzhutui
- baidu
- s360
- clock

## 三、使用方法

### 1. 页面统计（自动）

页面统计已自动集成到 `LYBaseActivity` 和 `LYBaseFragment` 中，无需手动调用。

如果需要自定义页面名称，可以在Activity或Fragment中重写 `getPageName()` 方法：

```kotlin
override fun getPageName(): String {
    return "自定义页面名称"
}
```

### 2. 事件统计

#### 简单事件统计

```kotlin
import com.xly.middlelibrary.utils.UmengHelper

// 统计按钮点击事件
UmengHelper.onEvent(this, "button_click")
```

#### 带参数的事件统计

```kotlin
val map = mapOf(
    "button_name" to "登录按钮",
    "page" to "LoginActivity"
)
UmengHelper.onEvent(this, "button_click", map)
```

#### 带数值的事件统计

```kotlin
val map = mapOf(
    "product_id" to "12345",
    "product_name" to "VIP会员"
)
UmengHelper.onEventValue(this, "purchase", map, 99L) // 99是购买金额
```

### 3. 用户统计

#### 用户登录

```kotlin
// 用户登录时调用
UmengHelper.onProfileSignIn("user_id_12345")
```

#### 用户登出

```kotlin
// 用户登出时调用
UmengHelper.onProfileSignOff()
```

#### 设置用户属性

```kotlin
// 设置用户性别和年龄
UmengHelper.setUserInfo(
    userId = "user_id_12345",
    sex = "M", // M: 男, F: 女
    age = 25
)
```

### 4. 错误统计

```kotlin
// 统计错误信息
try {
    // 你的代码
} catch (e: Exception) {
    UmengHelper.reportError(this, e)
    // 或者
    UmengHelper.reportError(this, "自定义错误信息")
}
```

## 四、常用事件示例

### 登录相关

```kotlin
// 登录按钮点击
UmengHelper.onEvent(this, "login_button_click")

// 登录成功
UmengHelper.onEvent(this, "login_success", mapOf("login_type" to "phone"))

// 登录失败
UmengHelper.onEvent(this, "login_failed", mapOf("error_code" to "1001"))
```

### 注册相关

```kotlin
// 注册按钮点击
UmengHelper.onEvent(this, "register_button_click")

// 注册成功
UmengHelper.onEvent(this, "register_success")
```

### VIP相关

```kotlin
// VIP开通按钮点击
UmengHelper.onEvent(this, "vip_open_click")

// VIP开通成功
val map = mapOf(
    "vip_type" to "VIP",
    "price" to "48"
)
UmengHelper.onEventValue(this, "vip_purchase_success", map, 48L)
```

### 匹配相关

```kotlin
// 卡片滑动 - 喜欢
UmengHelper.onEvent(this, "card_swipe_like")

// 卡片滑动 - 跳过
UmengHelper.onEvent(this, "card_swipe_pass")

// 随缘匹配点击
UmengHelper.onEvent(this, "fate_match_click")
```

### 动态相关

```kotlin
// 发布动态
UmengHelper.onEvent(this, "moment_publish")

// 查看动态详情
UmengHelper.onEvent(this, "moment_detail_view", mapOf("moment_id" to "12345"))

// 点赞动态
UmengHelper.onEvent(this, "moment_like", mapOf("moment_id" to "12345"))
```

## 五、测试验证

### 1. 开启调试模式

在开发阶段，SDK会自动开启日志输出（`isDebug = BuildConfig.DEBUG`）。

### 2. 查看日志

运行应用后，在Logcat中搜索 `UMENG` 关键字，可以看到友盟SDK的日志输出。

### 3. 验证数据上报

1. 运行应用并执行一些操作（页面跳转、事件触发等）
2. 等待几分钟（数据上报有延迟）
3. 登录友盟后台查看数据

## 六、注意事项

1. **AppKey配置**：请务必替换 `YOUR_UMENG_APPKEY` 为实际的AppKey
2. **渠道统计**：渠道名称会自动从 `productFlavors` 获取
3. **页面统计**：已自动集成，无需手动调用
4. **事件统计**：建议在关键业务节点添加事件统计
5. **用户隐私**：确保在用户同意隐私政策后再初始化SDK

## 七、友盟后台查看数据

1. 登录 [友盟后台](https://mobile.umeng.com/)
2. 选择应用
3. 在"统计分析"中查看：
   - 实时统计：查看实时用户数据
   - 用户分析：查看用户画像、留存等
   - 事件分析：查看自定义事件数据
   - 渠道分析：查看各渠道数据

## 八、常见问题

### Q1: 数据没有上报？
A: 检查以下几点：
- AppKey是否正确配置
- 网络连接是否正常
- 是否在用户同意隐私政策后初始化SDK
- 查看Logcat中的友盟日志是否有错误

### Q2: 如何测试事件统计？
A: 
- 在代码中添加事件统计
- 运行应用并触发事件
- 等待几分钟后在友盟后台查看"事件分析"

### Q3: 渠道统计不准确？
A: 
- 确保 `build.gradle` 中的 `productFlavors` 配置正确
- 确保 `manifestPlaceholders` 中设置了 `UMENG_CHANNEL_VALUE`
- 重新打包安装应用

## 九、相关文档

- [友盟官方文档](https://developer.umeng.com/docs/67966/detail/66734)
- [友盟统计SDK文档](https://developer.umeng.com/docs/67966/detail/149317)

---

**集成完成！请记得替换AppKey并测试验证。**

