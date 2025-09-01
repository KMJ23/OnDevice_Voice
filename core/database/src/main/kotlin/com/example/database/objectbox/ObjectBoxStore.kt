package com.example.database.objectbox

import android.content.Context
import com.example.database.objectbox.data.MyObjectBox
import io.objectbox.BoxStore

object ObjectBoxStore {

    lateinit var store: BoxStore
        private set

    fun init(context: Context) {
        store = MyObjectBox.builder().androidContext(context).build()
    }
}
