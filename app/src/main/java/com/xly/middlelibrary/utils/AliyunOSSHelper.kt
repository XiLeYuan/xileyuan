package com.xly.middlelibrary.utils

import android.content.Context
import android.util.Log
import com.alibaba.sdk.android.oss.ClientConfiguration
import com.alibaba.sdk.android.oss.ClientException
import com.alibaba.sdk.android.oss.OSS
import com.alibaba.sdk.android.oss.OSSClient
import com.alibaba.sdk.android.oss.ServiceException
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider
import com.alibaba.sdk.android.oss.model.PutObjectRequest
import com.alibaba.sdk.android.oss.model.PutObjectResult
import java.io.File
import java.util.UUID

object AliyunOSSHelper {

    private const val TAG = "AliyunOSSHelper"

    private const val ENDPOINT =
            "http://oss-cn-hangzhou.aliyuncs.com" // 例如：http://oss-cn-hangzhou.aliyuncs.com   https://oss-cn-hangzhou.aliyuncs.com
    private const val BUCKET_NAME = "xly-jiehunba-bucket"
    private const val ACCESS_KEY_ID = ""
    private const val ACCESS_KEY_SECRET = ""

    // 如果使用 STS，请使用 OSSStsTokenCredentialProvider
    // private const val STS_SERVER_URL = "http://your-sts-server.com/sts"

    private var oss: OSS? = null

    fun init(context: Context) {
        if (oss != null) return

        val conf = ClientConfiguration()
        conf.connectionTimeout = 15 * 1000 // 连接超时，默认15秒
        conf.socketTimeout = 15 * 1000 // socket超时，默认15秒
        conf.maxConcurrentRequest = 5 // 最大并发请求书，默认5个
        conf.maxErrorRetry = 2 // 失败后最大重试次数，默认2次

        // 明文设置 AK/SK (不推荐在生产环境中使用，建议使用 STS)
        val credentialProvider: OSSCredentialProvider =
                OSSPlainTextAKSKCredentialProvider(ACCESS_KEY_ID, ACCESS_KEY_SECRET)

        oss = OSSClient(context.applicationContext, ENDPOINT, credentialProvider, conf)
    }

    interface UploadCallback {
        fun onSuccess(url: String)
        fun onFailure(msg: String)
    }

    fun uploadFile(filePath: String, callback: UploadCallback) {
        if (oss == null) {
            callback.onFailure("OSSClient not initialized")
            return
        }

        val file = File(filePath)
        if (!file.exists()) {
            callback.onFailure("File not found")
            return
        }

        // 生成唯一文件名
        val objectKey = "avatar/${UUID.randomUUID()}_${file.name}"

        val put = PutObjectRequest(BUCKET_NAME, objectKey, filePath)

        // 异步上传
        val task =
                oss?.asyncPutObject(
                        put,
                        object : OSSCompletedCallback<PutObjectRequest, PutObjectResult> {
                            override fun onSuccess(
                                    request: PutObjectRequest?,
                                    result: PutObjectResult?
                            ) {
                                Log.d(TAG, "UploadSuccess")
                                Log.d(TAG, "ETag: " + result?.eTag)
                                Log.d(TAG, "RequestId: " + result?.requestId)

                                // 构造访问 URL (假设 Bucket 是公开读的，或者是签名 URL)
                                // 这里简单构造公开访问 URL
                                // 格式: https://BucketName.Endpoint/ObjectKey
                                val url =
                                        ENDPOINT.replace("https://", "https://$BUCKET_NAME.") +
                                                "/$objectKey"
                                callback.onSuccess(url)
                            }

                            override fun onFailure(
                                    request: PutObjectRequest?,
                                    clientException: ClientException?,
                                    serviceException: ServiceException?
                            ) {
                                // 请求异常
                                if (clientException != null) {
                                    // 本地异常如网络异常等
                                    clientException.printStackTrace()
                                    callback.onFailure(
                                            "ClientException: ${clientException.message}"
                                    )
                                }
                                if (serviceException != null) {
                                    // 服务异常
                                    Log.e(TAG, "ErrorCode: " + serviceException.errorCode)
                                    Log.e(TAG, "RequestId: " + serviceException.requestId)
                                    Log.e(TAG, "HostId: " + serviceException.hostId)
                                    Log.e(TAG, "RawMessage: " + serviceException.rawMessage)
                                    callback.onFailure(
                                            "ServiceException: ${serviceException.errorCode}"
                                    )
                                }
                            }
                        }
                )
    }
}
