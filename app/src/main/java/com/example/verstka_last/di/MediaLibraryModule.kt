package com.example.verstka_last.di

import com.example.verstka_last.media.presentation.favorites.viewmodel.FavoritesViewModel
import com.example.verstka_last.media.presentation.playlist.viewmodel.PlaylistsViewModel
import com.example.verstka_last.media.presentation.viewmodel.MediaLibraryViewModel
import com.example.verstka_last.playlist_create.presentation.viewmodel.PlaylistCreatorViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mediaLibraryModule = module {
    viewModel { MediaLibraryViewModel() }
    viewModel { PlaylistsViewModel() }
    viewModel { FavoritesViewModel(get()) }
    viewModel { PlaylistCreatorViewModel(get()) }
}