package com.fct.mvvm.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fct.mvvm.data.repository.RxJavaRepository

/**
 * SpaceX Launch ViewModel using reactive data (RxJava)
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
class RxJavaViewModel(
    private val repository: RxJavaRepository
) : ViewModel() {
    fun fetchLatestLaunch() = repository.getLatestLaunch()
    fun fetchPastLaunches() = repository.getPastLaunches()
    fun fetchUpcomingLaunches() = repository.getUpcomingLaunches()
}

class RxJavaViewModelFactory(private val repository: RxJavaRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RxJavaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RxJavaViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
