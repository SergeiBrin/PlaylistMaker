package com.example.playlistmaker.activity

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.adapter.TracksAdapter
import com.example.playlistmaker.api.ItunesApiService
import com.example.playlistmaker.deserializer.TrackDeserializer
import com.example.playlistmaker.model.Track
import com.example.playlistmaker.model.TrackResponse
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {
    private var inputText: String? = ""
    private val tracks: MutableList<Track> = mutableListOf()
    private val gson = GsonBuilder()
        .registerTypeAdapter(Track::class.java, TrackDeserializer())
        .create()
    private val apiService = Retrofit.Builder()
        .baseUrl("https://itunes.apple.com")
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
        .create(ItunesApiService::class.java)
    private lateinit var placeholderImage: ImageView
    private lateinit var placeholderText: TextView
    private lateinit var refreshButton: FrameLayout
    private lateinit var adapter: TracksAdapter

    companion object {
        const val SEARCH_TEXT_KEY = "SEARCH_TEXT"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        placeholderImage = findViewById(R.id.search_placeholder_image)
        placeholderText = findViewById(R.id.search_placeholder_text)
        refreshButton = findViewById(R.id.search_refresh_button)

        val arrowBackButton = findViewById<ImageView>(R.id.arrow_back_search_screen)
        arrowBackButton.setOnClickListener {
            finish()
        }

        val inputEditText = findViewById<EditText>(R.id.editing_search_text)
        if (savedInstanceState != null) {
            inputEditText.setText(inputText)
        }

        val clearButton = findViewById<FrameLayout>(R.id.clean_button)
        clearButton.setOnClickListener {
            inputEditText.setText("")
            hideKeyboard(inputEditText)
            clearTrackListAndUpdateAdapter()
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recycle_view)
        adapter = TracksAdapter(tracks)
        recyclerView.adapter = adapter

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Пока пусто
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.isVisible = !s.isNullOrEmpty()
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

        refreshButton.setOnClickListener {
            searchTracks() // Повторить запрос
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


    private fun hideKeyboard(inputEditText: EditText) {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(inputEditText.windowToken, 0)
    }

    private fun searchTracks() {
        makeViewsInvisible()

        apiService.search(inputText!!).enqueue(object : Callback<TrackResponse> {
            override fun onResponse(call: Call<TrackResponse>, response: Response<TrackResponse>) {
                if (response.isSuccessful) {
                    val result = response.body()?.tracks

                    if (result.isNullOrEmpty()) {
                        clearTrackListAndUpdateAdapter()
                        placeholderImage.visibility = View.VISIBLE
                        placeholderText.visibility = View.VISIBLE

                        placeholderImage.setImageResource(R.drawable.search_not_found)
                        placeholderText.text = getString(R.string.no_tracks_found)
                    } else {
                        tracks.addAll(result)
                        adapter.notifyDataSetChanged()
                    }
                } else {
                    processResponseWithError()
                }
            }

            override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                processResponseWithError()
            }
        })
    }

    private fun clearTrackListAndUpdateAdapter() {
        tracks.clear()
        adapter.notifyDataSetChanged()
    }

    private fun makeViewsInvisible() {
        placeholderImage.visibility = View.INVISIBLE
        placeholderText.visibility = View.INVISIBLE
        refreshButton.visibility = View.INVISIBLE

    }

    private fun processResponseWithError() {
        placeholderImage.visibility = View.VISIBLE
        placeholderText.visibility = View.VISIBLE

        placeholderImage.setImageResource(R.drawable.search_internet_problems)
        placeholderText.text = getString(R.string.problems_when_searching_for_a_tracks)

        refreshButton.visibility = View.VISIBLE
    }
}