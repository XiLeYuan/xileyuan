# 视频文件存放说明

## 文件夹位置
将本地视频文件放在此文件夹下：`app/src/main/assets/videos/`

## 支持的视频格式
- MP4 (推荐)
- 3GP
- WebM
- MKV
- 其他 ExoPlayer 支持的格式

## 使用方法
1. 将视频文件复制到此文件夹（例如：`sample1.mp4`, `sample2.mp4`）
2. 在 `MomentFragment.kt` 的 `generateMockData()` 方法中，更新 `localVideos` 列表，添加你的视频文件名
3. 视频路径格式：`videos/你的视频文件名.mp4`

## 示例
如果视频文件名为 `my_video.mp4`，则在代码中使用：
```kotlin
val localVideos = listOf(
    "videos/my_video.mp4"
)
```

## 注意事项
- 视频文件会被打包到APK中，会增加APK大小
- 建议视频文件不要太大（每个视频建议小于10MB）
- 视频文件名不要包含中文或特殊字符，建议使用英文和数字

