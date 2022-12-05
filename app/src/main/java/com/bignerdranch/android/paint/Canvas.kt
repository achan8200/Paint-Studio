package com.bignerdranch.android.paint

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Canvas(@PrimaryKey val id: UUID = UUID.randomUUID(),
                  var title: String = "",
                //var bitmap: Bitmap ?= null
                    )