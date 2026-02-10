package com.example.verstka_last.sharing.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.verstka_last.core.domain.models.Playlist
import com.example.verstka_last.core.domain.models.Track
import com.example.verstka_last.sharing.domain.api.SharingInteractor

class SharingViewModel(
    private val sharingInteractor: SharingInteractor
) : ViewModel() {

    private val _sharingEvent = MutableLiveData<SharingEvent?>()
    val sharingEvent: LiveData<SharingEvent?> = _sharingEvent

    private val _playlistSharingEvent = MutableLiveData<PlaylistSharingEvent?>()
    val playlistSharingEvent: LiveData<PlaylistSharingEvent?> = _playlistSharingEvent

    fun onShareClicked() {
        val shareData = sharingInteractor.getShareData()
        _sharingEvent.value = SharingEvent.ShareApp(
            message = shareData.message,
            link = shareData.link
        )
    }

    fun onSupportClicked() {
        val supportData = sharingInteractor.getSupportData()
        _sharingEvent.value = SharingEvent.ContactSupport(
            email = supportData.email,
            subject = supportData.subject,
            body = supportData.body
        )
    }

    fun onAgreementClicked() {
        val agreementData = sharingInteractor.getAgreementData()
        _sharingEvent.value = SharingEvent.OpenAgreement(
            url = agreementData.url
        )
    }

    fun onSharePlaylistClicked(playlist: Playlist, tracks: List<Track>) {
        if (tracks.isEmpty()) {
            _playlistSharingEvent.value = PlaylistSharingEvent.EmptyPlaylistError
        } else {
            val shareText = sharingInteractor.getPlaylistShareText(playlist, tracks)
            _playlistSharingEvent.value = PlaylistSharingEvent.SharePlaylist(shareText)
        }
    }

    fun onSharingEventHandled() {
        _sharingEvent.value = null
    }

    fun onPlaylistSharingEventHandled() {
        _playlistSharingEvent.value = null
    }

    sealed class SharingEvent {
        data class ShareApp(val message: String, val link: String) : SharingEvent()
        data class ContactSupport(val email: String, val subject: String, val body: String) : SharingEvent()
        data class OpenAgreement(val url: String) : SharingEvent()
    }

    sealed class PlaylistSharingEvent {
        data class SharePlaylist(val shareText: String) : PlaylistSharingEvent()
        object EmptyPlaylistError : PlaylistSharingEvent()
    }
}