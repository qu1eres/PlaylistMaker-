package com.example.verstka_last.di

import com.example.verstka_last.core.data.converters.PlaylistDbConverter
import com.example.verstka_last.core.data.converters.TrackDbConverter
import com.example.verstka_last.core.data.db.JsonMapper
import com.example.verstka_last.media.data.favorites.FavoritesRepositoryImpl
import com.example.verstka_last.media.data.playlist.PlaylistRepositoryImpl
import com.example.verstka_last.media.domain.favorites.FavoritesRepository
import com.example.verstka_last.media.domain.playlist.PlaylistRepository
import com.example.verstka_last.playlist_create.data.FileStorage
import com.example.verstka_last.playlist_create.data.impl.FileStorageRepositoryImpl
import com.example.verstka_last.playlist_create.domain.FileStorageRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {
    factory { TrackDbConverter() }
    factory { PlaylistDbConverter() }
    single { JsonMapper(get()) }
    single<FavoritesRepository> {
        FavoritesRepositoryImpl(get(), get())
    }

    single<FileStorageRepository> {
        FileStorageRepositoryImpl(get())
    }

    single<PlaylistRepository> {
        PlaylistRepositoryImpl(get(), get(), get())
    }

    single {
        FileStorage(androidContext())
    }
}