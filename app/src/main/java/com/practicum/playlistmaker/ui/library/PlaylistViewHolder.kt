package com.practicum.playlistmaker.ui.library

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.databinding.ItemPlaylistBinding
import com.practicum.playlistmaker.domain.models.Playlist
import com.practicum.playlistmaker.ui.common.loadPlaylistCover
import com.practicum.playlistmaker.ui.common.showTracksCount

class PlaylistViewHolder(
    private val binding: ItemPlaylistBinding,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(playlist: Playlist) {
        binding.playlistName.text = playlist.name
        binding.playlistTracksCount.showTracksCount(playlist.tracksCount)
        binding.playlistCover.loadPlaylistCover(playlist.coverPath)
    }

    companion object {

        fun from(parent: ViewGroup): PlaylistViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            return PlaylistViewHolder(ItemPlaylistBinding.inflate(inflater, parent, false))
        }
    }
}
