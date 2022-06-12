package com.jdars.h20

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        context =applicationContext
    }
    companion object{

        @SuppressLint("StaticFieldLeak")
        private var context: Context? = null
        fun getAppContext(): Context? {
            return context
        }
    }
}