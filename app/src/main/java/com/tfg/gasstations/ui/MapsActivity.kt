package com.tfg.gasstations.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Layout
import android.util.Log
import android.view.Gravity.*
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.tfg.gasstations.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.tfg.gasstations.core.RetrofitHelper
import com.tfg.gasstations.data.model.routes.RouteResponse
import com.tfg.gasstations.data.network.ApiServiceAllGas
import com.tfg.gasstations.data.network.ApiServiceGasByCity
import com.tfg.gasstations.domain.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map : GoogleMap
    private lateinit var idSelectedCity : String
    private var fuelType: String = "ALL"
    private var distance: String = "1km"
    private var start : String = ""
    private var end   : String = ""
    companion object {
        const val REQUEST_CODE_LOCATION = 0
    }

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        createMapFragment()
        GetCities().listCities()
        val layoutMenu = findViewById<LinearLayout>(R.id.lLMenu)
        val buttonMenu = findViewById<ImageButton>(R.id.bMenu)
        buttonMenu.setOnClickListener{
            if (layoutMenu.visibility == View.VISIBLE){
                layoutMenu.visibility = View.GONE
                buttonMenu.foregroundGravity = RIGHT
            }else{
                layoutMenu.visibility = View.VISIBLE
                buttonMenu.foregroundGravity = LEFT
            }
        }

        //Clickar para elegir el inicio y final de la ruta y llamar la funcion de crear la ruta
        val buttonCalculateRoute = findViewById<ImageButton>(R.id.bRoute)
        buttonCalculateRoute.setOnClickListener{
            if (layoutMenu.visibility == View.VISIBLE) {
                layoutMenu.visibility = View.GONE
            }
            customRoute()
        }
        //Limpiar el mapa
        val buttonClear : ImageButton = findViewById(R.id.bClear)
        buttonClear.setOnClickListener{ map.clear() }
        //Spinner para filtro por ciudades
        var citiesList: List<String> = GetCities().listCities()
        var spinnerCities = findViewById<Spinner>(R.id.spCities)
        spinnerCities.adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, citiesList)
        spinnerCities.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                CoroutineScope(Dispatchers.IO).launch {
                    idSelectedCity = GetCities().searchIdCity(citiesList[p2])
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
                "Error"
            }
        }
        val buttonSearch : ImageButton = findViewById(R.id.bSearch)
        buttonSearch.setOnClickListener{
            //markNearPos()
            markerGasByCity()
            closeToMe()
            layoutMenu.visibility = View.GONE
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
            }else -> {}
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
    fun onRBPosition(view: View){
        if (view is RadioButton) {
            val checked= view.isChecked
            when (view.getId()) {
                R.id.r1km -> if (checked) { distance= "1km" }
                R.id.r2km -> if (checked) { distance= "2km" }
                R.id.r6km -> if (checked) { distance= "6km" }
            }
        }
    }
    fun onRBfuelType(view: View){
        if (view is RadioButton) {
            val checked= view.isChecked
            when (view.getId()) {
                R.id.rAll -> if (checked) { fuelType= "ALL" }
                R.id.rGas -> if (checked) { fuelType= "GAS95" }
                R.id.rGasoil -> if (checked) { fuelType= "GASOIL" }
            }
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
        if (isPermissionsFineGranted()){ map.isMyLocationEnabled = true
        }else{ requestLocationPermission() }
    }
    //Solicita permisos de ubicación
    private fun requestLocationPermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            Toast.makeText(this, "Active permisos de localización", Toast.LENGTH_SHORT).show()
        }else{
            ActivityCompat.requestPermissions(this, arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE_LOCATION
            )
        }
    }
    //TODO función para iniciar en tu localización actual
    private fun myStartingLocalization(){}

    //Crear markers de las gasolineras con sus descripciones
    private fun markerGasByCity() {
        CoroutineScope(Dispatchers.IO).launch {
            var min95 = GetMinPrices().minPrice95(idSelectedCity)
            var minGasoil = GetMinPrices().minPriceGasoil(idSelectedCity)
            val call = RetrofitHelper.getApiGas().create(ApiServiceGasByCity::class.java)
                .getGasStationsByCity(idSelectedCity)
            if (call.isSuccessful && ::map.isInitialized){
                runOnUiThread{
                    for(i in call.body()!!.gasList){
                        var position = GetApiLatLng().toLatLng(i.lati,i.long)
                        if(fuelType=="ALL"){
                            var gasTypeForSnippet = GetHavePrice().price(i.gas95, i.gasol)
                            map.addMarker(
                                MarkerOptions().position(position).title("${i.label}, ${i.address}")
                                    .snippet(i.schedule+" | "+gasTypeForSnippet[0]+gasTypeForSnippet[1])
                                    .icon(GetMarkerIcon().select(i.label)))
                        }
                        if(fuelType=="GAS95" && i.gas95.isNotEmpty()){
                            if(min95== i.gas95.replace(",",".").toDouble()){
                                Log.i("DEPURANDO",i.lati+" "+ i.long)
                                findViewById<TextView>(R.id.tVMinPrice).text = i.gas95+"€"
                                val imageViewShell : TextView = findViewById(R.id.tVMinPrice)
                                val bitmapShell = Bitmap.createScaledBitmap(GetViewToBitmap()
                                    .viewTextToBitmap(imageViewShell), 136, 136, false)
                                map.addMarker(MarkerOptions().position(position).title(
                                    "${i.label}, ${i.address}").snippet("Horario: " + i.schedule)
                                    .icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap
                                        (bitmapShell, 136,136,false))))
                                map.animateCamera(
                                    CameraUpdateFactory.newLatLngZoom(position, 16f),
                                    4000,
                                    null)
                            }else{
                                findViewById<TextView>(R.id.tVNormal).text = i.gas95+"€"
                                val imageViewShell : TextView = findViewById(R.id.tVNormal)
                                val bitmapShell = Bitmap.createScaledBitmap(GetViewToBitmap()
                                    .viewTextToBitmap(imageViewShell), 136, 136, false)
                                map.addMarker(MarkerOptions().position(position).title(
                                    "${i.label}, ${i.address}").snippet("Horario: " + i.schedule)
                                    .icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap
                                        (bitmapShell, 136,136,false))))
                            }
                        }
                        if(fuelType=="GASOIL" && i.gasol.isNotEmpty()){
                            if(minGasoil== i.gasol.replace(",",".").toDouble()){
                                findViewById<TextView>(R.id.tVMinPrice).text = i.gasol+"€"
                                val imageViewShell : TextView = findViewById(R.id.tVMinPrice)
                                val bitmapShell = Bitmap.createScaledBitmap(GetViewToBitmap()
                                    .viewTextToBitmap(imageViewShell), 136, 136, false)
                                map.addMarker(MarkerOptions().position(position).title(
                                    "${i.label}, ${i.address}").snippet("Horario: " + i.schedule)
                                    .icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap
                                        (bitmapShell, 136,136,false))))
                                map.animateCamera(
                                    CameraUpdateFactory.newLatLngZoom(position, 16f),
                                    4000,
                                    null)
                            }else{
                                findViewById<TextView>(R.id.tVNormal).text = i.gasol+"€"
                                val imageViewShell : TextView = findViewById(R.id.tVNormal)
                                val bitmapShell = Bitmap.createScaledBitmap(GetViewToBitmap()
                                    .viewTextToBitmap(imageViewShell), 136, 136, false)
                                map.addMarker(MarkerOptions().position(position).title(
                                    "${i.label}, ${i.address}").snippet("Horario: " + i.schedule)
                                    .icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap
                                        (bitmapShell, 136,136,false))))
                            }
                        }
                    }
                }
            }
        }
    }
    //Crear ruta custom
    private fun customRoute(){
        start = ""
        end = ""
        var startAnimatedRoute = LatLng(0.0,0.0)
        if(::map.isInitialized){
            map.setOnMapClickListener {
                if (start.isEmpty()){
                    start = "${it.longitude}"+","+"${it.latitude}"
                    val startRoute = LatLng(it.latitude,it.longitude)
                    map.addMarker(MarkerOptions().position(startRoute).title("Inicio de ruta")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)))
                    startAnimatedRoute = startRoute
                }else if(end.isEmpty()){
                    end = "${it.longitude}"+","+"${it.latitude}"
                    val endRoute = LatLng(it.latitude,it.longitude)
                    map.addMarker(MarkerOptions().position(endRoute).title("Destino")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)))
                }else{
                    CoroutineScope(Dispatchers.IO).launch {
                        drawRoute(GetCreateRoutes().createRoute(start, end))
                    }
                    map.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(startAnimatedRoute, 16f),
                        4000,
                        null
                    )
                }
            }
        }
    }
    //Dibujar ruta
    private suspend fun drawRoute(body : RouteResponse?){
            val call = RetrofitHelper.getApiGas().create(ApiServiceAllGas::class.java)
                .getAllGasStations()
            if(call.isSuccessful){
                val polyLineOptions = PolylineOptions()
                runOnUiThread {
                body?.features?.first()?.geometry?.coordinates?.forEach {
                    polyLineOptions.add(LatLng(it[1], it[0])).width(6.8F).color(Color.RED)
                    for (i in call.body()!!.gasList) {
                        if (GetNearPos().calculateNear(
                                it[1],
                                it[0],
                                i.lati.replace(",", ".").toDouble(),
                                i.long.replace(",", ".").toDouble(),
                                distance
                            )
                        ) {
                            var position = GetApiLatLng().toLatLng(i.lati,i.long)
                            if(fuelType=="ALL"){
                                var gasTypeForSnippet = GetHavePrice().price(i.gas95, i.gasol)
                                map.addMarker(
                                    MarkerOptions().position(position).title("${i.label}, ${i.address}")
                                        .snippet(i.schedule+" | "+gasTypeForSnippet[0]+gasTypeForSnippet[1])
                                        .icon(GetMarkerIcon().select(i.label)))
                            }
                            if(fuelType=="GAS95" && i.gas95.isNotEmpty()){
                                    findViewById<TextView>(R.id.tVNormal).text = i.gas95+"€"
                                    val imageViewShell : TextView = findViewById(R.id.tVNormal)
                                    val bitmapShell = Bitmap.createScaledBitmap(GetViewToBitmap()
                                        .viewTextToBitmap(imageViewShell), 136, 136, false)
                                    map.addMarker(MarkerOptions().position(position).title(
                                        "${i.label}, ${i.address}").snippet("Horario: " + i.schedule)
                                        .icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap
                                            (bitmapShell, 136,136,false))))
                            }
                            if(fuelType=="GASOIL" && i.gasol.isNotEmpty()){
                                    findViewById<TextView>(R.id.tVNormal).text = i.gasol+"€"
                                    val imageViewShell : TextView = findViewById(R.id.tVNormal)
                                    val bitmapShell = Bitmap.createScaledBitmap(GetViewToBitmap()
                                        .viewTextToBitmap(imageViewShell), 136, 136, false)
                                    map.addMarker(MarkerOptions().position(position).title(
                                        "${i.label}, ${i.address}").snippet("Horario: " + i.schedule)
                                        .icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap
                                            (bitmapShell, 136,136,false))))
                            }
                        }
                    }
                }
            }
                runOnUiThread{ val route = map.addPolyline(polyLineOptions) }
        }
    }

    //añade markers a las gasolineras cercanas
    private suspend fun markNearPos(lat: Double, lng: Double) {
            val call = RetrofitHelper.getApiGas().create(ApiServiceAllGas::class.java)
                .getAllGasStations()
            if (call.isSuccessful && ::map.isInitialized) {
                runOnUiThread {
                    for (i in call.body()!!.gasList) {
                        if(GetNearPos().calculateNear(lat,
                                lng,
                                i.lati.replace(",",".").toDouble(),
                                i.long.replace(",",".").toDouble(),
                                distance)){
                            var position = GetApiLatLng().toLatLng(i.lati,i.long)
                            if(fuelType=="ALL"){
                                var gasTypeForSnippet = GetHavePrice().price(i.gas95, i.gasol)
                                map.addMarker(
                                    MarkerOptions().position(position).title("${i.label}, ${i.address}")
                                        .snippet(i.schedule+" | "+gasTypeForSnippet[0]+gasTypeForSnippet[1])
                                        .icon(GetMarkerIcon().select(i.label)))
                            }
                            if(fuelType=="GAS95" && i.gas95.isNotEmpty()){
                                findViewById<TextView>(R.id.tVNormal).text = i.gas95+"€"
                                val imageViewShell : TextView = findViewById(R.id.tVNormal)
                                val bitmapShell = Bitmap.createScaledBitmap(GetViewToBitmap()
                                    .viewTextToBitmap(imageViewShell), 136, 136, false)
                                map.addMarker(MarkerOptions().position(position).title(
                                    "${i.label}, ${i.address}").snippet("Horario: " + i.schedule)
                                    .icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap
                                        (bitmapShell, 136,136,false))))
                            }
                            if(fuelType=="GASOIL" && i.gasol.isNotEmpty()){
                                findViewById<TextView>(R.id.tVNormal).text = i.gasol+"€"
                                val imageViewShell : TextView = findViewById(R.id.tVNormal)
                                val bitmapShell = Bitmap.createScaledBitmap(GetViewToBitmap()
                                    .viewTextToBitmap(imageViewShell), 136, 136, false)
                                map.addMarker(MarkerOptions().position(position).title(
                                    "${i.label}, ${i.address}").snippet("Horario: " + i.schedule)
                                    .icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap
                                        (bitmapShell, 136,136,false))))
                            }
                        }
                    }
                }
            }

    }

    private fun closeToMe(){
        if(::map.isInitialized){
            var lat = map.myLocation.latitude
            var lng = map.myLocation.longitude
            var myLocation = LatLng(lat,lng)
            CoroutineScope(Dispatchers.IO).launch{
                markNearPos(lat,lng)
            }
            map.animateCamera(
                CameraUpdateFactory.newLatLngZoom(myLocation, 16f),
                4000,
                null
            )
            map.myLocation
        }
    }
}