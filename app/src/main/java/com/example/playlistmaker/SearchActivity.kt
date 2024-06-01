package com.example.playlistmaker

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class SearchActivity : AppCompatActivity() {

    private var inputText: String? = ""

    companion object {
        const val SEARCH_TEXT_KEY = "SEARCH_TEXT"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val arrowBackButton = findViewById<ImageView>(R.id.arrow_back_search_screen)
        arrowBackButton.setOnClickListener {
            finish()
        }

        val inputEditText = findViewById<EditText>(R.id.editing_search_text)
        if (savedInstanceState != null) {
            inputText = savedInstanceState.getString(SEARCH_TEXT_KEY)
            inputEditText.setText(inputText)
        }

        val clearButton = findViewById<FrameLayout>(R.id.clean_button)

        clearButton.setOnClickListener {
            inputEditText.setText("")
            hideKeyboard(inputEditText)
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Пока пусто
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = clearButtonVisibility(s)
            }

            override fun afterTextChanged(s: Editable?) {
                inputText = s.toString()
            }
        }

        inputEditText.addTextChangedListener(textWatcher)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT_KEY, inputText ?: "")

    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        if (s.isNullOrEmpty()) {
            return View.GONE
        } else {
            return View.VISIBLE
        }
    }

    private fun hideKeyboard(inputEditText: EditText) {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(inputEditText.windowToken, 0)
    }
}