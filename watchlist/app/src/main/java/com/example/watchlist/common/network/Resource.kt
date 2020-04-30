package com.example.watchlist.common.network

data class Resource<out T>(val responseStatus: ResponseStatus, val data: T?, val message: String?) {
    companion object {
        fun <T> success(data: T?): Resource<T> {
            return Resource(ResponseStatus.SUCCESS, data, null)
        }

        fun <T> error(msg: String, data: T?): Resource<T> {
            return Resource(ResponseStatus.ERROR, data, msg)
        }
    }
}