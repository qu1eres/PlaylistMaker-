package com.example.verstka_last.di

import com.example.verstka_last.media.domain.FavoritesInteractor
import com.example.verstka_last.media.domain.impl.FavoritesInteractorImpl
import org.koin.dsl.module

val interactorModule = module {
    single<FavoritesInteractor> {
        FavoritesInteractorImpl(get())
    }
}