package com.minuminu.haruu.wheremyhome.view.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.minuminu.haruu.wheremyhome.R

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setSupportActionBar(findViewById(R.id.settings_toolbar))
    }

}