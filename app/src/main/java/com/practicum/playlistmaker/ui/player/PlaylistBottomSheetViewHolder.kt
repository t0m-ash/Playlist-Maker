package com.practicum.playlistmaker.ui.player

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.databinding.ItemPlaylistBottomSheetBinding
import com.practicum.playlistmaker.domain.models.Playlist
import com.practicum.playlistmaker.ui.common.bind

class PlaylistBottomSheetViewHolder(
    private val binding: ItemPlaylistBottomSheetBinding,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(playlist: Playlist) {
        binding.bind(playlist)
    }

    companion object {

        fun from(parent: ViewGroup): PlaylistBottomSheetViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            return PlaylistBottomSheetViewHolder(
                ItemPlaylistBottomSheetBinding.inflate(inflater, parent, false)
            )
        }
    }
}
