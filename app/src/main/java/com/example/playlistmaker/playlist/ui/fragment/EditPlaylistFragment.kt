package com.example.playlistmaker.playlist.ui.fragment

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.activity.addCallback
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.signature.ObjectKey
import com.example.playlistmaker.R
import com.example.playlistmaker.core.util.Result
import com.example.playlistmaker.playlist.ui.viewmodel.EditPlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class EditPlaylistFragment : CreatePlaylistFragment() {
    private val editPlaylistViewModel: EditPlaylistViewModel by viewModel()
    private val args: EditPlaylistFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val playlistId = args.playlistId
        val playlistName = args.playlistName
        val playlistDescription = args.playlistDescription
        playlistImageUri = args.playlistImageUri?.toUri()

        headerTitle.text = getString(R.string.edit)
        createButton.text = getString(R.string.save)

        editTextPlaylistName.setText(playlistName)
        editTextPlaylistDescription.setText(playlistDescription)
        downloadPlaylistImage(playlistName)

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
                playlistId,
                inputPlaylistName,
                inputPlaylistDescription,
                playlistImageUri
            )
        }
    }

    private fun downloadPlaylistImage(playlistName: String) {
        val filePath = File(requireContext()
            .getExternalFilesDir(Environment.DIRECTORY_PICTURES), "playlist-album")

        val file = File(filePath, "${playlistName}.jpg")
        val signature = ObjectKey(file.lastModified())

        Glide.with(playlistImage)
            .load(file)
            .signature(signature)
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
                    imageButton.isVisible = false
                    playlistImagePlaceholder.isVisible = true
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
                    playlistImagePlaceholder.isVisible = false
                    return false
                }
            })
            .into(playlistImage)
    }
}