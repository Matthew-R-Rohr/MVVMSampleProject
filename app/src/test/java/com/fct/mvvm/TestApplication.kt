package com.fct.mvvm

import android.app.Application
import com.squareup.picasso.Picasso

/**
 * For Robolectric testing.
 */
class TestApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // picasso
        initPicasso()
    }

    private fun initPicasso() {
        // picasso tweak for robolectric tests : ensure only one singleton is ever created
        if (!isPicassoInit) {
            isPicassoInit = true
            Picasso.setSingletonInstance(Picasso.Builder(this).build())
        }
    }

    companion object {
        private var isPicassoInit: Boolean = false
    }
}