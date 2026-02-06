package com.example.verstka_last.di

import com.example.verstka_last.media.domain.favorites.FavoritesInteractor
import com.example.verstka_last.media.domain.favorites.impl.FavoritesInteractorImpl
import org.koin.dsl.module

val interactorModule = module {
    single<FavoritesInteractor> {
        FavoritesInteractorImpl(get())
    }
}