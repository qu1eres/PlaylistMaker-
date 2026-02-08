package com.example.verstka_last.di

import android.media.MediaPlayer
import com.example.verstka_last.player.data.impl.PlayerRepositoryImpl
import com.example.verstka_last.player.domain.api.PlayerInteractor
import com.example.verstka_last.player.domain.api.PlayerRepository
import com.example.verstka_last.player.domain.impl.PlayerInteractorImpl
import com.example.verstka_last.player.ui.PlayerViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val playerModule = module {
    factory<PlayerRepository> { PlayerRepositoryImpl { MediaPlayer() } }
    factory<PlayerInteractor> { PlayerInteractorImpl(get()) }
    viewModel { PlayerViewModel(get(), get(), get()) }
}