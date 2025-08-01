package com.jspp.utils

import com.tencent.mmkv.MMKV
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * MMKV管理类
 * 封装常用的存储操作
 */
object MMKVManager {
    
    // 默认MMKV实例
    private val defaultMMKV = MMKV.defaultMMKV()
    
    // 用户相关数据
    private val userMMKV = MMKV.mmkvWithID("user_data")
    
    // 配置相关数据
    private val configMMKV = MMKV.mmkvWithID("config_data")
    
    // 缓存相关数据
    private val cacheMMKV = MMKV.mmkvWithID("cache_data")
    
    // 加密MMKV实例
    private val encryptedMMKV = MMKV.mmkvWithID("encrypted_data", MMKV.MULTI_PROCESS_MODE, "encryption_key")
    
    // ==================== 基础操作 ====================
    
    /**
     * 存储字符串
     */
    fun putString(key: String, value: String, mmkv: MMKV = defaultMMKV) {
        mmkv.encode(key, value)
    }
    
    /**
     * 获取字符串
     */
    fun getString(key: String, defaultValue: String = "", mmkv: MMKV = defaultMMKV): String {
        return mmkv.decodeString(key, defaultValue) ?: defaultValue
    }
    
    /**
     * 存储整数
     */
    fun putInt(key: String, value: Int, mmkv: MMKV = defaultMMKV) {
        mmkv.encode(key, value)
    }
    
    /**
     * 获取整数
     */
    fun getInt(key: String, defaultValue: Int = 0, mmkv: MMKV = defaultMMKV): Int {
        return mmkv.decodeInt(key, defaultValue)
    }
    
    /**
     * 存储长整数
     */
    fun putLong(key: String, value: Long, mmkv: MMKV = defaultMMKV) {
        mmkv.encode(key, value)
    }
    
    /**
     * 获取长整数
     */
    fun getLong(key: String, defaultValue: Long = 0L, mmkv: MMKV = defaultMMKV): Long {
        return mmkv.decodeLong(key, defaultValue)
    }
    
    /**
     * 存储布尔值
     */
    fun putBoolean(key: String, value: Boolean, mmkv: MMKV = defaultMMKV) {
        mmkv.encode(key, value)
    }
    
    /**
     * 获取布尔值
     */
    fun getBoolean(key: String, defaultValue: Boolean = false, mmkv: MMKV = defaultMMKV): Boolean {
        return mmkv.decodeBool(key, defaultValue)
    }
    
    /**
     * 存储浮点数
     */
    fun putFloat(key: String, value: Float, mmkv: MMKV = defaultMMKV) {
        mmkv.encode(key, value)
    }
    
    /**
     * 获取浮点数
     */
    fun getFloat(key: String, defaultValue: Float = 0f, mmkv: MMKV = defaultMMKV): Float {
        return mmkv.decodeFloat(key, defaultValue)
    }
    
    /**
     * 存储字节数组
     */
    fun putBytes(key: String, value: ByteArray, mmkv: MMKV = defaultMMKV) {
        mmkv.encode(key, value)
    }
    
    /**
     * 获取字节数组
     */
    fun getBytes(key: String, defaultValue: ByteArray = ByteArray(0), mmkv: MMKV = defaultMMKV): ByteArray {
        return mmkv.decodeBytes(key, defaultValue) ?: defaultValue
    }
    
    /**
     * 存储对象（需要实现Parcelable）
     */
    fun <T : android.os.Parcelable> putParcelable(key: String, value: T, mmkv: MMKV = defaultMMKV) {
        mmkv.encode(key, value)
    }
    
    /**
     * 获取对象
     */
    inline fun <reified T : android.os.Parcelable> getParcelable(key: String, mmkv: MMKV = defaultMMKV): T? {
        return mmkv.decodeParcelable(key, T::class.java)
    }
    
    // ==================== 用户数据操作 ====================
    
    /**
     * 存储用户信息
     */
    fun putUserInfo(userId: String, userInfo: String) {
        userMMKV.encode("user_$userId", userInfo)
    }
    
    /**
     * 获取用户信息
     */
    fun getUserInfo(userId: String): String {
        return userMMKV.decodeString("user_$userId", "") ?: ""
    }
    
