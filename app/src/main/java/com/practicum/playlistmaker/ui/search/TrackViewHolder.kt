package com.practicum.playlistmaker.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ItemTrackBinding
import com.practicum.playlistmaker.domain.models.Track

class TrackViewHolder(
    private val binding: ItemTrackBinding,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(track: Track) {
        binding.trackName.text = track.trackName
        binding.artistName.text = track.artistName
        binding.trackTime.text = track.trackTime

        val cornerRadius =
            itemView.resources.getDimensionPixelSize(R.dimen.track_artwork_corner_radius)
        Glide.with(itemView)
            .load(track.artworkUrl)
            .placeholder(R.drawable.ic_placeholder)
            .error(R.drawable.ic_placeholder)
            .transform(CenterCrop(), RoundedCorners(cornerRadius))
            .into(binding.trackArtwork)
    }

    companion object {

        fun from(parent: ViewGroup): TrackViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            return TrackViewHolder(ItemTrackBinding.inflate(inflater, parent, false))
        }
    }
}
