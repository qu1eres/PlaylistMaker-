package com.example.verstka_last.di

import com.example.verstka_last.sharing.data.impl.SharingRepositoryImpl
import com.example.verstka_last.sharing.domain.api.SharingInteractor
import com.example.verstka_last.sharing.domain.api.SharingRepository
import com.example.verstka_last.sharing.domain.impl.SharingInteractorImpl
import com.example.verstka_last.sharing.ui.SharingViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val sharingModule = module {
    factory<SharingRepository> { SharingRepositoryImpl(get()) }
    factory<SharingInteractor> { SharingInteractorImpl(get()) }

    viewModel { SharingViewModel(get()) }
}