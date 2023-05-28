package com.example.driveaware

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.room.Room
import com.example.driveaware.db.Converters
import com.example.driveaware.db.RecordDao
import com.example.driveaware.db.RecordDatabase
import com.example.driveaware.db.RecordTable
import java.util.*

class RecordListViewModel(application: Application) : AndroidViewModel(application){

    val db : RecordDatabase
    val userDao : RecordDao
    var records = mutableListOf<RecordTable>()

    init {
        db = Room.databaseBuilder(application,
            RecordDatabase::class.java,
            "record-db").addTypeConverter(Converters()).allowMainThreadQueries().build()
        userDao = db.recordDao()
        for (i in 0 until 10) {
            val record = RecordTable(
                id = UUID.randomUUID(),
                reason =  "Use Phone while driving",
                date =  Date(),
                bitmap = null,
                longitude = 42.26109218187743,
                latitude = -71.80465658972277,
                path = "",
            )

            userDao.insert(record)
        }
        records = userDao.getAll() as MutableList<RecordTable>
    }
}