package com.example.verstka_last.di

import com.example.verstka_last.core.data.converters.TrackDbConverter
import com.example.verstka_last.media.data.FavoritesRepositoryImpl
import com.example.verstka_last.media.domain.FavoritesRepository
import org.koin.dsl.module

val repositoryModule = module {
    factory { TrackDbConverter() }
    single<FavoritesRepository> {
        FavoritesRepositoryImpl(get(), get())
    }
}