package com.fct.mvvm.viewmodels

import androidx.lifecycle.ViewModel
import com.fct.mvvm.data.LaunchEntity

class LaunchDetailsViewModel : ViewModel() {

    var launchEntity: LaunchEntity? = null
    var currentPhotoIndex = 0
        private set

    /**
     * Return the next photo in the list of [LaunchEntity.flickrImages] list.
     *
     * If the list is blank, returns a null
     */
    fun getNextLaunchPhoto(): String? {

        launchEntity?.flickrImages.takeIf { !it.isNullOrEmpty() }?.let {

            // reset loop if needed
            if (currentPhotoIndex == it.size) currentPhotoIndex = 0

            return it[currentPhotoIndex++]
        }
        return null
    }
}