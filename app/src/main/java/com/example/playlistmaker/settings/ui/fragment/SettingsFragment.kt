package com.example.playlistmaker.settings.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import com.example.playlistmaker.settings.ui.viewmodel.SettingsViewModel
import com.google.android.material.switchmaterial.SwitchMaterial
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {
    private val settingsViewModel: SettingsViewModel by viewModel()
    private lateinit var themeSwitcher: SwitchMaterial
    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val context = requireContext()

        themeSwitcher = binding.themeSwitcher

        settingsViewModel.getThemeSwitcherLiveData().observe(viewLifecycleOwner) { checked ->
            themeSwitcher.isChecked = checked
        }

        themeSwitcher.setOnCheckedChangeListener { _, checked ->
            settingsViewModel.switchTheme(checked)
            settingsViewModel.saveThemeSettings()
        }

        val actionShareAppButton = binding.btnShareApp
        actionShareAppButton.setOnClickListener {
            val shareAppIntent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getString(R.string.uri_android_developer))
            }

            context.startActivity(shareAppIntent)
        }

        val buttonSupportContact = binding.btnSupportContact
        buttonSupportContact.setOnClickListener {
            val supportContactIntent = Intent().apply {
                action = Intent.ACTION_SENDTO
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.my_email)))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.letter_subject))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.letter_text))
            }

            context.startActivity(supportContactIntent)
        }

        val buttonUserAgreement = binding.btnUserAgreement
        buttonUserAgreement.setOnClickListener {
            val uri = Uri.parse(getString(R.string.uri_practicum_offer))
            val userAgreementIntent = Intent().apply {
                action = Intent.ACTION_VIEW
                data = uri
            }

            context.startActivity(userAgreementIntent)
        }
    }
}