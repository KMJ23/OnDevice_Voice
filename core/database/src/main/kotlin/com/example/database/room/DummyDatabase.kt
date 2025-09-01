package com.example.database.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.database.room.data.DummyEntity

@Database(
    entities = [DummyEntity::class],
    version = 1,
    exportSchema = false
)
abstract class DummyDatabase: RoomDatabase() {
    abstract fun dummyDao(): DummyDao
}