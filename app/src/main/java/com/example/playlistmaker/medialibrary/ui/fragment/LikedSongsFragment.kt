package com.example.playlistmaker.medialibrary.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentLikedSongsBinding
import com.example.playlistmaker.medialibrary.ui.viewholder.LikedSongsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class LikedSongsFragment : Fragment() {
    private lateinit var binding: FragmentLikedSongsBinding
    private val viewModel: LikedSongsViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentLikedSongsBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        fun newInstance(): Fragment {
            return LikedSongsFragment()
        }
    }
}