package com.example.tweetssearch

import android.app.Application
import com.chibatching.kotpref.Kotpref
import timber.log.Timber

class MainApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(CrashReportingTree())
        }

        Kotpref.init(applicationContext)
    }

    /** A tree which logs important information for crash reporting.  */
    inner class CrashReportingTree : Timber.Tree() {
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        }
    }

}