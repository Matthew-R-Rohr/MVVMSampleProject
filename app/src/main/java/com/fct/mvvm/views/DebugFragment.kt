package com.fct.mvvm.views

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.fct.mvvm.GlobalApplication
import com.fct.mvvm.data.UIState
import com.fct.mvvm.databinding.FragmentDebugBinding
import com.fct.mvvm.extensions.addToComposite
import com.fct.mvvm.viewmodels.*
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlin.system.exitProcess

private const val TAG = "tcbcDebugFragment"

/**
 * Debugging Fragment used for development & manual testing purposes
 */
class DebugFragment : Fragment() {

    private lateinit var binding: FragmentDebugBinding

    // scope for flow data
    private val coroutineScope by lazy { MainScope() + CoroutineName(TAG) }

    // declaring individual jobs for the current scope allows us to cancel only specific flow observables
    // without having to call [scope.cancel()], which would cancel all observables and require re-init
    private var jobFlowLatest: Job = Job()
    private var jobFlowUpcoming: Job = Job()
    private var jobFlowPast: Job = Job()

    // RxJava disposable
    private val compositeDisposable = CompositeDisposable()

    private val debugViewModel: DebugViewModel by viewModels {
        with((activity?.application as GlobalApplication)) {
            DebugViewModelFactory(flowRepository, liveDataRepository, callBackRepository)
        }
    }
    private val liveDataViewModel: LiveDataViewModel by viewModels {
        LiveDataViewModelFactory((activity?.application as GlobalApplication).liveDataRepository)
    }
    private val callBackViewModel: CallBackViewModel by viewModels {
        CallBackViewModelFactory((activity?.application as GlobalApplication).callBackRepository)
    }
    private val rxJavaViewModel: RxJavaViewModel by viewModels {
        RxJavaViewModelFactory((activity?.application as GlobalApplication).rxJavaRepository)
    }
    private val flowViewModel: FlowViewModel by viewModels {
        FlowViewModelFactory((activity?.application as GlobalApplication).flowRepository)
    }

