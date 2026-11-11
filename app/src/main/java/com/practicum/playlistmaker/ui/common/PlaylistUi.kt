package com.practicum.playlistmaker.ui.common

import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ItemPlaylistBottomSheetBinding
import com.practicum.playlistmaker.domain.models.Playlist
import java.io.File

object PlaylistDiffCallback : DiffUtil.ItemCallback<Playlist>() {

    override fun areItemsTheSame(oldItem: Playlist, newItem: Playlist): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Playlist, newItem: Playlist): Boolean =
        oldItem == newItem
}

fun ImageView.loadPlaylistCover(coverPath: String?) {
    Glide.with(this)
        .load(coverPath?.let { File(it) })
        .placeholder(R.drawable.ic_playlist_placeholder)
        .error(R.drawable.ic_playlist_placeholder)
        .fallback(R.drawable.ic_playlist_placeholder)
        .centerCrop()
        .into(this)
}

fun TextView.showTracksCount(tracksCount: Int) {
    text = resources.getQuantityString(R.plurals.playlist_tracks_count, tracksCount, tracksCount)
}

fun ItemPlaylistBottomSheetBinding.bind(playlist: Playlist) {
    playlistName.text = playlist.name
    playlistTracksCount.showTracksCount(playlist.tracksCount)
    playlistCover.loadPlaylistCover(playlist.coverPath)
}
