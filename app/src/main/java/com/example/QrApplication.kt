package com.example

import android.app.Application

class QrApplication : Application() {
    companion object {
        lateinit var instance: QrApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}
