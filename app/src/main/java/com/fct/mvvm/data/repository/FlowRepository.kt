package com.fct.mvvm.data.repository

import android.util.Log
import com.fct.mvvm.api.SpaceXApi
import com.fct.mvvm.data.LaunchEntity
import com.fct.mvvm.data.database.LaunchDao

private const val TAG = "tcbcFlowRepo"

/**
 * Example repository implementation using Flow/Coroutines + Retrofit + ROOM DB cache
 *
 * Methods return cached data using [LaunchDao], otherwise will get fresh data through the [SpaceXApi]
 */
class FlowRepository(
    private val launchDao: LaunchDao
) {

    private val spaceXApi = SpaceXApi()
    private val dtoTransformer = LaunchDtoTransformer()

    /**
     * Returns latest SpaceX launch
     *
     * If cached data is not present, will contact the [SpaceXApi] to refresh the data within the SpaceXDatabase
     */
    suspend fun getLatestLaunch(): LaunchEntity? {
        val cached = launchDao.getLatestLaunch()
        return if (cached != null) cached
        else {
            Log.d(TAG, "empty cache, getting data")
            val response = spaceXApi.spaceXService.getLatestLaunchByCoroutine()
            if (response.isSuccessful) {
                response.body()?.let {
                    val entity = dtoTransformer.transformToLaunchEntity(it)

                    // update cache
                    launchDao.insert(entity)

                    // return result
                    return entity
                }
            } else {
                throw Exception("getLatestLaunch() was not successful: ${response.code()}") // error state)
            }
        }
    }

    /**
     * Returns all upcoming SpaceX launches
     *
     * If cached data is not present, will contact the [SpaceXApi] to refresh the data within the SpaceXDatabase
     */
    suspend fun getUpcomingLaunches(): List<LaunchEntity> {
        val cachedList = launchDao.getUpcomingLaunches()
        return if (cachedList.size > 1) cachedList
        else {
            Log.d(TAG, "empty cache, getting data")
            val response = spaceXApi.spaceXService.getUpcomingLaunchesByCoroutine()
            if (response.isSuccessful) {
                response.body()?.let {
                    val entityList = dtoTransformer.transformToLaunchEntityCollection(it)

                    // update cache
                    launchDao.insertAll(entityList)

                    // return results
                    return entityList
                }
            } else {
                throw Exception("getUpcomingLaunches() was not successful: ${response.code()}") // error state)
            }
            emptyList()
        }
    }

    /**
     * Returns all past SpaceX launches
     *
     * If cached data is not present, will contact the [SpaceXApi] to refresh the data within the SpaceXDatabase
     */
    suspend fun getPastLaunches(): List<LaunchEntity> {
        val cachedList = launchDao.getPastLaunches()
        return if (cachedList.size > 1) cachedList
        else {
            Log.d(TAG, "empty cache, getting data")
            val response = spaceXApi.spaceXService.getPastLaunchesByCoroutine()
            if (response.isSuccessful) {
                response.body()?.let {
                    val entityList = dtoTransformer.transformToLaunchEntityCollection(it)

                    // update cache
                    launchDao.insertAll(entityList)

                    // return results
                    return entityList
                }
            } else {
                throw Exception("getPastLaunches() was not successful: ${response.code()}") // error state)
            }
            emptyList()
        }
    }

    suspend fun deleteCaches() {
        launchDao.deleteAll()
    }
}

