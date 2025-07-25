package com.example.playlistmaker.playlist.ui.fragment

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.playlistmaker.R
import com.example.playlistmaker.core.model.Playlist
import com.example.playlistmaker.core.util.Result
import com.example.playlistmaker.databinding.FragmentCreatePlaylistBinding
import com.example.playlistmaker.playlist.ui.viewmodel.CreatePlaylistViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream

open class CreatePlaylistFragment : Fragment() {
    protected val createPlaylistViewModel: CreatePlaylistViewModel by viewModel()

    protected lateinit var confirmDialog: MaterialAlertDialogBuilder

    protected lateinit var binding: FragmentCreatePlaylistBinding
    protected lateinit var headerTitle: TextView
    protected lateinit var arrowBackButton: ImageButton
    protected lateinit var playlistImage: ImageView
    protected lateinit var imageButton: ImageView
    protected lateinit var editTextPlaylistName: EditText
    protected lateinit var playlistNameLabel: TextView
    protected lateinit var editTextPlaylistDescription: EditText
    protected lateinit var playlistDescriptionLabel: TextView
    protected lateinit var createButton: AppCompatButton
    protected lateinit var playlistImagePlaceholder: ImageView

    protected var inputPlaylistName = ""
    protected var inputPlaylistDescription = ""
    protected var playlistImageUri: Uri? = null

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            playlistImageUri = uri
            addImageToPlaylist(uri)
        } else {
            Log.d("PhotoPicker", "Ничего не выбрано")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreatePlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        headerTitle = binding.headerTitle
        arrowBackButton = binding.arrowBackCreatePlaylist
        playlistImage = binding.playlistImage
        imageButton = binding.addImageButton
        editTextPlaylistName = binding.playlistName
        editTextPlaylistDescription = binding.playlistDescription
        playlistNameLabel = binding.playlistNameLabel
        playlistDescriptionLabel = binding.playlistDescriptionLabel
        createButton = binding.createPlaylistButton
        playlistImagePlaceholder = binding.playlistImagePlaceholder

        playlistImagePlaceholder.isVisible = false

        buildDialog()

        if (savedInstanceState != null) {
            val playlistName = savedInstanceState.getString(PLAYLIST_NAME)
            val playlistDescription = savedInstanceState.getString(PLAYLIST_DESCRIPTION)

            editTextPlaylistName.setText(playlistName)
            editTextPlaylistName.setText(playlistDescription)
        }

        createPlaylistViewModel.getCreatePlaylistLiveData().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> {
                    saveImageToPrivateStorage()
                    parentFragmentManager.setFragmentResult("playlist_added", Bundle())
                    requireActivity().supportFragmentManager.popBackStack()
                }

                is Result.Failure -> requireActivity().supportFragmentManager.popBackStack()
            }
        }

        arrowBackButton.setOnClickListener {
            showDialogOrCloseFragment()
        }

        playlistImage.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        editTextPlaylistName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                createButton.isEnabled = !s.isNullOrBlank()
            }

            override fun afterTextChanged(s: Editable?) {
                inputPlaylistName = s.toString()
            }
        })

        editTextPlaylistName.setOnFocusChangeListener { v, hasFocus ->
            playlistNameLabel.isVisible = hasFocus
        }

        editTextPlaylistDescription.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                inputPlaylistDescription = s.toString()
            }
        })

        editTextPlaylistDescription.setOnFocusChangeListener { _, hasFocus ->
            playlistDescriptionLabel.isVisible = hasFocus
        }

        createButton.setOnClickListener {
            Toast.makeText(
                requireContext(),
                getString(R.string.playlist_created, inputPlaylistName),
                Toast.LENGTH_LONG
            ).show()
            createPlaylistViewModel.insertPlaylist(
                Playlist(
                    playlistName = inputPlaylistName,
                    playlistDescription = inputPlaylistDescription,
                    playlistImageUri = playlistImageUri
                )
            )
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            showDialogOrCloseFragment()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(PLAYLIST_NAME, inputPlaylistName ?: "")
        outState.putString(PLAYLIST_DESCRIPTION, inputPlaylistDescription ?: "")
    }


    private fun addImageToPlaylist(uri: Uri) {
        playlistImagePlaceholder.isVisible = false
        imageButton.isVisible = false
        playlistImage.setImageURI(uri)
    }

    private fun buildDialog() {
        confirmDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.dialog_finish_playlist_title))
            .setMessage(getString(R.string.dialog_finish_playlist_message))
            .setNegativeButton(getString(R.string.no)) { dialog, which ->
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.finish)) { dialog, which ->
                requireActivity().supportFragmentManager.popBackStack()
            }
    }

    private fun showDialogOrCloseFragment() {
        if (
            inputPlaylistName.isNotEmpty()
            || inputPlaylistDescription.isNotEmpty()
            || playlistImage.drawable != null
        ) {
            confirmDialog.show()
        } else {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    protected fun saveImageToPrivateStorage() {
        val filePath = File(requireContext()
            .getExternalFilesDir(Environment.DIRECTORY_PICTURES), "playlist-album")

        if (!filePath.exists()) {
            filePath.mkdirs()
        }

        playlistImageUri?.let {
            val file = File(filePath, "${inputPlaylistName}.jpg")
            requireContext().contentResolver.openInputStream(it)?.use { inputStream ->
                FileOutputStream(file).use { outputStream ->
                    BitmapFactory
                        .decodeStream(inputStream)
                        ?.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                }
            }
        }
    }

    companion object {
        const val PLAYLIST_NAME = "PLAYLIST_NAME"
        const val PLAYLIST_DESCRIPTION = "PLAYLIST_DESCRIPTION"
    }
}