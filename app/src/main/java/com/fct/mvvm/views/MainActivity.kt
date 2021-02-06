package com.fct.mvvm.views

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commitNow
import com.fct.mvvm.R
import com.fct.mvvm.data.DataTierType
import com.fct.mvvm.databinding.ActivityMainBinding

const val EXTRA_DATA_TYPE = "EXTRA_DATA_TYPE"

/**
 * Starts the [MainActivity] using this [DataTierType] type
 */
fun startMainActivity(activity: Activity, dataTierType: DataTierType) {
    activity.run {
        startActivity(Intent(activity, MainActivity::class.java).apply {
            putExtra(EXTRA_DATA_TYPE, dataTierType)
        })
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_left)
    }
}

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var dataTierType: DataTierType? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNav(savedInstanceState)
    }

    /**
     * Navigation needs to be manually setup to account for the various launch data tier fragments
     */
    private fun setupNav(savedInstanceState: Bundle?) {

        // current data tier type
        dataTierType = intent.getSerializableExtra(EXTRA_DATA_TYPE) as DataTierType

        // update title bar to show what data tier is being used
        supportActionBar?.title = getString(
            R.string.title_launch, when (dataTierType) {
                DataTierType.CALL_BACKS -> getString(R.string.general_callbacks)
                DataTierType.LIVE_DATA_COROUTINES -> getString(R.string.general_livedata)
                DataTierType.RX_JAVA -> getString(R.string.general_rxjava)
                DataTierType.FLOW_COROUTINES -> getString(R.string.general_flow)
                else -> getString(R.string.general_not_found)
            }
        )

        // set fragment for selected
        binding.navView.setOnNavigationItemSelectedListener {
            doFragmentSwap(
                when (it.itemId) {
                    R.id.navigation_launches -> getDataTierFragment()
                    R.id.navigation_debug -> DebugFragment()
                    else -> LaunchCallbackFragment()
                }
            )
            true
        }

        // set initially loaded fragment if not already set
        if (savedInstanceState == null) doFragmentSwap(getDataTierFragment() as Fragment)
    }

    private fun getDataTierFragment() = when (dataTierType) {
        DataTierType.CALL_BACKS -> LaunchCallbackFragment()
        DataTierType.LIVE_DATA_COROUTINES -> LaunchLiveDataFragment()
        DataTierType.FLOW_COROUTINES -> LaunchFlowFragment()
        DataTierType.RX_JAVA -> LaunchRxJavaFragment()
        else -> LaunchCallbackFragment()
    }

    private fun doFragmentSwap(fragment: Fragment) = supportFragmentManager.commitNow {
        replace(R.id.nav_host_fragment, fragment)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_right)
    }
}