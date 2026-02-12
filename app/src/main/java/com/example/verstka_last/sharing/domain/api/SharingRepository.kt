package com.example.verstka_last.sharing.domain.api

import com.example.verstka_last.core.domain.models.Playlist
import com.example.verstka_last.core.domain.models.Track

interface SharingRepository {
    fun getShareData(): ShareData
    fun getSupportData(): SupportData
    fun getAgreementData(): AgreementData
    fun getPlaylistShareText(playlist: Playlist, tracks: List<Track>): String
}