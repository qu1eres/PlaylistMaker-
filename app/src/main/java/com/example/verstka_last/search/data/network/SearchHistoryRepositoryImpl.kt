package com.example.verstka_last.search.data.network

import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.verstka_last.core.domain.models.Track
import com.example.verstka_last.search.domain.api.SearchHistoryRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistoryRepositoryImpl(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson
) : SearchHistoryRepository {
    private val key = "search_history"

    override fun saveTrack(track: Track) {
        val history = loadHistory().toMutableList()

        history.removeAll { it.trackId == track.trackId }

        history.add(0, track)

        val limitedHistory = if (history.size > 10) history.subList(0, 10) else history

        val json = gson.toJson(limitedHistory)
        sharedPreferences.edit { putString(key, json) }
    }

    override fun loadHistory(): List<Track> {
        val json = sharedPreferences.getString(key, null)
        return if (json != null) {
            val type = object : TypeToken<List<Track>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } else {
            emptyList()
        }
    }

    override fun clearHistory() {
        sharedPreferences.edit { remove(key) }
    }
}