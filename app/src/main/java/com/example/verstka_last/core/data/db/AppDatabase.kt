package com.example.verstka_last.core.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.verstka_last.core.data.db.dao.TrackDao
import com.example.verstka_last.core.data.db.entity.TrackEntity

@Database(version = 1, entities = [TrackEntity::class])
abstract class AppDatabase : RoomDatabase(){

    abstract fun trackDao(): TrackDao

}