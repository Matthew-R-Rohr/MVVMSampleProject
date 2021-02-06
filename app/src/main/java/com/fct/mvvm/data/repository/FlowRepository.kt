package com.fct.mvvm.data.repository

import android.util.Log
import com.fct.mvvm.api.SpaceXApi
import com.fct.mvvm.data.LaunchEntity
import com.fct.mvvm.data.UIState.Companion.error
import com.fct.mvvm.data.UIState.Companion.loading
import com.fct.mvvm.data.UIState.Companion.success
import com.fct.mvvm.data.database.LaunchDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

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
     * observes the latest SpaceX launch as a Flow with Coroutines
     *
     * If cached data is not present, will contact the [SpaceXApi] to refresh the data within the SpaceXDatabase
     */
    fun getLatestLaunch() = launchDao.getLatestLaunch()
        .distinctUntilChanged()
        // alternatively we could use .map, but using [flatMapLatest] allows for better control over the [UIState]
        .flatMapLatest { cachedEntity ->
            flow {
                if (cachedEntity != null) emit(success(cachedEntity)) // success state
                else {

                    emit(loading<LaunchEntity>()) // loading state
                    Log.d(TAG, "empty cache, getting data")

                    val response = spaceXApi.spaceXService.getLatestLaunchByCoroutine()
                    if (response.isSuccessful) {
                        response.body()?.let {
                            // update cache : this will also cause the observable from the DB to fire,
                            // which will pass back the [cachedEntity] above
                            launchDao.insert(dtoTransformer.transformToLaunchEntity(it))
                        }
                    } else {
                        emit(error<LaunchEntity>(Exception("getLatestLaunch() was not successful: ${response.code()}"))) // error state
                    }
                }
            }.flowOn(Dispatchers.IO).catch { exception ->
                emit(error(exception as Exception)) // error state
            }
        }

    /**
     * observes all upcoming SpaceX launches as a Flow with Coroutines
     *
     * If cached data is not present, will contact the [SpaceXApi] to refresh the data within the SpaceXDatabase
     */
    fun getUpcomingLaunches() = launchDao.getUpcomingLaunches()
        .distinctUntilChanged()
        .flatMapLatest { cachedEntity ->
            flow {
                if (cachedEntity.isNotEmpty()) emit(success(cachedEntity)) // success state
                else {

                    emit(loading<List<LaunchEntity>>()) // loading state
                    Log.d(TAG, "empty cache, getting data")

                    val response = spaceXApi.spaceXService.getUpcomingLaunchesByCoroutine()
                    if (response.isSuccessful) {
                        response.body()?.let {
                            // update cache : this will also cause the observable from the DB to fire,
                            // which will pass back the [cachedEntity] above
                            launchDao.insertAll(dtoTransformer.transformToLaunchEntityCollection(it))
                        }
                    } else {
                        emit(error<List<LaunchEntity>>(Exception("getUpcomingLaunches() was not successful: ${response.code()}"))) // error state
                    }
                }
            }.flowOn(Dispatchers.IO).catch { exception ->
                emit(error(exception as Exception)) // error state
            }
        }

    /**
     * observes all past SpaceX launches as a Flow with Coroutines
     *
     * If cached data is not present, will contact the [SpaceXApi] to refresh the data within the SpaceXDatabase
     */
    fun getPastLaunches() = launchDao.getPastLaunches()
        .distinctUntilChanged()
        .flatMapLatest { cachedEntity ->
            flow {
                if (cachedEntity.size > 1) emit(success(cachedEntity)) // success state
                else {

                    emit(loading<List<LaunchEntity>>()) // loading state
                    Log.d(TAG, "empty cache, getting data")

                    val response = spaceXApi.spaceXService.getPastLaunchesByCoroutine()
                    if (response.isSuccessful) {
                        response.body()?.let {
                            // update cache : this will also cause the observable from the DB to fire,
                            // which will pass back the [cachedEntity] above
                            launchDao.insertAll(dtoTransformer.transformToLaunchEntityCollection(it))
                        }
                    } else {
                        emit(error<List<LaunchEntity>>(Exception("getPastLaunches() was not successful: ${response.code()}"))) // error state
                    }
                }
            }.flowOn(Dispatchers.IO).catch { exception ->
                emit(error(exception as Exception)) // error state
            }
        }

    suspend fun deleteCaches() {
        launchDao.deleteAll()
    }
}

