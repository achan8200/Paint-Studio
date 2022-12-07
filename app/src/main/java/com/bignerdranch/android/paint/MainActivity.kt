package com.bignerdranch.android.paint

import android.annotation.SuppressLint
import android.database.CursorWindow
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import java.lang.reflect.Field
import java.util.*


class MainActivity : AppCompatActivity(),
    GalleryFragment.Callbacks {

    @SuppressLint("DiscouragedPrivateApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        try {
            val field: Field = CursorWindow::class.java.getDeclaredField("sCursorWindowSize")
            field.isAccessible = true
            field.set(null, 100 * 1024 * 1024) //the 100MB is the new size
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val currentFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container)

        if (currentFragment == null) {
            val fragment = GalleryFragment.newInstance()
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit()
        }
    }

    override fun onCanvasSelected(canvasId: UUID) {
        val fragment = CanvasFragment.newInstance(canvasId)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}