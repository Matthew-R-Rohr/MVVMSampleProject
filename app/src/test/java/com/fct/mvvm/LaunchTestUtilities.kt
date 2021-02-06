package com.fct.mvvm

import com.fct.mvvm.api.model.*
import com.fct.mvvm.data.LaunchEntity
import junit.framework.TestCase.assertEquals
import java.text.SimpleDateFormat
import java.util.*

/**
 * Generates a date from this String
 * @param dateString format should be in: MM/dd/yyyy hh::mm:ss
 */
fun generateDateFromString(dateString: String): Date? =
    SimpleDateFormat("MM/dd/yyyy hh:mm:ss", Locale.getDefault()).parse(dateString)

/**
 * Creates a testable [LaunchEntity]
 */
fun createLaunchEntity(
    success: Boolean = true,
    upcoming: Boolean = false,
    unixDate: Long = 1612137600,
    flightNumber: Int = -1,
    flickrImages: List<String>? = listOf(
        "https://live.staticflickr.com/65535/50814482042_476d87b020_o.jpg",
        "https://live.staticflickr.com/65535/50813630408_d98c2215f8_o.jpg",
        "https://live.staticflickr.com/65535/50814379121_8834b5362d_o.jpg",
        "https://live.staticflickr.com/65535/50814379056_f032a23955_o.jpg"
    )
) = LaunchEntity(
    flightNumber = flightNumber,
    name = "launch test v1",
    missionId = "12345",
    dateUnix = unixDate,
    details = "details string",
    upcoming = upcoming,
    success = success,
    rocketId = "98765",
    missionPatchLarge = "https://imgur.com/573IfGk.png",
    missionPatchSmall = "https://imgur.com/BrW201S.png",
    redditUrl = "https://www.reddit.com/r/spacex/comments/jhu37i/starlink_general_discussion_and_deployment_thread/",
    articleUrl = "https://spaceflightnow.com/2021/01/08/spacex-deploys-turkish-satellite-in-first-launch-of-2021/",
    wikiUrl = "https://en.wikipedia.org/wiki/T%C3%BCrksat_5A",
    webCastUrl = "https://youtu.be/9I0UYXVqIn8",
    flickrImages = flickrImages
)

/**
 * Creates a testable [LaunchDto]
 */
fun createLaunchDto() = LaunchDto(
    name = "test1",
    flightNumber = 123,
    missionId = "12345",
    dateUnix = 1612137600,
    details = "details string",
    upcoming = false,
    success = null,
    links = LaunchLinksDto(
        patch = PatchDto(
            "https://imgur.com/BrW201S.png",
            "https://imgur.com/573IfGk.png"
        ),
        reddit = RedditDto(
            campaign = "https://www.reddit.com/r/spacex/comments/jhu37i/starlink_general_discussion_and_deployment_thread/",
            launch = null,
            recovery = null
        ),
        flickr = FlickrDto(
            small = emptyList(),
            original = listOf(
                "https://live.staticflickr.com/65535/50814482042_476d87b020_o.jpg",
                "https://live.staticflickr.com/65535/50813630408_d98c2215f8_o.jpg",
                "https://live.staticflickr.com/65535/50814379121_8834b5362d_o.jpg",
                "https://live.staticflickr.com/65535/50814379056_f032a23955_o.jpg"
            )
        ),
        articleUrl = null,
        wikiUrl = "https://en.wikipedia.org/wiki/T%C3%BCrksat_5A",
        webcastUrl = "https://youtu.be/9I0UYXVqIn8"
    ),
    rocket = "rocketID"
)


/**
 * Validates that the [LaunchDto] is properly serialized
 */
fun validateLaunchDto(launchDto: LaunchDto?) {
    with(launchDto) {
        assertEquals("Starlink-16 Test (v1.0)", this?.name)
        assertEquals(114, this?.flightNumber)
        assertEquals("5fbfecce54ceb10a5664c80a", this?.missionId)
        assertEquals(1611147720L, this?.dateUnix)
        assertEquals(
            "This mission launches the sixteenth batch of operational Starlink satellites",
            this?.details
        )
        assertEquals(false, this?.upcoming)
        assertEquals(true, this?.success)
        assertEquals("5e9d0d95eda69973a809d1ec", this?.rocket)
    }

    with(launchDto?.links) {
        assertEquals("https://youtu.be/84Nct_Q9Lqw", this?.webcastUrl)
        assertEquals("https://en.wikipedia.org/wiki/Starlink", this?.wikiUrl)
        assertEquals(null, this?.articleUrl)
    }

    with(launchDto?.links?.flickr) {
        assertEquals(7, this?.original?.size)
        assertEquals(0, this?.small?.size)
    }

    with(launchDto?.links?.reddit) {
        assertEquals(
            "https://www.reddit.com/r/spacex/comments/jhu37i/starlink_general_discussion_and_deployment_thread/",
            this?.campaign
        )
        assertEquals(null, this?.launch)
        assertEquals(null, this?.recovery)
    }

    with(launchDto?.links?.patch) {
        assertEquals("https://imgur.com/BrW201S.png", this?.small)
        assertEquals("https://imgur.com/573IfGk.png", this?.large)
    }
}

/**
 * Validates that the [LaunchEntity] is properly serialized
 */
fun validateEntity(originalEntity: LaunchEntity, updatedEntity: LaunchEntity?) {
    with(updatedEntity) {
        assertEquals(originalEntity.name, this?.name)
        assertEquals(originalEntity.flightNumber, this?.flightNumber)
        assertEquals(originalEntity.missionId, this?.missionId)
        assertEquals(originalEntity.dateUnix, this?.dateUnix)
        assertEquals(originalEntity.details, this?.details)
        assertEquals(originalEntity.upcoming, this?.upcoming)
        assertEquals(originalEntity.success, this?.success)
        assertEquals(originalEntity.rocketId, this?.rocketId)
        assertEquals(originalEntity.webCastUrl, this?.webCastUrl)
        assertEquals(originalEntity.wikiUrl, this?.wikiUrl)
        assertEquals(originalEntity.articleUrl, this?.articleUrl)
        assertEquals(originalEntity.flickrImages?.size, this?.flickrImages?.size)
        assertEquals(originalEntity.redditUrl, this?.redditUrl)
        assertEquals(originalEntity.missionPatchSmall, this?.missionPatchSmall)
        assertEquals(originalEntity.missionPatchLarge, this?.missionPatchLarge)
    }
}