package com.example.verstka_last.di

import com.example.verstka_last.media.presentation.viewmodel.FavoritesViewModel
import com.example.verstka_last.media.presentation.viewmodel.MediaLibraryViewModel
import com.example.verstka_last.media.presentation.viewmodel.PlaylistsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mediaLibraryModule = module {
    viewModel { MediaLibraryViewModel() }
    viewModel { PlaylistsViewModel() }
    viewModel { FavoritesViewModel() }
}