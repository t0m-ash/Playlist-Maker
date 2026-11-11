package com.practicum.playlistmaker.ui.newplaylist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.practicum.playlistmaker.ui.newplaylist.models.NewPlaylistEvent
import com.practicum.playlistmaker.ui.newplaylist.models.NewPlaylistState
import com.practicum.playlistmaker.ui.newplaylist.view_model.NewPlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class NewPlaylistFragment : Fragment() {

    private var _binding: FragmentNewPlaylistBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NewPlaylistViewModel by viewModel()

    private val pickCoverLauncher =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) viewModel.onCoverPicked(uri.toString())
        }

    private var discardDialog: AlertDialog? = null

    private var renderedCoverUri: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        renderedCoverUri = null

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            viewModel.onBackPressed()
        }
        binding.newPlaylistBackButton.setOnClickListener { viewModel.onBackPressed() }

        binding.newPlaylistCoverContainer.setOnClickListener {
            pickCoverLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }

        binding.newPlaylistNameEditText.doOnTextChanged { text, _, _, _ ->
            val name = text?.toString().orEmpty()
            binding.newPlaylistNameLayout.updateHintColor(hasText = name.isNotEmpty())
            viewModel.onNameChanged(name)
        }
        binding.newPlaylistDescriptionEditText.doOnTextChanged { text, _, _, _ ->
            val description = text?.toString().orEmpty()
            binding.newPlaylistDescriptionLayout.updateHintColor(hasText = description.isNotEmpty())
            viewModel.onDescriptionChanged(description)
        }

        binding.newPlaylistCreateButton.setOnClickListener { viewModel.onCreateClicked() }

        viewModel.observeState().observe(viewLifecycleOwner) { state -> render(state) }
        viewModel.observeEvents().observe(viewLifecycleOwner) { event -> handleEvent(event) }
    }

    override fun onDestroyView() {
        discardDialog?.dismiss()
        discardDialog = null
        super.onDestroyView()
        _binding = null
    }

    private fun render(state: NewPlaylistState) {
        binding.newPlaylistCreateButton.isEnabled = state.isCreateEnabled
        renderCover(state.coverUri)
    }

    private fun renderCover(coverUri: String?) {
        if (coverUri == renderedCoverUri) return
        renderedCoverUri = coverUri

        binding.newPlaylistCoverPlaceholder.isVisible = coverUri == null
        binding.newPlaylistCover.isVisible = coverUri != null
        if (coverUri != null) {
            Glide.with(this)
                .load(coverUri.toUri())
                .centerCrop()
                .into(binding.newPlaylistCover)
        }
    }

    private fun handleEvent(event: NewPlaylistEvent) {
        when (event) {
            is NewPlaylistEvent.ShowDiscardDialog -> showDiscardDialog()
            is NewPlaylistEvent.Close -> findNavController().navigateUp()
            is NewPlaylistEvent.PlaylistCreated -> {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.new_playlist_created, event.playlistName),
                    Toast.LENGTH_SHORT,
                ).show()
                findNavController().navigateUp()
            }
        }
    }

    private fun showDiscardDialog() {
        discardDialog?.dismiss()
        discardDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.new_playlist_discard_title)
            .setMessage(R.string.new_playlist_discard_message)
            .setNegativeButton(R.string.new_playlist_discard_cancel, null)
            .setPositiveButton(R.string.new_playlist_discard_confirm) { _, _ ->
                viewModel.onDiscardConfirmed()
            }
            .show()
    }

    private fun TextInputLayout.updateHintColor(hasText: Boolean) {
        val colorRes = if (hasText) R.color.text_field_label else R.color.text_field_hint
        defaultHintTextColor = ContextCompat.getColorStateList(requireContext(), colorRes)
    }
}
