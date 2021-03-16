package com.fct.mvvm.views

import androidx.fragment.app.viewModels
import com.fct.mvvm.GlobalApplication
import com.fct.mvvm.data.LaunchType
import com.fct.mvvm.data.UIState
import com.fct.mvvm.extensions.addToComposite
import com.fct.mvvm.viewmodels.RxJavaViewModel
import com.fct.mvvm.viewmodels.RxJavaViewModelFactory
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable

/**
 * Implementing the Launch List using the RxJava Data Tier
 */
class LaunchRxJavaFragment : BaseLaunchFragment() {

    private val compositeDisposable = CompositeDisposable()

    private val rxJavaViewModel: RxJavaViewModel by viewModels {
        RxJavaViewModelFactory((activity?.application as GlobalApplication).rxJavaRepository)
    }

    override fun onStop() {
        super.onStop()
        compositeDisposable.clear()
    }

    /**
     * Update the UI based off of the [LaunchType] and then [UIState]
     */
    override fun loadData(launchType: LaunchType) {
        when (launchType) {
            LaunchType.LATEST -> {
                rxJavaViewModel.fetchLatestLaunch()
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe { showLoadingUI() }
                    .subscribe(::updateUI)
                    .addToComposite(compositeDisposable)
            }
            LaunchType.PAST -> {
                rxJavaViewModel.fetchPastLaunches()
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe { showLoadingUI() }
                    .subscribe(::updateUIWithList)
                    .addToComposite(compositeDisposable)
            }
            LaunchType.UPCOMING -> {
                rxJavaViewModel.fetchUpcomingLaunches()
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe { showLoadingUI() }
                    .subscribe(::updateUIWithList)
                    .addToComposite(compositeDisposable)
            }
        }
    }
}