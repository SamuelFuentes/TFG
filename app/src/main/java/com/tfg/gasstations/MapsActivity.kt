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
import android.widget.*
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
import com.tfg.gasstations.core.RetrofitHelper
import com.tfg.gasstations.data.model.routes.RouteResponse
import com.tfg.gasstations.data.network.ApiServiceCities
import com.tfg.gasstations.data.network.ApiServiceGasByCity
import com.tfg.gasstations.data.network.ApiServiceRoutes
import com.tfg.gasstations.domain.GetMinPrices

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map : GoogleMap
    private lateinit var selectedCity : String
    private var idSelectedCity : String = "00"
    private var fuelTypeAll : Boolean = true
    private var fuelTypeGas95 : Boolean = false
    private var fuelTypeGasoil : Boolean = false
    private lateinit var markerAvia : BitmapDescriptor
    private lateinit var markerBp : BitmapDescriptor
    private lateinit var markerCarrefour : BitmapDescriptor
    private lateinit var markerCepsa : BitmapDescriptor
    private lateinit var markerDisa : BitmapDescriptor
    private lateinit var markerEroski : BitmapDescriptor
    private lateinit var markerGalp : BitmapDescriptor
    private lateinit var markerHam : BitmapDescriptor
    private lateinit var markerNaturgy : BitmapDescriptor
    private lateinit var markerPetronor : BitmapDescriptor
    private lateinit var markerRepsol : BitmapDescriptor
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
        listCities()
        val markerView = (getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater)
            .inflate(R.layout.custom_markers, null)
        //markerAVIA
        val bitmapAvia = Bitmap.createScaledBitmap(viewToBitmap(markerView.findViewById
            (R.id.imageViewMarkerAvia)), 96, 96, false)
        markerAvia = BitmapDescriptorFactory.fromBitmap(bitmapAvia)
        //markerBp
        val bitmapBp = Bitmap.createScaledBitmap(viewToBitmap(markerView.findViewById
            (R.id.imageViewMarkerBp)), 96, 96, false)
        markerBp = BitmapDescriptorFactory.fromBitmap(bitmapBp)
        //markerCarrefour
        val bitmapCarrefour = Bitmap.createScaledBitmap(viewToBitmap(markerView.findViewById
            (R.id.imageViewMarkerCarrefour)), 96, 96, false)
        markerCarrefour = BitmapDescriptorFactory.fromBitmap(bitmapCarrefour)
        //markerCepsa
        val bitmapCepsa = Bitmap.createScaledBitmap(viewToBitmap(markerView.findViewById
            (R.id.imageViewMarkerCepsa)), 96, 96, false)
        markerCepsa = BitmapDescriptorFactory.fromBitmap(bitmapCepsa)
        //markerDisa
        val bitmapDisa = Bitmap.createScaledBitmap(viewToBitmap(markerView.findViewById
            (R.id.imageViewMarkerDisa)), 96, 96, false)
        markerDisa = BitmapDescriptorFactory.fromBitmap(bitmapDisa)
        //markerEroski
        val bitmapEroski = Bitmap.createScaledBitmap(viewToBitmap(markerView.findViewById
            (R.id.imageViewMarkerEroski)), 96, 96, false)
        markerEroski = BitmapDescriptorFactory.fromBitmap(bitmapEroski)
        //markerGalp
        val bitmapGalp = Bitmap.createScaledBitmap(viewToBitmap(markerView.findViewById
            (R.id.imageViewMarkerGalp)), 96, 96, false)
        markerGalp = BitmapDescriptorFactory.fromBitmap(bitmapGalp)
        //markerHam
        val bitmapHam = Bitmap.createScaledBitmap(viewToBitmap(markerView.findViewById
            (R.id.imageViewMarkerHam)), 96, 96, false)
        markerHam = BitmapDescriptorFactory.fromBitmap(bitmapHam)
        //markerNaturgy
        val bitmapNaturgy = Bitmap.createScaledBitmap(viewToBitmap(markerView.findViewById
            (R.id.imageViewMarkerNaturgy)), 96, 96, false)
        markerNaturgy = BitmapDescriptorFactory.fromBitmap(bitmapNaturgy)
        //markerPetronor
        val bitmapPetronor = Bitmap.createScaledBitmap(viewToBitmap(markerView.findViewById
            (R.id.imageViewMarkerPetronor)), 96, 96, false)
        markerPetronor = BitmapDescriptorFactory.fromBitmap(bitmapPetronor)
        //markerRepsol
        val bitmapRepsol = Bitmap.createScaledBitmap(viewToBitmap(markerView.findViewById
            (R.id.imageViewMarkerRepsol)), 96, 96, false)
        markerRepsol = BitmapDescriptorFactory.fromBitmap(bitmapRepsol)
        //markerShell
        val bitmapShell = Bitmap.createScaledBitmap(viewToBitmap(markerView.findViewById
            (R.id.imageViewMarkerShell)), 96, 96, false)
        markerShell = BitmapDescriptorFactory.fromBitmap(bitmapShell)

        //Clickar para elegir el inicio y final de la ruta y llamar la funcion de crear la ruta
        val buttonCalculateRoute = findViewById<Button>(R.id.buttonCalculateRoute)
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
        //Limpiar el mapa
        val buttonClear : Button = findViewById(R.id.buttonClear)
        buttonClear.setOnClickListener{ map.clear() }
        //Spinner para filtro por ciudades
        var citiesList: List<String> = listCities()
        var spinnerCities = findViewById<Spinner>(R.id.spinnerCities)
        spinnerCities.adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, citiesList)
        spinnerCities.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                selectedCity = citiesList[p2]
                searchIdCity(selectedCity)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
                "Error"
            }
        }
        val buttonSearch : Button = findViewById(R.id.buttonSearch)
        buttonSearch.setOnClickListener{ markerGasByCity() }
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
    fun onRadioButton(view: View) {
        if (view is RadioButton) {
            val checked = view.isChecked
            when (view.getId()) {
                R.id.radioAll -> if (checked) {
                    fuelTypeAll = true
                    fuelTypeGas95 = false
                    fuelTypeGasoil = false
                }
                R.id.radioGas -> if (checked) {
                    fuelTypeAll = false
                    fuelTypeGas95 = true
                    fuelTypeGasoil = false
                }
                R.id.radioGasoil -> if (checked) {
                    fuelTypeAll = false
                    fuelTypeGas95 = false
                    fuelTypeGasoil = true
                }
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

    //Crear markers de las gasolineras con sus descripciones
    private fun markerGasByCity(){
        CoroutineScope(Dispatchers.IO).launch {
            var min95 = GetMinPrices().minPrice95(idSelectedCity)
            var minGasoil = GetMinPrices().minPriceGasoil(idSelectedCity)
            val call = RetrofitHelper.getApiGas().create(ApiServiceGasByCity::class.java)
                .getGasStationsByCity(idSelectedCity)
            if(call.isSuccessful && ::map.isInitialized){
                runOnUiThread {
                    if(fuelTypeAll){
                        for (i in call.body()!!.gasList){
                            var markerIcon = selectMarkerIcon(i.label)
                            var position = convertApiPosToLatLng(i.lati, i.long)
                            var gasTypeForSnippet = isGasType(i.gas95, i.gasol)
                            map.addMarker(
                                MarkerOptions().position(position).title("${i.label}, ${i.address}")
                                    .snippet(i.schedule+" | "+gasTypeForSnippet[0]+gasTypeForSnippet[1]).icon(markerIcon)
                                    //.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_avia))
                            )
                        }
                    }
                    if(fuelTypeGas95){
                        for (i in call.body()!!.gasList){
                            if(i.gas95.isNotEmpty()){
                                if( min95 == i.gas95.replace(",",".").toDouble()){
                                    var position = convertApiPosToLatLng(i.lati, i.long)
                                    findViewById<TextView>(R.id.textViewMinPrice).text = i.gas95+"€"
                                    val imageViewShell : TextView = findViewById(R.id.textViewMinPrice)
                                    val bitmapShell = Bitmap.createScaledBitmap(viewTextToBitmap(imageViewShell),
                                        136, 136, false)
                                    map.addMarker(MarkerOptions().position(position).title(
                                        "${i.label}, ${i.address}")
                                        .snippet("Horario: " + i.schedule).icon(BitmapDescriptorFactory
                                            .fromBitmap(Bitmap.createScaledBitmap(bitmapShell,
                                                136,136,false)
                                            )
                                        )
                                    )
                                }
                                else{
                                    var position = convertApiPosToLatLng(i.lati, i.long)
                                    findViewById<TextView>(R.id.textViewNormal).text = i.gas95+"€"
                                    val imageViewShell : TextView = findViewById(R.id.textViewNormal)
                                    val bitmapShell = Bitmap.createScaledBitmap(viewTextToBitmap(imageViewShell),
                                        136, 136, false)
                                    map.addMarker(MarkerOptions().position(position).title(
                                        "${i.label}, ${i.address}")
                                        .snippet("Horario: " + i.schedule).icon(BitmapDescriptorFactory
                                            .fromBitmap(Bitmap.createScaledBitmap(bitmapShell,
                                                136,136,false)
                                            )
                                        )
                                    )
                                }
                            }
                         }
                    }
                    if(fuelTypeGasoil){
                        for (i in call.body()!!.gasList){
                            if (i.gasol.isNotEmpty()){
                                if(minGasoil == i.gasol.replace(",",".").toDouble()){
                                    var position = convertApiPosToLatLng(i.lati, i.long)
                                    findViewById<TextView>(R.id.textViewMinPrice).text = i.gasol+"€"
                                    val imageViewShell : TextView = findViewById(R.id.textViewMinPrice)
                                    val bitmapShell = Bitmap.createScaledBitmap(viewTextToBitmap(imageViewShell),
                                        136, 136, false)
                                    map.addMarker(MarkerOptions().position(position).title(
                                        "${i.label}, ${i.address}")
                                        .snippet("Horario: " + i.schedule).icon(BitmapDescriptorFactory
                                            .fromBitmap(Bitmap.createScaledBitmap(bitmapShell,
                                                136,136,false)
                                            )
                                        )
                                    )
                                }
                                else{
                                    var position = convertApiPosToLatLng(i.lati, i.long)
                                    findViewById<TextView>(R.id.textViewNormal).text = i.gasol+"€"
                                    val imageViewShell : TextView = findViewById(R.id.textViewNormal)
                                    val bitmapShell = Bitmap.createScaledBitmap(viewTextToBitmap(imageViewShell),
                                        136, 136, false)
                                    map.addMarker(MarkerOptions().position(position).title(
                                        "${i.label}, ${i.address}")
                                        .snippet("Horario: " + i.schedule).icon(BitmapDescriptorFactory
                                            .fromBitmap(Bitmap.createScaledBitmap(bitmapShell,
                                                136,136,false)
                                            )
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    //Crear lista de ciudades para mostrarlas en el Spinner

    //******************************************************

    //Llamada a la API y conversión del JSON

    //Crear rutas
    private fun createRoute(){
        CoroutineScope(Dispatchers.IO).launch {
            val call = RetrofitHelper.getApiRoutes().create(ApiServiceRoutes::class.java)
                .getRoute("5b3ce3597851110001cf624863cc2b9245844cacb6cf551d2d8490c1",
                    start,end)
            if (call.isSuccessful){
                drawRoute(call.body())
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

    //Crear lista de ciudades para mostrarlas en el Spinner
    private fun listCities(): List<String> {
        val citiesArrayList : MutableList<String> = mutableListOf("")
        CoroutineScope(Dispatchers.IO).launch {
            val call = RetrofitHelper.getApiGas().create(ApiServiceCities::class.java).getCities()
            if (call.isSuccessful) {
                for (i in call.body()!!) {
                    citiesArrayList.add(i.provincia)
                }
            }
        }
        return citiesArrayList
    }
    //Buscar la id de la ciudad elegida en el Spinner
    private fun searchIdCity(city : String){
        CoroutineScope(Dispatchers.IO).launch {
            val call = RetrofitHelper.getApiGas().create(ApiServiceCities::class.java).getCities()
            if (call.isSuccessful) {
                for (i in call.body()!!) {
                    if(city == i.provincia){
                        idSelectedCity = i.idProvincia
                    }
                }
            }
        }
    }
    //Convierte las coordenadas de latitud y longitud de la api en LatLng utilizables por googleMaps
    private fun convertApiPosToLatLng(lat : String, lng : String): LatLng{
        return LatLng(lat.replace(",",".").toDouble(),
            lng.replace(",",".").toDouble())
    }
    //Crear custom markers
    private fun viewTextToBitmap(view: View) : Bitmap{
        view.measure(136, 136)
        val bitmap = Bitmap.createBitmap(136, 136, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.layout(0,0,view.measuredWidth, view.measuredHeight)
        view.draw(canvas)
        return bitmap
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
    //Seleccionar el icono para el marker
    private fun selectMarkerIcon(label : String): BitmapDescriptor{
        //markerDefault
        val markerView = (getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater)
            .inflate(R.layout.custom_markers, null)
        val bitmapDefault = Bitmap.createScaledBitmap(viewToBitmap(markerView.findViewById
            (R.id.imageViewMarkerDefault)), 96, 96, false)
        var markerRes = BitmapDescriptorFactory.fromBitmap(bitmapDefault)
        if(label.contains("AVIA")){ markerRes = markerAvia }
        if(label.contains("BP")){ markerRes = markerBp }
        if(label.contains("CARREFOUR")){ markerRes = markerCarrefour }
        if(label.contains("CEPSA")){ markerRes = markerCepsa }
        if(label.contains("DISA")){ markerRes = markerDisa }
        if(label.contains("EROSKI")){ markerRes = markerEroski }
        if(label.contains("GALP")){ markerRes = markerGalp }
        if(label.contains("HAM")){ markerRes = markerHam }
        if(label.contains("NATURGY")){ markerRes = markerNaturgy }
        if(label.contains("PETRONOR")){ markerRes = markerPetronor }
        if(label.contains("REPSOL")){ markerRes = markerRepsol }
        if(label.contains("SHELL")){ markerRes = markerShell }
        return markerRes
    }
    //Mostrará unicamente los productos que tengan el precio en la API
    private fun isGasType(apiGas95 : String, apigasol : String):ArrayList<String>{
        var gas95 = ""
        var gasol = ""
        val gasTypeArrayList = ArrayList<String>()
        if(apiGas95.isNotEmpty()){ gas95 = "Gas 95: "+ apiGas95 +"€ " }
        if(apigasol.isNotEmpty()){ gasol = "Gasoleo: "+ apigasol +"€ " }
        gasTypeArrayList.add(gas95)
        gasTypeArrayList.add(gasol)
        return gasTypeArrayList
    }
}