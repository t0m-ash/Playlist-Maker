package com.practicum.playlistmaker.ui.common

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.domain.models.Track

class TrackAdapter(
    private val onTrackLongClick: ((Track) -> Unit)? = null,
    private val onTrackClick: (Track) -> Unit,
) : RecyclerView.Adapter<TrackViewHolder>() {

    var tracks: List<Track> = emptyList()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder =
        TrackViewHolder.from(parent)

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = tracks[position]
        holder.bind(track)
        holder.itemView.setOnClickListener { onTrackClick(track) }
        onTrackLongClick?.let { onLongClick ->
            holder.itemView.setOnLongClickListener {
                onLongClick(track)
                true
            }
        }
    }

    override fun getItemCount(): Int = tracks.size
}
