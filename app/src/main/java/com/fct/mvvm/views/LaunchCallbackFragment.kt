package com.fct.mvvm.views

import androidx.fragment.app.viewModels
import com.fct.mvvm.GlobalApplication
import com.fct.mvvm.data.LaunchType
import com.fct.mvvm.data.UIState
import com.fct.mvvm.viewmodels.CallBackViewModel
import com.fct.mvvm.viewmodels.CallBackViewModelFactory

/**
 * Implementing the Launch List using the Callbacks data tier
 */
class LaunchCallbackFragment : BaseLaunchFragment() {

    private val callBackViewModel: CallBackViewModel by viewModels {
        CallBackViewModelFactory((activity?.application as GlobalApplication).callBackRepository)
    }

    /**
     * Update the UI based off of the [LaunchType] and then [UIState]
     */
    override fun loadData(launchType: LaunchType) {
        when (launchType) {
            LaunchType.LATEST -> {
                callBackViewModel.fetchLatestLaunch().observe(viewLifecycleOwner, {
                    updateUI(it)
                })
            }
            LaunchType.PAST -> {
                callBackViewModel.fetchPastLaunches().observe(viewLifecycleOwner, {
                    updateUIWithList(it)
                })
            }
            LaunchType.UPCOMING -> {
                callBackViewModel.getUpcomingLaunches().observe(viewLifecycleOwner, {
                    updateUIWithList(it)
                })
            }
        }
    }
}