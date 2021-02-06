package com.fct.mvvm.data.repository

import com.fct.mvvm.createLaunchEntity
import org.junit.Assert.assertEquals
import org.junit.Test

class AgedMemoryCacheTest {

    private val expireTimeInMs = 1000L
    private val cacheKey1 = "key_1"
    private val cacheKey2 = "key_2"

    @Test
    fun `ensure cache operates with multiple entries`() {
        // arrange
        val cache = AgedMemoryCache(expireTimeInMs)
        val entity1 = createLaunchEntity()
        val entity2 = createLaunchEntity()
        val entity3 = createLaunchEntity()
        val list = listOf(entity1, entity2, entity3)

        // act
        cache.set(cacheKey1, entity1) // single
        cache.set(cacheKey2, list) // list

        // assert
        assertEquals(entity1, cache.get(cacheKey1))
        assertEquals(list, cache.get(cacheKey2))
    }

    @Test
    fun `ensure cache recycles`() {
        // arrange
        val cache = AgedMemoryCache(expireTimeInMs) // 1 second
        val entity = createLaunchEntity()

        // act
        cache.set(cacheKey1, entity)

        // assert
        assertEquals(entity, cache.get(cacheKey1))

        // sleep
        Thread.sleep(expireTimeInMs)

        // assert
        assertEquals(null, cache.get(cacheKey1))
    }

    @Test
    fun `ensure cache clears`() {
        // arrange
        val cache = AgedMemoryCache(expireTimeInMs) // 1 second
        val entity = createLaunchEntity()

        // act
        cache.set(cacheKey1, entity)

        // assert
        assertEquals(entity, cache.get(cacheKey1))

        // clear
        cache.clear()

        // assert
        assertEquals(null, cache.get(cacheKey1))
    }
}