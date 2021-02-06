package com.fct.mvvm.data.repository

import com.fct.mvvm.api.model.LaunchDto
import com.fct.mvvm.data.LaunchEntity

class LaunchDtoTransformer {

    /**
     * Transforms/flattens a list of [LaunchDto] into a list of [LaunchEntity]
     */
    fun transformToLaunchEntityCollection(
        launchDtoList: List<LaunchDto>
    ): List<LaunchEntity> = launchDtoList.map { transformToLaunchEntity(it) }

    /**
     * Transforms/flattens a [LaunchDto] into a [LaunchEntity]
     */
    fun transformToLaunchEntity(
        launchDto: LaunchDto
    ): LaunchEntity = LaunchEntity(
        flightNumber = launchDto.flightNumber,
        name = launchDto.name,
        missionId = launchDto.missionId,
        dateUnix = launchDto.dateUnix,
        details = launchDto.details,
        upcoming = launchDto.upcoming,
        success = launchDto.success,
        rocketId = launchDto.rocket,
        missionPatchLarge = launchDto.links.patch.large,
        missionPatchSmall = launchDto.links.patch.small,
        redditUrl = launchDto.links.reddit.campaign,
        articleUrl = launchDto.links.articleUrl,
        wikiUrl = launchDto.links.wikiUrl,
        webCastUrl = launchDto.links.webcastUrl,
        flickrImages = if (launchDto.links.flickr.small.isNotEmpty()) launchDto.links.flickr.small else launchDto.links.flickr.original,
    )
}
