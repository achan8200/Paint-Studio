package com.bignerdranch.android.paint

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import com.bignerdranch.android.paint.PaintView.Companion.colorList
import com.bignerdranch.android.paint.PaintView.Companion.currentBrush
import com.bignerdranch.android.paint.PaintView.Companion.pathList
import com.bignerdranch.android.paint.PaintView.Companion.undoneColorList
import com.bignerdranch.android.paint.PaintView.Companion.undonePathList
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import com.github.dhaval2404.colorpicker.ColorPickerDialog
import com.github.dhaval2404.colorpicker.model.ColorShape
import kotlinx.android.synthetic.main.paint_view.*
import yuku.ambilwarna.AmbilWarnaDialog

class MainActivity : AppCompatActivity() {

    private lateinit var colorButton: ImageButton
    private var myColor = paintBrush.color

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        colorButton = findViewById(R.id.colorPicker)
        val paintbrushButton = findViewById<ImageButton>(R.id.paintbrush)
        val eraserButton = findViewById<ImageButton>(R.id.eraser)
        val undoButton = findViewById<ImageButton>(R.id.undo)
        val redoButton = findViewById<ImageButton>(R.id.redo)
        val clearButton = findViewById<ImageButton>(R.id.clear)

        colorButton.setOnClickListener {
            currentColor(myColor)
            openColorPickerDialogue()
        }

        paintbrushButton.setOnClickListener {
            currentColor(myColor)
        }

        eraserButton.setOnClickListener {
            currentColor(Color.WHITE)
        }

        undoButton.setOnClickListener {
            paintView.setUndo()
        }

        redoButton.setOnClickListener {
            paintView.setRedo()
        }

        clearButton.setOnClickListener {
            pathList.clear()
            undonePathList.clear()
            colorList.clear()
            undoneColorList.clear()
            path.reset()
        }
    }

    private fun openColorPickerDialogue() {
        /*
        ColorPickerDialog
            .Builder(this)
            .setColorShape(ColorShape.SQUARE)
            .setDefaultColor(myColor)
            .setColorListener { color, _ ->
                colorButton.setBackgroundColor(color)
                currentColor(color)
                myColor = color
            }
            .show()
         */

        val colorPickerDialogue = AmbilWarnaDialog(this, myColor,
            object : AmbilWarnaDialog.OnAmbilWarnaListener {
                override fun onCancel(dialog: AmbilWarnaDialog?) {
                }

                override fun onOk(dialog: AmbilWarnaDialog?, color: Int) {
                    colorButton.setBackgroundColor(color)
                    currentColor(color)
                    myColor = color
                }
            })
        colorPickerDialogue.show()

    }

    private fun currentColor(color: Int) {
        currentBrush = color
        path = Path()
    }

    companion object {
        var path = Path()
        var paintBrush = Paint()
    }
}