package com.example.verstka_last.di

import androidx.room.Room
import com.example.verstka_last.core.data.db.AppDatabase
import com.example.verstka_last.core.data.db.JsonMapper
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module


val dataModule = module {
    single { Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database.db").build() }
}