package com.example.tweetssearch

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.example.tweetssearch.data.database.Database
import timber.log.Timber

val Context.dataStore by preferencesDataStore(name = "settings")

class MainApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(CrashReportingTree())
        }

        Database.setDb(applicationContext)
    }

    /** A tree which logs important information for crash reporting.  */
    inner class CrashReportingTree : Timber.Tree() {
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        }
    }

}