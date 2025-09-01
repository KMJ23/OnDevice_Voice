package com.example.database.room.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dummy")
data class DummyEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var watchingStartTime: String = "",
    var watchingEndTime: String = "",
    var broadcast: String = "",
    var program: String = "",
    var channelNumber: String = "",
    var channelId: String = ""
)