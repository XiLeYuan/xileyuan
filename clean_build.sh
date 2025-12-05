#!/bin/bash

# 彻底清理Android项目构建缓存脚本

echo "开始清理构建缓存..."

# 删除构建目录
echo "删除构建目录..."
rm -rf app/build
rm -rf build
rm -rf app/.cxx
rm -rf .gradle
rm -rf .idea
rm -rf *.iml
rm -rf app/*.iml

# 清理Gradle缓存（可选，如果需要彻底清理）
# rm -rf ~/.gradle/caches/

# 执行Gradle清理
echo "执行Gradle清理..."
./gradlew clean --no-daemon

# 清理完成
echo "清理完成！"
echo "请在Android Studio中执行："
echo "1. File -> Invalidate Caches / Restart -> Invalidate and Restart"
echo "2. File -> Sync Project with Gradle Files"
echo "3. Build -> Rebuild Project"

