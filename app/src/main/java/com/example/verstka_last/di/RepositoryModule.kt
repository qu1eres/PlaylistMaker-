package com.example.verstka_last.di

import com.example.verstka_last.core.data.converters.TrackDbConverter
import com.example.verstka_last.core.data.repository.FavoritesRepositoryImpl
import com.example.verstka_last.core.domain.db.FavoritesRepository
import org.koin.dsl.module

val repositoryModule = module {
    factory { TrackDbConverter() }
    single<FavoritesRepository> {
        FavoritesRepositoryImpl(get(), get())
    }
}