package com.example.verstka_last.sharing.domain.api

import com.example.verstka_last.core.domain.models.Playlist
import com.example.verstka_last.core.domain.models.Track

interface SharingInteractor {
    fun getShareData(): ShareData
    fun getSupportData(): SupportData
    fun getAgreementData(): AgreementData
    fun getPlaylistShareText(playlist: Playlist, tracks: List<Track>): String
}

data class ShareData(
    val message: String,
    val link: String
)

data class SupportData(
    val email: String,
    val subject: String,
    val body: String
)

data class AgreementData(
    val url: String
)