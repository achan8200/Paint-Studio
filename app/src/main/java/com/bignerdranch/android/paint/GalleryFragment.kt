package com.bignerdranch.android.paint

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item_canvas.*
import java.util.*

private const val TAG = "GalleryFragment"
private const val NON_THIN = "[^iIl1.,']"

class GalleryFragment : Fragment() {

    interface Callbacks {
        fun onCanvasSelected(canvasId: UUID)
    }

    private var callbacks: Callbacks? = null
    private lateinit var emptyGalleryMessage: TextView
    private lateinit var newCanvasButton: Button

    private lateinit var canvasRecyclerView: RecyclerView
    private var adapter: CanvasAdapter? = CanvasAdapter(emptyList())

    private val galleryViewModel: GalleryViewModel by lazy {
        ViewModelProviders.of(this)[GalleryViewModel::class.java]
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_gallery, container, false)

        canvasRecyclerView = view.findViewById(R.id.canvas_recycler_view) as RecyclerView
        canvasRecyclerView.layoutManager = GridLayoutManager(context, 2)
        canvasRecyclerView.adapter = adapter

        emptyGalleryMessage = view.findViewById(R.id.new_canvas) as TextView
        newCanvasButton = view.findViewById(R.id.add_canvas) as Button

        newCanvasButton.setOnClickListener {
            val canvas = Canvas()
            galleryViewModel.addCanvas(canvas)
            callbacks?.onCanvasSelected(canvas.id)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        galleryViewModel.galleryLiveData.observe(
            viewLifecycleOwner
        ) { canvases ->
            canvases?.let {
                Log.i(TAG, "Got canvases ${canvases.size}")
                updateUI(canvases)
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_gallery, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.new_canvas -> {
                val canvas = Canvas()
                galleryViewModel.addCanvas(canvas)
                callbacks?.onCanvasSelected(canvas.id)
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun updateUI(canvases: List<Canvas>) {
        adapter = CanvasAdapter(canvases)
        canvasRecyclerView.adapter = adapter
        if (canvases.isNotEmpty()) {
            emptyGalleryMessage.visibility = View.INVISIBLE
            newCanvasButton.visibility = View.INVISIBLE
            canvasRecyclerView.visibility = View.VISIBLE
        }
        else {
            emptyGalleryMessage.visibility = View.VISIBLE
            newCanvasButton.visibility = View.VISIBLE
            canvasRecyclerView.visibility = View.INVISIBLE
        }
    }

    private inner class CanvasHolder(view: View)
        : RecyclerView.ViewHolder(view), View.OnClickListener {

        private lateinit var canvas: Canvas

        val canvasView: ImageView = itemView.findViewById(R.id.canvas)
        val titleView: TextView = itemView.findViewById(R.id.canvas_title)

        init {
            itemView.setOnClickListener(this)
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(canvas: Canvas) {
            this.canvas = canvas
            val bmp : Bitmap? = stringToBitmap(this.canvas.bitmap)
            if (bmp != null) {
                canvasView.setImageBitmap(bmp.changeBackgroundColor(Color.WHITE))
            } else {
                canvasView.setImageResource(R.drawable.default_canvas)
            }
            titleView.text = if (this.canvas.title == "") {
                "Untitled"
            } else {
                ellipsize(this.canvas.title)
            }
        }

        override fun onClick(v: View?) {
            callbacks?.onCanvasSelected(canvas.id)
        }
    }

    private inner class CanvasAdapter(var canvases: List<Canvas>)
        : RecyclerView.Adapter<CanvasHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
                : CanvasHolder {
            val view = layoutInflater.inflate(R.layout.list_item_canvas, parent, false)
            return CanvasHolder(view)
        }

        override fun getItemCount() = canvases.size

        @RequiresApi(Build.VERSION_CODES.O)
        override fun onBindViewHolder(holder: CanvasHolder, position: Int) {
            val canvas = canvases[position]
            holder.bind(canvas)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun stringToBitmap(encodedString: String): Bitmap? {
        return try {
            val encodeByte: ByteArray = Base64.getDecoder().decode(encodedString)
            BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.size)
        } catch (e: Exception) {
            null
        }
    }

    fun Bitmap.changeBackgroundColor(color: Int): Bitmap {
        val newBitmap = Bitmap.createBitmap(width, height, config)
        val canvas = android.graphics.Canvas(newBitmap)
        canvas.drawColor(color)
        canvas.drawBitmap(this, 0F, 0F, null)
        recycle()
        return newBitmap
    }

    private fun textWidth(str: String): Int {
        return (str.length - str.replace(NON_THIN.toRegex(), "").length / 2)
    }

    private fun ellipsize(text: String, max: Int = 20): String {
        if (textWidth(text) <= max) {
            return text
        }
        var end = text.lastIndexOf(' ', max - 1)
        if (end == -1) {
            return text.substring(0, max - 1) + "…"
        }
        var newEnd = end
        do {
            end = newEnd
            newEnd = text.indexOf(' ', end + 1)
            // No more spaces.
            if (newEnd == -1) newEnd = text.length
        } while (textWidth(text.substring(0, newEnd) + "…") < max)
        return text.substring(0, end) + "…"
    }

    companion object {
        fun newInstance(): GalleryFragment {
            return GalleryFragment()
        }
    }
}