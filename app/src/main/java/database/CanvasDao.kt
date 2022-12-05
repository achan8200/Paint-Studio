package database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
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
}