package com.bignerdranch.android.paint

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.bignerdranch.android.paint.MainActivity.Companion.drawing
import kotlin.math.abs

class PaintView : View {

    private var params : ViewGroup.LayoutParams? = null
    private var mX : Float ?= null
    private var mY : Float ?= null
    private var touchTolerance : Float = 4f

    companion object {
        var drawings : ArrayList<Drawing> = ArrayList()
        var undoneDrawings : ArrayList<Drawing> = ArrayList()
        var currentBrush = Color.BLACK
        var currentWidth = 10f
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
        drawing.paintBrush.isAntiAlias = true
        drawing.paintBrush.color = currentBrush
        drawing.paintBrush.style = Paint.Style.STROKE
        drawing.paintBrush.strokeJoin = Paint.Join.ROUND
        drawing.paintBrush.strokeWidth = currentWidth

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
        drawing = Drawing()
        drawing.paintBrush.color = currentBrush
        drawing.paintBrush.strokeWidth = currentWidth
        drawing.paintBrush.style = Paint.Style.STROKE
        drawing.paintBrush.strokeJoin = Paint.Join.ROUND
        drawing.paintBrush.isAntiAlias = true
        drawings.add(drawing)
        drawing.path.reset()
        drawing.path.moveTo(x,y)
        mX = x
        mY = y
    }

    private fun touchMove(x: Float, y: Float) {
        val dX : Float = abs(x - mX!!)
        val dY : Float = abs(y - mY!!)
        if (dX >= touchTolerance || dY >= touchTolerance) {
            drawing.path.quadTo(mX!!, mY!!, (x + mX!!) / 2, (y + mY!!) / 2)
            mX = x
            mY = y
        }
    }

    private fun touchUp() {
        drawing.path.lineTo(mX!!, mY!!)
        //path.lineTo(mX!!, mY!!)
    }

    override fun onDraw(canvas: Canvas) {
        for (i in drawings.indices) {
            canvas.drawPath(drawings[i].path, drawings[i].paintBrush)
            invalidate()
        }
    }

    fun setUndo() {
        if (drawings.isNotEmpty()) {
            undoneDrawings.add(drawings.removeAt(drawings.size - 1))
            invalidate()
        }
    }

    fun setRedo() {
        if (undoneDrawings.isNotEmpty()) {
            drawings.add(undoneDrawings.removeAt(undoneDrawings.size - 1))
            invalidate()
        }
    }
}