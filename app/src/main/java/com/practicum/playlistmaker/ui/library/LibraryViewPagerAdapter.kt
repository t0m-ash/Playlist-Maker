package com.practicum.playlistmaker.ui.library

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class LibraryViewPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int = TAB_COUNT

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            FAVORITE_TRACKS_POSITION -> FavoriteTracksFragment.newInstance()
            PLAYLISTS_POSITION -> PlaylistsFragment.newInstance()
            else -> throw IllegalArgumentException("Unknown tab position: $position")
        }
    }

    companion object {
        const val FAVORITE_TRACKS_POSITION = 0
        const val PLAYLISTS_POSITION = 1
        private const val TAB_COUNT = 2
    }
}
