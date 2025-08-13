package com.xly.middlelibrary.utils

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.xly.middlelibrary.net.LYResponse
import okhttp3.ResponseBody
import retrofit2.Converter
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class JsonResponseConverter<T>(
    private val dataType: Type,
    private val gson: Gson = Gson()
) : Converter<ResponseBody, LYResponse<T>> {

    override fun convert(value: ResponseBody): LYResponse<T> {
        val jsonString = value.string()

        return try {
            val responseType = object : ParameterizedType {
                override fun getActualTypeArguments(): Array<Type> = arrayOf(dataType)
                override fun getRawType(): Type = LYResponse::class.java
                override fun getOwnerType(): Type? = null
            }

            gson.fromJson<LYResponse<T>>(jsonString, responseType).apply {
                rawJson = jsonString
            }
        } catch (e: JsonSyntaxException) {
            LYResponse<T>().apply {
                success = false
                message = "JSON解析失败: ${e.message}"
                rawJson = jsonString
            }
        }
    }
}