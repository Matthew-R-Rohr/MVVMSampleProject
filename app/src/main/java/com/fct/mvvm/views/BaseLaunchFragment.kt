package com.fct.mvvm.views

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.fct.mvvm.R
import com.fct.mvvm.adapters.LaunchListAdapter
import com.fct.mvvm.data.*
import com.fct.mvvm.databinding.FragmentLaunchBinding
import com.fct.mvvm.viewmodels.LaunchViewModel

private const val TAG = "tcbcBaseLaunchFragment"

abstract class BaseLaunchFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private val launchViewModel: LaunchViewModel by viewModels()
    private lateinit var binding: FragmentLaunchBinding

    //region LifeCycle

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLaunchBinding.inflate(inflater, container, false)
        setupUIComponents()
        return binding.root
    }

    //endregion

    //region UI Methods

    private fun setupUIComponents() {

        // setup the spinner launch selection
        context?.let {
            ArrayAdapter.createFromResource(
                it, R.array.launch_array, android.R.layout.simple_spinner_item
            ).also { adapter ->

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinnerLaunchSelection.adapter = adapter

                // store the selected filter value in the view model
                launchViewModel.filterSelection = adapter.getItem(0)?.toString() ?: ""
            }
        }

        // bind spinner selection
        binding.spinnerLaunchSelection.onItemSelectedListener = this

        // setup the list
        binding.rvLaunchList.run {
            this.layoutManager = LinearLayoutManager(requireContext())
            adapter = LaunchListAdapter(clickListener)
        }
    }

    // onClick to start details activity
    private val clickListener: (entity: LaunchEntity) -> Unit = { entity ->
        activity?.let {
            startLaunchDetailsActivity(it, entity)
        }
    }

    /**
     * Updates UI based off of a list of data
     */
    protected fun updateUIWithList(result: UIState<List<LaunchEntity>>) {
        when (result.status) {
            UIState.Status.SUCCESS -> submitList(result.data)
            UIState.Status.ERROR -> showMessageUI(getString(R.string.generic_load_error, result.error?.message))
            UIState.Status.LOADING -> showLoadingUI()
            UIState.Status.EMPTY -> showMessageUI(getString(R.string.generic_empty_message))
        }
    }

    /**
     * Update UI based off of a single data
     */
    protected fun updateUI(result: UIState<LaunchEntity>) {
        when (result.status) {
            UIState.Status.SUCCESS -> submitList(result.data?.asList())
            UIState.Status.ERROR -> showMessageUI(getString(R.string.generic_load_error, result.error?.message))
            UIState.Status.LOADING -> showLoadingUI()
            UIState.Status.EMPTY -> showMessageUI(getString(R.string.generic_empty_message))
        }
    }

    /**
     * Hides the current list and shows a message TextView
     */
    private fun showMessageUI(message: String) {
        with(binding) {
            txtMessage.text = message
            txtMessage.visibility = View.VISIBLE
            pbLoadingIndicator.visibility = View.GONE
            rvLaunchList.visibility = View.GONE
        }
    }

    /**
     * Hides the current list and shows a loading progress bar
     */
    protected fun showLoadingUI() {
        with(binding) {
            txtMessage.visibility = View.GONE
            pbLoadingIndicator.visibility = View.VISIBLE
            rvLaunchList.visibility = View.GONE
        }
    }

    /**
     * Updates the list UI
     */
    private fun submitList(data: List<LaunchEntity>?) {

        if (!data.isNullOrEmpty())
            Log.d(TAG, "refreshing list with ${data.size} items")

        with(binding) {
            (rvLaunchList.adapter as LaunchListAdapter).submitList(data)
            txtMessage.visibility = View.GONE
            pbLoadingIndicator.visibility = View.GONE
            rvLaunchList.visibility = View.VISIBLE
        }
    }

    //endregion

    //region Data Methods

    /**
     * Orders inherited fragments to load this data set
     */
    abstract fun loadData(launchType: LaunchType)

    //endregion

    //region Helper Methods

    /**
     * Spinner filter selection
     */
    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
        launchViewModel.filterSelection =
            binding.spinnerLaunchSelection.adapter.getItem(position).toString()

        submitList(emptyList())
        loadData(launchViewModel.filterSelection.convertToLaunchType())
    }

    override fun onNothingSelected(parent: AdapterView<*>?) = Unit

    //endregion
}