package com.fct.mvvm.data

import android.content.Context
import com.fct.mvvm.R
import com.fct.mvvm.createLaunchEntity
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class LaunchEntityKtTest {

    @MockK
    private lateinit var mockContext: Context

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        with(mockContext) {
            every { getString(R.string.details_flight_number, any()) } returns "Flight 124"
            every { getString(R.string.launch_upcoming) } returns "Upcoming"
            every { getString(R.string.launch_fail) } returns "Failed"
            every { getString(R.string.launch_success) } returns "Success"
        }
    }

    @Test
    fun `ensure proper success message is returned`() {

        // arrange
        val entity1 = createLaunchEntity(success = true, upcoming = false)
        val entity2 = createLaunchEntity(success = false, upcoming = false)
        val entity3 = createLaunchEntity(success = false, upcoming = true)

        // act
        val result1 = entity1.generateSuccessMessage(mockContext)
        val result2 = entity2.generateSuccessMessage(mockContext)
        val result3 = entity3.generateSuccessMessage(mockContext)

        // assert
        assertEquals("Success", result1)
        assertEquals("Failed", result2)
        assertEquals("Upcoming", result3)
    }

    @Test
    fun `ensure proper meta data string is generated for a LaunchEntity`() {
        // arrange
        val launchEntity = createLaunchEntity()

        // act
        val metaData = launchEntity.generateMetaDataString(mockContext)

        // assert
        assertEquals("Flight 124 • Success • 01/31/2021", metaData)
    }
}