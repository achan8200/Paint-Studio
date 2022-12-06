package com.bignerdranch.android.paint

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.bignerdranch.android.paint.PaintView.Companion.currentBrush
import com.bignerdranch.android.paint.PaintView.Companion.currentWidth
import com.bignerdranch.android.paint.PaintView.Companion.drawings
import com.bignerdranch.android.paint.PaintView.Companion.mBitmap
import com.bignerdranch.android.paint.PaintView.Companion.undoneDrawings
import kotlinx.android.synthetic.main.fragment_canvas.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener
import yuku.ambilwarna.AmbilWarnaDialog
import java.io.ByteArrayOutputStream
import java.util.*

private const val ARG_CANVAS_ID = "canvas_id"

class CanvasFragment : Fragment() {

    private lateinit var mContext: Context
    private lateinit var canvas: Canvas
    private lateinit var editTitle: EditText
    private lateinit var colorButton: ImageButton
    private lateinit var paintbrushButton: ImageButton
    private lateinit var brushWidthButton: ImageButton
    private lateinit var eraserButton: ImageButton
    private lateinit var undoButton: ImageButton
    private lateinit var redoButton: ImageButton
    private lateinit var clearButton: ImageButton
    private lateinit var saveButton: ImageButton
    private var myColor = currentBrush

    private val canvasDetailViewModel: CanvasDetailViewModel by lazy {
        ViewModelProviders.of(this)[CanvasDetailViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        canvas = Canvas()
        val canvasId: UUID = arguments?.getSerializable(ARG_CANVAS_ID) as UUID
        canvasDetailViewModel.loadCanvas(canvasId)
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_canvas, container, false)

        mContext = container!!.context
        editTitle = view.findViewById(R.id.edit_title)
        colorButton = view.findViewById(R.id.colorPicker)
        paintbrushButton = view.findViewById(R.id.paintbrush)
        brushWidthButton = view.findViewById(R.id.brush_width)
        eraserButton = view.findViewById(R.id.eraser)
        undoButton = view.findViewById(R.id.undo)
        redoButton = view.findViewById(R.id.redo)
        clearButton = view.findViewById(R.id.clear)
        saveButton = view.findViewById(R.id.save)

        editTitle.isCursorVisible = false

        return view
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        canvasDetailViewModel.canvasLiveData.observe(
            viewLifecycleOwner
        ) { canvas ->
            canvas?.let {
                this.canvas = canvas
                updateUI()
            }
        }
    }

    override fun onStart() {
        super.onStart()

        val titleWatcher = object : TextWatcher {

            override fun beforeTextChanged(
                sequence: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {

            }

            override fun onTextChanged(
                sequence: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                canvas.title = sequence.toString()
            }

            override fun afterTextChanged(sequence: Editable?) {
                canvas.title = sequence.toString()
            }
        }

        KeyboardVisibilityEvent.setEventListener(
            requireActivity(), KeyboardVisibilityEventListener {
                isOpen ->  editTitle.isCursorVisible = isOpen
            })

        editTitle.addTextChangedListener(titleWatcher)

        colorButton.setBackgroundColor(currentBrush)

        colorButton.setOnClickListener {
            currentColor(myColor)
            openColorPickerDialogue()
        }

        paintbrushButton.setOnClickListener {
            currentColor(myColor)
            paintbrushButton.isEnabled = false
        }

        brushWidthButton.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(mContext)
            val seekBar = SeekBar(mContext)

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
            seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
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
            paintbrushButton.isEnabled = true
        }

        undoButton.setOnClickListener {
            paintView.setUndo()
        }

        redoButton.setOnClickListener {
            paintView.setRedo()
        }

        clearButton.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(mContext)

            dialogBuilder.setMessage("Do you want to remove all strokes permanently?")
                .setCancelable(true)
                .setPositiveButton("Yes") { _, _ ->
                    run {
                        drawings.clear()
                        undoneDrawings.clear()
                        drawing.path.reset()
                        mBitmap.eraseColor(Color.WHITE)
                        paintView.setBackgroundColor(Color.WHITE)
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
            val dialogBuilder = AlertDialog.Builder(mContext)
            var title = canvas.title

            dialogBuilder.setCancelable(true)
                .setPositiveButton("Yes") { _, _ ->
                    run {
                        if (title.replace("\\s".toRegex(), "") == "") {
                            title = "Untitled"
                        }
                        paintView.setDrawingCacheEnabled(true)
                        val imgSaved = MediaStore.Images.Media.insertImage(
                            mContext.contentResolver,
                            paintView.getDrawingCache(),
                            //UUID.randomUUID().toString(),
                            title.replace("\\s".toRegex(), "-"),
                            "drawing"
                        )
                        if (imgSaved != null) {
                            Toast.makeText(mContext, "Saved to photos", Toast.LENGTH_SHORT)
                                .show()
                        }
                        else {
                            Toast.makeText(mContext, "Error, unable to save to photos", Toast.LENGTH_SHORT)
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStop() {
        super.onStop()
        val bmp : Bitmap = convertToBitmap(paintView)
        canvas.bitmap = bitmapToString(bmp)
        Log.i("Tag", canvas.title)
        Log.i("Tag", canvas.bitmap)
        canvasDetailViewModel.saveCanvas(canvas)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        drawings.clear()
        undoneDrawings.clear()
        drawing.path.reset()
        mBitmap.eraseColor(Color.WHITE)
        paintView.setBackgroundColor(Color.WHITE)
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateUI() {
        editTitle.setText(canvas.title)
        bitmap = canvas.bitmap
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun bitmapToString(bitmap: Bitmap): String {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val b = stream.toByteArray()
        return Base64.getEncoder().encodeToString(b)
    }

    private fun convertToBitmap(view: View): Bitmap {
        view.isDrawingCacheEnabled = true
        return view.drawingCache
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

        val colorPickerDialogue = AmbilWarnaDialog(mContext, myColor,
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

        lateinit var bitmap: String
        var drawing = Drawing()

        fun newInstance(canvasId: UUID): CanvasFragment {
            val args = Bundle().apply {
                putSerializable(ARG_CANVAS_ID, canvasId)
            }
            return CanvasFragment().apply {
                arguments = args
            }
        }
    }
}