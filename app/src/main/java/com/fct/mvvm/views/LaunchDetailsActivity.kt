package com.fct.mvvm.views

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commitNow
import com.fct.mvvm.R
import com.fct.mvvm.data.LaunchEntity
import com.fct.mvvm.databinding.ActivityLaunchDetailsBinding

const val LAUNCH_ENTITY_TYPE = "LAUNCH_ENTITY_TYPE"

/**
 * Starts the [LaunchDetailsActivity] using this [LaunchEntity]
 */
fun startLaunchDetailsActivity(activity: Activity, launchEntity: LaunchEntity) {
    activity.run {
        startActivity(Intent(activity, LaunchDetailsActivity::class.java).apply {
            putExtra(LAUNCH_ENTITY_TYPE, launchEntity)
        })
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_left)
    }
}

class LaunchDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLaunchDetailsBinding

    //region LifeCycle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLaunchDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // get [LaunchEntity] and then setup nav/fragments
        val entity = intent.getParcelableExtra<LaunchEntity>(LAUNCH_ENTITY_TYPE)
        entity?.let {
            setupNav(it.name)
            supportFragmentManager.takeIf { savedInstanceState == null }?.commitNow {
                replace(R.id.launch_details_container, LaunchDetailsFragment(it))
            }
        }

        if (entity == null) throw IllegalArgumentException("LaunchEntity should not be null")
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_right)
    }

    //endregion

    //region Setup Methods

    private fun setupNav(name: String) {

        // show/enable the toolbar back arrow
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        with(binding.toolbar) {
            title = getString(R.string.title_details, name)
            setSupportActionBar(this)
            setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
            setNavigationOnClickListener { onBackPressed() }
        }
    }

    //endregion
}