package com.dhy.retrofitrxutil

open class Error(private val code: Int, private val message: String) : IError {
    override fun getCode(): Int {
        return code
    }

    override fun getMessage(): String {
        return message
    }
}