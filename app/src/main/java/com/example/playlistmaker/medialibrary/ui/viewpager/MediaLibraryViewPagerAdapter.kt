package com.example.playlistmaker.medialibrary.ui.viewpager

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.playlistmaker.medialibrary.ui.fragment.LikedSongsFragment
import com.example.playlistmaker.medialibrary.ui.fragment.PlaylistsFragment

class MediaLibraryViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> LikedSongsFragment.newInstance()
            1 -> PlaylistsFragment.newInstance()
            else -> throw IllegalArgumentException()
        }
    }
}