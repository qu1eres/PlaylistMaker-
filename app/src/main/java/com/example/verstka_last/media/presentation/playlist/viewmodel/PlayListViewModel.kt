package com.example.verstka_last.media.presentation.playlist.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.verstka_last.search.presentation.SearchViewModel.NavigationEvent


class PlaylistsViewModel : ViewModel() {

    private val _navigationEvent = MutableLiveData<NavigationEvent?>()
    val navigationEvent: LiveData<NavigationEvent?> = _navigationEvent



}