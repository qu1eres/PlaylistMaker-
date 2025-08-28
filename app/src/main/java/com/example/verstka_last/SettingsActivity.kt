package com.example.verstka_last

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        val themeSwitcher = findViewById<SwitchMaterial>(R.id.themeSwitcher)

        themeSwitcher.isChecked = (application as App).darkTheme

        themeSwitcher.setOnCheckedChangeListener { _, checked ->
            (application as App).switchTheme(checked)
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val buttonSettings = findViewById<MaterialButton>(R.id.settings_button_back)
        buttonSettings.setOnClickListener {
            finish()
        }
        val shareButton = findViewById<MaterialButton>(R.id.button_share)
        shareButton.setOnClickListener {
            shareApp()
        }

        val supportButton = findViewById<MaterialButton>(R.id.button_support)
        supportButton.setOnClickListener {
            contactSupport()
        }

        val agreementButton = findViewById<MaterialButton>(R.id.button_agreement)
        agreementButton.setOnClickListener {
            openAgreement()
        }
    }

    private fun shareApp() {
        val shareText = getString(
            R.string.share_message,
            getString(R.string.share_link)
        )

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
        startActivity(intent)
    }

    private fun openAgreement() {
        val agreementUrl = getString(R.string.agreement_link)
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(agreementUrl))
        startActivity(intent)
    }
}