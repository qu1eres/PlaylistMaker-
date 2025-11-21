package com.example.verstka_last.sharing.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.verstka_last.sharing.domain.api.SharingInteractor

class SharingViewModel(
    private val sharingInteractor: SharingInteractor
) : ViewModel() {

    private val _sharingEvent = MutableLiveData<SharingEvent?>()
    val sharingEvent: LiveData<SharingEvent?> = _sharingEvent

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

    fun onSharingEventHandled() {
        _sharingEvent.value = null
    }

    sealed class SharingEvent {
        data class ShareApp(val message: String, val link: String) : SharingEvent()
        data class ContactSupport(val email: String, val subject: String, val body: String) : SharingEvent()
        data class OpenAgreement(val url: String) : SharingEvent()
    }
}