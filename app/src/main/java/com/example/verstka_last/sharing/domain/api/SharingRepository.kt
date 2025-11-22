package com.example.verstka_last.sharing.domain.api

interface SharingRepository {
    fun getShareData(): ShareData
    fun getSupportData(): SupportData
    fun getAgreementData(): AgreementData
}