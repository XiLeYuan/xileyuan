package com.xly.middlelibrary.utils

import com.xly.middlelibrary.net.LYResponse
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class JsonResponseConverterFactory : Converter.Factory() {

    override fun responseBodyConverter(
        type: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? {

        return when (type) {
            is ParameterizedType -> {
                val rawType = type.rawType
                if (rawType == LYResponse::class.java) {
                    val dataType = type.actualTypeArguments[0]
                    JsonResponseConverter<Any>(dataType)
                } else {
                    null
                }
            }
            else -> null
        }
    }
}