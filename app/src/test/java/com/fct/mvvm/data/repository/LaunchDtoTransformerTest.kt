package com.fct.mvvm.data.repository

import com.fct.mvvm.createLaunchDto
import org.junit.Assert.assertEquals
import org.junit.Test

class LaunchDtoTransformerTest {

    private val transformer = LaunchDtoTransformer()

    @Test
    fun `ensure LaunchDto parses into a LaunchEntity`() {
        // arrange
        val launchDto = createLaunchDto()

        // act
        val launchEntity = transformer.transformToLaunchEntity(launchDto)

        // assert
        with(launchEntity) {
            assertEquals(flightNumber, launchDto.flightNumber)
            assertEquals(name, launchDto.name)
            assertEquals(missionId, launchDto.missionId)
            assertEquals(dateUnix, launchDto.dateUnix)
            assertEquals(details, launchDto.details)
            assertEquals(upcoming, launchDto.upcoming)
            assertEquals(success, launchDto.success)
            assertEquals(rocketId, launchDto.rocket)
            assertEquals(missionPatchLarge, launchDto.links.patch.large)
            assertEquals(missionPatchSmall, launchDto.links.patch.small)
            assertEquals(redditUrl, launchDto.links.reddit.campaign)
            assertEquals(articleUrl, launchDto.links.articleUrl)
            assertEquals(wikiUrl, launchDto.links.wikiUrl)
            assertEquals(webCastUrl, launchDto.links.webcastUrl)
            assertEquals(flickrImages, launchDto.links.flickr.original)
        }
    }
}