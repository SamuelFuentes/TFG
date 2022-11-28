package com.tfg.gasstations

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map : GoogleMap
    private lateinit var buttonCalculateRoute : Button
    private lateinit var markerDefault : BitmapDescriptor
    private lateinit var markerCepsa : BitmapDescriptor
    private lateinit var markerRepsol : BitmapDescriptor
    private lateinit var markerBp : BitmapDescriptor
    private lateinit var markerGalp : BitmapDescriptor
    private lateinit var markerShell : BitmapDescriptor

    private var start : String = ""
    private var end   : String = ""
    companion object {
        const val REQUEST_CODE_LOCATION = 0
    }

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        createMapFragment()
        markerGasByCity()
        //markerCepsa
        val MarkerViewCepsa = (getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.custom_markers, null)
        val cardViewCepsa : CardView = MarkerViewCepsa.findViewById(R.id.cardViewCepsa)
        val bitmapCepsa = Bitmap.createScaledBitmap(viewToBitmap(cardViewCepsa), cardViewCepsa.width, cardViewCepsa.height, false)
        markerCepsa = BitmapDescriptorFactory.fromBitmap(bitmapCepsa)
        //repsolMarker
        val MarkerViewRepsol = (getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.custom_markers, null)
        val cardViewRepsol : CardView = MarkerViewRepsol.findViewById(R.id.cardViewRepsol)
        val bitmapRepsol = Bitmap.createScaledBitmap(viewToBitmap(cardViewRepsol), cardViewRepsol.width, cardViewRepsol.height, false)
        markerRepsol = BitmapDescriptorFactory.fromBitmap(bitmapRepsol)
        //markerBp
        val MarkerViewBp = (getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.custom_markers, null)
        val cardViewBp : CardView = MarkerViewBp.findViewById(R.id.cardViewBp)
        val bitmapBp = Bitmap.createScaledBitmap(viewToBitmap(cardViewBp), cardViewBp.width, cardViewBp.height, false)
        markerBp = BitmapDescriptorFactory.fromBitmap(bitmapBp)
        //markerGalp
        val markerViewGalp = (getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.custom_markers, null)
        val cardViewGalp : CardView = markerViewGalp.findViewById(R.id.cardViewGalp)
        val bitmapGalp = Bitmap.createScaledBitmap(viewToBitmap(cardViewGalp), cardViewGalp.width, cardViewGalp.height, false)
        markerGalp = BitmapDescriptorFactory.fromBitmap(bitmapGalp)
        //markerShell
        val markerViewShell = (getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.custom_markers, null)
        val cardViewShell : CardView = markerViewShell.findViewById(R.id.cardViewShell)
        val bitmapShell = Bitmap.createScaledBitmap(viewToBitmap(cardViewShell), cardViewShell.width, cardViewShell.height, false)
        markerShell = BitmapDescriptorFactory.fromBitmap(bitmapShell)

        val buttonClear : Button = findViewById(R.id.buttonClear)

        //Clickar para elegir el inicio y final de la ruta y llamar la funcion de crear la ruta
        buttonCalculateRoute = findViewById(R.id.buttonCalculateRoute)
        buttonCalculateRoute.setOnClickListener{
            start = ""
            end = ""
            var startAnimatedRoute = LatLng(0.0,0.0)
            if(::map.isInitialized){
                map.setOnMapClickListener {
                    if (start.isEmpty()){
                        start = "${it.longitude}"+","+"${it.latitude}"
                        val startRoute = LatLng(it.latitude,it.longitude)
                        map.addMarker(MarkerOptions().position(startRoute).title("Inicio de ruta").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)))
                        startAnimatedRoute = startRoute
                    }
                    else if(end.isEmpty()){
                        end = "${it.longitude}"+","+"${it.latitude}"
                        val endRoute = LatLng(it.latitude,it.longitude)
                        map.addMarker(MarkerOptions().position(endRoute).title("Destino").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)))
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
        buttonClear.setOnClickListener{
            map.clear()
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
        val baseURLGas : String = "https://sedeaplicaciones.minetur.gob.es/"
        return Retrofit.Builder().baseUrl(baseURLGas)
            .addConverterFactory(GsonConverterFactory.create()).build()
    }
    //Crear markes de las gasolineras con sus descripciones
    private fun markerGasByCity(): ArrayList<String> {
        val gasArrayList = ArrayList<String>()
        CoroutineScope(Dispatchers.IO).launch {
            val call = getGasStations().create(ApiServiceGasByCity::class.java)
                .getGasStationsByCity("41")
            //35
            if(call.isSuccessful){
                Log.i("DEPURANDO", "SUCCES")
                for (i in call.body()?.gasList!!){
                    gasArrayList.add(i.label)
                }
                Log.i("DEPURANDO", gasArrayList.toString())
                if(::map.isInitialized){
                    runOnUiThread {
                        for (i in call.body()!!.gasList){
                            //var labelIcon = selectIcon(gasArrayList)
                            var markerIcon = selectMarkerIcon(i.label)
                            val lat = i.lati.replace(",",".").toDouble()
                            val lng = i.long.replace(",",".").toDouble()
                            val position = LatLng(lat, lng)
                            var gas95 = ""
                            var gas98 = ""
                            var gasol = ""
                            if(i.gas95.isNotEmpty()){ gas95 = "Gas 95: "+ i.gas95 +"€, " }
                            if(i.gas98.isNotEmpty()){ gas98 = "Gas 98: "+ i.gas98 +"€, " }
                            if(i.gasoleo.isNotEmpty()){ gasol = "Gasoleo: "+ i.gasoleo +"€, " }
                            map.addMarker(
                                MarkerOptions().position(position).title(
                                    "${i.label}, ${i.address} Horario: ${i.schedule}")
                                    .snippet("$gas95$gas98$gasol").icon(markerIcon)
                                    //.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                            )
                        }
                    }
                }
            }
            else{
                Log.i("DEPURANDO", "NOT SUCCES")
            }
        }
        return gasArrayList
    }
    //Crear custom markers
    private fun viewToBitmap(view: View) : Bitmap{
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val bitmap = Bitmap.createBitmap(view.measuredWidth, view.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.layout(0,0,view.measuredWidth, view.measuredHeight)
        view.draw(canvas)
        return bitmap
    }
    //Selector de iconos para los markers
    private fun selectMarkerIcon(label : String): BitmapDescriptor{
        //defaultMarker
        val defaultMarkerView = (getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.custom_markers, null)
        val cardViewDefault : CardView = defaultMarkerView.findViewById(R.id.cardViewDefault)
        val bitmapDefault = Bitmap.createScaledBitmap(viewToBitmap(cardViewDefault), cardViewDefault.width, cardViewDefault.height, false)
        markerDefault = BitmapDescriptorFactory.fromBitmap(bitmapDefault)
        var markerRes = BitmapDescriptorFactory.fromBitmap(bitmapDefault)
        if(label == "CEPSA"){ markerRes = markerCepsa }
        if(label == "REPSOL"){ markerRes = markerRepsol }
        if(label == "BP"){ markerRes = markerBp }
        if(label == "GALP"){ markerRes = markerGalp }
        if(label == "SHELL"){ markerRes = markerShell }
        return markerRes
    }
    /*
    private fun myLocation(){
        CoroutineScope(Dispatchers.IO).launch{
            if(::map.isInitialized){
                runOnUiThread {
                    val myPosition = LatLng(map.myLocation.latitude, map.myLocation.longitude)
                    Log.i("DEPURANDO","myposition ${myPosition}")
                    map.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(myPosition, 16f),
                        4000,
                        null
                    )
                }
            }
        }
    }*/
}