package com.practicum.playlistmaker.ui.player

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.practicum.playlistmaker.domain.models.Playlist
import com.practicum.playlistmaker.ui.common.PlaylistDiffCallback

class PlaylistsBottomSheetAdapter(
    private val onPlaylistClick: (Playlist) -> Unit,
) : ListAdapter<Playlist, PlaylistBottomSheetViewHolder>(PlaylistDiffCallback) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): PlaylistBottomSheetViewHolder = PlaylistBottomSheetViewHolder.from(parent)

    override fun onBindViewHolder(holder: PlaylistBottomSheetViewHolder, position: Int) {
        val playlist = getItem(position)
        holder.bind(playlist)
        holder.itemView.setOnClickListener { onPlaylistClick(playlist) }
    }
}
