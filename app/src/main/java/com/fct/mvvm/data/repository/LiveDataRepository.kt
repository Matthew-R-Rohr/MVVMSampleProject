package com.fct.mvvm.data.repository

import android.util.Log
import com.fct.mvvm.api.SpaceXApi
import com.fct.mvvm.data.LaunchEntity
import java.util.concurrent.TimeUnit

private const val TAG = "tcbcLiveDataRepo"

private val CACHE_EXPIRATION_MILLIS = TimeUnit.MINUTES.toMillis(1)
private const val LATEST_LAUNCH_KEY = "latestLaunch"
private const val PAST_LAUNCHES_KEY = "pastLaunches"
private const val UPCOMING_LAUNCHES_KEY = "upcomingLaunches"

/**
 * Example repository implementation using Coroutines + Retrofit + InMem cache
 *
 * This implementation is very similar to the [LiveDataRepository], as they both return from [suspend] but differ in
 * actual cached data management
 *
 * Methods return cached data using [AgedMemoryCache], otherwise will return fresh data through the [SpaceXApi]
 */
class LiveDataRepository {

    private val spaceXApi = SpaceXApi()
    private val dtoTransformer = LaunchDtoTransformer()
    private val agedInMemCache = AgedMemoryCache(CACHE_EXPIRATION_MILLIS)

    /**
     * fetches the latest SpaceX launch
     *
     * If cached data is not present, will contact the [SpaceXApi] to refresh the [AgedMemoryCache]
     */
    suspend fun getLatestLaunch(): LaunchEntity? {

        // check in memory cache
        val cachedData = agedInMemCache.get(LATEST_LAUNCH_KEY) as? LaunchEntity
        return if (cachedData != null) cachedData
        else {
            Log.d(TAG, "cache is stale, getting fresh data")

            // make the API call
            val response = spaceXApi.spaceXService.getLatestLaunchByCoroutine()
            if (response.isSuccessful) {
                response.body()?.let {

                    // convert from dto to entity
                    val launchEntity = dtoTransformer.transformToLaunchEntity(it)
                    agedInMemCache.set(LATEST_LAUNCH_KEY, launchEntity) // update cache
                    return launchEntity
                }
            } else {
                throw(Exception("getLatestLaunch() was not successful: ${response.code()}"))
            }
        }
    }

    /**
     * fetches all upcoming SpaceX launches
     *
     * If cached data is not present, will contact the [SpaceXApi] to refresh the [AgedMemoryCache]
     */
    @Suppress("UNCHECKED_CAST")
    suspend fun getUpcomingLaunches(): List<LaunchEntity> {

        // check in memory cache
        val cachedData = agedInMemCache.get(UPCOMING_LAUNCHES_KEY) as? List<LaunchEntity>
        return if (!cachedData.isNullOrEmpty()) cachedData
        else {
            Log.d(TAG, "cache is stale, getting fresh data")

            // make the API call
            val response = spaceXApi.spaceXService.getUpcomingLaunchesByCoroutine()
            if (response.isSuccessful) {
                response.body()?.let { result ->

                    // convert from dto to entity
                    val launchEntities = dtoTransformer
                        .transformToLaunchEntityCollection(result)
                        .sortedBy { it.dateUnix }

                    agedInMemCache.set(UPCOMING_LAUNCHES_KEY, launchEntities) // update cache
                    return launchEntities
                }
            } else {
                throw(Exception("getUpdateLaunches() was not successful: ${response.code()}"))
            }
            emptyList()
        }
    }

    /**
     * fetches all past SpaceX launches
     *
     * If cached data is not present, will contact the [SpaceXApi] to refresh the [AgedMemoryCache]
     */
    @Suppress("UNCHECKED_CAST")
    suspend fun getPastLaunches(): List<LaunchEntity> {

        // check in memory cache
        val cachedData = agedInMemCache.get(PAST_LAUNCHES_KEY) as? List<LaunchEntity>
        return if (!cachedData.isNullOrEmpty()) cachedData
        else {
            Log.d(TAG, "cache is stale, getting fresh data")

            // make the API call
            val response = spaceXApi.spaceXService.getPastLaunchesByCoroutine()
            if (response.isSuccessful) {
                response.body()?.let { result ->

                    // convert from dto to entity
                    val launchEntities = dtoTransformer
                        .transformToLaunchEntityCollection(result)
                        .sortedByDescending { it.dateUnix }

                    agedInMemCache.set(PAST_LAUNCHES_KEY, launchEntities) // update cache
                    return launchEntities
                }
            } else {
                throw(Exception("getUpdateLaunches() was not successful: ${response.code()}"))
            }
            emptyList()
        }
    }

    fun deleteCaches() {
        agedInMemCache.clear()
    }
}

