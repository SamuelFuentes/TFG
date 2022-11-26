package com.tfg.gasstations

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.Query
import kotlin.math.log

class GasStationsActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var spinnerCities: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gas_stations)
        val buttonSignIn: Button = findViewById(R.id.buttonMap)
        val buttonTest: Button = findViewById(R.id.buttonTest)
        listCities()
        var citiesArrayList: ArrayList<String> = listCities()
        spinnerCities = findViewById(R.id.spinnerCities) as Spinner
        spinnerCities.adapter =
            ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, citiesArrayList)
        spinnerCities.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                citiesArrayList.get(p2)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                "Error"
            }
        }

        firebaseAuth = Firebase.auth

        buttonTest.setOnClickListener() {
            listCities()
        }

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
        return
    }

    //Llamada a la API y conversión del JSON
    private fun getCities(): Retrofit {
        return Retrofit.Builder().baseUrl("https://sedeaplicaciones.minetur.gob.es/")
            .addConverterFactory(GsonConverterFactory.create()).build()
    }

    //Crear lista de ciudades para mostrarlas en el spinner
    private fun listCities(): ArrayList<String> {
        val citiesArrayList = ArrayList<String>()
        CoroutineScope(Dispatchers.IO).launch {
            val call = getCities().create(ApiServiceCities::class.java).getCities()
            if (call.isSuccessful) {
                for (i in call.body()!!) {
                    citiesArrayList.add(i.provincia)
                }
                Log.i("DEPURANDO", citiesArrayList.toString())
            } else {
                Log.i("DEPURANDO", "NOT SUCCES")
            }
        }
        return citiesArrayList
    }
}