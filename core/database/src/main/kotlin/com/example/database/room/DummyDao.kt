package com.example.database.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.database.room.data.DummyEntity

@Dao
interface DummyDao {
    @Query("SELECT * FROM dummy")
    fun getAll(): List<DummyEntity>

    @Query("SELECT * FROM dummy WHERE id IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<DummyEntity>

    @Insert
    fun insert(dummy: List<DummyEntity>)

    @Insert
    fun insert(vararg dummy: DummyEntity)

    @Delete
    fun delete(vararg dummy: DummyEntity)

    @Query("DELETE FROM dummy")
    fun deleteAll()
}