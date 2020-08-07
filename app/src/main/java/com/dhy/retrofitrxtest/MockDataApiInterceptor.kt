package com.dhy.retrofitrxtest

import okhttp3.*
import java.io.IOException

class MockDataApiInterceptor : Interceptor {
    companion object {
        const val allOk = "{\"result\":{\"code\":0,\"desc\":\"success\"}}"
        const val bzError = "{\"result\":{\"code\":1,\"desc\":\"bz error\"}}"
        const val authorizeFailed = "{\"result\":{\"code\":9001,\"desc\":\"登录失效，请重新登录\"},\"data\":null,\"validResult\":null}"
    }


    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        Thread.sleep(1500)
        val request = chain.request()
        val url = chain.request().url().toString()
        val net = url.contains("net=1")
        val bz = url.contains("bz=1")
        println("intercept: url=$url")
        return if (net && bz) {
            createResponse(request, 200, "ok", allOk)
        } else {
            if (net && !bz) {
                createResponse(request, 200, "ok", bzError)
            } else if (!net && bz) {//net error
                createResponse(request, 404, "page not found")
            } else {//authorizeFailed
                createResponse(request, 200, "ok", authorizeFailed)
            }
        }
    }

    private fun createResponse(request: Request, statusCode: Int, message: String, body: String? = null): Response {
        val builder = Response.Builder()
                .code(statusCode)
                .message(message)
                .request(request)
                .protocol(Protocol.HTTP_1_0)
                .addHeader("Content-Type", "application/json")
                .body(ResponseBody.create(MediaType.parse("application/json"), body ?: ""))
        return builder.build()
    }
}
