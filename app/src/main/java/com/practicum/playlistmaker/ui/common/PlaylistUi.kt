package com.practicum.playlistmaker.ui.common

import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.practicum.playlistmaker.R
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
    val lastDigit = tracksCount % 10
    val lastTwoDigits = tracksCount % 100
    val stringRes = when {
        lastTwoDigits in 11..14 -> R.string.playlist_tracks_count_many
        lastDigit == 1 -> R.string.playlist_tracks_count_one
        lastDigit in 2..4 -> R.string.playlist_tracks_count_few
        else -> R.string.playlist_tracks_count_many
    }
    text = context.getString(stringRes, tracksCount)
}
