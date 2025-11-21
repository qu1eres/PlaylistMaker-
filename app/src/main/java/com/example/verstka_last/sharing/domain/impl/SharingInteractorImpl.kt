package com.example.verstka_last.sharing.domain.impl

import com.example.verstka_last.sharing.domain.api.SharingInteractor
import com.example.verstka_last.sharing.domain.api.SharingRepository

class SharingInteractorImpl(
    private val sharingRepository: SharingRepository
) : SharingInteractor {

    override fun getShareData(): ShareData {
        return sharingRepository.getShareData()
    }

    override fun getSupportData(): SupportData {
        return sharingRepository.getSupportData()
    }

    override fun getAgreementData(): AgreementData {
        return sharingRepository.getAgreementData()
    }
}