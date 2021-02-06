package com.fct.mvvm.data.repository

import java.util.concurrent.TimeUnit

/**
 * InMemory cache.  Auto expires all cached data after [expireMillis] has elapsed
 * @param expireMillis time to keep cache before recycling it
 */
class AgedMemoryCache(private val expireMillis: Long) {
    private var lastFlushTime = System.nanoTime()
    private val cache = HashMap<Any, Any>()

    val size: Int
        get() = cache.size

    @Synchronized
    fun set(key: Any, value: Any) {
        cache[key] = value
    }

    @Synchronized
    fun remove(key: Any): Any? {
        recycle()
        return cache.remove(key)
    }

    @Synchronized
    fun get(key: Any): Any? {
        recycle()
        return cache[key]
    }

    @Synchronized
    fun clear() = cache.clear()

    private fun recycle() {
        val shouldRecycle =
            System.nanoTime() - lastFlushTime >= TimeUnit.MILLISECONDS.toNanos(expireMillis)
        if (!shouldRecycle) return
        clear()
    }
}