    //region LifeCycle

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDebugBinding.inflate(inflater, container, false)
        setupUIComponents()
        return binding.root
    }

    override fun onStop() {
        super.onStop()
        unBindObservableDataTiers()
    }

    //endregion

    //region UI

    private fun setupUIComponents() {

        // ensure log out [TextView] is manually scrollable
        binding.txtDebugOutput.movementMethod = ScrollingMovementMethod()

        // event to delete all caches
        binding.btnDeleteCaches.setOnClickListener {
            deleteAllCaches()
        }

        // event to restart the app
        binding.btnRestartApp.setOnClickListener {
            deleteAllCaches()
            context?.startActivity(
                Intent(context, WelcomeActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            )
            exitProcess(0)
        }

        // event to test the Callback data tier flow
        binding.btnTestCallbacks.setOnClickListener {
            updateLog("\n--------------------------------")
            updateLog("Using InMem Cache")
            unBindObservableDataTiers()
            testCallBacksDataTier()
        }

        // event to test the RxJava data tier flow
        binding.btnTestRxjava.setOnClickListener {
            updateLog("\n--------------------------------")
            updateLog("Using ROOM DB Cache")
            unBindObservableDataTiers()
            testRxJavaDataTier()
        }

        // event to test the Flow data tier flow
        binding.btnTestFlow.setOnClickListener {
            updateLog("\n--------------------------------")
            updateLog("Using ROOM DB Cache")
            unBindObservableDataTiers()
            testFlowDataTier()
        }

        // event to test the LiveData data tier flow
        binding.btnTestLiveData.setOnClickListener {
            updateLog("\n--------------------------------")
            updateLog("Using InMem Cache")
            unBindObservableDataTiers()
            testLiveData()
        }
    }

    //endregion

    //region Data Testing Methods

    /**
     * LiveData Testing
     */
    private fun testLiveData() {

        // LATEST
        liveDataViewModel.fetchLatestLaunch().observe(viewLifecycleOwner, {
            when (it.status) {
                UIState.Status.SUCCESS -> updateLog("LATEST LIVEDATA Success: ${it.data?.name}")
                UIState.Status.ERROR -> {
                    Log.e(TAG, it.error?.message, it.error)
                    updateLog("Error: ${it.error?.message}")
                }
                UIState.Status.LOADING -> updateLog("LATEST LIVEDATA - LOADING")
            }
        })

        // UPCOMING
        liveDataViewModel.fetchUpcomingLaunches().observe(viewLifecycleOwner, {
            when (it.status) {
                UIState.Status.SUCCESS -> updateLog("UPCOMING LIVEDATA Success: ${it.data?.size}")
                UIState.Status.ERROR -> {
                    Log.e(TAG, it.error?.message, it.error)
                    updateLog("Error: ${it.error?.message}")
                }
                UIState.Status.LOADING -> updateLog("UPCOMING LIVEDATA - LOADING")
            }
        })

        // PAST
        liveDataViewModel.fetchPastLaunches().observe(viewLifecycleOwner, {
            when (it.status) {
                UIState.Status.SUCCESS -> updateLog("PAST LIVEDATA Success: ${it.data?.size}")
                UIState.Status.ERROR -> {
                    Log.e(TAG, it.error?.message, it.error)
                    updateLog("Error: ${it.error?.message}")
                }
                UIState.Status.LOADING -> updateLog("PAST LIVEDATA - LOADING")
            }
        })
    }

    /**
     * Flow/Coroutine Testing
     */
    private fun testFlowDataTier() {

        // LATEST
        jobFlowLatest = coroutineScope.launch {
            flowViewModel.fetchLatestLaunch().collect {
                when (it.status) {
                    UIState.Status.SUCCESS -> updateLog("LATEST FLOW Success: ${it.data?.name}")
                    UIState.Status.ERROR -> {
                        Log.e(TAG, it.error?.message, it.error)
                        updateLog("Error: ${it.error?.message}")
                    }
                    UIState.Status.LOADING -> updateLog("LATEST FLOW - LOADING")
                }
            }
        }

        // UPCOMING
        jobFlowUpcoming = coroutineScope.launch {
            flowViewModel.fetchUpcomingLaunches().collect {
                when (it.status) {
                    UIState.Status.SUCCESS -> updateLog("UPCOMING FLOW Success: ${it.data?.size}")
                    UIState.Status.ERROR -> {
                        Log.e(TAG, it.error?.message, it.error)
                        updateLog("Error: ${it.error?.message}")
                    }
                    UIState.Status.LOADING -> updateLog("UPCOMING FLOW - LOADING")
                }
            }
        }

        // PAST
        jobFlowPast = coroutineScope.launch {
            flowViewModel.fetchPastLaunches().collect {
                when (it.status) {
                    UIState.Status.SUCCESS -> updateLog("PAST FLOW Success: ${it.data?.size}")
                    UIState.Status.ERROR -> {
                        Log.e(TAG, it.error?.message, it.error)
                        updateLog("Error: ${it.error?.message}")
                    }
                    UIState.Status.LOADING -> updateLog("PAST FLOW - LOADING")
                }
            }
        }
    }

    /**
     * RxJava Testing
     */
    private fun testRxJavaDataTier() {

        // LATEST
        rxJavaViewModel.fetchLatestLaunch()
            .doOnSubscribe { updateLog("LATEST RXJAVA - LOADING") }
            .subscribe { result ->
                when (result?.status) {
                    UIState.Status.SUCCESS -> updateLog("LATEST RXJAVA Success: ${result.data?.name}")
                    UIState.Status.ERROR -> {
                        Log.e(TAG, result.error?.message, result.error)
                        updateLog("Error: ${result.error?.message}")
                    }
                    UIState.Status.LOADING -> updateLog("LATEST RXJAVA - LOADING")
                }
            }.addToComposite(compositeDisposable)

        // UPCOMING
        rxJavaViewModel.fetchUpcomingLaunches()
            .doOnSubscribe { updateLog("UPCOMING RXJAVA - LOADING") }
            .subscribe { result ->
                when (result?.status) {
                    UIState.Status.SUCCESS -> updateLog("UPCOMING RXJAVA Success: ${result.data?.size}")
                    UIState.Status.ERROR -> {
                        Log.e(TAG, result.error?.message, result.error)
                        updateLog("Error: ${result.error?.message}")
                    }
                    UIState.Status.LOADING -> updateLog("LATEST RXJAVA - LOADING")
                }
            }.addToComposite(compositeDisposable)

        // PAST
        rxJavaViewModel.fetchPastLaunches()
            .doOnSubscribe { updateLog("PAST RXJAVA - LOADING") }
            .subscribe { result ->
                when (result?.status) {
                    UIState.Status.SUCCESS -> updateLog("PAST RXJAVA Success: ${result.data?.size}")
                    UIState.Status.ERROR -> {
                        Log.e(TAG, result.error?.message, result.error)
                        updateLog("Error: ${result.error?.message}")
                    }
                    UIState.Status.LOADING -> updateLog("LATEST RXJAVA - LOADING")
                }
            }.addToComposite(compositeDisposable)
    }

    /**
     * Callback Testing
     */
    private fun testCallBacksDataTier() {

        // LATEST
        callBackViewModel.fetchLatestLaunch().observe(viewLifecycleOwner, {
            when (it.status) {
                UIState.Status.SUCCESS -> updateLog("LATEST Callback Success: ${it.data?.name}")
                UIState.Status.ERROR -> {
                    Log.e(TAG, it.error?.message, it.error)
                    updateLog("Error: ${it.error?.message}")
                }
                UIState.Status.LOADING -> updateLog("LATEST Callback - LOADING")
            }
        })

        // PAST
        callBackViewModel.fetchPastLaunches().observe(viewLifecycleOwner, {
            when (it.status) {
                UIState.Status.SUCCESS -> updateLog("PAST Callback Success: ${it.data?.size}")
                UIState.Status.ERROR -> {
                    Log.e(TAG, it.error?.message, it.error)
                    updateLog("Error: ${it.error?.message}")
                }
                UIState.Status.LOADING -> updateLog("PAST Callback - LOADING")
            }
        })

        // PAST
        callBackViewModel.getUpcomingLaunches().observe(viewLifecycleOwner, {
            when (it.status) {
                UIState.Status.SUCCESS -> updateLog("UPCOMING Callback Success: ${it.data?.size}")
                UIState.Status.ERROR -> {
                    Log.e(TAG, it.error?.message, it.error)
                    updateLog("Error: ${it.error?.message}")
                }
                UIState.Status.LOADING -> updateLog("UPCOMING Callback - LOADING")
            }
        })
    }

    //endregion

    //region Helper Methods

    private fun deleteAllCaches() {
        updateLog("Deleting All Caches")
        debugViewModel.deleteAllCaches()
    }

    private fun unBindObservableDataTiers() {
        updateLog("Unbinding All Data Tiers")
        jobFlowLatest.cancel()
        jobFlowPast.cancel()
        jobFlowUpcoming.cancel()
        compositeDisposable.clear()
    }

    @SuppressLint("SetTextI18n")
    private fun updateLog(message: String) {

        // update visual log
        with(binding.txtDebugOutput) {
            post {
                append("\n$message")
            }
        }

        // console log
        Log.d(TAG, message)
    }

    //endregion
}