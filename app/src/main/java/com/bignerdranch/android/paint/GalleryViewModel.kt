package com.bignerdranch.android.paint

import androidx.lifecycle.ViewModel

class GalleryViewModel : ViewModel() {

    private val canvasRepository = CanvasRepository.get()
    val galleryLiveData = canvasRepository.getCanvases()

    fun addCanvas(canvas: Canvas) {
        canvasRepository.addCanvas(canvas)
    }
}