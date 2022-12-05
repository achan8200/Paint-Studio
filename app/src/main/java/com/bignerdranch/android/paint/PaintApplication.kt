package com.bignerdranch.android.paint

import android.app.Application

class PaintApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        CanvasRepository.initialize(this)
    }
}