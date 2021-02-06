package com.fct.mvvm.api

import com.fct.mvvm.RxTrampolineSchedulerRule
import com.fct.mvvm.validateLaunchDto
import io.mockk.MockKAnnotations
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.File

class SpaceXApiTest {

    private lateinit var api: SpaceXApi
    private var mockWebServer: MockWebServer = MockWebServer()

    @Rule
    @JvmField
    var testSchedulerRule = RxTrampolineSchedulerRule()


    @Before
    fun setup() {
        MockKAnnotations.init(this)
        mockWebServer.start()

        // important, this sets the base URL relative to our test JSON file
        api = SpaceXApi(mockWebServer.url("/").toString())
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `test API serialization using Coroutines`() {

        // arrange
        mockWebServer.enqueue(getMockResponse("launchResponse_v4.json"))

        // act
        runBlocking {
            val response = api.spaceXService.getLatestLaunchByCoroutine()
            with(response.body()) {
                validateLaunchDto(this)  // assert
            }
        }
    }

    @Test
    fun `test API serialization using RxJava`() {

        // arrange
        mockWebServer.enqueue(getMockResponse("launchResponse_v4.json"))

        // act
        api.spaceXService.getLatestLaunchByRX()
            .test()
            .assertOf {
                with(it.values()[0].body()) {
                    validateLaunchDto(this) // assert
                }
            }
    }

    @Test
    fun `test API serialization using Callbacks`() {

        // arrange
        mockWebServer.enqueue(getMockResponse("launchResponse_v4.json"))

        // act
        val response = api.spaceXService.getLatestLaunchByCallback().execute()

        // assert
        validateLaunchDto(response.body())
    }

    /**
     * Generates [MockResponse] for the given file
     */
    @Suppress("SameParameterValue")
    private fun getMockResponse(file: String) = MockResponse().setResponseCode(200).setBody(
        File("../app/src/androidTest/assets/$file").readText(Charsets.UTF_8)
    )
}