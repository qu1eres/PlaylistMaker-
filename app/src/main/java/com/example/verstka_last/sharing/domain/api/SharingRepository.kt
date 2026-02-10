package com.example.verstka_last.sharing.domain.api

import com.example.verstka_last.core.domain.models.Playlist

interface SharingRepository {
    fun getShareData(): ShareData
    fun getSupportData(): SupportData
    fun getAgreementData(): AgreementData
    fun sharePlaylist(playlist: Playlist)
}