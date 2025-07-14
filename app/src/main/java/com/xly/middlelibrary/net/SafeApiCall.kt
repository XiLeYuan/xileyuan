package com.xly.middlelibrary.net

suspend fun <T> safeApiCall(
    call: suspend () -> LYResponse<T>
): Result<T> {
    return try {
        val response = call()
        if (response.code == 200 && response.data != null) {
            Result.success(response.data)
        } else {
            Result.failure(Exception(response.message))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}