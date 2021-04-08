package com.fct.mvvm.data.repository

import com.fct.mvvm.api.SpaceXApi
import com.fct.mvvm.data.UIState.Companion.empty
import com.fct.mvvm.data.UIState.Companion.error
import com.fct.mvvm.data.UIState.Companion.success
import com.fct.mvvm.data.database.LaunchDao
import io.reactivex.schedulers.Schedulers

/**
 * Example repository implementation using RxJava + Retrofit + ROOM DB cache
 *
 * Methods return cached data using [LaunchDao], otherwise will get fresh data through the [SpaceXApi]
 */
class RxJavaRepository(
    private val launchDao: LaunchDao
) {

    private val spaceXApi = SpaceXApi()
    private val dtoTransformer = LaunchDtoTransformer()

    /**
     * fetches the latest SpaceX launch as RxJava
     *
     * If cached data is not present, will contact the [SpaceXApi] to refresh the data within the SpaceXDatabase
     */
    fun getLatestLaunch() = launchDao.fetchLatestLaunch()
        .subscribeOn(Schedulers.io())
        .map { success(it) }
        .switchIfEmpty(
            spaceXApi.spaceXService.getLatestLaunchByRX()
                .subscribeOn(Schedulers.io())
                .map { response ->
                    if (response.isSuccessful) {
                        val entity = response.body()

                        // convert from dto to entity
                        if (entity != null) {
                            val launchEntity = dtoTransformer.transformToLaunchEntity(entity)
                            launchDao.insertStandard(launchEntity) // update cache
                            success(launchEntity) // success state
                        } else empty()

                    } else {
                        error(Exception("getLatestLaunch() was not successful: ${response.code()}")) // error state
                    }
                }
        )
        .onErrorReturn { error(it as Exception) } // error state

    /**
     * fetches all upcoming SpaceX launches as RxJava
     *
     * If cached data is not present, will contact the [SpaceXApi] to refresh the data within the SpaceXDatabase
     */
    fun getUpcomingLaunches() = launchDao.fetchUpcomingLaunches()
        .subscribeOn(Schedulers.io())
        .filter { it.isNotEmpty() }
        .map { success(it) }
        .switchIfEmpty(
            spaceXApi.spaceXService.getUpcomingLaunchesByRX()
                .subscribeOn(Schedulers.io())
                .map { response ->
                    if (response.isSuccessful) {
                        response.body()?.let { result ->

                            // convert from dto to entity
                            val launchEntities = dtoTransformer
                                .transformToLaunchEntityCollection(result)
                                .sortedBy { it.dateUnix }

                            if (launchEntities.isNotEmpty()) {
                                launchDao.insertAllStandard(launchEntities) // update cache
                                success(launchEntities) // success state
                            } else empty()
                        }
                    } else {
                        error(Exception("getUpcomingLaunches() was not successful: ${response.code()}")) // error state
                    }
                }
        )
        .onErrorReturn { error(it as Exception) } // error state

    /**
     * fetches all past SpaceX launches as RxJava
     *
     * If cached data is not present, will contact the [SpaceXApi] to refresh the data within the SpaceXDatabase
     */
    fun getPastLaunches() = launchDao.fetchPastLaunches()
        .subscribeOn(Schedulers.io())
        .filter { it.size > 1 }
        .map { success(it) }
        .switchIfEmpty(
            spaceXApi.spaceXService.getPastLaunchesByRX()
                .subscribeOn(Schedulers.io())
                .map { response ->
                    if (response.isSuccessful) {
                        response.body()?.let { results ->

                            // convert from dto to entity
                            val launchEntities = dtoTransformer
                                .transformToLaunchEntityCollection(results)
                                .sortedByDescending { it.dateUnix }

                            if (launchEntities.isNotEmpty()) {
                                launchDao.insertAllStandard(launchEntities) // update cache
                                success(launchEntities) // success state
                            } else empty()
                        }
                    } else {
                        error(Exception("getPastLaunches() was not successful: ${response.code()}")) // error state
                    }
                }
        )
        .onErrorReturn { error(it as Exception) } // error state
}

