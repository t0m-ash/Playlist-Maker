package com.practicum.playlistmaker.domain.sharing

import com.practicum.playlistmaker.domain.sharing.models.EmailData

interface SharingRepository {

    fun getShareAppLink(): String

    fun getTermsLink(): String

    fun getSupportEmailData(): EmailData
}
