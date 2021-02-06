package com.fct.mvvm.data

enum class LaunchType(val type: String) {
    LATEST("Latest Launch"),
    UPCOMING("Upcoming Launches"),
    PAST("Past Launches")
}

fun String.convertToLaunchType() =
    LaunchType.values().find { it.type == this } ?: LaunchType.LATEST