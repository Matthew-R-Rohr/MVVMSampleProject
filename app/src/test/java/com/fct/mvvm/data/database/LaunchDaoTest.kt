package com.fct.mvvm.data.database

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.fct.mvvm.RxTrampolineSchedulerRule
import com.fct.mvvm.TestApplication
import com.fct.mvvm.createLaunchEntity
import com.fct.mvvm.validateEntity
import junit.framework.TestCase.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import java.io.IOException

@RunWith(AndroidJUnit4::class)
@Config(application = TestApplication::class)
class LaunchDaoTest {

    private lateinit var launchDao: LaunchDao
    private lateinit var database: SpaceXDatabase

    // unix testing dates
    private val entityLatest =
        createLaunchEntity(upcoming = false, unixDate = 1612137600L, flightNumber = 2) // latest - jan 31
    private val entityPast =
        createLaunchEntity(upcoming = false, unixDate = 1611137600L, flightNumber = 1) // past - jan 20
    private val entityUpcoming =
        createLaunchEntity(upcoming = true, unixDate = 1613137600L, flightNumber = 3) // upcoming - feb 12

    @Rule
    @JvmField
    var testSchedulerRule = RxTrampolineSchedulerRule()

    @Before
    fun setup() {

        // setup test in memory db
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(), SpaceXDatabase::class.java
        )
            .setQueryExecutor { it.run() }
            .allowMainThreadQueries()
            .build()

        launchDao = database.launchDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        database.close()
    }

    @Test
    fun `test getting Latest Launch`() {

        // arrange
        launchDao.insertAll(
            listOf(
                entityLatest,
                entityUpcoming,
                entityPast,
            )
        )

        // assert
        validateEntity(entityLatest, launchDao.getLatestLaunch())
    }

    @Test
    fun `test getting Upcoming Launches`() {

        // arrange
        launchDao.insertAll(
            listOf(
                entityLatest,
                entityUpcoming,
                entityPast,
            )
        )

        // assert
        validateEntity(entityUpcoming, launchDao.getUpcomingLaunches().take(1)[0])
    }

    @Test
    fun `test getting Past Launches`() {

        // arrange
        launchDao.insertAll(
            listOf(
                entityLatest,
                entityUpcoming,
                entityPast,
            )
        )

        // assert
        val results = launchDao.getPastLaunches()
        assertEquals(2, results.size)
        validateEntity(entityLatest, results[0])
        validateEntity(entityPast, results[1])
    }


    @Test
    fun `test getting Latest launches with RxJava`() {

        // arrange
        launchDao.insertAllStandard(
            listOf(
                entityLatest,
                entityUpcoming,
                entityPast,
            )
        )

        // assert
        launchDao.fetchLatestLaunch()
            .test()
            .assertOf {
                with(it.values()[0]) {
                    validateEntity(entityLatest, this)
                }
            }
    }

    @Test
    fun `test getting Upcoming launch with RxJava`() {

        // arrange
        launchDao.insertAllStandard(
            listOf(
                entityLatest,
                entityUpcoming,
                entityPast,
            )
        )

        // act
        launchDao.fetchUpcomingLaunches()
            .test()
            .assertOf {
                with(it.values()[0]) {
                    // assert
                    validateEntity(entityUpcoming, this[0])
                }
            }
    }

    @Test
    fun `test getting Past launches with RxJava`() {

        // arrange
        launchDao.insertAllStandard(
            listOf(
                entityLatest,
                entityUpcoming,
                entityPast,
            )
        )

        // act
        launchDao.fetchPastLaunches()
            .test()
            .assertOf {
                with(it.values()[0]) {
                    // assert
                    assertEquals(2, this.size)
                    validateEntity(entityLatest, this[0])
                    validateEntity(entityPast, this[1])
                }
            }
    }
}