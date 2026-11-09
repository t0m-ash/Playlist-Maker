package com.practicum.playlistmaker.ui.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentLibraryBinding

class LibraryFragment : Fragment() {

    private var _binding: FragmentLibraryBinding? = null
    private val binding get() = _binding!!

    private var tabMediator: TabLayoutMediator? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLibraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewPager.adapter =
            LibraryViewPagerAdapter(childFragmentManager, viewLifecycleOwner.lifecycle)

        tabMediator = TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                LibraryViewPagerAdapter.FAVORITE_TRACKS_POSITION ->
                    getString(R.string.library_favorite_tracks_tab)

                LibraryViewPagerAdapter.PLAYLISTS_POSITION ->
                    getString(R.string.library_playlists_tab)

                else -> throw IllegalArgumentException("Unknown tab position: $position")
            }
        }
        tabMediator?.attach()
    }

    override fun onDestroyView() {
        tabMediator?.detach()
        tabMediator = null
        binding.viewPager.adapter = null
        super.onDestroyView()
        _binding = null
    }
}
