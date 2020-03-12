package com.dhy.retrofitrxutil

open class Error(private val httpCode: Int, private val code: Int, private val message: String) : IError {
    override fun getCode() = code
    override fun httpCode() = httpCode
    override fun getMessage() = message
}