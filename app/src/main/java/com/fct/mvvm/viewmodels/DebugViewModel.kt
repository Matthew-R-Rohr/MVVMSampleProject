package com.fct.mvvm.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fct.mvvm.data.repository.CallbackRepository
import com.fct.mvvm.data.repository.FlowRepository
import com.fct.mvvm.data.repository.LiveDataRepository
import kotlinx.coroutines.launch

class DebugViewModel(
    private val flowRepository: FlowRepository,
    private val liveDataRepository: LiveDataRepository,
    private val callbackRepository: CallbackRepository
) : ViewModel() {

    /**
     * removes both ROOM and Repository in-memory caches
     */
    fun deleteAllCaches() {
        viewModelScope.launch {
            flowRepository.deleteCaches() // deletes all DB data
            liveDataRepository.deleteCaches() // removes in mem cache relevant to LiveData implementation
            callbackRepository.deleteCaches() // removes in mem cache relevant to Callback implementation
        }
    }
}

class DebugViewModelFactory(
    private val flowRepository: FlowRepository,
    private val liveDataRepository: LiveDataRepository,
    private val callbackRepository: CallbackRepository
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DebugViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DebugViewModel(flowRepository, liveDataRepository, callbackRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
