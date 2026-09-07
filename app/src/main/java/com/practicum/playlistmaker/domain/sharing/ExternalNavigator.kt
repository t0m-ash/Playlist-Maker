package com.practicum.playlistmaker.domain.sharing

import com.practicum.playlistmaker.domain.sharing.models.EmailData

interface ExternalNavigator {

    fun shareLink(link: String)

    fun openLink(link: String)

    fun openEmail(emailData: EmailData)
}
