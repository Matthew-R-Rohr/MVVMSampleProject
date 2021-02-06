package com.fct.mvvm.extensions

import com.fct.mvvm.generateDateFromString
import org.junit.Assert.assertEquals
import org.junit.Test

class ExtensionsKtTest {

    @Test
    fun `test parsing from Unix Long into Date`() {

        // arrange
        val unixDate: Long = 1612137600

        // act
        val date = unixDate.toDateFromUnix()

        // assert
        val expected = generateDateFromString("01/31/2021 16:00:00")
        assertEquals(expected, date)
    }

    @Test
    fun `test parsing from Date to String format`() {
        // arrange
        val today = generateDateFromString("01/31/2021 16:00:00")

        // act
        val dateString = today?.toMonthDayYearString()

        // assert
        assertEquals("01/31/2021", dateString)
    }

    @Test
    fun `test returning an empty String instead of a null`() {
        // arrange
        val nullString: String? = null

        // act
        val cleanString = nullString.returnEmptyStringIfNull()

        // assert
        assertEquals("", cleanString)
    }
}