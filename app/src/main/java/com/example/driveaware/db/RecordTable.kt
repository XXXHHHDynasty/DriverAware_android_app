package com.example.driveaware.db

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Blob
import java.util.*

@Entity(tableName = "records")
data class RecordTable(
    @PrimaryKey val id: UUID,
    @ColumnInfo val reason: String?,
    @ColumnInfo val path: String?,
    @ColumnInfo val date: Date?,
    @ColumnInfo(name = "photo") val bitmap: Bitmap? = null,
    @ColumnInfo val longitude: Double? = 0.0,
    @ColumnInfo val latitude: Double? = 0.0
)