package com.bignerdranch.android.paint

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import database.CanvasDatabase
import java.util.*
import java.util.concurrent.Executors

private const val DATABASE_NAME = "canvas-database"

class CanvasRepository private constructor(context: Context) {

    private val database: CanvasDatabase = Room.databaseBuilder(
        context.applicationContext,
        CanvasDatabase::class.java,
        DATABASE_NAME
    ).build()

    private val canvasDao = database.canvasDao()
    private val executor = Executors.newSingleThreadExecutor()

    fun getCanvases(): LiveData<List<Canvas>> = canvasDao.getCanvases()

    fun getCanvas(id: UUID): LiveData<Canvas?> = canvasDao.getCanvas(id)

    fun updateCanvas(canvas: Canvas) {
        executor.execute {
            canvasDao.updateCanvas(canvas)
        }
    }

    fun addCanvas(canvas: Canvas) {
        executor.execute {
            canvasDao.addCanvas(canvas)
        }
    }

    companion object {
        private var INSTANCE: CanvasRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = CanvasRepository(context)
            }
        }

        fun get(): CanvasRepository {
            return INSTANCE ?:
            throw IllegalStateException("CanvasRepository must be initialized")
        }
    }
}