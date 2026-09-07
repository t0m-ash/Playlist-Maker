package com.practicum.playlistmaker.data.sharing

import android.content.Context
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.sharing.SharingRepository
import com.practicum.playlistmaker.domain.sharing.models.EmailData

class SharingRepositoryImpl(private val context: Context) : SharingRepository {

    override fun getShareAppLink(): String = context.getString(R.string.share_app_url)

    override fun getTermsLink(): String = context.getString(R.string.user_agreement_url)

    override fun getSupportEmailData(): EmailData = EmailData(
        address = context.getString(R.string.support_email),
        subject = context.getString(R.string.support_email_subject),
        text = context.getString(R.string.support_email_text),
    )
}
