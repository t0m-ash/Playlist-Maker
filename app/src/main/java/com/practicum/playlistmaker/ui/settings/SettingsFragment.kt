package com.practicum.playlistmaker.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.databinding.FragmentSettingsBinding
import com.practicum.playlistmaker.ui.settings.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.observeThemeSettings().observe(viewLifecycleOwner) { settings ->
            binding.themeSwitcher.isChecked = settings.darkTheme
        }

        binding.themeSwitcher.setOnClickListener {
            viewModel.onThemeSwitchClicked(binding.themeSwitcher.isChecked)
        }

        binding.btnShareApp.setOnClickListener { viewModel.onShareAppClicked() }

        binding.btnWriteSupport.setOnClickListener { viewModel.onSupportClicked() }

        binding.btnUserAgreement.setOnClickListener { viewModel.onTermsClicked() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
