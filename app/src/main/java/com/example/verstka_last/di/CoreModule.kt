package com.example.verstka_last.di

import android.content.Context
import com.example.verstka_last.core.data.network.NetworkClient
import com.example.verstka_last.core.data.network.RetrofitNetworkClient
import com.example.verstka_last.search.data.network.iTunesAPI
import com.google.gson.Gson
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val coreModule = module {

    single { createRetrofit() }
    single { get<Retrofit>().create(iTunesAPI::class.java) }
    single<NetworkClient> { RetrofitNetworkClient(get()) }

    single(named("app_settings")) {
        get<Context>().getSharedPreferences("app_settings", Context.MODE_PRIVATE)
    }
    single(named("search_history")) {
        get<Context>().getSharedPreferences("search_history", Context.MODE_PRIVATE)
    }
    single(named("local_storage")) {
        get<Context>().getSharedPreferences("local_storage", Context.MODE_PRIVATE)
    }

    factory { Gson() }
}

private fun createRetrofit(): Retrofit {
    return Retrofit.Builder()
        .baseUrl("https://itunes.apple.com")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}