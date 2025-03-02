package com.example.playlistmaker.search.ui.fragment

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.core.model.Track
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.search.ui.adapter.TrackSearchHistoryAdapter
import com.example.playlistmaker.search.ui.adapter.TracksAdapter
import com.example.playlistmaker.search.ui.viewmodel.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding

    // ViewModel
    private val searchViewModel: SearchViewModel by viewModel()

    private lateinit var inputEditText: EditText
    private var inputText: String? = ""
    private lateinit var clearButton: FrameLayout

    // Search tracks
    private lateinit var trackAdapter: TracksAdapter
    private lateinit var searchTrackRecyclerView: RecyclerView
    private lateinit var placeholderImage: ImageView
    private lateinit var placeholderText: TextView
    private lateinit var refreshButton: FrameLayout
    private val tracks: MutableList<Track> = mutableListOf()

    // Search History
    private lateinit var trackSearchHistoryAdapter: TrackSearchHistoryAdapter
    private lateinit var historyRecycleView: RecyclerView
    private lateinit var historyTrackContainer: LinearLayout
    private lateinit var clearSearchHistoryButton: Button
    private val historyTracks: MutableList<Track> = mutableListOf()

    // For auto search in 2 seconds
    private lateinit var progressBar: ProgressBar
    private val searchRunnable = Runnable { searchTracks() }
    private val handler = Handler(Looper.getMainLooper())

    private var isClickAllowed = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Загружаем историю поиска через ViewModel
        searchViewModel.downloadSearchHistory() // Здесь был viewHistory

        inputEditText = binding.editingSearchText

        // Восстановление текста в поисковой строке
        if (savedInstanceState != null) {
            inputText = savedInstanceState.getString(SEARCH_TEXT_KEY)
            inputEditText.setText(inputText)
        }

        clearButton = binding.cleanButton
        progressBar = binding.progressBar

        trackAdapter = TracksAdapter(tracks) { track ->
            if (clickDebounce()) {
                searchViewModel.saveTrackInHistoryTrackList(track)
                val bundle = Bundle().apply {
                    putSerializable(INTENT_TRACK_KEY, track)
                }

                findNavController().navigate(R.id.action_searchFragment_to_playerActivity, bundle)
            }
        }

        searchTrackRecyclerView = binding.recycleViewTracksSearch
        placeholderImage = binding.searchPlaceholderImage
        placeholderText = binding.searchPlaceholderText
        refreshButton = binding.searchRefreshButton

        trackSearchHistoryAdapter = TrackSearchHistoryAdapter(historyTracks) { track ->
            val bundle = Bundle().apply {
                putSerializable(INTENT_TRACK_KEY, track)
            }

            findNavController().navigate(R.id.action_searchFragment_to_playerActivity, bundle)
        }

        historyRecycleView = binding.recycleViewTracksSearchHistory
        historyTrackContainer = binding.historyTrackContainer
        clearSearchHistoryButton = binding.clearSearchHistoryButton

        searchTrackRecyclerView.adapter = trackAdapter
        historyRecycleView.adapter = trackSearchHistoryAdapter

        // Вывод списка треков на экран после его изменения в LiveData
        searchViewModel.getTracksLiveData().observe(viewLifecycleOwner) { foundTracks ->
            if (foundTracks == null) {
                searchTrackRecyclerView.visibility = View.INVISIBLE
                processResponseWithError()
                return@observe
            }

            if (foundTracks.isEmpty()) {
                searchTrackRecyclerView.visibility = View.INVISIBLE
                historyTrackContainer.isVisible = false
                placeholderImage.visibility = View.VISIBLE
                placeholderText.visibility = View.VISIBLE

                placeholderImage.setImageResource(R.drawable.search_not_found)
                placeholderText.text = getString(R.string.no_tracks_found)
            } else {
                searchTrackRecyclerView.visibility = View.VISIBLE
                updateTrackList(foundTracks)
                progressBar.visibility = View.GONE
            }
        }

        // При изменении в LiveData обновляетя список для адаптера
        searchViewModel.getHistoryTracksLiveData().observe(viewLifecycleOwner) { tracks ->
            updateSearchHistoryTrackList(tracks)
        }

        // Удаление текста в поисковом запросе
        clearButton.setOnClickListener {
            inputEditText.setText("")
            hideKeyboard(inputEditText)
            clearTrackList()
        }

        // Повтор запроса
        refreshButton.setOnClickListener {
            searchTracks()
        }

        // Очистка истории поиска
        clearSearchHistoryButton.setOnClickListener {
            searchViewModel.deleteSearchHistory()
            historyTrackContainer.isVisible = false
        }

        // Фокус для поисковой строки
        inputEditText.setOnFocusChangeListener { _, hasFocus ->
            val isHistoryVisible = hasFocus && inputEditText.text.isEmpty()
            if (isHistoryVisible) {
                if (historyTracks.isNotEmpty()) {
                    historyTrackContainer.isVisible = true
                    searchTrackRecyclerView.isVisible = false
                } else {
                    historyTrackContainer.isVisible = false
                    searchTrackRecyclerView.isVisible = true
                }
            }
        }

        // Методы ввода текста в поисковой строке
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.isVisible = !s.isNullOrEmpty()

                val isSearchHistoryVisible = if (
                    inputEditText.hasFocus()
                    && s?.isEmpty() == true
                    && historyTracks.isNotEmpty()
                ) true else false

                if (isSearchHistoryVisible) {
                    makeViewsInvisible()
                    clearTrackList()
                    searchTrackRecyclerView.isVisible = false
                    historyTrackContainer.isVisible = true
                } else {
                    makeViewsInvisible()
                    historyTrackContainer.isVisible = false
                    searchTrackRecyclerView.isVisible = true
                }

                searchDebounce(s)
            }

            override fun afterTextChanged(s: Editable?) {
                inputText = s.toString()
            }
        }

        inputEditText.addTextChangedListener(textWatcher)

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchTracks()
                true
            }
            false
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT_KEY, inputText ?: "")

    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchViewModel.saveSearchHistoryInPreferences()
    }

    private fun searchDebounce(s: CharSequence?) {
        handler.removeCallbacks(searchRunnable)

        if (s.isNullOrEmpty()) {
            progressBar.isVisible = false
        } else {
            handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
        }
    }

    private fun searchTracks() {
        makeViewsInvisible()
        searchViewModel.searchTracks(inputText!!)
        progressBar.isVisible = true
    }

    private fun processResponseWithError() {
        placeholderImage.visibility = View.VISIBLE
        placeholderText.visibility = View.VISIBLE

        placeholderImage.setImageResource(R.drawable.search_internet_problems)
        placeholderText.text = getString(R.string.problems_when_searching_for_a_tracks)

        refreshButton.visibility = View.VISIBLE
    }

    private fun makeViewsInvisible() {
        placeholderImage.visibility = View.INVISIBLE
        placeholderText.visibility = View.INVISIBLE
        refreshButton.visibility = View.INVISIBLE
        progressBar.visibility = View.INVISIBLE
    }

    private fun clearTrackList() {
        tracks.clear()
        trackAdapter.notifyDataSetChanged()
    }

    private fun updateTrackList(foundTracks: List<Track>) {
        tracks.clear()
        tracks.addAll(foundTracks)
        trackAdapter.notifyDataSetChanged()
    }

    private fun updateSearchHistoryTrackList(tracks: List<Track>) {
        historyTracks.clear()
        historyTracks.addAll(tracks)
        trackSearchHistoryAdapter.notifyDataSetChanged()
    }

    private fun hideKeyboard(inputEditText: EditText) {
        val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(inputEditText.windowToken, 0)
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }

        return current
    }

    companion object {
        private const val SEARCH_TEXT_KEY = "SEARCH_TEXT"
        private const val INTENT_TRACK_KEY = "TRACK"
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}