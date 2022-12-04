package com.bignerdranch.android.paint

import android.graphics.Color
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bignerdranch.android.paint.PaintView.Companion.currentBrush
import com.bignerdranch.android.paint.PaintView.Companion.currentWidth
import com.bignerdranch.android.paint.PaintView.Companion.drawings
import com.bignerdranch.android.paint.PaintView.Companion.undoneDrawings
import kotlinx.android.synthetic.main.paint_view.*
import yuku.ambilwarna.AmbilWarnaDialog
import java.util.UUID


class MainActivity : AppCompatActivity() {

    private lateinit var colorButton: ImageButton
    private var myColor = currentBrush

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
        val saveButton = findViewById<ImageButton>(R.id.save)

        colorButton.setBackgroundColor(myColor)

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
            seekBar.progress = currentWidth.toInt()

            dialogBuilder.setTitle("Brush Width")
                .setMessage(currentWidth.toInt().toString() + "px")
                .setView(seekBar)
                .setPositiveButton("Select") { _, _ ->
                    run {
                        currentWidth = seekBar.progress.toFloat()
                        currentSize(currentWidth)
                    }
                }
                .setNegativeButton("Cancel") { _, _ ->
                    run {
                        // left intentionally blank
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
                    // left intentionally blank
                }
                override fun onStopTrackingTouch(seekBar: SeekBar) {
                    // left intentionally blank
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
                        drawings.clear()
                        undoneDrawings.clear()
                        drawing.path.reset()
                    }
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.cancel()
                }

            val alert = dialogBuilder.create()
            alert.setTitle("Warning")
            alert.show()
        }

        saveButton.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(this)

            dialogBuilder.setCancelable(true)
                .setPositiveButton("Yes") { _, _ ->
                    run {
                        paintView.setDrawingCacheEnabled(true)
                        val imgSaved = MediaStore.Images.Media.insertImage(
                            contentResolver,
                            paintView.getDrawingCache(),
                            UUID.randomUUID().toString(),
                            "drawing"
                        )
                        if (imgSaved != null) {
                            Toast.makeText(this, "Saved to photos", Toast.LENGTH_SHORT)
                                .show()
                        }
                        paintView.destroyDrawingCache()
                    }
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.cancel()
                }

            val alert = dialogBuilder.create()
            alert.setTitle("Save as Photo?")
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
    }

    private fun currentSize(width: Float) {
        currentWidth = width
    }

    companion object {
        var drawing = Drawing()
    }
}