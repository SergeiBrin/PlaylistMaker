package com.example.playlistmaker.search.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.core.model.Track
import com.example.playlistmaker.player.ui.activity.PlayerActivity
import com.example.playlistmaker.search.ui.adapter.TrackSearchHistoryAdapter
import com.example.playlistmaker.search.ui.adapter.TracksAdapter
import com.example.playlistmaker.search.ui.viewmodel.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : AppCompatActivity() {
    // ViewModel
    private val searchViewModel: SearchViewModel by viewModel()

    private lateinit var arrowBackButton: ImageView

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        // Загружаем историю поиска через ViewModel
        searchViewModel.downloadSearchHistory() // Здесь был viewHistory

        inputEditText = findViewById(R.id.editing_search_text)
        // Восстановление текста в поисковой строке
        if (savedInstanceState != null) {
            inputEditText.setText(inputText)
        }

        arrowBackButton = findViewById(R.id.arrow_back_search_screen)
        clearButton = findViewById(R.id.clean_button)
        progressBar = findViewById(R.id.progress_bar)

        trackAdapter = TracksAdapter(tracks) { track ->
            if (clickDebounce()) {
                searchViewModel.saveTrackInHistoryTrackList(track)
                val playerIntent = Intent(this, PlayerActivity::class.java).apply {
                    putExtra("Track", track)
                }
                startActivity(playerIntent)
            }
        }

        searchTrackRecyclerView = findViewById(R.id.recycle_view_tracks_search)
        placeholderImage = findViewById(R.id.search_placeholder_image)
        placeholderText = findViewById(R.id.search_placeholder_text)
        refreshButton = findViewById(R.id.search_refresh_button)

        trackSearchHistoryAdapter = TrackSearchHistoryAdapter(historyTracks) { track ->
            val playerIntent = Intent(this, PlayerActivity::class.java).apply {
                putExtra("Track", track)
            }
            startActivity(playerIntent)
        }

        historyRecycleView = findViewById(R.id.recycle_view_tracks_search_history)
        historyTrackContainer = findViewById(R.id.history_track_container)
        clearSearchHistoryButton = findViewById(R.id.clear_search_history_button)

        searchTrackRecyclerView.adapter = trackAdapter
        historyRecycleView.adapter = trackSearchHistoryAdapter

        // Вывод списка треков на экран после его изменения в LiveData
        searchViewModel.getTracksLiveData().observe(this) { foundTracks ->
            if (foundTracks == null) {
                processResponseWithError()
                return@observe
            }

            if (foundTracks.isEmpty()) {
                historyTrackContainer.isVisible = false
                placeholderImage.visibility = View.VISIBLE
                placeholderText.visibility = View.VISIBLE

                placeholderImage.setImageResource(R.drawable.search_not_found)
                placeholderText.text = getString(R.string.no_tracks_found)
            } else {
                updateTrackList(foundTracks)
                progressBar.visibility = View.GONE
            }
        }

        // При изменении в LiveData обновляетя список для адаптера
        searchViewModel.getHistoryTracksLiveData().observe(this) { tracks ->
            updateSearchHistoryTrackList(tracks)
        }

        // Возврат к предыдущему активити
        arrowBackButton.setOnClickListener {
            finish()
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
                } else {
                    historyTrackContainer.isVisible = false
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

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        inputText = savedInstanceState.getString(SEARCH_TEXT_KEY)
    }

    /**
     * При завершении работы активити список треков истории сохраняется в shared preferences
     */
    override fun onStop() {
        super.onStop()
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
        progressBar.visibility = View.VISIBLE
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
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
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
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

}