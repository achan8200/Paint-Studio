package com.bignerdranch.android.paint

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Canvas(@PrimaryKey val id: UUID = UUID.randomUUID(),
                  var title: String = "",
                  var bitmap: String = ""
                    )