package com.fct.mvvm.api.model

import com.google.gson.annotations.SerializedName

data class LaunchLinksDto(
    val patch: PatchDto,
    val reddit: RedditDto,
    val flickr: FlickrDto,
    @SerializedName("article") val articleUrl: String?,
    @SerializedName("wikipedia") val wikiUrl: String?,
    @SerializedName("webcast") val webcastUrl: String?
)