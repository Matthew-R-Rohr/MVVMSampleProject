package com.fct.mvvm.data

import android.content.Context
import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fct.mvvm.R
import com.fct.mvvm.extensions.toDateFromUnix
import com.fct.mvvm.extensions.toMonthDayYearString
import kotlinx.android.parcel.Parcelize

/**
 * Launch Entity Model
 */
@Parcelize
@Entity(tableName = "launch_entity")
data class LaunchEntity(
    @PrimaryKey(autoGenerate = false) val flightNumber: Int,
    val name: String,
    val missionId: String,
    val dateUnix: Long,
    val details: String?,
    val upcoming: Boolean,
    val success: Boolean?,
    val rocketId: String?,
    val missionPatchLarge: String?,
    val missionPatchSmall: String?,
    val redditUrl: String?,
    val articleUrl: String?,
    val wikiUrl: String?,
    val webCastUrl: String?,
    val flickrImages: List<String>?,
) : Parcelable

/**
 * Generate a success message if this launch was in the past or a future message
 */
fun LaunchEntity.generateSuccessMessage(context: Context): String = when {
    this.upcoming -> context.getString(R.string.launch_upcoming)
    this.success == true -> context.getString(R.string.launch_success)
    else -> context.getString(R.string.launch_fail)
}

/**
 * Generate a meta data string for this [LaunchEntity]
 *
 * Example: Flight 123 • Successful • 09/29/2013
 */
fun LaunchEntity.generateMetaDataString(context: Context): String {

    // flight number
    val flight = context.getString(R.string.details_flight_number, this.flightNumber)

    // if this was a successful or future launch
    val success = this.generateSuccessMessage(context)

    // date of launch
    val date = this.dateUnix
        .toDateFromUnix()
        .toMonthDayYearString()

    return listOf(flight, success, date)
        .joinToString(" • ")
}

/**
 * Convert a single [LaunchEntity] into a List
 */
fun LaunchEntity.asList(): List<LaunchEntity> = listOf(this)

class LaunchDiffCallback : DiffUtil.ItemCallback<LaunchEntity>() {
    override fun areItemsTheSame(oldItem: LaunchEntity, newItem: LaunchEntity) =
        oldItem.dateUnix == newItem.dateUnix

    override fun areContentsTheSame(oldItem: LaunchEntity, newItem: LaunchEntity): Boolean =
        oldItem == newItem
}
