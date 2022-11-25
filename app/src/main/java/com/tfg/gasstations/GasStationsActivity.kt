package com.tfg.gasstations

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GasStationsActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gas_stations)

        val buttonSignIn : Button = findViewById(R.id.buttonMap)
        val buttonTest : Button = findViewById(R.id.buttonTest)

        firebaseAuth = Firebase.auth

        buttonTest.setOnClickListener(){
            CoroutineScope(Dispatchers.IO).launch {
                cities()
            }
        }

        buttonSignIn.setOnClickListener(){
            val i = Intent(this, MapsActivity::class.java)
            startActivity(i)
        }
    }
    //Mostrar menú
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu,menu)
        return true
    }
    //Cerrar sesión
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_signOut -> {
                signOut()
            }
        }
        return super.onOptionsItemSelected(item)
    }
    private fun signOut(){
        firebaseAuth.signOut()
        val i = Intent(this, MainActivity::class.java)
        startActivity(i)
    }
    //Anular botón back
    override fun onBackPressed() {
        return
    }

    private fun getCities() : Retrofit {
        return Retrofit.Builder().baseUrl("https://sedeaplicaciones.minetur.gob.es/")
            .addConverterFactory(GsonConverterFactory.create()).build()
    }

    private fun cities(){
        CoroutineScope(Dispatchers.IO).launch {
            val call = getCities().create(ApiServiceCities::class.java).getCity()
            if(call.isSuccessful){
                Log.i("DEPURANDO","SUCCESSFUL")
            }
            else{
                Log.i("DEPURANDO","NOT SUCCESSFUL")
            }
            call.body()
        }
    }
}