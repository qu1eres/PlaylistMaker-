package com.example.verstka_last.di

import com.example.verstka_last.core.domain.db.FavoritesInteractor
import com.example.verstka_last.core.domain.impl.FavoritesInteractorImpl
import org.koin.dsl.module

val interactorModule = module {
    single<FavoritesInteractor> {
        FavoritesInteractorImpl(get())
    }
}