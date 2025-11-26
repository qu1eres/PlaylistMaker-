package com.example.verstka_last.di

import com.example.verstka_last.media.presentation.FavoritesViewModel
import com.example.verstka_last.media.presentation.MediaLibraryViewModel
import com.example.verstka_last.media.presentation.PlaylistsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mediaLibraryModule = module {
    viewModel { MediaLibraryViewModel() }
    viewModel { PlaylistsViewModel() }
    viewModel { FavoritesViewModel() }
}