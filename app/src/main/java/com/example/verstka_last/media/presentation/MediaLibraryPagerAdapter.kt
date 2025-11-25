package com.example.verstka_last.media.presentation

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class MediaLibraryPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> com.example.verstka_last.media.presentation.fragments.FavoritesFragment.newInstance()
            1 -> com.example.verstka_last.media.presentation.fragments.PlaylistsFragment.newInstance()
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }
}