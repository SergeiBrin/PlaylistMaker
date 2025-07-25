package com.example.playlistmaker.medialibrary.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.core.model.Playlist
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.medialibrary.ui.adapter.MediaLibraryPlaylistAdapter
import com.example.playlistmaker.medialibrary.ui.viewmodel.PlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {
    private lateinit var binding: FragmentPlaylistsBinding
    private val viewModel: PlaylistsViewModel by viewModel()

    private lateinit var emptyPlaylistsView: LinearLayout
    private lateinit var createPlaylistButton: Button
    private lateinit var recycleView: RecyclerView
    private lateinit var adapter: MediaLibraryPlaylistAdapter

    private val playlists: MutableList<Playlist> = mutableListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        emptyPlaylistsView = binding.emptyPlaylistsView
        createPlaylistButton = binding.createPlaylistButton
        recycleView = binding.playlistsRecyclerView
        recycleView.layoutManager = GridLayoutManager(requireContext(), 2)

        adapter = MediaLibraryPlaylistAdapter(playlists) {
            val action = MediaLibraryFragmentDirections.actionMediaLibraryFragmentToPlaylistFragment(it.id)
            findNavController().navigate(action)
        }

        recycleView.adapter = adapter

        viewModel.getAllPlaylists()

        viewModel.getMyPlaylistsLiveData().observe(viewLifecycleOwner) {
            updateMyPlaylists(it)
        }

        createPlaylistButton.setOnClickListener {
            findNavController().navigate(R.id.action_mediaLibraryFragment_to_createPlaylistFragment)
        }
    }

    private fun updateMyPlaylists(updatePlaylists: List<Playlist>) {
        playlists.clear()
        playlists.addAll(updatePlaylists)
        adapter.notifyDataSetChanged()

        if (playlists.isEmpty()) {
            emptyPlaylistsView.isVisible = true
        } else {
            emptyPlaylistsView.isVisible = false
        }
    }

    companion object {
        fun newInstance(): Fragment {
            return PlaylistsFragment()
        }
    }
}