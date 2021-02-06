package com.fct.mvvm.data

/**
 * UI helper class representing current data tier status
 */
data class UIState<out T>(
    val status: Status,
    val data: T?,
    val error: Exception?
) {

    enum class Status {
        SUCCESS,
        ERROR,
        LOADING
    }

    companion object {
        fun <T> success(data: T): UIState<T> {
            return UIState(Status.SUCCESS, data, null)
        }

        fun <T> error(error: Exception, data: T? = null): UIState<T> {
            return UIState(Status.ERROR, data, error)
        }

        fun <T> loading(data: T? = null): UIState<T> {
            return UIState(Status.LOADING, data, null)
        }
    }
}