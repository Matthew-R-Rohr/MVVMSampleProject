package com.fct.mvvm.views

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.fct.mvvm.data.DataTierType
import com.fct.mvvm.databinding.ActivityWelcomeBinding

/**
 * App entry activity
 */
class WelcomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()

        binding.btnRxJava.setOnClickListener {
            startMainActivity(this, DataTierType.RX_JAVA)
        }

        binding.btnFlow.setOnClickListener {
            startMainActivity(this, DataTierType.FLOW_COROUTINES)
        }

        binding.btnCallbacks.setOnClickListener {
            startMainActivity(this, DataTierType.CALL_BACKS)
        }

        binding.btnLiveData.setOnClickListener {
            startMainActivity(this, DataTierType.LIVE_DATA_COROUTINES)
        }
    }
}