package com.fct.mvvm.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fct.mvvm.data.LaunchEntity
import com.fct.mvvm.data.UIState
import com.fct.mvvm.data.UIState.Companion.empty
import com.fct.mvvm.data.UIState.Companion.error
import com.fct.mvvm.data.UIState.Companion.loading
import com.fct.mvvm.data.UIState.Companion.success
import com.fct.mvvm.data.repository.FlowRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

/**
 * SpaceX Launch ViewModel using reactive data (Flow)
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
class FlowViewModel(
    private val repository: FlowRepository
) : ViewModel() {

    /**
     * Returns the Latest Launch data within a [UIState] as a [flow] observable
     */
    fun fetchLatestLaunch() = flow {
        emit(loading<LaunchEntity>()) // loading state

        // return either the empty or success state with data
        repository.getLatestLaunch().also { result ->
            emit(if (result == null) empty() else success(result))
        }
    }
        .flowOn(Dispatchers.IO)
        .catch { exception ->
            emit(error(exception as Exception)) // error state
        }

    /**
     * Returns Past Launch data within a [UIState] as a [flow] observable
     */
    fun fetchPastLaunches() = flow {
        emit(loading<List<LaunchEntity>>()) // loading state

        // return either the empty or success state with data
        repository.getPastLaunches().also { result ->
            emit(if (result.isEmpty()) empty() else success(result))
        }
    }
        .flowOn(Dispatchers.IO)
        .catch { exception ->
            emit(error(exception as Exception)) // error state
        }

    /**
     * Returns Upcoming Launch data within a [UIState] as a [flow] observable
     */
    fun fetchUpcomingLaunches() = flow {
        emit(loading<List<LaunchEntity>>()) // loading state

        // return either the empty or success state with data
        repository.getUpcomingLaunches().also { result ->
            emit(if (result.isEmpty()) empty() else success(result))
        }
    }
        .flowOn(Dispatchers.IO)
        .catch { exception ->
            emit(error(exception as Exception)) // error state
        }

}

class FlowViewModelFactory(private val repository: FlowRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FlowViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FlowViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
