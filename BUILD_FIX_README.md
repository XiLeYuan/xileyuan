# 构建错误彻底解决方案

## 问题描述
运行一次后重新运行出现错误：
```
Error loading build artifacts from: /Users/chenyang/Documents/xly/app/build/intermediates/apk_ide_redirect_file/AliDebug/createAliDebugApkListingFileRedirect/redirect.txt
```

## 彻底解决方案

### 方法1：使用清理脚本（推荐）

项目根目录已提供 `clean_build.sh` 脚本，执行：

```bash
./clean_build.sh
```

然后在 Android Studio 中：
1. File → Invalidate Caches / Restart → Invalidate and Restart
2. File → Sync Project with Gradle Files
3. Build → Rebuild Project

### 方法2：手动清理

如果脚本不可用，手动执行以下命令：

```bash
# 删除所有构建目录
rm -rf app/build build .gradle app/.cxx

# 执行Gradle清理
./gradlew clean --no-daemon

# 重新构建
./gradlew :app:assembleAliDebug --no-daemon --no-build-cache
```

### 方法3：Android Studio 中操作

1. **清理项目**
   - Build → Clean Project

2. **使缓存失效**
   - File → Invalidate Caches / Restart → Invalidate and Restart

3. **同步项目**
   - File → Sync Project with Gradle Files

4. **重新构建**
   - Build → Rebuild Project

## 已配置的优化

已在 `gradle.properties` 中添加以下配置以防止构建缓存问题：

```properties
# 防止构建缓存问题
org.gradle.caching=false
org.gradle.configureondemand=false
org.gradle.parallel=false
```

## 预防措施

1. **定期清理**：如果遇到构建问题，先执行清理脚本
2. **避免频繁切换分支**：切换 Git 分支前先清理构建
3. **使用 Gradle Wrapper**：确保使用项目自带的 Gradle 版本
4. **检查磁盘空间**：确保有足够的磁盘空间

## 如果问题仍然存在

1. 删除 `~/.gradle/caches/` 目录（会清除所有 Gradle 缓存）
2. 重新导入项目
3. 检查 Android Studio 版本是否与项目兼容

