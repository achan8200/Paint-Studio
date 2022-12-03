package com.bignerdranch.android.paint

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.os.Bundle
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bignerdranch.android.paint.PaintView.Companion.colorList
import com.bignerdranch.android.paint.PaintView.Companion.currentBrush
import com.bignerdranch.android.paint.PaintView.Companion.pathList
import com.bignerdranch.android.paint.PaintView.Companion.undoneColorList
import com.bignerdranch.android.paint.PaintView.Companion.undonePathList
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
        val brushWidthButton = findViewById<ImageButton>(R.id.brush_width)
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

        brushWidthButton.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(this)
            val seekBar = SeekBar(this)

            seekBar.max = 100
            seekBar.progress = paintBrush.strokeWidth.toInt()

            dialogBuilder.setTitle("Brush Width")
                .setMessage(paintBrush.strokeWidth.toInt().toString() + "px")
                .setView(seekBar)
                .setPositiveButton("Select") { _, _ ->
                    run {
                        paintBrush.strokeWidth = seekBar.progress.toFloat()
                    }
                }
                .setNegativeButton("Cancel") { _, _ ->
                    run {

                    }
                }
            val dialog = dialogBuilder.create()
            var width: Int
            seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    width = progress
                    dialog.setMessage(width.toString() + "px")
                }
                override fun onStartTrackingTouch(seekBar: SeekBar) {

                }
                override fun onStopTrackingTouch(seekBar: SeekBar) {

                }
            })
            dialog.show()
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
            val dialogBuilder = AlertDialog.Builder(this)

            dialogBuilder.setMessage("Do you want to remove all strokes permanently?")
                .setCancelable(true)
                .setPositiveButton("Yes") { _, _ ->
                    run {
                        pathList.clear()
                        undonePathList.clear()
                        colorList.clear()
                        undoneColorList.clear()
                        path.reset()
                    }
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.cancel()
                }

            val alert = dialogBuilder.create()
            alert.setTitle("Warning")
            alert.show()
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