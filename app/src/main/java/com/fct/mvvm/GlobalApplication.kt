package com.fct.mvvm

import android.app.Application
import com.facebook.flipper.android.AndroidFlipperClient
import com.facebook.flipper.android.utils.FlipperUtils
import com.facebook.flipper.plugins.databases.DatabasesFlipperPlugin
import com.facebook.flipper.plugins.inspector.DescriptorMapping
import com.facebook.flipper.plugins.inspector.InspectorFlipperPlugin
import com.facebook.flipper.plugins.sharedpreferences.SharedPreferencesFlipperPlugin
import com.facebook.soloader.SoLoader
import com.fct.mvvm.data.database.SpaceXDatabase
import com.fct.mvvm.data.repository.CallbackRepository
import com.fct.mvvm.data.repository.FlowRepository
import com.fct.mvvm.data.repository.LiveDataRepository
import com.fct.mvvm.data.repository.RxJavaRepository
import com.squareup.picasso.Picasso

class GlobalApplication : Application() {

    // database and the repository are only created when they're needed
    // rather than when the application starts
    private val database by lazy { SpaceXDatabase.getDatabase(this) }
    val callBackRepository by lazy { CallbackRepository() }
    val liveDataRepository by lazy { LiveDataRepository() }
    val flowRepository by lazy { FlowRepository(database.launchDao()) }
    val rxJavaRepository by lazy { RxJavaRepository(database.launchDao()) }

    override fun onCreate() {
        super.onCreate()

        // picasso
        initPicasso()

        // flipper by Facebook.  Useful for debugging
        initFlipper()
    }

    private fun initFlipper() {
        SoLoader.init(this, false)

        // skip if not debug build
        if (BuildConfig.DEBUG && FlipperUtils.shouldEnableFlipper(this)) {
            val context = this
            with(AndroidFlipperClient.getInstance(context)) {
                addPlugin(InspectorFlipperPlugin(context, DescriptorMapping.withDefaults()))
                addPlugin(DatabasesFlipperPlugin(context))
                addPlugin(SharedPreferencesFlipperPlugin(context))
                start()
            }
        }
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