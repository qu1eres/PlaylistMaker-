package com.example.verstka_last.di

import com.example.verstka_last.media.domain.favorites.FavoritesInteractor
import com.example.verstka_last.media.domain.favorites.impl.FavoritesInteractorImpl
import com.example.verstka_last.media.domain.playlist.PlaylistInteractor
import com.example.verstka_last.media.domain.playlist.impl.PlaylistInteractorImpl
import com.example.verstka_last.playlist_create.domain.PlaylistCreatorInteractor
import com.example.verstka_last.playlist_create.domain.impl.PlaylistCreatorInteractorImpl
import org.koin.dsl.module

val interactorModule = module {
    single<FavoritesInteractor> {
        FavoritesInteractorImpl(get())
    }
    single<PlaylistInteractor> {
        PlaylistInteractorImpl(get())
    }
    single<PlaylistCreatorInteractor> {
        PlaylistCreatorInteractorImpl(get(), get())
    }
}