package com.fct.mvvm.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fct.mvvm.data.repository.LiveDataRepository

/**
 * SpaceX Launch ViewModel using reactive data (LiveData)
 *
 * While most ViewModel classes are used to store and manage UI-related data in a lifecycle conscious way,
 * this implementation passes the observable data through to the UI layer from the repository.  The data is stored and
 * managed within the repository.
 *
 * For a more traditional implementation, where the data is passed into a LiveData object from the repository and stored
 * within the ViewModel (where it can survive orientation changes), see the [CallBackViewModel]
 *
 * Additional ViewModel information:
 * @see "https://developer.android.com/topic/libraries/architecture/viewmodel"
 */
class LiveDataViewModel(
    private val repository: LiveDataRepository
) : ViewModel() {
    fun fetchLatestLaunch() = repository.getLatestLaunch()
    fun fetchPastLaunches() = repository.getPastLaunches()
    fun fetchUpcomingLaunches() = repository.getUpcomingLaunches()
}

class LiveDataViewModelFactory(private val repository: LiveDataRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LiveDataViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LiveDataViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
