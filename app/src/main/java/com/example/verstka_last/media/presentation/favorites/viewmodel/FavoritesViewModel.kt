package com.example.verstka_last.media.presentation.favorites.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.verstka_last.core.domain.models.Track
import com.example.verstka_last.media.domain.favorites.FavoritesInteractor
import com.example.verstka_last.media.presentation.favorites.FavoriteState
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val favoritesInteractor: FavoritesInteractor
) : ViewModel() {

    private val stateLiveData = MutableLiveData<FavoriteState>()

    fun observeState(): LiveData<FavoriteState> = stateLiveData

    fun fillData() {
        viewModelScope.launch {
            favoritesInteractor
                .getTracks()
                .collect { tracks ->
                    processResult(tracks)
                }
        }
    }

    private fun processResult(tracks: List<Track>) {
        if (tracks.isEmpty()) {
            renderState(FavoriteState.Empty)
        } else {
            renderState(FavoriteState.Content(tracks))
        }
    }

    private fun renderState(state: FavoriteState) {
        stateLiveData.postValue(state)
    }
}