package com.example.database.objectbox.data

import io.objectbox.annotation.Entity
import io.objectbox.annotation.HnswIndex
import io.objectbox.annotation.Id

@Entity
data class Segment(
    @Id var id: Long = 0,
    var uuid: String = "",
    var text: String = "",
    @HnswIndex(dimensions = 768) var vector: FloatArray = floatArrayOf()
)