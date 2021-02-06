package com.fct.mvvm.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fct.mvvm.data.LaunchEntity
import com.fct.mvvm.data.UIState
import com.fct.mvvm.data.repository.CallbackRepository

/**
 * Implementing observer pattern using Callbacks from the [CallbackRepository], transitioning into LiveData
 *
 * The ViewModel class is designed to store and manage UI-related data in a lifecycle conscious way.
 * The ViewModel class allows data to survive configuration changes such as screen rotations.
 *
 * Additional ViewModel information:
 * @see "https://developer.android.com/topic/libraries/architecture/viewmodel"
 */
class CallBackViewModel(
    private val repository: CallbackRepository
) : ViewModel() {

    // LATEST
    private val latestLaunch by lazy {
        MutableLiveData<UIState<LaunchEntity>>().also { liveData ->

            liveData.value = UIState.loading() // loading state

            repository.getLatestLaunch(object : CallbackRepository.FetchDataCallback {
                override fun onFetchSuccess(launchEntities: List<LaunchEntity>) {
                    launchEntities.firstOrNull()?.let {
                        liveData.value = UIState.success(it) // data state
                    }
                }

                override fun onFetchFailure(error: Exception) {
                    liveData.value = UIState.error(error) // error state
                }
            })
        }
    }

    fun fetchLatestLaunch(): LiveData<UIState<LaunchEntity>> = latestLaunch

    // PAST
    private val pastLaunches by lazy {
        MutableLiveData<UIState<List<LaunchEntity>>>().also { liveData ->

            liveData.value = UIState.loading() // loading state

            repository.getPastLaunches(object : CallbackRepository.FetchDataCallback {
                override fun onFetchSuccess(launchEntities: List<LaunchEntity>) {
                    liveData.value = UIState.success(launchEntities) // data state
                }

                override fun onFetchFailure(error: Exception) {
                    liveData.value = UIState.error(error) // error state
                }
            })
        }
    }

    fun fetchPastLaunches(): LiveData<UIState<List<LaunchEntity>>> = pastLaunches

    // UPCOMING
    private val upcomingLaunches by lazy {
        MutableLiveData<UIState<List<LaunchEntity>>>().also { liveData ->

            liveData.value = UIState.loading() // loading state

            repository.getUpcomingLaunches(object : CallbackRepository.FetchDataCallback {
                override fun onFetchSuccess(launchEntities: List<LaunchEntity>) {
                    liveData.value = UIState.success(launchEntities) // data state
                }

                override fun onFetchFailure(error: Exception) {
                    liveData.value = UIState.error(error) // error state
                }
            })
        }
    }

    fun getUpcomingLaunches(): LiveData<UIState<List<LaunchEntity>>> = upcomingLaunches
}

class CallBackViewModelFactory(private val repository: CallbackRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CallBackViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CallBackViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
