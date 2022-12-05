package database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.bignerdranch.android.paint.Canvas

@Database(entities = [ Canvas::class ], version=1)
@TypeConverters(CanvasTypeConverters::class)
abstract class CanvasDatabase : RoomDatabase() {

    abstract fun canvasDao(): CanvasDao
}