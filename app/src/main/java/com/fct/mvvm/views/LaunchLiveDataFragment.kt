package com.fct.mvvm.views

import androidx.fragment.app.viewModels
import com.fct.mvvm.GlobalApplication
import com.fct.mvvm.data.LaunchType
import com.fct.mvvm.data.UIState
import com.fct.mvvm.viewmodels.LiveDataViewModel
import com.fct.mvvm.viewmodels.LiveDataViewModelFactory

/**
 * Implementing the Launch List using the LiveData tier
 */
class LaunchLiveDataFragment : BaseLaunchFragment() {

    private val liveDataViewModel: LiveDataViewModel by viewModels {
        LiveDataViewModelFactory((activity?.application as GlobalApplication).liveDataRepository)
    }

    /**
     * Update the UI based off of the [LaunchType] and then [UIState]
     */
    override fun loadData(launchType: LaunchType) {
        when (launchType) {
            LaunchType.LATEST -> {
                liveDataViewModel.fetchLatestLaunch()
                    .observe(viewLifecycleOwner, { updateUI(it) })
            }
            LaunchType.PAST -> {
                liveDataViewModel.fetchPastLaunches()
                    .observe(viewLifecycleOwner, { updateUIWithList(it) })
            }
            LaunchType.UPCOMING -> {
                liveDataViewModel.fetchUpcomingLaunches()
                    .observe(viewLifecycleOwner, { updateUIWithList(it) })
            }
        }
    }
}