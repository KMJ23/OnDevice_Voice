package com.example.database.di

import android.content.Context
import androidx.room.Room
import com.example.database.room.DummyDao
import com.example.database.room.DummyDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDummyData(
        @ApplicationContext context: Context
    ): DummyDatabase = Room.databaseBuilder(
        context,
        DummyDatabase::class.java,
        "dummy_database"
    ).fallbackToDestructiveMigration(false).build()

    @Provides
    @Singleton
    fun provideDummyDao(
        dummyDatabase: DummyDatabase
    ): DummyDao = dummyDatabase.dummyDao()
}

