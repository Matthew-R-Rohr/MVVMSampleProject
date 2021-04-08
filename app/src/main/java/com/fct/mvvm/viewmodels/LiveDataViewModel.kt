package com.fct.mvvm.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import com.fct.mvvm.data.LaunchEntity
import com.fct.mvvm.data.UIState
import com.fct.mvvm.data.UIState.Companion.error
import com.fct.mvvm.data.UIState.Companion.loading
import com.fct.mvvm.data.repository.LiveDataRepository
import kotlinx.coroutines.Dispatchers

/**
 * SpaceX Launch ViewModel using reactive data (LiveData)
 *
 * This ViewModel handles deciphering data from the repository and wrapping the results in a [UIState].
 *
 * The data is stored and managed within the repository.
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

    /**
     * Emits Latest Launch data within a [UIState] as a LiveData observable
     */
    fun fetchLatestLaunch() = liveData(Dispatchers.IO) {
        emit(loading<LaunchEntity>()) // loading state
        try {
            // return either the empty or success state with data
            repository.getLatestLaunch().also { result ->
                emit(if (result == null) UIState.empty() else UIState.success(result))
            }
        } catch (exception: Exception) {
            emit(error<LaunchEntity>(exception)) // error state
        }
    }

    /**
     * Emits Past Launch data within a [UIState] as a LiveData observable
     */
    fun fetchPastLaunches() = liveData(Dispatchers.IO) {
        emit(loading<List<LaunchEntity>>()) // loading state
        try {
            // return either the empty or success state with data
            repository.getPastLaunches().also { result ->
                emit(if (result.isEmpty()) UIState.empty() else UIState.success(result))
            }
        } catch (exception: Exception) {
            emit(error<List<LaunchEntity>>(exception)) // error state
        }
    }

    /**
     * Emits Upcoming Launch data within a [UIState] as a LiveData observable
     */
    fun fetchUpcomingLaunches() = liveData(Dispatchers.IO) {
        emit(loading<List<LaunchEntity>>()) // loading state
        try {
            // return either the empty or success state with data
            repository.getUpcomingLaunches().also { result ->
                emit(if (result.isEmpty()) UIState.empty() else UIState.success(result))
            }
        } catch (exception: Exception) {
            emit(error<List<LaunchEntity>>(exception)) // error state
        }
    }
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
