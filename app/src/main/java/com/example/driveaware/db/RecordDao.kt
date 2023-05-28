package com.example.driveaware.db

import androidx.room.*
import java.util.UUID

@Dao
interface RecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg records: RecordTable)
    @Delete
    fun delete(vararg records: RecordTable)
    @Update
    fun update(vararg records: RecordTable)
    @Query("SELECT * FROM records")
    fun getAll(): List<RecordTable>
    @Query("SELECT * FROM records WHERE id IN (:ids)")
    fun getAllByIds(ids: List<UUID>): List<RecordTable>
}