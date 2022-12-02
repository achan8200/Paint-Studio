package com.bignerdranch.android.paint

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.bignerdranch.android.paint.MainActivity.Companion.paintBrush
import com.bignerdranch.android.paint.MainActivity.Companion.path
import kotlin.math.abs

class PaintView : View {

    private var params : ViewGroup.LayoutParams? = null
    private var isCleared : Boolean = false
    private var mX : Float ?= null
    private var mY : Float ?= null
    private var touchTolerance : Float = 4f

    companion object {
        var pathList = ArrayList<Path>()
        var colorList = ArrayList<Int>()
        var undonePathList = ArrayList<Path>()
        var undoneColorList = ArrayList<Int>()
        var currentBrush = Color.BLACK
    }

    constructor(context: Context) : this(context, null) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0){
        init()
    }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        paintBrush.isAntiAlias = true
        paintBrush.color = currentBrush
        paintBrush.style = Paint.Style.STROKE
        paintBrush.strokeJoin = Paint.Join.ROUND
        paintBrush.strokeWidth = 10f

        params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                touchStart(x, y)
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                touchMove(x, y)
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                touchUp()
                invalidate()
            }
            else -> {}
        }
        invalidate()
        return true
    }

    private fun touchStart(x: Float, y: Float) {
        path = Path()
        pathList.add(path)
        colorList.add(currentBrush)
        path.reset()
        path.moveTo(x,y)
        mX = x
        mY = y
    }

    private fun touchMove(x: Float, y: Float) {
        val dX : Float = abs(x - mX!!)
        val dY : Float = abs(y - mY!!)
        if (dX >= touchTolerance || dY >= touchTolerance) {
            path.quadTo(mX!!, mY!!, (x + mX!!) / 2, (y + mY!!) / 2)
            mX = x
            mY = y
        }
    }

    private fun touchUp() {
        path.lineTo(mX!!, mY!!)
    }

    override fun onDraw(canvas: Canvas) {
        for (i in pathList.indices) {
            paintBrush.color = (colorList[i])
            canvas.drawPath(pathList[i], paintBrush)
            invalidate()
        }
    }

    fun setUndo() {
        if (pathList.isNotEmpty()) {
            undonePathList.add(pathList.removeAt(pathList.size - 1))
            undoneColorList.add(colorList.removeAt(colorList.size - 1))
            invalidate()
        }
    }

    fun setRedo() {
        if (undonePathList.isNotEmpty()) {
            pathList.add(undonePathList.removeAt(undonePathList.size - 1))
            colorList.add(undoneColorList.removeAt(undoneColorList.size - 1))
            invalidate()
        }
    }
}