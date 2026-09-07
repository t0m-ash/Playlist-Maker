package com.practicum.playlistmaker.domain.sharing.impl

import com.practicum.playlistmaker.domain.sharing.ExternalNavigator
import com.practicum.playlistmaker.domain.sharing.SharingInteractor
import com.practicum.playlistmaker.domain.sharing.SharingRepository

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator,
    private val sharingRepository: SharingRepository,
) : SharingInteractor {

    override fun shareApp() {
        externalNavigator.shareLink(sharingRepository.getShareAppLink())
    }

    override fun openTerms() {
        externalNavigator.openLink(sharingRepository.getTermsLink())
    }

    override fun openSupport() {
        externalNavigator.openEmail(sharingRepository.getSupportEmailData())
    }
}
