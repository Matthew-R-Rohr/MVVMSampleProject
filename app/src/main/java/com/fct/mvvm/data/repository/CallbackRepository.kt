package com.fct.mvvm.data.repository

import android.util.Log
import com.fct.mvvm.api.SpaceXApi
import com.fct.mvvm.api.model.LaunchDto
import com.fct.mvvm.data.LaunchEntity
import com.fct.mvvm.data.asList
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit

private const val TAG = "tcbcCallbackRepo"

private val CACHE_EXPIRATION_MILLIS = TimeUnit.MINUTES.toMillis(1)
private const val LATEST_LAUNCH_KEY = "latestLaunch"
private const val PAST_LAUNCHES_KEY = "pastLaunches"
private const val UPCOMING_LAUNCHES_KEY = "upcomingLaunches"

/**
 * Example repository implementation using traditional Callbacks + Retrofit + InMem cache
 *
 * Methods return cached data using [AgedMemoryCache], otherwise will return fresh data through the [SpaceXApi]
 */
class CallbackRepository {

    private val spaceXApi = SpaceXApi()
    private val dtoTransformer = LaunchDtoTransformer()
    private val agedInMemCache = AgedMemoryCache(CACHE_EXPIRATION_MILLIS)

    interface FetchDataCallback {
        fun onFetchSuccess(launchEntities: List<LaunchEntity>)
        fun onFetchFailure(error: Exception)
    }

    /**
     * fetches the latest SpaceX launch using tradition callbacks
     *
     * If cached data is not present, will contact the [SpaceXApi] to refresh the [AgedMemoryCache]
     */
    fun getLatestLaunch(callback: FetchDataCallback) {

        // check in memory cache
        val cachedData = agedInMemCache.get(LATEST_LAUNCH_KEY) as? LaunchEntity
        cachedData?.let {
            return callback.onFetchSuccess(it.asList())
        }
        Log.d(TAG, "cache is stale, getting fresh data")

        // make the API call
        spaceXApi.spaceXService.getLatestLaunchByCallback().enqueue(object : Callback<LaunchDto> {
            override fun onResponse(call: Call<LaunchDto>, response: Response<LaunchDto>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        val launchEntity = dtoTransformer.transformToLaunchEntity(it)  // convert from dto to entity
                        agedInMemCache.set(LATEST_LAUNCH_KEY, launchEntity) // update cache
                        callback.onFetchSuccess(launchEntity.asList()) // success
                    }
                } else {
                    callback.onFetchFailure(Exception("getLatestLaunch() was not successful: ${response.code()}"))
                }
            }

            override fun onFailure(call: Call<LaunchDto>, t: Throwable) {
                callback.onFetchFailure(t as Exception)
            }
        })
    }

    /**
     * fetches all upcoming SpaceX launches using tradition callbacks
     *
     * If cached data is not present, will contact the [SpaceXApi] to refresh the [AgedMemoryCache]
     */
    @Suppress("UNCHECKED_CAST")
    fun getUpcomingLaunches(callback: FetchDataCallback) {

        // check in memory cache
        val cachedData = agedInMemCache.get(UPCOMING_LAUNCHES_KEY) as? List<LaunchEntity>
        cachedData?.let {
            return callback.onFetchSuccess(it)
        }
        Log.d(TAG, "cache is stale, getting fresh data")

        // make the API call
        spaceXApi.spaceXService.getUpcomingLaunchesByCallback()
            .enqueue(object : Callback<List<LaunchDto>> {
                override fun onResponse(call: Call<List<LaunchDto>>, response: Response<List<LaunchDto>>) {
                    if (response.isSuccessful) {
                        response.body()?.let { result ->

                            // convert from dto to entity
                            val launchCollection = dtoTransformer
                                .transformToLaunchEntityCollection(result)
                                .sortedBy { it.dateUnix }

                            agedInMemCache.set(UPCOMING_LAUNCHES_KEY, launchCollection) // update cache
                            callback.onFetchSuccess(launchCollection)  // success
                        }
                    } else {
                        callback.onFetchFailure(Exception("getUpcomingLaunches() was not successful: ${response.code()}"))
                    }
                }

                override fun onFailure(call: Call<List<LaunchDto>>, t: Throwable) {
                    callback.onFetchFailure(t as Exception)
                }
            })
    }

    /**
     * fetches all past SpaceX launches using tradition callbacks
     *
     * If cached data is not present, will contact the [SpaceXApi] to refresh the [AgedMemoryCache]
     */
    @Suppress("UNCHECKED_CAST")
    fun getPastLaunches(callback: FetchDataCallback) {

        // check in memory cache
        val cachedData = agedInMemCache.get(PAST_LAUNCHES_KEY) as? List<LaunchEntity>
        cachedData?.let {
            return callback.onFetchSuccess(it)
        }
        Log.d(TAG, "cache is stale, getting fresh data")

        // make the API call
        spaceXApi.spaceXService.getPastLaunchesByCallback()
            .enqueue(object : Callback<List<LaunchDto>> {
                override fun onResponse(call: Call<List<LaunchDto>>, response: Response<List<LaunchDto>>) {
                    if (response.isSuccessful) {
                        response.body()?.let { result ->

                            // convert from dto to entity
                            val launchCollection = dtoTransformer
                                .transformToLaunchEntityCollection(result)
                                .sortedByDescending { it.dateUnix }

                            agedInMemCache.set(PAST_LAUNCHES_KEY, launchCollection) // update cache
                            callback.onFetchSuccess(launchCollection) // success
                        }
                    } else {
                        callback.onFetchFailure(Exception("getPastLaunches() was not successful: ${response.code()}"))
                    }
                }

                override fun onFailure(call: Call<List<LaunchDto>>, t: Throwable) {
                    callback.onFetchFailure(t as Exception)
                }
            })
    }

    fun deleteCaches() {
        agedInMemCache.clear()
    }
}

