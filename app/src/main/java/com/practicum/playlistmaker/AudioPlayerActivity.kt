package com.practicum.playlistmaker

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.gson.Gson

class AudioPlayerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)

        val track = intent.getStringExtra(EXTRA_TRACK)
            ?.let { Gson().fromJson(it, Track::class.java) }

        if (track == null) {
            finish()
            return
        }

        findViewById<ImageButton>(R.id.playerBackButton).setOnClickListener {
            finish()
        }

        findViewById<TextView>(R.id.playerTrackName).text = track.trackName
        findViewById<TextView>(R.id.playerArtistName).text = track.artistName
        findViewById<TextView>(R.id.playerDurationValue).text = track.trackTime
        findViewById<TextView>(R.id.playerGenreValue).text = track.primaryGenreName
        findViewById<TextView>(R.id.playerCountryValue).text = track.country

        bindOptionalRow(
            R.id.playerAlbumLabel,
            R.id.playerAlbumValue,
            track.collectionName,
        )
        bindOptionalRow(
            R.id.playerYearLabel,
            R.id.playerYearValue,
            track.releaseYear,
        )

        val cover = findViewById<ImageView>(R.id.playerCover)
        Glide.with(this)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.ic_player_placeholder)
            .error(R.drawable.ic_player_placeholder)
            .into(cover)
    }

    private fun bindOptionalRow(labelId: Int, valueId: Int, value: String?) {
        val label = findViewById<TextView>(labelId)
        val valueView = findViewById<TextView>(valueId)
        if (value.isNullOrEmpty()) {
            label.visibility = View.GONE
            valueView.visibility = View.GONE
        } else {
            valueView.text = value
        }
    }

    companion object {
        const val EXTRA_TRACK = "extra_track"
    }
}
