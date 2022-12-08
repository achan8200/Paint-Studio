package database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.bignerdranch.android.paint.Canvas
import java.util.*

@Dao
interface CanvasDao {

    @Query("SELECT * FROM canvas")
    fun getCanvases(): LiveData<List<Canvas>>

    @Query("SELECT * FROM canvas WHERE id=(:id)")
    fun getCanvas(id: UUID): LiveData<Canvas?>

    @Update
    fun updateCanvas(canvas: Canvas)

    @Insert
    fun addCanvas(canvas: Canvas)

    @Delete
    fun deleteCanvas(canvas: Canvas)
}