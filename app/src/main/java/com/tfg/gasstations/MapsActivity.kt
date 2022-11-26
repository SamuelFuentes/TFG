package com.tfg.gasstations

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var buttonCalculateRoute:Button
    private var start : String = ""
    private var end   : String = ""
    companion object {
        const val REQUEST_CODE_LOCATION = 0
    }

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        createMapFragment()

        //Clickar para elegir el inicio y final de la ruta y llamar la funcion de crear la ruta
        buttonCalculateRoute = findViewById(R.id.buttonCalculateRoute)
        buttonCalculateRoute.setOnClickListener{
            map.clear()
            start = ""
            end = ""
            var startAnimatedRoute = LatLng(0.0,0.0)
            if(::map.isInitialized){
                map.setOnMapClickListener {
                    if (start.isEmpty()){
                        start = "${it.longitude}"+","+"${it.latitude}"
                        val startRoute = LatLng(it.latitude,it.longitude)
                        map.addMarker(MarkerOptions().position(startRoute).title("Inicio de ruta"))
                        startAnimatedRoute = startRoute
                    }
                    else if(end.isEmpty()){
                        end = "${it.longitude}"+","+"${it.latitude}"
                        val endRoute = LatLng(it.latitude,it.longitude)
                        map.addMarker(MarkerOptions().position(endRoute).title("Destino"))
                    }
                    else{
                        createRoute()
                        map.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(startAnimatedRoute, 16f),
                            4000,
                            null)
                    }
                }
            }
        }
    }
    //Arrancar GoogleMaps
    override fun onMapReady(googleMap: GoogleMap){
        map = googleMap
        enableMyLocation()
    }
    @SuppressLint("MissingSuperCall", "MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray){
        when(requestCode){
            REQUEST_CODE_LOCATION -> if(grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                map.isMyLocationEnabled = true
            }
            else{
                Toast.makeText(this, "Active permisos de localización", Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }
    //Al maximizar la aplicación, vuelve a comprobar los permisos de ubicación
    @SuppressLint("MissingPermission")
    override fun onResumeFragments() {
        super.onResumeFragments()
        if (!::map.isInitialized) return
        if(!isPermissionsFineGranted()){
            map.isMyLocationEnabled = false
            Toast.makeText(this, "Active permisos de localización", Toast.LENGTH_SHORT)
                .show()
        }
    }
    private fun createMapFragment(){
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }
    //Comprueba si están los permisos de ubicación
    private fun isPermissionsFineGranted() = ContextCompat.checkSelfPermission(
        this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

    //Activa la localización si están los permisos de ubicación concedidos
    @SuppressLint("MissingPermission")
    private fun enableMyLocation(){
        if (!::map.isInitialized) return
        if (isPermissionsFineGranted()){
            map.isMyLocationEnabled = true
        }
        else{
            requestLocationPermission()
        }
    }
    //Solicita permisos de ubicación
    private fun requestLocationPermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            Toast.makeText(this, "Active permisos de localización", Toast.LENGTH_SHORT)
                .show()
        }
        else{
            ActivityCompat.requestPermissions(this, arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE_LOCATION)
        }
    }
    //TODO función para iniciar en tu localización actual
    private fun myStartingLocalization(){}

    //Llamada a la API y conversión del JSON
    private fun getRoute() : Retrofit {
        val baseURLRoutes : String = "https://api.openrouteservice.org/"
        return Retrofit.Builder().baseUrl(baseURLRoutes)
            .addConverterFactory(GsonConverterFactory.create()).build()
    }
    //Crear rutas
    private fun createRoute(){
        CoroutineScope(Dispatchers.IO).launch {
            val call = getRoute().create(ApiServiceRoutes::class.java)
                .getRoute("5b3ce3597851110001cf624863cc2b9245844cacb6cf551d2d8490c1",
                    start,end)
            if (call.isSuccessful){
                drawRoute(call.body())
            }
            else{
                Log.i("DEPURANDO","NOT SUCCESSFUL")
            }
        }
    }
    //Dibujar ruta
    private fun drawRoute(body : RouteResponse?){
        val polyLineOptions = PolylineOptions()
        body?.features?.first()?.geometry?.coordinates?.forEach{
            polyLineOptions.add(LatLng(it[1],it[0])).width(6.8F)
                .color(Color.RED)
        }
        runOnUiThread{
            val route = map.addPolyline(polyLineOptions)
        }
    }

    //Llamada a la API y conversión del JSON
    private fun getGasStations() : Retrofit {
        val baseURLGas : String = "https://sedeaplicaciones.minetur.gob.es/" +
                "ServiciosRESTCarburantes/PreciosCarburantes/EstacionesTerrestres/"
        return Retrofit.Builder().baseUrl(baseURLGas)
            .addConverterFactory(GsonConverterFactory.create()).build()
    }

    private fun createMarkers(body: Array<GasStationsResponse>){
        //address : String, schedule : String, lati : String, long : String,
        //gasoleo : String, gas95 : String, gas98 : String, label : String
        CoroutineScope(Dispatchers.IO).launch {
            if(::map.isInitialized) {
                val arrayGas = ArrayList<String>()
                body.forEach {
                    arrayGas.add(it.label)
                    Log.i("DEPURANDO",arrayGas.toString())
                    var position: LatLng = LatLng(it.lati.toDouble(), it.long.toDouble())
                    map.addMarker(
                        MarkerOptions().position(position).title(
                            "${it.label}, ${it.address}, " + "Horario: ${it.schedule} " +
                                    "Gasoleo: ${it.gasoleo}€, Gasolina 95: ${it.gas95}€, " +
                                    "Gasolina 98: ${it.gas98}€"
                        )
                    )
                }
            }
        }
    }
    private fun crear(){
        CoroutineScope(Dispatchers.IO).launch {
            val call = getGasStations().create(ApiServiceGas::class.java).getGasStations()
            if (call.isSuccessful){
                call.body()?.let { createMarkers(it) }
                Log.i("DEPURANDO", "SI")
            }
        }
    }
}