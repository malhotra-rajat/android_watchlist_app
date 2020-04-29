package com.example.watchlist.common.network

import com.example.watchlist.common.network.Resource.Companion.success
import java.net.SocketTimeoutException

open class ResponseHandler {
    fun <T : Any> handleSuccess(data: T): Resource<T> {
        return success(data)
    }

    fun <T : Any> handleException(e: Exception): Resource<T> {
        return when (e) {
            is SocketTimeoutException -> Resource.error(
                "Timeout",
                null
            )
            else -> Resource.error(e.message ?: "Something went wrong", null)
        }
    }
}