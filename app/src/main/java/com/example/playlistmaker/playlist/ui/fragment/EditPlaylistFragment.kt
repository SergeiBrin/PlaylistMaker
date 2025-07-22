package com.example.playlistmaker.playlist.ui.fragment

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.playlistmaker.R
import com.example.playlistmaker.core.util.Result
import com.example.playlistmaker.playlist.ui.viewmodel.EditPlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class EditPlaylistFragment : CreatePlaylistFragment() {
    private val editPlaylistViewModel: EditPlaylistViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val playlistId = arguments?.getInt(ID)
        val playlistName = arguments?.getCharSequence(PLAYLIST_NAME)
        val playlistDescription = arguments?.getCharSequence(PLAYLIST_DESCRIPTION)

        headerTitle.text = getString(R.string.edit)
        createButton.text = getString(R.string.save)

        editTextPlaylistName.setText(playlistName)
        editTextPlaylistDescription.setText(playlistDescription)
        downloadPlaylistImage(playlistName.toString())

        editPlaylistViewModel.getUpdatePlaylistLiveData().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> {
                    saveImageToPrivateStorage()
                    findNavController().popBackStack()
                }

                is Result.Failure -> findNavController().popBackStack()
            }
        }

        arrowBackButton.setOnClickListener {
            findNavController().popBackStack()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack()
        }

        createButton.setOnClickListener {
            editPlaylistViewModel.updatePlaylist(
                playlistId!!,
                inputPlaylistName,
                inputPlaylistDescription,
                playlistImageUri
            )
        }
    }

    private fun downloadPlaylistImage(playlistName: String) {
        val filePath = File(requireContext()
            .getExternalFilesDir(Environment.DIRECTORY_PICTURES), "playlist-album")
        val file = File(filePath, "${playlistName.toString()}.jpg")
        Glide.with(playlistImage)
            .load(file)
            .transform(
                CenterCrop()
            )
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    imageButton.isVisible = true
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    imageButton.isVisible = false
                    return false
                }
            })
            .into(playlistImage)

    }

    companion object {
        private const val ID = "PLAYLIST_ID"
        private const val PLAYLIST_NAME = "PLAYLIST_NAME"
        private const val PLAYLIST_DESCRIPTION = "PLAYLIST_DESCRIPTION"
    }
}