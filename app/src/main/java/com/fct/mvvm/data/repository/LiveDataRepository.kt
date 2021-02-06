package com.fct.mvvm.data.repository

import android.util.Log
import androidx.lifecycle.liveData
import com.fct.mvvm.api.SpaceXApi
import com.fct.mvvm.data.LaunchEntity
import com.fct.mvvm.data.UIState.Companion.error
import com.fct.mvvm.data.UIState.Companion.loading
import com.fct.mvvm.data.UIState.Companion.success
import io.reactivex.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import java.util.concurrent.TimeUnit

private const val TAG = "tcbcLiveDataRepo"

private val CACHE_EXPIRATION_MILLIS = TimeUnit.MINUTES.toMillis(1)
private const val LATEST_LAUNCH_KEY = "latestLaunch"
private const val PAST_LAUNCHES_KEY = "pastLaunches"
private const val UPCOMING_LAUNCHES_KEY = "upcomingLaunches"

/**
 * Example repository implementation using LiveData/Coroutines + Retrofit + InMem cache
 *
 * Methods return cached data using [AgedMemoryCache], otherwise will return fresh data through the [SpaceXApi]
 */
class LiveDataRepository {

    private val spaceXApi = SpaceXApi()
    private val dtoTransformer = LaunchDtoTransformer()
    private val agedInMemCache = AgedMemoryCache(CACHE_EXPIRATION_MILLIS)

    /**
     * fetches the latest SpaceX launch as LiveData using Coroutines
     *
     * If cached data is not present, will contact the [SpaceXApi] to refresh the [AgedMemoryCache]
     */
    fun getLatestLaunch() = liveData(Dispatchers.IO) {
        emit(loading()) // loading state
        runCatching {
            // check cache
            val cachedData = agedInMemCache.get(LATEST_LAUNCH_KEY) as? LaunchEntity
            cachedData?.let {
                emit(success(cachedData))
                return@liveData
            }
            Log.d(TAG, "cache is stale, getting fresh data")

            // make the API call
            val response = spaceXApi.spaceXService.getLatestLaunchByCoroutine()
            if (response.isSuccessful) {
                response.body()?.let {

                    // convert from dto to entity
                    val launchEntity = dtoTransformer.transformToLaunchEntity(it)

                    agedInMemCache.set(LATEST_LAUNCH_KEY, launchEntity) // update cache
                    emit(success(launchEntity)) // success state
                }
            } else {
                emit(error<LaunchEntity>(Exception("getLatestLaunch() was not successful: ${response.code()}"))) // error state
            }
        }.onFailure {
            emit(error<LaunchEntity>(it as Exception)) // error state
        }
    }

    /**
     * fetches all upcoming SpaceX launches as LiveData using Coroutines
     *
     * If cached data is not present, will contact the [SpaceXApi] to refresh the [AgedMemoryCache]
     */
    @Suppress("UNCHECKED_CAST")
    fun getUpcomingLaunches() = liveData(Dispatchers.IO) {
        emit(loading()) // loading state
        runCatching {
            // check in memory cache
            val cachedData = agedInMemCache.get(UPCOMING_LAUNCHES_KEY) as? List<LaunchEntity>
            cachedData?.let {
                emit(success(cachedData))
                return@liveData
            }
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
                    emit(success(launchEntities)) // success state
                }
            } else {
                emit(error<List<LaunchEntity>>(Exception("getUpdateLaunches() was not successful: ${response.code()}"))) // error state
            }

        }.onFailure {
            emit(error<List<LaunchEntity>>(it as Exception)) // error state
        }
    }

    /**
     * fetches all past SpaceX launches as LiveData using Coroutines
     *
     * If cached data is not present, will contact the [SpaceXApi] to refresh the [AgedMemoryCache]
     */
    @Suppress("UNCHECKED_CAST")
    fun getPastLaunches() = liveData(Dispatchers.IO) {
        emit(loading()) // loading state
        runCatching {
            // check in memory cache
            val cachedData = agedInMemCache.get(PAST_LAUNCHES_KEY) as? List<LaunchEntity>
            cachedData?.let {
                emit(success(cachedData))
                return@liveData
            }
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
                    emit(success(launchEntities)) // success state
                }
            } else {
                emit(error<List<LaunchEntity>>(Exception("getPastLaunches() was not successful: ${response.code()}"))) // error state
            }
        }.onFailure {
            emit(error<List<LaunchEntity>>(it as Exception)) // error state
        }
    }

    fun deleteCaches() {
        agedInMemCache.clear()
    }
}

