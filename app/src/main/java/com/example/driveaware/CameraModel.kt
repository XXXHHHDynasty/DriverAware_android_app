package com.example.driveaware

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.room.Room
import com.example.driveaware.db.Converters
import com.example.driveaware.db.RecordDao
import com.example.driveaware.db.RecordDatabase
import com.example.driveaware.db.RecordTable
import java.util.*

class CameraModel (application: Application) : AndroidViewModel(application){

    val db : RecordDatabase
    val userDao : RecordDao
    var records = mutableListOf<RecordTable>()


    /**
     * 插入经纬度
     */
    fun insert(longitude:Double, latitude:Double, reason:String?, path: String){
        val record = RecordTable(
            id = UUID.randomUUID(),
//            reason =  "Use Phone while driving",
            reason = reason,
            date =  Date(),
            bitmap = null,
            longitude = longitude,
            latitude = latitude,
            path = path,
        )

        userDao.insert(record)
    }
    fun getAllData(): MutableList<RecordTable> {
        records = userDao.getAll() as MutableList<RecordTable>
        return records;
    }

    init {
        db = Room.databaseBuilder(application,
            RecordDatabase::class.java,
            "record-db").addTypeConverter(Converters()).allowMainThreadQueries().build()
        userDao = db.recordDao()
    }
}