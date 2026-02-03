package com.example.verstka_last.settings.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.verstka_last.R
import com.example.verstka_last.databinding.FragmentSettingsBinding
import com.example.verstka_last.sharing.ui.SharingViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val settingsViewModel: SettingsViewModel by viewModel()
    private val sharingViewModel: SharingViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSettingsBinding.bind(view)

        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        binding.buttonShare.setOnClickListener {
            sharingViewModel.onShareClicked()
        }

        binding.buttonSupport.setOnClickListener {
            sharingViewModel.onSupportClicked()
        }

        binding.buttonAgreement.setOnClickListener {
            sharingViewModel.onAgreementClicked()
        }

        binding.themeSwitcher.setOnCheckedChangeListener { _, checked ->
            settingsViewModel.onThemeToggled(checked)
        }
    }

    private fun setupObservers() {
        settingsViewModel.themeSettingsState.observe(viewLifecycleOwner) { isDark ->
            updateThemeSwitch(isDark)
            applySystemTheme(isDark)
        }

        sharingViewModel.sharingEvent.observe(viewLifecycleOwner) { event ->
            event?.let { sharingEvent ->
                when (sharingEvent) {
                    is SharingViewModel.SharingEvent.ShareApp -> shareApp(sharingEvent.message, sharingEvent.link)
                    is SharingViewModel.SharingEvent.ContactSupport -> contactSupport(
                        sharingEvent.email,
                        sharingEvent.subject,
                        sharingEvent.body
                    )
                    is SharingViewModel.SharingEvent.OpenAgreement -> openAgreement(sharingEvent.url)
                }
                sharingViewModel.onSharingEventHandled()
            }
        }
    }

    private fun updateThemeSwitch(isDark: Boolean) {
        binding.themeSwitcher.setOnCheckedChangeListener(null)
        binding.themeSwitcher.isChecked = isDark
        binding.themeSwitcher.setOnCheckedChangeListener { _, checked ->
            settingsViewModel.onThemeToggled(checked)
        }
    }

    private fun applySystemTheme(isDark: Boolean) {
        val newMode = if (isDark) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        if (AppCompatDelegate.getDefaultNightMode() != newMode) {
            AppCompatDelegate.setDefaultNightMode(newMode)
        }
    }

    private fun shareApp(message: String, link: String) {
        val shareText = String.format(message, link)
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    private fun contactSupport(email: String, subject: String, body: String) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }
        try {
            startActivity(intent)
        } catch (e: Exception) {}
    }

    private fun openAgreement(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        try {
            startActivity(intent)
        } catch (e: Exception) {}
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = SettingsFragment()
    }
}