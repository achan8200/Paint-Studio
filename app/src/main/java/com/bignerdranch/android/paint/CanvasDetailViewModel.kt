package com.bignerdranch.android.paint

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import java.util.*

class CanvasDetailViewModel : ViewModel() {

    private val canvasRepository = CanvasRepository.get()
    private val canvasIdLiveData = MutableLiveData<UUID>()

    var canvasLiveData: LiveData<Canvas?> =
        Transformations.switchMap(canvasIdLiveData) { canvasId ->
            canvasRepository.getCanvas(canvasId)
        }

    fun loadCanvas(canvasId: UUID) {
        canvasIdLiveData.value = canvasId
    }

    fun saveCanvas(canvas: Canvas) {
        canvasRepository.updateCanvas(canvas)
    }

    fun deleteCanvas(canvas: Canvas) {
        canvasRepository.deleteCanvas(canvas)
    }
}