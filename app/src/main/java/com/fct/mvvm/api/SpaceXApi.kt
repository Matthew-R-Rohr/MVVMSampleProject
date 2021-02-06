package com.fct.mvvm.api

import com.fct.mvvm.api.model.LaunchDto
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

private const val PAST_LAUNCHES_ENDPOINT = "launches/past"
private const val UPCOMING_LAUNCHES_ENDPOINT = "launches/upcoming"
private const val LATEST_LAUNCH_ENDPOINT = "launches/latest"

/**
 * SpaceX Service API
 *
 * There are three versions of each call, which is the starting point for each of the data tier impl examples.
 *
 * link in constructor is for testability
 */
class SpaceXApi(baseUrl: String = "https://api.spacexdata.com/v4/") {

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
        .build()

    var spaceXService: SpaceXService = retrofit.create(SpaceXService::class.java)

    interface SpaceXService {

        //region Coroutines

        @GET(PAST_LAUNCHES_ENDPOINT)
        suspend fun getPastLaunchesByCoroutine(): Response<List<LaunchDto>>

        @GET(LATEST_LAUNCH_ENDPOINT)
        suspend fun getLatestLaunchByCoroutine(): Response<LaunchDto>

        @GET(UPCOMING_LAUNCHES_ENDPOINT)
        suspend fun getUpcomingLaunchesByCoroutine(): Response<List<LaunchDto>>

        //endregion

        //region RxJava

        @GET(PAST_LAUNCHES_ENDPOINT)
        fun getPastLaunchesByRX(): Single<Response<List<LaunchDto>>>

        @GET(LATEST_LAUNCH_ENDPOINT)
        fun getLatestLaunchByRX(): Single<Response<LaunchDto>>

        @GET(UPCOMING_LAUNCHES_ENDPOINT)
        fun getUpcomingLaunchesByRX(): Single<Response<List<LaunchDto>>>

        //endregion

        //region CALLBACKS - Classic blehp Method

        @GET(PAST_LAUNCHES_ENDPOINT)
        fun getPastLaunchesByCallback(): Call<List<LaunchDto>>

        @GET(LATEST_LAUNCH_ENDPOINT)
        fun getLatestLaunchByCallback(): Call<LaunchDto>

        @GET(UPCOMING_LAUNCHES_ENDPOINT)
        fun getUpcomingLaunchesByCallback(): Call<List<LaunchDto>>

        //endregion
    }
}