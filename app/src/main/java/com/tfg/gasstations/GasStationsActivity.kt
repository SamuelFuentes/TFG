package com.tfg.gasstations

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tfg.gasstations.ui.MainActivity

class GasStationsActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gas_stations)
        val buttonSignIn: Button = findViewById(R.id.buttonMap)
        firebaseAuth = Firebase.auth

        buttonSignIn.setOnClickListener() {
            val i = Intent(this, MapsActivity::class.java)
            startActivity(i)
        }
    }

    //Mostrar menú
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)
        return true
    }

    //Cerrar sesión
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_signOut -> {
                signOut()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    //Salir de la sección
    private fun signOut() {
        firebaseAuth.signOut()
        val i = Intent(this, MainActivity::class.java)
        startActivity(i)
    }

    //Anular botón back
    override fun onBackPressed() {
        finish()
    }
}