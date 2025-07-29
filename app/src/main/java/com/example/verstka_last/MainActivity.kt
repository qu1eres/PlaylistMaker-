package com.example.verstka_last

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonSearch = findViewById<MaterialButton>(R.id.main_menu_search)
        val buttonMedia = findViewById<MaterialButton>(R.id.main_menu_media)
        val buttonSettings = findViewById<MaterialButton>(R.id.main_menu_settings)
        val mediaIntent = Intent(this, MediaLibraryActivity::class.java)
        val searchIntent = Intent(this, SearchActivity::class.java)
        val settingsIntent = Intent(this, SettingsActivity::class.java)

        val buttonClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                when (v?.id) {
                    R.id.main_menu_search -> {
                        startActivity(searchIntent)
                    }
                    R.id.main_menu_settings -> {
                        startActivity(settingsIntent)
                    }
                }
            }
        }

        buttonMedia.setOnClickListener {
            startActivity(mediaIntent)
        }

        buttonSearch.setOnClickListener(buttonClickListener)
        buttonSettings.setOnClickListener(buttonClickListener)
    }
}