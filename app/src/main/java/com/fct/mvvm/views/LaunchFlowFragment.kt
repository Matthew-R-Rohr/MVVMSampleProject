package com.fct.mvvm.views

import androidx.fragment.app.viewModels
import com.fct.mvvm.GlobalApplication
import com.fct.mvvm.data.LaunchType
import com.fct.mvvm.data.UIState
import com.fct.mvvm.viewmodels.FlowViewModel
import com.fct.mvvm.viewmodels.FlowViewModelFactory
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect

private const val TAG = "tcbcLaunchFlowFragment"

/**
 * Implementing the Launch List using the Flow data tier
 */
class LaunchFlowFragment : BaseLaunchFragment() {

    private val scope by lazy { MainScope() + CoroutineName(TAG) }

    // declaring individual jobs for the current scope allows us to cancel only specific flow observables
    // without having to call [scope.cancel()], which would cancel all observables and require re-init
    private var jobFlowLatest: Job = Job()
    private var jobFlowUpcoming: Job = Job()
    private var jobFlowPast: Job = Job()

    private val flowViewModel: FlowViewModel by viewModels {
        FlowViewModelFactory((activity?.application as GlobalApplication).flowRepository)
    }

    /**
     * Update the UI based off of the [LaunchType] and then [UIState]
     */
    override fun loadData(launchType: LaunchType) {
        unBindObservables()

        when (launchType) {
            LaunchType.LATEST -> {
                jobFlowLatest = scope.launch {
                    flowViewModel.fetchLatestLaunch().collect {
                        updateUI(it)
                    }
                }
            }
            LaunchType.PAST -> {
                jobFlowPast = scope.launch {
                    flowViewModel.fetchPastLaunches().collect {
                        updateUIWithList(it)
                    }
                }
            }
            LaunchType.UPCOMING -> {
                jobFlowUpcoming = scope.launch {
                    flowViewModel.fetchUpcomingLaunches().collect {
                        updateUIWithList(it)
                    }
                }
            }
        }
    }

    private fun unBindObservables() {
        jobFlowLatest.cancel()
        jobFlowPast.cancel()
        jobFlowUpcoming.cancel()
    }
}