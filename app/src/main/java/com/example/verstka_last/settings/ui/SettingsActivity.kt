package com.example.verstka_last.settings.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.verstka_last.R
import com.example.verstka_last.creator.Creator
import com.example.verstka_last.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private val viewModel: SettingsViewModel by viewModels {
        SettingsViewModelFactory(Creator.provideThemeInteractor(this))
    }

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
            viewModel.onShareClicked()
        }

        binding.buttonSupport.setOnClickListener {
            viewModel.onSupportClicked()
        }

        binding.buttonAgreement.setOnClickListener {
            viewModel.onAgreementClicked()
        }

        binding.themeSwitcher.setOnCheckedChangeListener { _, checked ->
            viewModel.onThemeToggled(checked)
        }
    }

    private fun setupObservers() {
        viewModel.themeSettingsState.observe(this) { isDark ->
            updateThemeSwitch(isDark)
            applySystemTheme(isDark)
        }

        viewModel.navigationEvent.observe(this) { event ->
            event?.let { navigation ->
                when (navigation) {
                    SettingsViewModel.NavigationEvent.ShareApp -> shareApp()
                    SettingsViewModel.NavigationEvent.ContactSupport -> contactSupport()
                    SettingsViewModel.NavigationEvent.OpenAgreement -> openAgreement()
                }
                viewModel.onNavigationHandled()
            }
        }
    }

    private fun updateThemeSwitch(isDark: Boolean) {
        binding.themeSwitcher.setOnCheckedChangeListener(null)
        binding.themeSwitcher.isChecked = isDark
        binding.themeSwitcher.setOnCheckedChangeListener { _, checked ->
            viewModel.onThemeToggled(checked)
        }
    }

    private fun applySystemTheme(isDark: Boolean) {
        val newMode = if (isDark) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        if (AppCompatDelegate.getDefaultNightMode() != newMode) {
            AppCompatDelegate.setDefaultNightMode(newMode)
        }
    }

    private fun shareApp() {
        val shareText = getString(R.string.share_message, getString(R.string.share_link))
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    private fun contactSupport() {
        val email = getString(R.string.support_email)
        val subject = getString(R.string.support_subject)
        val body = getString(R.string.support_body)

        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }
        try { startActivity(intent) } catch (e: Exception) { }
    }

    private fun openAgreement() {
        val agreementUrl = getString(R.string.agreement_link)
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(agreementUrl))
        try { startActivity(intent) } catch (e: Exception) { }
    }
}