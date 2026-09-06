package com.practicum.playlistmaker.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.switchmaterial.SwitchMaterial
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.Creator
import com.practicum.playlistmaker.R

class SettingsActivity : AppCompatActivity() {

    private val settingsInteractor by lazy { Creator.provideSettingsInteractor(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val btnBack = findViewById<ImageButton>(R.id.btn_back)
        val btnShareApp = findViewById<ImageButton>(R.id.btn_share_app)
        val btnWriteSupport = findViewById<ImageButton>(R.id.btn_write_support)
        val btnUserAgreement = findViewById<ImageButton>(R.id.btn_user_agreement)
        val themeSwitcher = findViewById<SwitchMaterial>(R.id.themeSwitcher)

        themeSwitcher.isChecked = settingsInteractor.isDarkThemeEnabled()
        themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            settingsInteractor.updateDarkTheme(isChecked)
            App.applyTheme(isChecked)
        }

        btnBack.setOnClickListener {
            finish()
        }

        btnShareApp.setOnClickListener {
            val shareAppIntent = Intent(Intent.ACTION_SEND)
            shareAppIntent.setType("text/plain")
            shareAppIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_app_url))
            startActivity(shareAppIntent)
        }

        btnWriteSupport.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SENDTO)
            shareIntent.data = Uri.parse("mailto:")
            shareIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support_email)))
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_email_subject))
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.support_email_text))
            startActivity(shareIntent)
        }

        btnUserAgreement.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW)
            browserIntent.data = Uri.parse(getString(R.string.user_agreement_url))
            startActivity(browserIntent)
        }
    }
}
