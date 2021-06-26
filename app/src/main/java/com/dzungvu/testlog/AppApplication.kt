package com.dzungvu.testlog

import android.app.Application
import com.dzungvu.packlog.LogcatHelper

class AppApplication : Application() {

    companion object {
        lateinit var instance: AppApplication
    }

    val logcatHelper: LogcatHelper by lazy { LogcatHelper.LogcatBuilder().build(this) }

    override fun onCreate() {
        super.onCreate()
        instance = this
        logcatHelper.start()
    }
}