    /**
     * 存储用户Token
     */
    fun putUserToken(userId: String, token: String) {
        userMMKV.encode("token_$userId", token)
    }
    
    /**
     * 获取用户Token
     */
    fun getUserToken(userId: String): String {
        return userMMKV.decodeString("token_$userId", "") ?: ""
    }
    
    // ==================== 配置数据操作 ====================
    
    /**
     * 存储应用配置
     */
    fun putConfig(key: String, value: String) {
        configMMKV.encode(key, value)
    }
    
    /**
     * 获取应用配置
     */
    fun getConfig(key: String, defaultValue: String = ""): String {
        return configMMKV.decodeString(key, defaultValue) ?: defaultValue
    }
    
    /**
     * 存储主题设置
     */
    fun putTheme(theme: String) {
        configMMKV.encode("theme", theme)
    }
    
    /**
     * 获取主题设置
     */
    fun getTheme(): String {
        return configMMKV.decodeString("theme", "light") ?: "light"
    }
    
    // ==================== 缓存数据操作 ====================
    
    /**
     * 存储缓存数据
     */
    fun putCache(key: String, value: String) {
        cacheMMKV.encode(key, value)
    }
    
    /**
     * 获取缓存数据
     */
    fun getCache(key: String, defaultValue: String = ""): String {
        return cacheMMKV.decodeString(key, defaultValue) ?: defaultValue
    }
    
    /**
     * 清除缓存
     */
    fun clearCache() {
        cacheMMKV.clearAll()
    }
    
    // ==================== 加密数据操作 ====================
    
    /**
     * 存储加密数据
     */
    fun putEncrypted(key: String, value: String) {
        encryptedMMKV.encode(key, value)
    }
    
    /**
     * 获取加密数据
     */
    fun getEncrypted(key: String, defaultValue: String = ""): String {
        return encryptedMMKV.decodeString(key, defaultValue) ?: defaultValue
    }
    
    // ==================== 工具方法 ====================
    
    /**
     * 检查键是否存在
     */
    fun containsKey(key: String, mmkv: MMKV = defaultMMKV): Boolean {
        return mmkv.containsKey(key)
    }
    
    /**
     * 删除指定键
     */
    fun remove(key: String, mmkv: MMKV = defaultMMKV) {
        mmkv.removeValueForKey(key)
    }
    
    /**
     * 清除所有数据
     */
    fun clearAll(mmkv: MMKV = defaultMMKV) {
        mmkv.clearAll()
    }
    
    /**
     * 获取所有键
     */
    fun allKeys(mmkv: MMKV = defaultMMKV): Set<String> {
        return mmkv.allKeys()
    }
    
    /**
     * 获取存储大小
     */
    fun totalSize(mmkv: MMKV = defaultMMKV): Int {
        return mmkv.totalSize()
    }
    
    /**
     * 获取实际大小
     */
    fun actualSize(mmkv: MMKV = defaultMMKV): Int {
        return mmkv.actualSize()
    }
    
    // ==================== 异步操作 ====================
    
    /**
     * 异步存储字符串
     */
    suspend fun putStringAsync(key: String, value: String, mmkv: MMKV = defaultMMKV) {
        withContext(Dispatchers.IO) {
            mmkv.encode(key, value)
        }
    }
    
    /**
     * 异步获取字符串
     */
    suspend fun getStringAsync(key: String, defaultValue: String = "", mmkv: MMKV = defaultMMKV): String {
        return withContext(Dispatchers.IO) {
            mmkv.decodeString(key, defaultValue) ?: defaultValue
        }
    }
    
    /**
     * 批量存储
     */
    suspend fun putBatch(data: Map<String, String>, mmkv: MMKV = defaultMMKV) {
        withContext(Dispatchers.IO) {
            data.forEach { (key, value) ->
                mmkv.encode(key, value)
            }
        }
    }
    
    /**
     * 批量获取
     */
    suspend fun getBatch(keys: List<String>, mmkv: MMKV = defaultMMKV): Map<String, String> {
        return withContext(Dispatchers.IO) {
            keys.associateWith { key ->
                mmkv.decodeString(key, "") ?: ""
            }
        }
    }
} 