package com.example.verstka_last.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.verstka_last.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistory(private val context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("search_history", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val key = "search_history"

    fun saveTrack(track: Track) {
        val history = loadHistory().toMutableList()

        history.removeAll { it.trackId == track.trackId }

        history.add(0, track)

        val limitedHistory = if (history.size > 10) history.subList(0, 10) else history

        val json = gson.toJson(limitedHistory)
        sharedPreferences.edit { putString(key, json) }
    }

    fun loadHistory(): List<Track> {
        val json = sharedPreferences.getString(key, null)
        return if (json != null) {
            val type = object : TypeToken<List<Track>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } else {
            emptyList()
        }
    }

    fun clearHistory() {
        sharedPreferences.edit { remove(key) }
    }
}