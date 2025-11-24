package com.example.verstka_last.di

import com.example.verstka_last.search.data.network.SearchHistoryRepositoryImpl
import com.example.verstka_last.search.data.network.TrackRepositoryImpl
import com.example.verstka_last.search.domain.api.SearchHistoryInteractor
import com.example.verstka_last.search.domain.api.TrackRepository
import com.example.verstka_last.search.domain.api.TracksInteractor
import com.example.verstka_last.search.domain.impl.SearchHistoryInteractorImpl
import com.example.verstka_last.search.domain.impl.TracksInteractorImpl
import com.example.verstka_last.search.presentation.SearchViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val searchModule = module {
    single<TrackRepository> { TrackRepositoryImpl(get()) }
    single<TracksInteractor> { TracksInteractorImpl(get()) }
    single<SearchHistoryInteractor> {
        SearchHistoryInteractorImpl(
            SearchHistoryRepositoryImpl(
                get(named("search_history")),
                get()
            )
        )
    }

    viewModel { SearchViewModel(get(), get()) }
}
