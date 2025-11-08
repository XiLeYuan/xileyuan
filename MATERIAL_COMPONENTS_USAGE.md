# Material Components Android 使用指南

## 1. 依赖配置

在 `app/build.gradle` 中添加：
```gradle
dependencies {
    implementation 'com.google.android.material:material:1.7.0'
}
```

## 2. 主题配置

### Material 2 主题（传统）
```xml
<style name="AppTheme" parent="Theme.MaterialComponents.DayNight.NoActionBar">
    <!-- 自定义配置 -->
</style>
```

### Material 3 主题（推荐）
```xml
<style name="AppTheme" parent="Theme.Material3.DayNight.NoActionBar">
    <!-- Material 3 配置 -->
</style>
```

## 3. 常用组件使用

### TextInputLayout（文本输入框）
```xml
<com.google.android.material.textfield.TextInputLayout
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:hint="请输入内容"
    app:boxMode="outlined"
    app:endIconMode="clear_text"
    app:errorEnabled="true">

    <com.google.android.material.textfield.TextInputEditText
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:inputType="text" />
</com.google.android.material.textfield.TextInputLayout>
```

**Box 模式：**
- `filled` - 填充模式（默认）
- `outlined` - 边框模式
- `none` - 无背景

### Button（按钮）
```xml
<com.google.android.material.button.MaterialButton
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="按钮"
    app:icon="@drawable/ic_add"
    app:iconGravity="textStart" />
```

### Card（卡片）
```xml
<com.google.android.material.card.MaterialCardView
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:cardCornerRadius="8dp"
    app:cardElevation="4dp">

    <!-- 卡片内容 -->
</com.google.android.material.card.MaterialCardView>
```

### FloatingActionButton（浮动操作按钮）
```xml
<com.google.android.material.floatingactionbutton.FloatingActionButton
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:src="@drawable/ic_add"
    app:tint="@color/white" />
```

### BottomNavigationView（底部导航）
```xml
<com.google.android.material.bottomnavigation.BottomNavigationView
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:menu="@menu/bottom_nav_menu"
    app:itemIconTint="@color/selector_nav_icon"
    app:itemTextColor="@color/selector_nav_text" />
```

### Chip（标签）
```xml
<com.google.android.material.chip.Chip
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="标签"
    app:chipIcon="@drawable/ic_tag"
    app:closeIconVisible="true" />
```

### Snackbar（提示条）
```kotlin
Snackbar.make(view, "提示信息", Snackbar.LENGTH_SHORT)
    .setAction("操作") {
        // 点击操作
    }
    .show()
```

### BottomSheet（底部表单）
```xml
<com.google.android.material.bottomsheet.BottomSheetDialogFragment>
    <!-- 内容 -->
</com.google.android.material.bottomsheet.BottomSheetDialogFragment>
```

## 4. 代码中使用

### Kotlin 示例
```kotlin
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar

// 设置输入框错误
textInputLayout.error = "错误信息"

// 设置按钮样式
materialButton.isEnabled = false

// 显示 Snackbar
Snackbar.make(rootView, "消息", Snackbar.LENGTH_SHORT).show()
```

## 5. 自定义样式

### 自定义 TextInputLayout 样式
```xml
<style name="CustomTextInputLayout" parent="Widget.MaterialComponents.TextInputLayout.OutlinedBox">
    <item name="boxStrokeColor">@color/custom_stroke_color</item>
    <item name="boxCornerRadiusTopStart">12dp</item>
    <item name="boxCornerRadiusTopEnd">12dp</item>
    <item name="boxCornerRadiusBottomStart">12dp</item>
    <item name="boxCornerRadiusBottomEnd">12dp</item>
</style>
```

### 自定义 Button 样式
```xml
<style name="CustomButton" parent="Widget.MaterialComponents.Button">
    <item name="backgroundTint">@color/primary</item>
    <item name="cornerRadius">8dp</item>
</style>
```

## 6. Material 3 新特性

Material 3 引入了新的设计系统，包括：
- 动态颜色（Dynamic Color）
- 更大的圆角
- 新的组件样式
- 改进的动画效果

使用 Material 3 需要：
1. 使用 `Theme.Material3.*` 主题
2. 确保 targetSdk 为 31+
3. 使用 Material 3 组件样式

## 7. 官方资源

- GitHub: https://github.com/material-components/material-components-android
- 文档: https://material.io/develop/android
- 组件目录: https://material.io/components
- 设计指南: https://material.io/design

## 8. 常见问题

### Q: 如何设置圆角边框？
A: 使用 `OutlinedBox` 样式，并通过代码设置圆角：
```kotlin
textInputLayout.setBoxCornerRadiusTopStart(12)
textInputLayout.setBoxCornerRadiusTopEnd(12)
textInputLayout.setBoxCornerRadiusBottomStart(12)
textInputLayout.setBoxCornerRadiusBottomEnd(12)
```

### Q: 如何自定义颜色？
A: 在主题中设置：
```xml
<item name="colorPrimary">@color/primary</item>
<item name="colorSecondary">@color/secondary</item>
<item name="colorError">@color/error</item>
```

### Q: 如何实现聚焦效果？
A: 监听焦点变化：
```kotlin
editText.setOnFocusChangeListener { _, hasFocus ->
    if (hasFocus) {
        textInputLayout.setBoxStrokeColor(ContextCompat.getColor(this, R.color.primary))
    }
}
```

