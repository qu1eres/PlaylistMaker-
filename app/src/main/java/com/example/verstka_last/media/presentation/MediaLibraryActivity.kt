package com.example.verstka_last.media.presentation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.verstka_last.R
import com.example.verstka_last.databinding.FragmentLibraryBinding
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.androidx.viewmodel.ext.android.viewModel

class MediaLibraryFragment : Fragment(R.layout.fragment_library) {

    private var _binding: FragmentLibraryBinding? = null
    private val binding get() = _binding!!
    private val mediaViewModel: MediaLibraryViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentLibraryBinding.bind(view)

        setupViewPager()
    }

    private fun setupViewPager() {
        val pagerAdapter = MediaLibraryPagerAdapter(this)
        binding.viewPager2.adapter = pagerAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager2) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.favorites)
                else -> getString(R.string.playlists)
            }
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = MediaLibraryFragment()
    }
}