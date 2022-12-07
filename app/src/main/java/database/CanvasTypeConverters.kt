package database

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Base64.*
import androidx.annotation.RequiresApi
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream
import java.util.*


class CanvasTypeConverters {

    @TypeConverter
    fun toUUID(uuid: String?): UUID? {
        return UUID.fromString(uuid)
    }

    @TypeConverter
    fun fromUUID(uuid: UUID?): String? {
        return uuid?.toString()
    }

    /*
    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun bitmapToString(bitmap: Bitmap): String {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val b = stream.toByteArray()
        return Base64.getEncoder().encodeToString(b)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun stringToBitmap(encodedString: String): Bitmap? {
        return try {
            val encodeByte: ByteArray = Base64.getDecoder().decode(encodedString)
            BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.size)
        } catch (e: Exception) {
            null
        }
    }

     */
}