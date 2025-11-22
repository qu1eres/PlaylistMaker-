package com.example.verstka_last.sharing.domain.api

interface SharingInteractor {
    fun getShareData(): ShareData
    fun getSupportData(): SupportData
    fun getAgreementData(): AgreementData
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