package com.tfg.gasstations.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.tfg.gasstations.R

class FavoritesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)

        val bBorrar = findViewById<Button>(R.id.bDBDelete)

        bBorrar.setOnClickListener{

        }
    }
}