package com.zipdori.autoplanner.modules

import android.app.Application
import android.content.Context

//글로벌 컨텍스트 호출용. 예시 : tvProgressMessage?.text = App.context().getString(R.string.loading)
class App : Application() {
    init {
        instance = this
    }
    companion object {
        private var instance: App? = null
        fun context() : Context {
            return instance!!.applicationContext
        }
    }
}