package com.xly

import android.app.Application
import com.tencent.mmkv.MMKV

class LYApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        MMKV.initialize(this)
    }
}