package com.fct.mvvm.api.model

import com.google.gson.annotations.SerializedName

data class LaunchDto(

    val name: String,
    @SerializedName("flight_number") val flightNumber: Int,
    @SerializedName("id") val missionId: String,
    @SerializedName("date_unix") val dateUnix: Long,
    val details: String?,
    val upcoming: Boolean,
    val success: Boolean?,
    val links: LaunchLinksDto,
    val rocket: String
)