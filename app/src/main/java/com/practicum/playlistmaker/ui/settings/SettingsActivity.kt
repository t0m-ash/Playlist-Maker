package com.practicum.playlistmaker.ui.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding
import com.practicum.playlistmaker.ui.settings.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.observeThemeSettings().observe(this) { settings ->
            binding.themeSwitcher.isChecked = settings.darkTheme
        }

        binding.themeSwitcher.setOnClickListener {
            viewModel.onThemeSwitchClicked(binding.themeSwitcher.isChecked)
        }

        binding.btnBack.setOnClickListener { finish() }

        binding.btnShareApp.setOnClickListener { viewModel.onShareAppClicked() }

        binding.btnWriteSupport.setOnClickListener { viewModel.onSupportClicked() }

        binding.btnUserAgreement.setOnClickListener { viewModel.onTermsClicked() }
    }
}
