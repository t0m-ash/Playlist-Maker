package com.practicum.playlistmaker.ui.library

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.practicum.playlistmaker.domain.models.Playlist
import com.practicum.playlistmaker.ui.common.PlaylistDiffCallback

class PlaylistsAdapter : ListAdapter<Playlist, PlaylistViewHolder>(PlaylistDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder =
        PlaylistViewHolder.from(parent)

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
