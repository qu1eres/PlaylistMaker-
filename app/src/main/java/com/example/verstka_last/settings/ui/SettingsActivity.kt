package com.example.verstka_last.settings.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.verstka_last.databinding.ActivitySettingsBinding
import com.example.verstka_last.sharing.ui.SharingViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private val settingsViewModel: SettingsViewModel by viewModel()
    private val sharingViewModel: SharingViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupEdgeToEdge()
        setupViews()
        setupObservers()
    }

    private fun setupEdgeToEdge() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupViews() {
        binding.settingsButtonBack.setOnClickListener {
            finish()
        }

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
        settingsViewModel.themeSettingsState.observe(this) { isDark ->
            updateThemeSwitch(isDark)
            applySystemTheme(isDark)
        }

        sharingViewModel.sharingEvent.observe(this) { event ->
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
}